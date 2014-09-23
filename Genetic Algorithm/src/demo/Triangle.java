package demo;
import java.util.Comparator;


/**
 * A simple class to store the information necessary to make a triangle.
 * @author collinsd
 */
public class Triangle
{
  /** The x coordinate of the first vertex. */
  public volatile int vertex1x;
  
  /** The y coordinate of the first vertex. */
  public volatile int vertex1y;
  
  /** The x coordinate of the second vertex. */
  public volatile int vertex2x;
  
  /** The y coordinate of the second vertex. */
  public volatile int vertex2y;
  
  /** The x coordinate of the third vertex. */
  public volatile int vertex3x;
  
  /** The y coordinate of the third vertex. */
  public volatile int vertex3y;
  
  // or
  // public volatile java.awt.Point vertex1;
  // public volatile java.awt.Point vertex2;
  // public volatile java.awt.Point vertex3;
  
  /** An integer whose bytes from highest order to lowest order represent
   * the following: alpha, red, green, and blue */
  public volatile int argb;
  
  // or public volatile int rgba;
  
  /**
   * Copies information from another Triangle into this triangle.
   * @param other The other Triangle from which to copy data.
   */
  public void copyFrom(Triangle other)
  {
    this.vertex1x = other.vertex1x;
    this.vertex1y = other.vertex1y;
    this.vertex2x = other.vertex2x;
    this.vertex2y = other.vertex2y;
    this.vertex3x = other.vertex3x;
    this.vertex3y = other.vertex3y;
    this.argb = other.argb;
  }
  
  /**
   * Returns a comparator which compares Triangles based on a fitness
   * function.
   * @param fitnessFunction The FitnessFunction by which the returned
   * comparator will compare the Triangles. 
   * @return A Comparator which compares Triangles based on a fitness
   * function.
   */
  public static Comparator<Triangle> getComparator(
      FitnessFunction fitnessFunction)
  {
    return (triangle1, triangle2) ->
    {
      return Integer.compare(
          fitnessFunction.applyAsInt(triangle1),
          fitnessFunction.applyAsInt(triangle2));
    };
  }
  
  /**
   * Returns true if another Triangle is better than this triangle based on
   * a given fitness function.
   * @param fitnessFunction The FitnessFunction that will be used to compare
   * the two Triangles. 
   * @param other The other Triangle to compare to this triangle.
   * @return True if the the other Triangle is better than this Triangle
   * according to the fitness function.
   */
  public boolean isOtherTriangleBetter(
      FitnessFunction fitnessFunction, Triangle other)
  {
    int thisFitness = fitnessFunction.applyAsInt(this);
    int otherFitness = fitnessFunction.applyAsInt(other);
    return thisFitness > otherFitness;
  }
}
