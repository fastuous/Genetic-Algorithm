package trianglegenome;

import java.awt.image.BufferedImage;
import java.util.List;
import trianglegenome.gui.DrawPanel;
import trianglegenome.util.Constants;

public class HillClimbing extends Thread
{
  private int fitnessBefore = 0;
  private int fitnessAfter = 0;
  private boolean successfulEvolution = false;
  private int successfulDNA = Constants.rand.nextInt(10);
  private int successfulMultiplier = 1;
  private int evenOrOdd = Constants.rand.nextInt(100000) % 2;
  private int stepSize = Constants.rand.nextInt(2)+1;
  private int triangle;
  private DrawPanel drawPanel;
  private BufferedImage targetImage;

  private FitnessEvaluator fitnessEvaluator;
  private List<GenomeDrawPanelPair> genomeStates;
  private boolean paused = false;

  public HillClimbing(List<GenomeDrawPanelPair> genomeStates, BufferedImage target)
  {
    targetImage = target;
    fitnessEvaluator = new FitnessEvaluator(targetImage);
    this.genomeStates = genomeStates;
  }

  @Override
  public void run()
  {
    // performEvolution on all genomeStates
    while (!super.isInterrupted())
    {
      if (!this.paused)
      {
        for (GenomeDrawPanelPair state : genomeStates)
        {
          performEvolution(state);
        }
      }
      else
      {
        try
        {
          Thread.sleep(100);
        }
        catch (Exception e)
        {
          this.pause();
          e.printStackTrace();
        }
      }
    }
  }

  public void pause()
  {
    this.paused = true;
  }

  public void unpause()
  {
    this.paused = false;
  }

  public boolean isPaused()
  {
    if (this.paused) return true;
    return false;
  }
  
  public void evolve(Genome genome)
  {
    List<Triangle> genes = genome.getGenes();
    triangle = Constants.rand.nextInt(Constants.TRIANGLE_COUNT);
    Triangle tri = genes.get(triangle);
    int location = genes.indexOf(tri);
    do
    {
      if (!successfulEvolution)
      {
        evenOrOdd = Constants.rand.nextInt(100000) % 2;
        stepSize = Constants.rand.nextInt(2)+1;
        if (evenOrOdd == 0)
        {
          tri.dna[successfulDNA] += successfulMultiplier * stepSize;
        }
        else
        {
          tri.dna[successfulDNA] -= successfulMultiplier * stepSize;
        }
      }
    }
    while (!tri.isValidTriangle(tri));
    genes.set(location, tri);
  }
  
  public void devolve(Genome genome)
  {
    List<Triangle> genes = genome.getGenes();
    Triangle tri = genes.get(triangle);
    int location = genes.indexOf(tri);
        if (evenOrOdd == 1)
        {
          tri.dna[successfulDNA] += successfulMultiplier * stepSize;
        }
        else
        {
          tri.dna[successfulDNA] -= successfulMultiplier * stepSize;
        }
    genes.set(location, tri);
  }
  
  public void performEvolution(GenomeDrawPanelPair genomeState)
  {
    genomeState.drawPanel.setTriangles(genomeState.genome.getGenes());
    genomeState.drawPanel.repaint();
    BufferedImage drawPanelSnapshot = genomeState.drawPanel.getSnapshot();
    fitnessBefore = fitnessEvaluator.differenceSum(drawPanelSnapshot);
    do
    {
      evolve(genomeState.genome);
      genomeState.drawPanel.setTriangles(genomeState.genome.getGenes());
      genomeState.drawPanel.repaint();
      drawPanelSnapshot = genomeState.drawPanel.getSnapshot();
      fitnessAfter = fitnessEvaluator.differenceSum(drawPanelSnapshot);
      if (fitnessAfter >= fitnessBefore)
      {
        devolve(genomeState.genome);
        successfulMultiplier = 1;
        successfulDNA = Constants.rand.nextInt(10);
      }
    }
    while (fitnessAfter >= fitnessBefore);
    successfulMultiplier += .5;
  }
}
