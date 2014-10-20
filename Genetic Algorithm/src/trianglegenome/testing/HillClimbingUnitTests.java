package trianglegenome.testing;

import java.util.ArrayList;
import java.util.List;

import trianglegenome.FitnessEvaluator;
import trianglegenome.Genome;
import trianglegenome.GenomeDrawPanelPair;
import trianglegenome.HillClimbing;
import trianglegenome.RandomGenome;
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
  
  public static void main(String [] args)
  {
    new HillClimbingUnitTests();
  }
}
