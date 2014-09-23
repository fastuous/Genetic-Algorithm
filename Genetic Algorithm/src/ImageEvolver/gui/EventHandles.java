package ImageEvolver.gui;

/**
 * A simple class to test the functionality of our GUI, may evolve into view-controller
 * 
 * Right now we still have to decide how we'll implement the genome selector and the tribe selector.
 * All other required GUI elements can be tested here.
 * 
 * @author Truman
 */
public class EventHandles
{
  private static boolean isPaused = false;

  public static void pause()
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

  public static void next()
  {

    System.out.print("Next button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public static void reset()
  {
    System.out.print("Reset button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public static void genomeTable()
  {
    System.out.print("Genome Table button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public static void writeGenome()
  {
    System.out.print("Write Genome button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public static void readGenome()
  {
    System.out.print("Read Genome button clicked.");
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public static void tribeSliderUpdate(int value)
  {
    System.out.print("Tribe slider changed to " + value);
  }

  public static void triangleSliderUpdate(int value)
  {

    // we'll actually have to implement this one
    System.out.print("Triangle slider changed to " + value);
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }

  public static void pictureSelect(String fileName)
  {
    // we'll actually have to implement this one
    System.out.print("Image selected: " + fileName);
    if (!isPaused) System.out.print(" **WARNING: PROGRAM IS NOT PAUSED**");
    System.out.println();
  }
}
