package trianglegenome;

import java.awt.image.BufferedImage;
import java.util.*;
import trianglegenome.gui.*;
import trianglegenome.util.Constants;

public class HillClimbing extends Thread
{
  int fitnessBefore = 0;
  int fitnessAfter = 0;
  boolean successfulEvolution = false;
  int successfulDNA = Constants.rand.nextInt(10);
  int successfulStepsize;
  int successfulMultiplier = 1;
  int evenOrOdd = Constants.rand.nextInt(100000) % 2;
  int stepSize = Constants.rand.nextInt(2)+1;
  int triangle;
  ImagePanel imagePanel;
  DrawPanel drawPanel;
  BufferedImage imagePanelSnapshot;
  private FitnessEvaluator fitnessEvaluator;
  private List<GenomeState> genomeStates;
  private boolean paused = false;

  public HillClimbing(List<GenomeState> genomeStates, ImagePanel imagePanel)
  {
    this.imagePanel = imagePanel;
    imagePanelSnapshot = imagePanel.getSnapshot();
    fitnessEvaluator = new FitnessEvaluator(imagePanelSnapshot);
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
        for (GenomeState state : genomeStates)
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
  
  public void performEvolution(GenomeState genomeState)
  {
    drawPanel.setTriangles(genomeState.genome.getGenes());
    drawPanel.repaint();
    BufferedImage drawPanelSnapshot = drawPanel.getSnapshot();
    fitnessBefore = fitnessEvaluator.differenceSumCL(drawPanelSnapshot);
    do
    {
      evolve(genomeState.genome);
      drawPanel.setTriangles(genomeState.genome.getGenes());
      drawPanel.repaint();
      drawPanelSnapshot = drawPanel.getSnapshot();
      fitnessAfter = fitnessEvaluator.differenceSumCL(drawPanelSnapshot);
      if (fitnessAfter <= fitnessBefore)
      {
        devolve(genomeState.genome);
        successfulMultiplier = 1;
        successfulDNA = Constants.rand.nextInt(10);
      }
    }
    while (fitnessAfter <= fitnessBefore);
    successfulMultiplier += .5;
  }
}
