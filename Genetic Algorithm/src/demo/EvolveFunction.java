package demo;
import java.util.function.Function;

/**
 * A FunctionalInterface which takes a Triangle and maps it to
 * another more (or less) evolved Triangle.
 * @author collinsd
 */
@FunctionalInterface
public interface EvolveFunction extends Function<Triangle, Triangle>
{
  /**
   * Returns true if the EvolveFunction can improve a given triangle
   * according to a given fitness function.
   * @param fitnessFunction The fitness function that determines the fitness
   * of a triangle before and after applying this function to that
   * triangle. 
   * @param triangle The triangle used for the before and after comparison.
   * @return True if the EvolveFunction can improve a given triangle
   * according to a given fitness function.
   */
  
  public default boolean improvesTriangle(FitnessFunction fitnessFunction, Triangle triangle)
  {
    int fitnessBefore = fitnessFunction.applyAsInt(triangle);
    int fitnessAfter = fitnessFunction.compose(this).applyAsInt(triangle);
    return fitnessBefore > fitnessAfter;
  }
}
