package trianglegenome;

import static java.lang.Math.ceil;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import trianglegenome.gui.DrawPanel;
import trianglegenome.util.Constants;

/**
 * Manages the threads that will perform hill climbing with collections of genomes.
 * 
 * @author David Collins
 */
public class HillClimberSpawner
{
  private List<Genome> genomes;
  private int threadCount;
  private Collection<HillClimbing> hillClimbingThreads;
  private BufferedImage target;

  public HillClimberSpawner(int threadCount, List<Genome> genomes, BufferedImage target)
  {
    this.threadCount = threadCount;
    this.genomes = genomes;
    this.hillClimbingThreads = new LinkedList<HillClimbing>();
    this.target = target;

    populateHillClimbingThreads(this.threadCount);
  }

  private void populateHillClimbingThreads(int threadCount)
  {
    stopHillClimbing();

    int genomesPerThread = (int) ceil((double) genomes.size() / (double) threadCount);

    for (int i = 0; i < threadCount; i++)
    {
      DrawPanel drawPanel = new DrawPanel(Constants.width, Constants.height);

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

  public boolean hillClimbersArePaused()
  {
    return hillClimbingThreads
        .stream()
        .allMatch(t -> t.isPaused());
  }
  
  public boolean anyHillClimberIsPaused()
  {
    return hillClimbingThreads
        .stream()
        .anyMatch(t -> t.isPaused());
  }
  
  public boolean anyHillClimberIsUnpaused()
  {
    return hillClimbingThreads
        .stream()
        .anyMatch(t -> !t.isPaused());
  }
  
  public void performOneEvolution()
  {
    hillClimbingThreads.forEach(t->t.performOneEvolution());
  }
  
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

  public void unpauseHillClimbers()
  {
    hillClimbingThreads.forEach(t -> t.unpause());
  }

  public void startHillClimbing()
  {
    hillClimbingThreads.forEach(t -> t.start());
  }

  public void stopHillClimbing()
  {
    hillClimbingThreads.forEach(t -> t.interrupt());
    hillClimbingThreads.clear();
  }

  public int getThreadCount()
  {
    return threadCount;
  }
  
  public List<Genome> getGenomesFromThread(int threadIndex)
  {
    int genomesPerThread = (int) ceil((double) genomes.size() / (double) threadCount);
    return genomes
        .stream()
        .skip(threadIndex * genomesPerThread)
        .limit(genomesPerThread)
        .collect(Collectors.toList());
  }

  @Override
  public void finalize()
  {
    stopHillClimbing();
  }
}
