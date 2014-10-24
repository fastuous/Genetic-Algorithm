package trianglegenome;

import static java.lang.Math.max;
import static java.lang.Math.abs;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import trianglegenome.util.Constants;

/**
 * A class which extends {@link java.lang.Thread} to manage hill climbing and crossover of
 * the genomes.
 * @author collinsd
 */
public class EvolutionModel extends Thread
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
   * {@link EvolutionModel#genomes}. */
  private HillClimberSpawner hillClimberSpawner;
  
  /** The class that will perform crossovers on the genomes. */
  private GenomeCrossover genomeCrossover;
  
  /** The reference image that the triangles in each genome will eventually resemble through
   * hill climbing and crossover. */
  private BufferedImage target;
  
  /** The number of threads that will perform the hill climbing. */
  private int threadCount;
  
  /** The average fitness of all genomes in all tribes. */
  int averageFitness;
  
  /**
   * Given an initial thread count, a list of genomes and a reference image, create an
   * Evolution manager that will try to evolve the genomes to look like the reference image
   * using the given number of threads.
   * @param threadCount The number of threads to use for hill climbing.
   * @param genomes The genomes that this EvolutionManager will manage.
   * @param target The image that the genomes should resemble.
   */
  public EvolutionModel(int threadCount, List<Genome> genomes, BufferedImage target)
  {
    super("EvloutionManager-Thread");
    Objects.requireNonNull(genomes, "genomes cannot be null");
    Objects.requireNonNull(target, "target cannot be null");
    this.genomes = genomes;
    this.threadCount = threadCount;
    this.target = target;
    startTime = System.currentTimeMillis();
    init();
    this.paused = false;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run()
  {
    int fitnessDelta = 0;
    int maxFitnessDelta = 0;
    hillClimberSpawner.startHillClimbing();
    if (!paused) hillClimberSpawner.startHillClimbing();
    while (!super.isInterrupted())
    {
      if (!paused)
      {
        if (crossoverFlag)
        {
          hillClimberSpawner.pauseHillClimbers();
          int crossoverCount = max(1, Constants.rand.nextInt(genomes.size() / 8));
          genomeCrossover.crossover(crossoverCount);
          hillClimberSpawner.unpauseHillClimbers();
          crossoverFlag = false;
          averageFitness = 0;
        }
        
        try { Thread.sleep(2000); }
        catch (Exception e)
        {
          hillClimberSpawner.stopHillClimbing();
          synchronized (this) { this.notify(); }
        }
        
        int newAverageFitness = (int)genomes
            .stream()
            .mapToLong(g -> g.getFitness())
            .average()
            .getAsDouble();
        fitnessDelta = abs(newAverageFitness - averageFitness);
        if (fitnessDelta > maxFitnessDelta) maxFitnessDelta = fitnessDelta;
        else if (fitnessDelta < maxFitnessDelta / 16)
        {
          if (averageFitness != 0) crossoverFlag = true;
        }
        averageFitness = newAverageFitness;
      }
      else
      {
        try { Thread.sleep(200); }
        catch (Exception e)
        {
          hillClimberSpawner.stopHillClimbing();
          synchronized (this) { this.notify(); }
        }
      }
    }
    hillClimberSpawner.stopHillClimbing();
    synchronized (this) { this.notify(); }
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
  
  /**
   * Performs one evolution on each of the hill climbing threads, regardless of any thread
   * state.
   */
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
   * Returns the running time of this thread, according to the wall clock.
   * @return The running time of this thread, according to the wall clock.
   */
  public long getElapsedTime() { return System.currentTimeMillis() - startTime; }
  
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
  
  /**
   * Returns the number of hill climbs performed by all HillClimbing threads.
   * @return The number of hill climbs performed by all HillClimbing threads.
   */
  public long getHillClimbGenerations()
  {
    return hillClimberSpawner.getHillClimbGenerations();
  }
  
  /**
   * Returns the number of crossovers performed.
   * @return The number of crossovers performed.
   */
  public long getCrossoverGenerations()
  {
    return genomeCrossover.getGenerationCount();
  }
  
  /**
   * Returns the number of generations created from both hill climbing and crossover.
   * @return The number of generations created from both hill climbing and crossover.
   */
  public long getTotalGenerations()
  {
    return getCrossoverGenerations() + getHillClimbGenerations();
  }
  
  /**
   * Returns the most fit genome's fitness value from the global population.
   * @return The most fit genome's fitness value from the global population.
   */
  public long getBestFitnessValue()
  {
    return genomes
        .stream()
        .mapToLong(g -> g.getFitness())
        .min()
        .getAsLong();
  }
  
  /**
   * Returns the most fit genome's fitness value from the given tribe number.
   * @param The tribe from which to get the best fitness value.
   * @return The most fit genome's fitness value from the given tribe number.
   */
  public long getBestFitnessValue(int tribeIndex)
  {
    return getGenomesFromTribe(tribeIndex)
        .stream()
        .mapToLong(g -> g.getFitness())
        .min()
        .getAsLong();
  }
  
  /**
   * Returns the least fit genome's fitness value from the global population.
   * @return The least fit genome's fitness value from the global population.
   */
  public long getWorstFitnessValue()
  {
    return genomes
        .stream()
        .mapToLong(g -> g.getFitness())
        .max()
        .getAsLong();
  }
  
  /**
   * Returns the least fit genome's fitness value from the given tribe number.
   * @param The tribe from which to get the least fitness value.
   * @return The least fit genome's fitness value from the given tribe number.
   */
  public long getWorstFitnessValue(int tribeIndex)
  {
    return getGenomesFromTribe(tribeIndex)
        .stream()
        .mapToLong(g -> g.getFitness())
        .max()
        .getAsLong();
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
    synchronized (this) { this.notify(); }
  }
}
