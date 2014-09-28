package ImageEvolver.gui;

import javax.swing.SwingUtilities;

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

    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public void appendStats()
  {

  }

  public void setDrawPanel(DrawPanel drawPanel)
  {
    this.drawPanel = drawPanel;
  }
  
  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(new Runnable() {

      public void run()
      {
        GUIController controller = new GUIController();
        new MainFrame(controller).setVisible(true);;
      }
    });
    
  }
}
