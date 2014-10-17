package trianglegenome.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import trianglegenome.Genome;
import trianglegenome.Triangle;
import trianglegenome.util.Constants;

/**
 * @author Truman DeYoung
 */
public class SeedGenome
{
  public static Genome generateSeed(BufferedImage image)
  {
    Image scaledImage = image.getScaledInstance(3, 3, Image.SCALE_AREA_AVERAGING);
    int width = scaledImage.getWidth(null);
    int height = scaledImage.getHeight(null);

    // width and height are of the toolkit image
    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics g = newImage.getGraphics();
    g.drawImage(scaledImage, 0, 0, null);
    g.dispose();
    
    int topLeftColor = newImage.getRGB(0, 0);
    int topColor = newImage.getRGB(1, 0);
    int topRightColor = newImage.getRGB(2, 0);
    int leftColor = newImage.getRGB(0, 1);
    int middleColor = newImage.getRGB(1, 1);
    int rightColor = newImage.getRGB(2, 1);
    int bottomLeftColor = newImage.getRGB(0, 2);
    int bottomColor = newImage.getRGB(1, 2);
    int bottomRightColor = newImage.getRGB(2, 2);
   
    
    Color topTriangleColor = averageColors(topLeftColor, topColor, topRightColor, middleColor);
    Color leftTriangleColor = averageColors(topLeftColor, leftColor, middleColor, bottomLeftColor);
    Color rightTriangleColor = averageColors(topRightColor, middleColor, rightColor, bottomRightColor);
    Color bottomTriangleColor = averageColors(middleColor, bottomLeftColor, bottomColor, bottomRightColor);
    
    int leftX = 0;
    int rightX = image.getWidth() - 1;
    int midX = image.getWidth() / 2;
    
    int topY = 0;
    int bottomY = image.getHeight() - 1;
    int midY = image.getHeight() / 2;
    
    
    Triangle topTriangle = new Triangle(leftX, rightX, midX, topY, topY, midY, topTriangleColor.getRed(), topTriangleColor.getGreen(), topTriangleColor.getBlue(), 255);
    Triangle leftTriangle = new Triangle(leftX, midX, leftX, topY, midY, bottomY, leftTriangleColor.getRed(), leftTriangleColor.getGreen(), leftTriangleColor.getBlue(), 255);
    Triangle rightTriangle = new Triangle(rightX, midX, rightX, topY, midY, bottomY, rightTriangleColor.getRed(), rightTriangleColor.getGreen(), rightTriangleColor.getBlue(), 255);
    Triangle bottomTriangle = new Triangle(leftX, midX, rightX, bottomY, midY, bottomY, bottomTriangleColor.getRed(), bottomTriangleColor.getGreen(), bottomTriangleColor.getBlue(), 255);
    
    Genome seed = new Genome();
    seed.addGene(topTriangle);
    seed.addGene(leftTriangle);
    seed.addGene(rightTriangle);
    seed.addGene(bottomTriangle);
    
    for (int i = 0; i < Constants.TRIANGLE_COUNT - 4; i++)
    {
      int xPoints[] = new int[3];
      for (int j = 0; j < xPoints.length; j++)
      {
        xPoints[j] = Constants.rand.nextInt(Constants.width);
      }

      int yPoints[] = new int[3];
      for (int j = 0; j < yPoints.length; j++)
      {
        yPoints[j] = Constants.rand.nextInt(Constants.height);
      }

      int rgba[] = new int[4];
      for (int j = 0; j < rgba.length - 1; j++)
      {
        rgba[j] = Constants.rand.nextInt(Constants.MAX_RGBA);
      }
      
      rgba[3] = Constants.rand.nextInt(100);

      Triangle gene = new Triangle(xPoints, yPoints, rgba);
      seed.addGene(gene);
    }
    
    return seed;
  }
  
  private static Color averageColors(int ... colors)
  {
    int red = 0;
    int green = 0;
    int blue = 0;
    
    for (int color : colors)
    {
      Color c = new Color(color);
      red += c.getRed();
      green += c.getGreen();
      blue += c.getBlue();
    }
    
    return new Color(red / colors.length, green / colors.length, blue / colors.length);
  }
}
