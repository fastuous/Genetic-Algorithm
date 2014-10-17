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
  ImagePanel imagePanel;
  DrawPanel drawPanel;
  BufferedImage imagePanelSnapshot;
  private FitnessEvaluator fitnessEvaluator;
  private List<GenomeState> genomeStates;

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
  }

  public void performEvolution(GenomeState genomeState)
  {
    drawPanel.setTriangles(genomeState.previous.getGenes());
    drawPanel.repaint();
    BufferedImage drawPanelSnapshot = drawPanel.getSnapshot();
    EvolveGenome evolution = (genome)->
    {
      List<Triangle> genes = genome.getGenes();
      Triangle tri = genes.get(Constants.rand.nextInt(Constants.TRIANGLE_COUNT));
      
      int location = genes.indexOf(tri);
      do
      {
        if (!successfulEvolution)
        {
          if (Constants.rand.nextInt(100000) % 2 == 0)
          {
            tri.dna[successfulDNA] += successfulMultiplier * (Constants.rand.nextInt(2) + 1);
          }
          else
          {
            tri.dna[successfulDNA] -= successfulMultiplier * (Constants.rand.nextInt(2) + 1);
          }
        }
      }
      while (!tri.isValidTriangle(tri));
      genes.set(location, tri);
      return genome;
    };
    fitnessBefore = fitnessEvaluator.differenceSumCL(drawPanelSnapshot);
    do
    {
      evolution.Evolve(genomeState.genome);
      drawPanel.setTriangles(genomeState.genome.getGenes());
      drawPanel.repaint();
      drawPanelSnapshot = drawPanel.getSnapshot();
      fitnessAfter = fitnessEvaluator.differenceSumCL(drawPanelSnapshot);
      if(fitnessAfter <= fitnessBefore)
      {
        successfulMultiplier = 1;
        successfulDNA = Constants.rand.nextInt(10);
      }
    }
    while (fitnessAfter <= fitnessBefore);
    successfulMultiplier += .5;
    genomeState.previous.copyFrom(genomeState.genome);
  }
}
