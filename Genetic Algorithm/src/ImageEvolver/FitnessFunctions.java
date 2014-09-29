package ImageEvolver;

import java.awt.image.BufferedImage;

public class FitnessFunctions
{
  public static int getSimpleFitness(BufferedImage source, BufferedImage target)
  {
    int sourceWidth = source.getWidth();
    int sourceHeight = source.getHeight();
    int targetWidth = target.getWidth();
    int targetHeight = target.getHeight();

    int fitness = 0;

    if (sourceWidth != targetWidth || sourceHeight != targetHeight)
    {
      System.err.println("Fitness error: dimension mismatch");
      return 0;
    }

    for (int i = 0; i < sourceWidth; i++)
    {
      for (int j = 0; j < sourceHeight; j++)
      {
        int sourceColor = source.getRGB(i, j);
        int targetColor = target.getRGB(i, j);
        
        int dRed = Math.abs((sourceColor >> 16) & 0xFF - (targetColor >> 16) & 0xFF);
        int dGreen = Math.abs((sourceColor >> 8) & 0xFF - (targetColor >> 8) & 0xFF);
        int dBlue = Math.abs(sourceColor & 0xFF - targetColor & 0xFF);
        int dAlpha = Math.abs((sourceColor >> 24) & 0xFF - (targetColor >> 24) & 0xFF);
        
        fitness += dRed + dGreen + dBlue + dAlpha;
      }
    }
    
    return fitness;
  }
}
