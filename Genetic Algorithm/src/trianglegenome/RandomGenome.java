package trianglegenome;

import trianglegenome.util.Constants;

/**
 * Utility class to satisfy requirements for Milestone 1
 * 
 * @author Truman DeYoung
 */
public class RandomGenome
{

  /**
   * Using the project-wide Random instance, generates a genome with valid triangles, given that the
   * global image height and width have been set.
   * 
   * @return A genome with randomly generated genes.
   */
  public static Genome generateGenome()
  {
    Genome genome = new Genome();

    for (int i = 0; i < Constants.TRIANGLE_COUNT; i++)
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
