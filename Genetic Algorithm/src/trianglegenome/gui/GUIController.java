package trianglegenome.gui;

import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import trianglegenome.FitnessEvaluator;
import trianglegenome.Genome;
import trianglegenome.RandomGenome;
import trianglegenome.SeedGenome;

/**
 * A simple class to test the functionality of our GUI, may evolve into view-controller
 * 
 * Right now we still have to decide how we'll implement the genome selector and the tribe selector.
 * All other required GUI elements can be tested here.
 * 
 * @author Truman
 */
public class GUIController
{
  public boolean isPaused = true;
  private DrawPanel drawPanel;
  private ImagePanel imagePanel;
  private static MainFrame mainFrame;
  public boolean imageChanged = false;
  
  private FitnessEvaluator fitnessEvaluator = null;

  public void toggleRunning()
  {
    System.out.print("Pause button clicked.");
    if (!isPaused)
    {
      isPaused = true;
      System.out.println(" STATE: PAUSED");
    }
    else
    {
      isPaused = false;
      System.out.println(" STATE: RUNNING");
    }
  }

  public void next()
  {
    Genome genome = SeedGenome.generateSeed(imagePanel.getSnapshot());
    drawPanel.setTriangles(genome.getGenes());
    drawPanel.repaint();
    
    BufferedImage drawPanelSnapshot = drawPanel.getSnapshot();
    BufferedImage imagePanelSnapshot = imagePanel.getSnapshot();
    
    fitnessEvaluator = new FitnessEvaluator(imagePanelSnapshot);
    
    long time1 = System.currentTimeMillis();
    int fitness1 = fitnessEvaluator.differenceSumCL(drawPanelSnapshot);
    time1 = System.currentTimeMillis() - time1;

    long time2 = System.currentTimeMillis();
    int fitness2 = fitnessEvaluator.differenceSumJava(drawPanelSnapshot);
    time2 = System.currentTimeMillis() - time2;

    System.out.println("OpenCL Fitness : " + fitness1);
    System.out.println("Java Fitness   : " + fitness2);
    System.out.println("OpenCL time : " + time1);
    System.out.println("Java time   : " + time2);
    
    mainFrame.setDisplayFitness(fitness1);
    
    System.out.print("Next button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public void reset()
  {
    
  }

  public void genomeTable()
  {
    System.out.print("Genome Table button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public void writeGenome()
  {
    System.out.print("Write Genome button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public void readGenome()
  {
    System.out.print("Read Genome button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public void tribeSliderUpdate(int value)
  {
    System.out.print("Tribe slider changed to " + value);
  }

  public void triangleSliderUpdate(int value)
  {
    
    drawPanel.setTriangleDrawLimit(value);
    mainFrame.setTriangleLabel(value);
    if (!isPaused) System.out.println(" **WARNING: PROGRAM IS NOT PAUSED**");
  }

  public void appendStats()
  {

  }

  public void setDrawPanel(DrawPanel drawPanel)
  {
    this.drawPanel = drawPanel;
  }
  
  public void setImagePanel(ImagePanel imagePanel)
  {
    this.imagePanel = imagePanel;
  }
  
  public static void main(String[] args)
  {
    
    
    SwingUtilities.invokeLater(new Runnable() {

      public void run()
      {
        GUIController controller = new GUIController();
        controller.reset();
        mainFrame = new MainFrame(controller);
        mainFrame.setVisible(true);
      }
    });
    
  }
}
