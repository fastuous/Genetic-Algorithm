package trianglegenome;

import java.awt.image.BufferedImage;
import java.util.*;
import trianglegenome.gui.*;
import trianglegenome.util.Constants;

public class HillClimbing
{
  int fitnessBefore = 0;
  int fitnessAfter = 0;
  boolean successfulEvolution = false;
  int successfulDNA;
  int successfulStepsize;
  int successfulMultiplier = 1;
  Random stepSize = new Random();
  Random randomTriangle = new Random();
  Random dnaSelection = new Random();
  Genome genomeBefore;
  Genome genomeAfter;
  ImagePanel imagePanel;
  DrawPanel drawPanel;
  
  private FitnessEvaluator fitnessEvaluator = new FitnessEvaluator();
  
  public HillClimbing(Genome genome, ImagePanel imagePanel, DrawPanel drawPanel)
  {
    genomeBefore = genome;
    this.imagePanel = imagePanel;
    this.drawPanel = drawPanel;
  }
  
  public Genome performEvolution()
  {
    drawPanel.setTriangles(genomeBefore.getGenes());
    drawPanel.repaint();
    
    BufferedImage drawPanelSnapshot = drawPanel.getSnapshot();
    BufferedImage imagePanelSnapshot = imagePanel.getSnapshot();
    
    EvolveGenome evolution = (genome, triangle, dna, step) ->
    {
      Triangle tri = genome.getGenes().remove(randomTriangle.nextInt(Constants.TRIANGLE_COUNT));
      do
      {
        tri.dna[dnaSelection.nextInt(10)] += stepSize.nextInt(2);
      } while(!tri.isValidTriangle(tri));
      return genome;
    };
    fitnessBefore = fitnessEvaluator.differenceSumCL(drawPanelSnapshot, imagePanelSnapshot);
    
    return genomeBefore;
  }
}
