package trianglegenome.testing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import trianglegenome.FitnessEvaluator;
import trianglegenome.Genome;
import trianglegenome.GenomeDrawPanelPair;
import trianglegenome.HillClimbing;
import trianglegenome.RandomGenome;
import trianglegenome.Triangle;
import trianglegenome.gui.DrawPanel;
import trianglegenome.util.Constants;

public class HillClimbingUnitTests
{
  private FitnessEvaluator fitnessEvaluator;
  private HillClimbing hillClimber;
  private DrawPanel drawPanel;
  private GenomeDrawPanelPair genomeDrawPanelPair;
  private List<GenomeDrawPanelPair> genomeDrawPanelPairSingleton;
  private Genome genome;
  
  public HillClimbingUnitTests()
  {
    initData();
    testHillClimbing();
    stallingTest();
  }
  
  private void initData()
  {
    Constants.width = Constants.IMAGES[0].getWidth();
    Constants.height = Constants.IMAGES[0].getHeight();
    genome = RandomGenome.generateGenome();
    fitnessEvaluator = new FitnessEvaluator(Constants.IMAGES[0]);
    drawPanel = new DrawPanel(Constants.IMAGES[0].getWidth(), Constants.IMAGES[0].getHeight());
    genomeDrawPanelPair = new GenomeDrawPanelPair(genome, drawPanel);
    genomeDrawPanelPairSingleton = new ArrayList<GenomeDrawPanelPair>(1);
    genomeDrawPanelPairSingleton.add(genomeDrawPanelPair);
    hillClimber = new HillClimbing(genomeDrawPanelPairSingleton, Constants.IMAGES[0]);
  }
  
  private void testHillClimbing()
  {
    drawPanel.setTriangles(genome.getGenes());
    int initialFitness = fitnessEvaluator.differenceSum(drawPanel.getSnapshot());
    int newFitness = Integer.MAX_VALUE;
    
    try
    {
      hillClimber.start();
      Thread.sleep(2000);
      hillClimber.interrupt();
    }
    catch (Exception e) {}
    
    drawPanel.setTriangles(genome.getGenes());
    newFitness = fitnessEvaluator.differenceSum(drawPanel.getSnapshot());
    
    assert initialFitness > newFitness;
    
    return;
  }
  
  @SuppressWarnings("serial") // Justification: serial ID not needed.
  private void stallingTest()
  {
    drawPanel = new DrawPanel(Constants.IMAGES[0].getWidth(), Constants.IMAGES[0].getHeight())
    {
      /**
       * Always returns a blank image so that the hillClimber will never make progress.
       */
      @Override
      public BufferedImage getSnapshot()
      {
        return new BufferedImage(Constants.width, Constants.height, BufferedImage.TYPE_INT_RGB);
      }
    };
    genomeDrawPanelPair = new GenomeDrawPanelPair(genome, drawPanel);
    genomeDrawPanelPairSingleton = new ArrayList<GenomeDrawPanelPair>(1);
    genomeDrawPanelPairSingleton.add(genomeDrawPanelPair);
    hillClimber = new HillClimbing(genomeDrawPanelPairSingleton, Constants.IMAGES[0]);
    
    boolean passed = false;
    
    try
    {
      hillClimber.start();
      Thread.sleep(200);
      hillClimber.pause();
      
      assert hillClimber.isPaused();
      Thread.sleep(200);
      Genome genomeClone = genome.clone();
      Thread.sleep(200);
      passed = genomeClone.equals(genome); // If this is not true, then HillClimbing is still running and is hung.
      hillClimber.interrupt();
    }
    catch (Exception e) {}
    
    assert passed;
    
    return;
  }
  
  public static void main(String [] args)
  {
    new HillClimbingUnitTests();
  }
}
