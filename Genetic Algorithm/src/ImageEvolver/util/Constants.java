package ImageEvolver.util;

import java.util.Random;

/**
 * Centralized place to keep hard-coded variables. Like in AntWorld
 * 
 * @author Truman DeYoung
 */
public class Constants
{
  public static final int TRIANGLE_COUNT = 200;

  public static final String[] IMAGE_FILES =
  { "mona-lisa-cropped-512x413.png", "poppyfields-512x384.png",
      "the_great_wave_off_kanagawa-512x352.png" };
  
  public static final int MAX_RGBA = 255;
  
  public static int width;
  public static int height;
  
  public static final Random rand = new Random();
}
