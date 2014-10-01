package trianglegenome.util;

import java.io.File;
import java.util.Random;

/**
 * Centralized place to keep hard-coded variables. Like in AntWorld
 * 
 * @author Truman DeYoung
 */
public class Constants
{
  public static String[] imageFiles;

  static
  {
    File folder = new File("src/images/");
    File[] listFile = folder.listFiles();
    imageFiles = new String[listFile.length];
    for (int i = 0; i < listFile.length; i++)
    {
      if (listFile[i].isFile())
      {
        imageFiles[i] = listFile[i].getName();
      }
    }

  }
  public static final int TRIANGLE_COUNT = 200;

  public static final int MAX_RGBA = 255;

  /** The width of the target image. */
  public static int width;
  /** The height of the target image. */
  public static int height;

  public static final Random rand = new Random();
}
