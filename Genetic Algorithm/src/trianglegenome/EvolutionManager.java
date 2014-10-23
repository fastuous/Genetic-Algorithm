package trianglegenome;

import static java.lang.Math.max;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import trianglegenome.util.Constants;

/**
 * A class which extends {@link java.lang.Thread} to manage hill climbing and crossover of
 * the genomes.
 * @author collinsd
 */
public class EvolutionManager extends Thread
{
  /** When true, this thread will not perform any actions. */
  private volatile boolean paused;
  
  /** Used for keeping track of running time. */
  private volatile long startTime;
  
  /** Will be true when a genome should be in a crossover. */
  private boolean crossoverFlag;
  
  /** The genomes on which hill climbing and crossovers will be performed. */
  private List<Genome> genomes;
  
  /** The {@link HillClimberSpawner} that is responsible for spawning the
   * {@link HillClimbing} threads that perform hill climbing on the
   * {@link EvolutionManager#genomes}. */
  private HillClimberSpawner hillClimberSpawner;
  
  /** The class that will perform crossovers on the genomes. */
  private GenomeCrossover genomeCrossover;
  
  /** The reference image that the triangles in each genome will eventually resemble through
   * hill climbing and crossover. */
  private BufferedImage target;
  
  /** The number of threads that will perform the hill climbing. */
  private int threadCount;
  
  /**
   * Given an initial thread count, a list of genomes and a reference image, create an
   * Evolution manager that will try to evolve the genomes to look like the reference image
   * using the given number of threads.
   * @param threadCount The number of threads to use for hill climbing.
   * @param genomes The genomes that this EvolutionManager will manage.
   * @param target The image that the genomes should resemble.
   */
  public EvolutionManager(int threadCount, List<Genome> genomes, BufferedImage target)
  {
    Objects.requireNonNull(genomes, "genomes cannot be null");
    Objects.requireNonNull(target, "target cannot be null");
    this.genomes = genomes;
    this.threadCount = threadCount;
    this.target = target;
    startTime = System.currentTimeMillis();
    init();
    this.paused = false;
  }
  
  /**
   * Used for initializing and reinitializing this EvolutionManager.
   */
  private void init()
  {
    this.paused = true;
    if (hillClimberSpawner != null) hillClimberSpawner.stopHillClimbing();
    hillClimberSpawner = new HillClimberSpawner(threadCount, genomes, target);
    genomeCrossover = new GenomeCrossover(genomes);
  }
  
  /**
   * Interrupts the current hill climbing threads and creates new ones. This function
   * will not run if the new thread count is the same as the old thread count.
   * @param threadCount The number of hill climbing threads to create.
   */
  public void setThreadCount(int threadCount)
  {
    if (threadCount != this.threadCount)
    {
      this.threadCount = threadCount;
      init();
    }
  }
  
  public void performOneEvolution()
  {
    hillClimberSpawner.performOneEvolution();
  }
  
  /**
   * Returns the number of hill climbing threads.
   * @return The number of hill climbing threads.
   */
  public int getThreadCount()
  {
    return threadCount;
  }
  
  /**
   * Changes the genomes that this EvolutionManager will perform hill climbing and crossovers
   * on. This function also interrupts the current hill climbing threads and creates new ones.
   * @param genomes The new genomes that the EvolutionManager will manage.
   */
  public void setGenomes(List<Genome> genomes)
  {
    Objects.requireNonNull(genomes, "genomes cannot be null");
    this.genomes = genomes;
    init();
  }
  
  /**
   * Changes the reference image that this EvolutionManager will use when performing hill
   * climbing and crossovers. This function also interrupts the current hill climbing
   * threads and creates new ones.
   * @param target The new BufferedImage that the EvolutionManager will use.
   */
  public void setTargetImage(BufferedImage target)
  {
    Objects.requireNonNull(target, "target cannot be null");
    this.target = target;
    init();
    startTime = System.currentTimeMillis();
  }
  
  /**
   * Returns the running time of this thread, according to the wall clock.
   * @return The running time of this thread, according to the wall clock.
   */
  public long getRunningTime() { return System.currentTimeMillis() - startTime; }
  
  /**
   * Pauses the EvolutionManager after all atomic operations (e.g. the hill climbing
   * of a tribe) are finished.
   */
  public void pause()
  {
    this.paused = true;
    hillClimberSpawner.pauseHillClimbers();
  }

  /**
   * Resumes evolution of the genomes.
   */
  public void unpause()
  {
    this.paused = false;
    hillClimberSpawner.unpauseHillClimbers();
  }

  /**
   * Returns whether or not this EvolutionManager thread is paused.
   * @return Whether or not this EvolutionManager thread is paused.
   */
  public boolean isPaused()
  {
    return this.paused;
  }
  
  /**
   * Returns the genomes managed by a given thread, specified by index.
   * @param threadIndex The thread from which the genomes will be taken.
   * @return The genomes managed by a given thread, specified by index.
   */
  public List<Genome> getGenomesFromTribe(int threadIndex)
  {
    if (threadIndex >= threadCount)
    {
      throw new IllegalArgumentException("threadIndex exceeds hill climbing thread count.");
    }
    return hillClimberSpawner.getGenomesFromThread(threadIndex);
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run()
  {
    int iterations = 0;
    while (!super.isInterrupted())
    {
      if (!paused)
      {
        if (!hillClimberSpawner.hillClimbersAreRunning())
        {
          hillClimberSpawner.startHillClimbing();
          hillClimberSpawner.unpauseHillClimbers();
        }
        if (hillClimberSpawner.anyHillClimberIsPaused())
        {
          throw new IllegalStateException(
              "No HillClimbing thread can be paused while EvolutionManager is running");
        }
        if (crossoverFlag)
        {
          hillClimberSpawner.pauseHillClimbers();
          int crossoverCount = max(1, Constants.rand.nextInt(genomes.size() / 8));
          genomeCrossover.crossover(crossoverCount);
          hillClimberSpawner.unpauseHillClimbers();
        }
        
        // TODO put some real crossover conditions.
        if (iterations > 2097152)
        {
          crossoverFlag = true;
          iterations = 0;
        }
        iterations++;
      }
      else try { Thread.sleep(100); } catch (Exception e) {}
    }
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#finalize()
   */
  @Override
  public void finalize()
  {
    hillClimberSpawner.stopHillClimbing();
    this.interrupt();
  }
}
