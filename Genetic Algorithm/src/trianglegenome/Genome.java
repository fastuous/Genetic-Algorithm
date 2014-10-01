package trianglegenome;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import trianglegenome.util.Constants;

public class Genome implements Cloneable
{
  private List<Triangle> genes = new LinkedList<>();
  
  public void addGene(Triangle gene)
  {
    if (genes.size() == Constants.TRIANGLE_COUNT) return;
    genes.add(gene);
  }
  
  public void addGenes(List<Triangle> genes)
  {
    if (this.genes.size() + genes.size() > Constants.TRIANGLE_COUNT) return;
    
    this.genes.addAll(genes);
  }
  
  public List<Triangle> getGenes()
  {
    return genes;
  }
  
  /**
   * Given two parents and two children, performs a double point crossover
   * of the two parents and writes the data to the two children. 
   * @param parent1 The first parent of the crossover.
   * @param parent2 The second parent of the crossover.
   * @param child1 The first child, to which the data will be overwritten.
   * @param child2 The second child, to which the data will be overwritten.
   * @param start The starting point of the swap (inclusive).
   * @param end The ending point of the swap (exclusive).
   */
  public static void doublePointCrossOver(
      Genome parent1, Genome parent2,
      Genome child1, Genome child2,
      int start, int end)
  {
    child1.copyFrom(parent1);
    child2.copyFrom(parent2);
    doublePointCrossoverInPlace(child1, child2, start, end);
  }
  
  /**
   * Given two parents and two children, performs a single point crossover
   * of the two parents and writes the data to the two children. 
   * @param parent1 The first parent of the crossover.
   * @param parent2 The second parent of the crossover.
   * @param child1 The first child, to which the data will be overwritten.
   * @param child2 The second child, to which the data will be overwritten.
   * @param end The ending point of the swap (exclusive).
   */
  public static void singlePointCrossOver(
      Genome parent1, Genome parent2,
      Genome child1, Genome child2,
      int end)
  {
    child1.copyFrom(parent1);
    child2.copyFrom(parent2);
    singlePointCrossoverInPlace(child1, child2, end);
  }

  /**
   * Given two triangle Genomes, performs a crossover at a given start point and end point.
   * The ending point should not be greater than the number of triangles in a Genome
   * multiplied by the triangle DNA length.
   * @param a The first Genome of the crossover.
   * @param b The second Genome of the crossover.
   * @param start The starting point of the swap (inclusive).
   * @param end The ending point of the swap (exclusive).
   */
  public static void doublePointCrossoverInPlace(Genome a, Genome b, int start, int end)
  {
    // The swap ending point for the last triangle pair to be swapped.
    int end2 = end % Triangle.DNA_LENGTH;

    // The swap starting point for the first triangle pair to be swapped.
    int start1 = start % Triangle.DNA_LENGTH;
    
    // Number of triangles that can be fully swapped.
    int fullSwaps = ((end - end2) - (start + start1)) / Triangle.DNA_LENGTH;
    
    // The first triangle that will be swapped.
    int firstTriangle = start / Triangle.DNA_LENGTH;
    
    // The last triangle to be swapped.
    int lastTriangle = end / Triangle.DNA_LENGTH;
    
    List<Triangle> aSubList = a.genes.subList(firstTriangle, lastTriangle + 1);
    List<Triangle> bSubList = b.genes.subList(firstTriangle, lastTriangle + 1);
    Iterator<Triangle> aIterator = aSubList.iterator();
    Iterator<Triangle> bIterator = bSubList.iterator();
    
    if (aIterator.hasNext() && bIterator.hasNext())
    {
      Triangle.swapDNA(aIterator.next(), bIterator.next(), start1, Triangle.DNA_LENGTH);
    }
    for (int i = 0; i < fullSwaps && aIterator.hasNext() && bIterator.hasNext(); i++)
    {
      Triangle.swapDNA(aIterator.next(), bIterator.next());
    }
    if (aIterator.hasNext() && bIterator.hasNext())
    {
      Triangle.swapDNA(aIterator.next(), bIterator.next(), 0, end2);
    }
  }
  
  /**
   * Given two triangle Genomes, performs a crossover from the beginning to a given end point.
   * The ending point should not be greater than the number of triangles in a Genome
   * multiplied by the triangle DNA length.
   * @param a The first Genome of the crossover.
   * @param b The second Genome of the crossover.
   * @param end The ending point of the swap (exclusive).
   */
  public static void singlePointCrossoverInPlace(Genome a, Genome b, int end)
  {
    doublePointCrossoverInPlace(a, b, 0, end);
  }
  
  /**
   * Clears this Genome's {{@link #genes} and clones each {@link trianglegenome.Triangle}
   * from a different Genome into this Genome's genes. 
   * @param other The other Genome from which to copy the genes.
   */
  private void copyFrom(Genome other)
  {
    this.genes.clear();
    other.genes.forEach(t -> this.addGene(t.clone()));
  }
  
  /**
   * Returns a deep-copy of this Genome.
   * @return A deep-copy of this Genome.
   */
  @Override
  public Genome clone()
  {
    Genome copy = new Genome();
    this.genes.forEach(t -> copy.addGene(t.clone()));
    return copy;
  }
}
