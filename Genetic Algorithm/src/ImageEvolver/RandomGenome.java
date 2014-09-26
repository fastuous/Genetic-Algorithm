package ImageEvolver;

import ImageEvolver.util.Constants;

public class RandomGenome
{

  public static Genome generateGenome()
  {
    Genome genome = new Genome();

    for (int i = 0; i < 100; i++)
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
      for (int j = 0; j < rgba.length; j++)
      {
        rgba[j] = Constants.rand.nextInt(Constants.MAX_RGBA);
      }

      Triangle gene = new Triangle(xPoints, yPoints, rgba);
      genome.addGene(gene);
    }

    return genome;
  }
}
