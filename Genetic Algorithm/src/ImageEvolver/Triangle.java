package ImageEvolver;

public class Triangle
{

  public int[] dna = new int[10];

  public Triangle(int[] xPoints, int[] yPoints, int[] rgba)
  {
    dna[0] = xPoints[0];
    dna[1] = xPoints[1];
    dna[2] = xPoints[2];
    
    dna[3] = yPoints[0];
    dna[4] = yPoints[1];
    dna[5] = yPoints[2];
    
    dna[6] = rgba[0];
    dna[7] = rgba[1];
    dna[8] = rgba[2];
    dna[9] = rgba[3];

  }
}
