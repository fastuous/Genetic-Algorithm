package ImageEvolver;

import java.awt.Color;



public class Triangle
{
  public int x1;
  public int x2;
  public int x3;

  public int y1;
  public int y2;
  public int y3;

  public int red;
  public int green;
  public int blue;
  public int alpha;

  public Triangle(int[] xPoints, int[] yPoints, int red, int green, int blue, int alpha)
  {
    x1 = xPoints[0];
    x2 = xPoints[1];
    x3 = xPoints[2];
    
    y1 = yPoints[0];
    y2 = yPoints[1];
    y3 = yPoints[2];
    
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
  }

}
