package ImageEvolver;

public class Triangle
{
  public int[] xs = new int[3];
  public int[] ys = new int[3];
  public int[] rgba = new int[4];

  public Triangle(int[] xPoints, int[] yPoints, int red, int green, int blue, int alpha)
  {
    xs = xPoints;
    ys = yPoints;
    
    rgba[0] = red;
    rgba[1] = green;
    rgba[2] = blue;
    rgba[3] = alpha;
  }

}
