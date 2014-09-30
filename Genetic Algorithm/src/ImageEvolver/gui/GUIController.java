package ImageEvolver.gui;

import javax.swing.SwingUtilities;

import ImageEvolver.SimpleFitness;
import ImageEvolver.Genome;
import ImageEvolver.RandomGenome;

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

  public void pause()
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
    Genome genome = RandomGenome.generateGenome();
    drawPanel.setTriangles(genome.getGenes());
    drawPanel.repaint();
    
    SimpleFitness simpleFitness = SimpleFitness.getSimpleFitnessSingleton();
    int fitness = simpleFitness.getSimpleFitness(drawPanel.getSnapshot(), imagePanel.getSnapshot());
    mainFrame.setDisplayFitness(fitness);
    
    System.out.print("Next button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public void reset()
  {
    System.out.print("Reset button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
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
    MainFrame.jLabel5.setText("Triangle: " + value + "/200 ");
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
        mainFrame = new MainFrame(controller);
        mainFrame.setVisible(true);
      }
    });
    
  }
}
