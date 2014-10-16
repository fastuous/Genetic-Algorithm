package trianglegenome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import trianglegenome.gui.DrawPanel;
import trianglegenome.gui.ImagePanel;
import static java.lang.Math.ceil;

/**
 * Manages the threads that will perform hill climbing with collections of genomes.
 * @author David Collins
 */
public class HillClimberSpawner
{
  private List<Genome> genomes;
  private int threadCount;
  private Collection<Thread> hillClimbingThreads;
  private ImagePanel referenceImagePanel;
  private List<DrawPanel> drawPanels; 
  
  public HillClimberSpawner(int threadCount, List<Genome> genomes, ImagePanel reference)
  {
    this.threadCount = threadCount;
    this.genomes = genomes;
    this.hillClimbingThreads = new LinkedList<Thread>();
    this.drawPanels = new ArrayList<DrawPanel>(genomes.size());
    
    throw new RuntimeException("Not ready for production");
  }
  
  private void populateHillClimbingThreads(int threadCount)
  {
    stopHillClimbing();
    
    int genomesPerThread = (int)ceil((double)genomes.size() / (double)threadCount);
    
    for (int i = 0; i < threadCount; i++)
    {
      List<Genome> threadGenomes = genomes
          .stream()
          .skip(i * genomesPerThread)
          .limit(genomesPerThread)
          .collect(Collectors.toList());
      List<DrawPanel> threadDrawPanels = drawPanels
          .stream()
          .skip(i * genomesPerThread)
          .limit(genomesPerThread)
          .collect(Collectors.toList());
      // HillClimbing hillClimbingThread =
      //     new HillClimbing(threadGenomes, referenceImagePanel, threadDrawPanels);
      // hillClimbingThreads.add(hillClimbingThread);
      

      throw new RuntimeException("Not yet implemented");
    }
  }
  
  public void pauseHillClimbers()
  {
    // hillClimbingThreads.forEach(t -> t.pause());
    throw new RuntimeException("Not yet implemented");
  }
  
  public void unpauseHillClimbers()
  {
    // hillClimbingThreads.forEach(t -> t.unpause());
    throw new RuntimeException("Not yet implemented");
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
  
  @Override
  public void finalize()
  {
    stopHillClimbing();
  }
  
  public List<DrawPanel> getDrawPanels()
  {
    return drawPanels;
  }
}
