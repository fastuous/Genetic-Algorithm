package trianglegenome.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import trianglegenome.FitnessEvaluator;

/**
 * Centralized place to keep hard-coded variables. Like in AntWorld
 * 
 * @author Truman DeYoung
 */
public class Constants
{
  public static final String[] IMAGE_FILES;

  static
  {
    File folder = new File("src/images/");
    File[] listFile = folder.listFiles();
    IMAGE_FILES = new String[listFile.length];
    for (int i = 0; i < listFile.length; i++)
    {
      if (listFile[i].isFile())
      {
        IMAGE_FILES[i] = listFile[i].getName();
      }
    }

  }
  
  public static final BufferedImage[] IMAGES;
  static
  {
    IMAGES = new BufferedImage[IMAGE_FILES.length];
    
    for (int i = 0; i < IMAGES.length; i++)
    {
      try
      {
        BufferedImage image = ImageIO.read(new File("src/images/" + IMAGE_FILES[i]));
        IMAGES[i] = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = IMAGES[i].getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public static final FitnessEvaluator[] FITNESS_EVALUATORS;
  static
  {
    FITNESS_EVALUATORS = new FitnessEvaluator[IMAGE_FILES.length];
    for (int i = 0; i < IMAGES.length; i++)
    {
      FITNESS_EVALUATORS[i] = new FitnessEvaluator(IMAGES[i]);
    }
  }
  
  public static int selectedImage = 0;
  
  public static final int MAX_CONCURRENT_OPENCL_EVALS = 8;
  
  public static boolean useOpenCL = true;
  
  public static final int TRIANGLE_COUNT = 200;

  public static final int MAX_RGBA = 255;

  public static int threadCount;
  /** The width of the target image. */
  public static int width;
  /** The height of the target image. */
  public static int height;

  public static final Random rand = new Random();
}
