package trianglegenome;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import trianglegenome.util.Constants;

/**
 * A class that generates a good candidate for a seed genome, given a buffered image of the target.
 * It does this by finding the average color of nine subsections of the image, then from those nine
 * colors, averaging together the colors that correspond roughly to the areas of triangles that take
 * up the top, left, right, and bottom of an image. We construct Triangle objects using those
 * colors, then add them to the base layer of the genome. The rest of the triangles are randomly
 * generated, with their opacity restricted so as to get more color-interaction between overlapping
 * triangles.
 * 
 * @author Truman DeYoung
 */
public class SeedGenome
{
  /**
   * Generate a genome based on a given target image. The genome is generated in accordance with the
   * procedure outlined in the class comments.
   * 
   * @param image The target image.
   * @return A genome to seed evolution.
   */
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
    
    Color averageColor = averageColors(topTriangleColor.getRGB(), leftTriangleColor.getRGB(), rightTriangleColor.getRGB(), bottomTriangleColor.getRGB());
    int averageRGB[] = new int[3]; 
    averageRGB[0] = averageColor.getRed();
    averageRGB[1] =  averageColor.getGreen();
    averageRGB[2] = averageColor.getBlue();
    System.out.println("red: " + averageRGB[0] + " green: " + averageRGB[1] + " blue: " + averageRGB[2]);
    
    

    int leftX = 0;
    int rightX = image.getWidth() - 1;
    int midX = image.getWidth() / 2;

    int topY = 0;
    int bottomY = image.getHeight() - 1;
    int midY = image.getHeight() / 2;

    Triangle topTriangle = new Triangle(leftX, rightX, midX, topY, topY, midY, topTriangleColor.getRed(),
        topTriangleColor.getGreen(), topTriangleColor.getBlue(), 255);
    Triangle leftTriangle = new Triangle(leftX, midX, leftX, topY, midY, bottomY, leftTriangleColor.getRed(),
        leftTriangleColor.getGreen(), leftTriangleColor.getBlue(), 255);
    Triangle rightTriangle = new Triangle(rightX, midX, rightX, topY, midY, bottomY, rightTriangleColor.getRed(),
        rightTriangleColor.getGreen(), rightTriangleColor.getBlue(), 255);
    Triangle bottomTriangle = new Triangle(leftX, midX, rightX, bottomY, midY, bottomY, bottomTriangleColor.getRed(),
        bottomTriangleColor.getGreen(), bottomTriangleColor.getBlue(), 255);

    Genome seed = new Genome();
    seed.addGene(topTriangle);
    seed.addGene(leftTriangle);
    seed.addGene(rightTriangle);
    seed.addGene(bottomTriangle);


    for (int i = 0; i < Constants.TRIANGLE_COUNT/2 - 2; i++)
    {
      int xPoints[] = new int[3];
      for (int j = 0; j < xPoints.length; j++)
      {
        xPoints[j] = (int) (Math.floor(Constants.width)*Math.pow(Math.random(), 2.0));
      }

      int yPoints[] = new int[3];
      for (int j = 0; j < yPoints.length; j++)
      {
        yPoints[j] = (int)  Constants.rand.nextInt(Constants.height);
      }

      int rgba[] = new int[4];
      for (int j = 0; j < rgba.length - 1; j++)
      {
        rgba[j] = averageRGB[j] + (40 - Constants.rand.nextInt(80));
      }

      rgba[3] = (int) Math.sqrt(Constants.rand.nextInt(150*150));;

      Triangle gene = new Triangle(xPoints, yPoints, rgba);
      seed.addGene(gene);
    }
    
    for (int i = Constants.TRIANGLE_COUNT/2 - 2; i < Constants.TRIANGLE_COUNT - 4; i++)
    {
      int xPoints[] = new int[3];
      for (int j = 0; j < xPoints.length; j++)
      {
        xPoints[j] = (int) (Math.sqrt(Constants.rand.nextInt(Constants.width*Constants.width)));
      }

      int yPoints[] = new int[3];
      for (int j = 0; j < yPoints.length; j++)
      {
        yPoints[j] = Constants.rand.nextInt(Constants.height);
      }

      int rgba[] = new int[4];
      for (int j = 0; j < rgba.length - 1; j++)
      {
        rgba[j] = averageRGB[j] + (40 - Constants.rand.nextInt(80));
      }

      rgba[3] = (int) Math.sqrt(Constants.rand.nextInt(150*150));

      Triangle gene = new Triangle(xPoints, yPoints, rgba);
      seed.addGene(gene);
    }

    return seed;
  }

  /**
   * Takes the average of any number of given int-colors. Returns a completely opaque color.
   * 
   * @param colors The colors to average together.
   * @return An opaque color of the average.
   */
  private static Color averageColors(int... colors)
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
