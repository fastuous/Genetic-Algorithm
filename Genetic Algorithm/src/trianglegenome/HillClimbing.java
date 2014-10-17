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
  Genome genomeBefore;
  Genome genomeAfter;
  ImagePanel imagePanel;
  DrawPanel drawPanel;
  BufferedImage imagePanelSnapshot;
  private FitnessEvaluator fitnessEvaluator;

  public HillClimbing(Genome genome, ImagePanel imagePanel, DrawPanel drawPanel)
  {
    genomeBefore = genome;
    this.imagePanel = imagePanel;
    this.drawPanel = drawPanel;
    imagePanelSnapshot = imagePanel.getSnapshot();
    fitnessEvaluator = new FitnessEvaluator(imagePanelSnapshot);
  }

  public Genome performEvolution()
  {
    drawPanel.setTriangles(genomeBefore.getGenes());
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
      genomeAfter = evolution.Evolve(genomeBefore);
      drawPanel.setTriangles(genomeAfter.getGenes());
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
    genomeBefore = genomeAfter;
    return genomeBefore;
  }
}
