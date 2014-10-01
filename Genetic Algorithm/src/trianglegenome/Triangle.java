package trianglegenome;

import java.util.Arrays;

/**
 * Contains the information of a triangle for drawing.
 * @author Truman DeYoung
 */
public class Triangle implements Cloneable
{
  /**
   * The length of the {{@link #dna} array that keeps track of the triangle DNA.
   */
  public static int DNA_LENGTH = 10;
  
  /**
   * Stores the color and the location of the triangle vertexes.
   * Indices 0-2 are the three x coordinates of the vertices.
   * Indices 3-5 are the three y coordinates of the vertices.
   * Indices 6, 7, 8, and 9 are the red, green, blue and alpha
   * respectively.
   */
  public int[] dna;

  /**
   * Given three x and three y coordinates and four values for rgba,
   * creates a Triangle.  
   * @param xPoints An array of length 3 containing the x coordinates of the Triangle vertices.
   * @param yPoints An array of length 3 containing the y coordinates of the Triangle vertices.
   * @param rgba An array of length 4 containing the red, green, blue and alpha of the Triangle.
   */
  public Triangle(int[] xPoints, int[] yPoints, int[] rgba)
  {
    dna = new int[DNA_LENGTH];
    
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
  
  /**
   * Given a dna as an array. Creates a new Triangle whose dna will be the given array.
   * The given array must be of length {@value #DNA_LENGTH} (the value of {link #DNA_LENGTH}).
   * @param dna The dna that this new triangle will use for its {@link #dna}.
   */
  public Triangle(int[] dna)
  {
    if (dna.length != DNA_LENGTH)
    {
      throw new IllegalArgumentException("DNA must be of length " + DNA_LENGTH);
    }
    this.dna = dna;
  }
  
  /**
   * A private constructor that does not initialize the {@link #dna}.
   */
  private Triangle()
  {
  }

  /**
   * Compares this Triangle with an Object. If the Object is not an instance
   * of Triangle then this automatically returns false. Otherwise, does
   * a element-wise comparison of the two triangle's {@link Triangle#dna}
   * fields.
   * @param other The other object to compare with this Triangle.
   */
  @Override
  public boolean equals(Object other)
  {
    if (other instanceof Triangle)
    {
      return Arrays.equals(this.dna, ((Triangle)other).dna);
    }
    else return false;
  }
  
  /**
   * Returns the number of differences between this Triangle and another triangle.
   * @param with The triangle to compare to this triangle.
   * @return The number of differences between this Triangle and another triangle.
   */
  public int countDifferences(Triangle with)
  {
    int differences = 0;
    for (int i = 0; i < DNA_LENGTH; i++)
    {
      if (with.dna[i] != this.dna[1]) differences++;
    }
    return differences;
  }
  
  /**
   * Swaps two triangles' DNA starting at a given point and ending at a
   * given point.
   * @param a The first triangle of the swap.
   * @param b The second triangle of the swap.
   * @param startInclusive The start index (inclusive) of the DNA to swap.
   * @param endExclusive The end index (exclusive) of the DNA to swap.
   */
  public static void swapDNA(Triangle a, Triangle b, int startInclusive, int endExclusive)
  {
    int [] temp = Arrays.copyOfRange(b.dna, startInclusive, endExclusive);
    for (int i = startInclusive, j = 0; i < endExclusive; i++, j++)
    {
      a.dna[i] = b.dna[i];
      b.dna[i] = temp[j];
    }
  }
  
  /**
   * Swaps two triangles' DNA.
   * @param a The first triangle of the swap.
   * @param b The second triangle of the swap.
   */
  public static void swapDNA(Triangle a, Triangle b)
  {
    int [] temp = a.dna;
    a.dna = b.dna;
    b.dna = temp;
  }
  
  /**
   * Returns a deep copy of this triangle.
   * @return A deep copy of this triangle.
   */
  @Override
  public Triangle clone()
  {
    Triangle t = new Triangle();
    t.dna = Arrays.copyOf(this.dna, Triangle.DNA_LENGTH);
    return t;
  }
  
  /**
   * Performs the unit tests for all of the triangles.
   * @author collinsd
   */
  public static class TriangleUnitTests
  {
    static
    {
      Triangle t1 = new Triangle(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
      Triangle t2 = new Triangle(new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
      Triangle t3 = new Triangle(new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2});
      Triangle t4 = new Triangle(new int[] {3, 3, 3, 3, 3, 3, 3, 3, 3, 3});
      Triangle t5 = new Triangle(new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4, 4});
      Triangle t6 = new Triangle(new int[] {5, 5, 5, 5, 5, 5, 5, 5, 5, 5});
      
      // TODO put unit tests here. We can move this code somewhere else later.
    }
  }
}
