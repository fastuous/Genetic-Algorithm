package trianglegenome;

import static java.lang.Math.ceil;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import trianglegenome.gui.DrawPanel;
import trianglegenome.gui.DrawPanelBufferedImage;
import trianglegenome.gui.DrawPanelVolatileImage;
import trianglegenome.util.Constants;

/**
 * Manages the threads that will perform hill climbing with collections of genomes.
 * <br /><br />
 * Example code:<br/>
 * <code><pre>
 *  // Assume that genomes is a global population of genomes
 *  // Assume that target is the reference image.
 *  
 *  HillClimberSpawner hcsp = new HillClimberSpawner(4, genomes, target);
 *  hcsp.startHillClimbing();
 *  Thread.sleep(500); // Assume exception is thrown by method
 *  hcsp.pauseHillClimbers();
 *  Thread.sleep(500);
 *  hcsp.unpauseHillClimbers();
 *  Thread.sleep(500);
 *  synchronized (hcsp)
 *  {
 *    hcsp.stopHillClimbing();
 *    hcsp.wait();
 *  }
 *  
 * </pre></code>
 * 
 * @author David Collins
 */
public class HillClimberSpawner
{
  /** A global population of genomes that will be split amongst {@link HillClimbing} threads */
  private List<Genome> genomes;
  
  /** The number of {@link HillClimbing} threads to split the
   * {@link HillClimberSpawner#genomes} */
  private int threadCount;
  
  /** The threads that will hill climb with the {@link HillClimberSpawner#genomes} */
  private Collection<HillClimbing> hillClimbingThreads;
  
  /** The image that the genomes should eventually resemble */
  private BufferedImage target;

  /**
   * Given a thread count, a global genome list and a target image,
   * create a HillClimberSpawner and create HillClimbing threads but don't start them.
   * @param threadCount The number of {@link HillClimbing} threads to create.
   * @param genomes A global population of genomes.
   * @param target The image that the genomes should eventually resemble.
   */
  public HillClimberSpawner(int threadCount, List<Genome> genomes, BufferedImage target)
  {
    this.threadCount = threadCount;
    this.genomes = genomes;
    this.hillClimbingThreads = new LinkedList<HillClimbing>();
    this.target = target;

    populateHillClimbingThreads(this.threadCount);
  }

  /**
   * Creates {@link HillClimbing} threads.
   * @param threadCount The number of {@link HillClimbing} threads to create.
   */
  private void populateHillClimbingThreads(int threadCount)
  {
    stopHillClimbing();

    int genomesPerThread = (int) ceil((double) genomes.size() / (double) threadCount);

    for (int i = 0; i < threadCount; i++)
    {
      DrawPanel drawPanel = (Constants.useVolatileImage)
          ? new DrawPanelVolatileImage(Constants.width, Constants.height)
          : new DrawPanelBufferedImage(Constants.width, Constants.height);

      List<GenomeDrawPanelPair> threadGenomes = genomes
          .stream()
          .skip(i * genomesPerThread)
          .limit(genomesPerThread)
          .map(g -> new GenomeDrawPanelPair(g, drawPanel))
          .collect(Collectors.toList());

      HillClimbing hillClimbingThread = new HillClimbing(threadGenomes, target);
      hillClimbingThreads.add(hillClimbingThread);
    }
  }

  /**
   * Returns whether or not all of the hill climbers are paused.
   * @return Whether or not all of the hill climbers are paused.
   */
  public boolean hillClimbersArePaused()
  {
    return hillClimbingThreads
        .stream()
        .allMatch(t -> t.isPaused());
  }

  /**
   * Returns whether or not all of the hill climbers are running.
   * @return Whether or not all of the hill climbers are running.
   */
  public boolean hillClimbersAreRunning()
  {
    return hillClimbingThreads
        .stream()
        .allMatch(t -> t.isAlive());
  }
  
  /**
   * Returns whether or not any of the hill climbers is not running.
   * @return Whether or not any of the hill climbers is not running.
   */
  public boolean anyHillClimberIsNotRunning()
  {
    return hillClimbingThreads
        .stream()
        .allMatch(t -> t.isAlive());
  }
  
  /**
   * Returns whether or not any of the hill climbers is paused.
   * @return Whether or not any of the hill climbers is paused.
   */
  public boolean anyHillClimberIsPaused()
  {
    return hillClimbingThreads
        .stream()
        .anyMatch(t -> t.isPaused());
  }
  
  /**
   * Returns whether or not any of the hill climbers is not paused.
   * @return Whether or not any of the hill climbers is not paused.
   */
  public boolean anyHillClimberIsUnpaused()
  {
    return hillClimbingThreads
        .stream()
        .anyMatch(t -> !t.isPaused());
  }
  
  /**
   * Causes all hill climbing threads to perform one evolution, regardless of state.
   */
  public void performOneEvolution()
  {
    hillClimbingThreads.forEach(t->t.performOneEvolution());
  }
  
  /**
   * Synchronously pauses all hill climbers.
   */
  public void pauseHillClimbers()
  {
    for (HillClimbing hc : hillClimbingThreads)
    {
      hc.pause();
      if (!hc.isPaused())
      {
        try
        {
          synchronized (hc) { hc.wait(); }
        }
        catch (Exception e) {}
      }
    }
  }

  /**
   * Unpauses the hill climbers.
   */
  public void unpauseHillClimbers()
  {
    hillClimbingThreads.forEach(t -> t.unpause());
  }

  /**
   * Starts the hill climbers.
   */
  public void startHillClimbing()
  {
    hillClimbingThreads.forEach(t -> t.start());
  }

  /**
   * Synchronously interrupts all hill climbers.
   */
  public void stopHillClimbing()
  {
    for (HillClimbing hc : hillClimbingThreads)
    {
      try
      {
        synchronized (hc)
        {
          hc.pause();
          hc.interrupt();
          hc.wait();
        }
      }
      catch (Exception e) {}
    }
    hillClimbingThreads.clear();
    synchronized (this) { this.notify(); }
  }

  /**
   * Returns the number of {@link HillClimbing} threads managed by this HillClimberSpawner.
   * @return The number of {@link HillClimbing} threads managed by this HillClimberSpawner.
   */
  public int getThreadCount()
  {
    return threadCount;
  }
  
  /**
   * Returns a list of genomes managed by a {@link HillClimbing} thread.
   * @param threadIndex The index of the thread from which to get the managed genomes.
   * @return A list of genomes managed by a {@link HillClimbing} thread.
   */
  public List<Genome> getGenomesFromThread(int threadIndex)
  {
    int genomesPerThread = (int) ceil((double) genomes.size() / (double) threadCount);
    return genomes
        .stream()
        .skip(threadIndex * genomesPerThread)
        .limit(genomesPerThread)
        .collect(Collectors.toList());
  }
  
  /**
   * Returns the number of hill climbs performed by all HillClimbing threads.
   * @return The number of hill climbs performed by all HillClimbing threads.
   */
  public long getHillClimbGenerations()
  {
    return hillClimbingThreads
        .stream()
        .mapToLong(hc -> hc.getGenerationCount())
        .sum();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#finalize()
   */
  @Override
  public void finalize()
  {
    stopHillClimbing();
  }
}
