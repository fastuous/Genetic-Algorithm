package trianglegenome;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

/**
 * A class which extends {@link java.lang.Thread} to manage hill climbing and crossover of
 * the genomes.
 * @author collinsd
 */
public class EvolutionManager extends Thread
{
  /** When true, this thread will not perform any actions. */
  private volatile boolean paused;
  
  /** Will be true when a genome should be in a crossover. */
  private volatile boolean crossoverFlag;
  
  /** Used for keeping track of running time. */
  private volatile long startTime;
  
  /** The genomes on which hill climbing and crossovers will be performed. */
  private List<Genome> genomes;
  
  /** The {@link HillClimberSpawner} that is responsible for spawning the
   * {@link HillClimbing} threads that perform hill climbing on the
   * {@link EvolutionManager#genomes}. */
  private HillClimberSpawner hillClimberSpawner;
  
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
    this.genomes = genomes;
    this.threadCount = threadCount;
    init();
  }
  
  /**
   * Used for initializing and reinitializing this EvolutionManager.
   */
  private void init()
  {
    if (hillClimberSpawner != null) hillClimberSpawner.stopHillClimbing();
    hillClimberSpawner = new HillClimberSpawner(threadCount, genomes, target);
    startTime = System.currentTimeMillis();
    this.paused = true;
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
   * Returns the running time of this thread, according to the wall clock.
   * @return
   */
  public long getRunningTime()
  {
    return System.currentTimeMillis() - startTime;
  }
  
  /**
   * Pauses the EvolutionManager after all atomic operations (e.g. the hill climbing
   * of a tribe) are finished.
   */
  public void pause()
  {
    this.paused = true;
  }

  /**
   * Resumes evolution of the genomes.
   */
  public void unpause()
  {
    this.paused = false;
  }

  /**
   * Returns whether or not this EvolutionManager thread is paused.
   * @return Whether or not this EvolutionManager thread is paused.
   */
  public boolean isPaused()
  {
    return this.paused;
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
      if (!paused)
      {
        if (crossoverFlag)
        {
          
        }
        else
        {
          
        }
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
