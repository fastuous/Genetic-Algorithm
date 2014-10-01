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
   * Given two triangle Genomes, performs a crossover at a given start point and end point.
   * The ending point should not be greater than the number of triangles in a Genome
   * multiplied by the triangle DNA length.
   * @param a The first Genome of the crossover.
   * @param b The second Genome of the crossover.
   * @param startInclusive The starting point of the swap.
   * @param endExclusive The ending point of the swap.
   */
  public static void doublePointCrossover(Genome a, Genome b, int startInclusive, int endExclusive)
  {
    // The swap ending point for the last triangle pair to be swapped.
    int end2 = endExclusive % Triangle.DNA_LENGTH;

    // The swap starting point for the first triangle pair to be swapped.
    int start1 = startInclusive % Triangle.DNA_LENGTH;
    
    // Number of triangles that can be fully swapped.
    int fullSwaps = ((endExclusive - end2) - (startInclusive + start1)) / Triangle.DNA_LENGTH;
    
    // The first triangle that will be swapped.
    int firstTriangle = startInclusive / Triangle.DNA_LENGTH;
    
    // The last triangle to be swapped.
    int lastTriangle = endExclusive / Triangle.DNA_LENGTH;
    
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
  
  /**
   * Given two triangle Genomes, performs a crossover from the beginning to a given end point.
   * The ending point should not be greater than the number of triangles in a Genome
   * multiplied by the triangle DNA length.
   * @param a The first Genome of the crossover.
   * @param b The second Genome of the crossover.
   * @param endExclusive The ending point of the swap.
   */
  public static void singlePointCrossover(Genome a, Genome b, int endExclusive)
  {
    doublePointCrossover(a, b, 0, endExclusive);
  }
}
