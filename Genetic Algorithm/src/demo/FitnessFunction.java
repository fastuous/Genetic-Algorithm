package demo;
import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * A function that maps a Triangle to an int. The lower the value of the
 * int, the more "fit" the Triangle is.
 * @author collinsd
 */
@FunctionalInterface
public interface FitnessFunction extends ToIntFunction<Triangle>
{
  /**
   * Composes an EvolveFunction with a FitnessFunction for use in seeing the
   * resulting fitness after applying an EvolveFunction to it.
   * @param before The EvolveFunction to apply before this
   * FitnessFunction.
   * @return A FitnessFunction that evaluates the fitness of a Triangle
   * after the given EvolveFunction is applied to it.
   */
  default FitnessFunction compose(EvolveFunction before)
  {
    Objects.requireNonNull(before);
    return triangle -> this.applyAsInt(before.apply(triangle));
  }
}