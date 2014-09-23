package demo;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Evolves a Collection of Triangles in parallel. A ParallelEvolver makes
 * use of Java 8 Streams and "jailing" a parallel stream in a ForkJoinPool.
 * The idea was taken from This is based off an idea from
 * <a href="http://stackoverflow.com/questions/21163108">
 * a Stack Overflow post</a>.
 * @author collinsd
 */
public class ParallelEvolver extends Thread
{
  /** A Collection of EvolveFunctions that mutate a triangle. For every
   * iteration of evolution, the EvolveFunction which makes the
   * triangle the most fit will be run on the triangle. */
  private Collection<EvolveFunction> hillClimbFunctions;
  
  /** A FitnessFunction which this ParallelEvolver instance uses
   * to determine which EvolveFunction can evolve a Triangle better. */
  private FitnessFunction fitnessFunction;
  
  /** A Collection of Triangles to evolve. */
  private Collection<Triangle> triangles;
  
  /** The number of threads that this thread will spawn to evolve the
   * triangles. */
  private int threadCount = 0;
  
  /** The ForkJoinPool used to "jail" the parallel stream that will
   * evolve the triangles in parallel. */
  private ForkJoinPool forkJoinPool;
  
  /**
   * Given a Collection of EvolveFunctions, a FitnessFunction, a collection
   * of triangles and a number of threads to use, creates a ParallelEvolver
   * thread that  evolves all given triangles in parallel until
   * {@link java.lang.Thread#interrupt()} is called on this thread.
   * @param evolveFunctions A Collection of EvolveFunctions that mutate a
   * triangle.
   * @param fitnessFucntion A FitnessFunction which this ParallelEvolver
   * instance uses to determine which EvolveFunction can evolve a Triangle
   * better.
   * @param triangles A Collection of Triangles to evolve.
   * @param threadCount The number of threads that this thread will spawn
   * to evolve the triangles.
   */
  public ParallelEvolver(
      Collection<EvolveFunction> evolveFunctions,
      FitnessFunction fitnessFucntion,
      Collection<Triangle> triangles,
      int threadCount)
  {
    super("Parallel Evolver (" + threadCount + " threads)");
    this.hillClimbFunctions = evolveFunctions;
    this.fitnessFunction = fitnessFucntion;
    this.triangles = triangles;
    this.threadCount = threadCount;
    this.forkJoinPool = new ForkJoinPool(this.threadCount);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run()
  {
    while (!super.isInterrupted())
    {
      forkJoinPool.submit(this::evolveTriangles);
    }
  }
  
  /**
   * Creates a parallel Stream from the triangles Collection and
   * runs {{@link #evolveIfPossible(Triangle)} on each of the triangles
   * in the Stream. 
   */
  private void evolveTriangles()
  {
    triangles
        .parallelStream()
        .forEach(this::evolveIfPossible);
  }
  
  /**
   * Runs {#getEvolvedTriangle(Triangle)} on a triangle and if that
   * function can produce an evolved triangle, copies the information
   * from the evolved triangle into the given triangle. 
   * @param triangle The Triangle that this function will attempt to
   * evolve.
   */
  private void evolveIfPossible(Triangle triangle)
  {
    Optional<Triangle> evolvedTriangle = getEvolvedTriangle(triangle);
    if (evolvedTriangle.isPresent()) triangle.copyFrom(evolvedTriangle.get());
  }
  
  /**
   * Returns an Optional&lt;Triangle&gt; that contains an evolved
   * Triangle if one of the EvolveFunctions in {{@link #hillClimbFunctions}
   * can make the Triangle better. If none of the EvolveFunctions
   * can make the triangle better, returns an empty Optional.
   * @param triangle The triangle that this function will attempt to
   * make better.
   * @return An Optional&lt;Triangle&gt; that contains an evolved
   * Triangle if one of the EvolveFunctions in {{@link #hillClimbFunctions}
   * can make the Triangle better. If none of the EvolveFunctions
   * can make the triangle better, returns an empty Optional.
   */
  private Optional<Triangle> getEvolvedTriangle(Triangle triangle)
  {
    Function<EvolveFunction, Triangle> toEvolvedTriangle =
        getApplyEvolverFunction(triangle);
    
    Comparator<Triangle> triangleComparator =
        Triangle.getComparator(fitnessFunction);
    
    Predicate<EvolveFunction> canMakeTriangleBetter =
        e -> e.improvesTriangle(fitnessFunction, triangle);
    
    return hillClimbFunctions
        .stream()
        .filter(canMakeTriangleBetter)
        .map(toEvolvedTriangle)
        .min(triangleComparator);
  }
  
  /**
   * Returns a Function where, given a triangle, applies an EvolveFunction
   * to that triangle.
   * @param triangle The triangle to which the Function will cause the
   * EvolveFunction to be applied.
   * @return A Function that takes an EvolveFunction and applies it to
   * a single Triangle.
   */
  private Function<EvolveFunction, Triangle> getApplyEvolverFunction(
      Triangle triangle)
  {
    return (evolver) ->
    {
      synchronized (triangle)
      {
        return evolver.apply(triangle);
      }
    };
  }
  
  /**
   * Returns an Optional&lt;Triangle&gt; that contains an evolved
   * Triangle if one of the EvolveFunctions in {{@link #hillClimbFunctions}
   * can make the Triangle better. If none of the EvolveFunctions
   * can make the triangle better, returns an empty Optional.
   * @param triangle The triangle that this function will attempt to
   * make better.
   * @return An Optional&lt;Triangle&gt; that contains an evolved
   * Triangle if one of the EvolveFunctions in {{@link #hillClimbFunctions}
   * can make the Triangle better. If none of the EvolveFunctions
   * can make the triangle better, returns an empty Optional.
   */
  @SuppressWarnings("unused") // Justification: demo purposes.
  private Optional<Triangle> betterGetEvolvedTriangle(Triangle triangle)
  // Decided to keep the older one for demo purposes.
  // This one (theoretically) is slightly faster and probably more simple.
  {
    Function<EvolveFunction, Triangle> toEvolvedTriangle =
        getApplyEvolverFunction(triangle);
    
    Comparator<Triangle> triangleComparator =
        Triangle.getComparator(fitnessFunction);
    
    Predicate<Triangle> isBetterThanOriginal =
        t -> triangle.isOtherTriangleBetter(fitnessFunction, t);
    
    return hillClimbFunctions
        .stream()
        .map(toEvolvedTriangle)
        .filter(isBetterThanOriginal)
        .min(triangleComparator);
  }
}
