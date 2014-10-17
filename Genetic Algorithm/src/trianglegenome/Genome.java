package trianglegenome;

import static java.lang.Math.min;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import trianglegenome.util.Constants;

@XmlRootElement(name="genome")
public class Genome implements Cloneable
{
  private List<Triangle> genes = new LinkedList<>();
  
  public Genome()
  {
  }
  
  public Genome(Triangle ... triangles)
  {
    for (Triangle t : triangles) addGene(t);
  }
  
  public Genome(int ... dna)
  {
    if (dna.length % Triangle.DNA_LENGTH != 0)
    {
      throw new IllegalArgumentException("DNA must be divisible by " + Triangle.DNA_LENGTH);
    }
    for (int i = 0; i < dna.length / Triangle.DNA_LENGTH; i ++)
    {
      int[] tDna = Arrays.copyOfRange(dna, i * Triangle.DNA_LENGTH, (i+1) * Triangle.DNA_LENGTH);
      Triangle t = new Triangle(tDna);
      addGene(t);
    }
  }
  
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
  
  @XmlElement(name="gene")
  public List<Triangle> getGenes()
  {
    return genes;
  }
  
  public static int getHammingDistance(Genome genome1, Genome genome2)
  {
    int distance = 0;
    List<Triangle> genes1 = genome1.getGenes();
    List<Triangle> genes2 = genome2.getGenes();
    
    for (int i = 0; i < Constants.TRIANGLE_COUNT; i++)
    {
      distance += genes1.get(i).countDifferences(genes2.get(i));
    }
    
    return distance;
  }
  
  /**
   * Given two parents and two children, performs a double point crossover
   * of the two parents and writes the data to the two children. 
   * @param parent1 The first parent of the crossover.
   * @param parent2 The second parent of the crossover.
   * @param child1 The first child, to which the data will be overwritten.
   * @param child2 The second child, to which the data will be overwritten.
   * @param start The starting point of the swap (inclusive).
   * @param end The ending point of the swap (inclusive).
   */
  public static void doublePointCrossOver(
      Genome parent1, Genome parent2,
      Genome child1, Genome child2,
      int start, int end)
  {
    child1.copyFrom(parent1);
    child2.copyFrom(parent2);
    doublePointCrossoverInPlace(child2, child1, start, end);
  }
  
  /**
   * Given two parents and two children, performs a single point crossover
   * of the two parents and writes the data to the two children. 
   * @param parent1 The first parent of the crossover.
   * @param parent2 The second parent of the crossover.
   * @param child1 The first child, to which the data will be overwritten.
   * @param child2 The second child, to which the data will be overwritten.
   * @param start The start point of the swap (inclusive).
   */
  public static void singlePointCrossOver(
      Genome parent1, Genome parent2,
      Genome child1, Genome child2,
      int start)
  {
    child1.copyFrom(parent1);
    child2.copyFrom(parent2);
    singlePointCrossoverInPlace(child2, child1, start);
  }

  /**
   * Given two triangle Genomes, performs a crossover at a given start point and end point.
   * The ending point should not be greater than the number of triangles in a Genome
   * multiplied by the triangle DNA length.
   * @param a The first Genome of the crossover.
   * @param b The second Genome of the crossover.
   * @param start The starting point of the swap (inclusive).
   * @param end The ending point of the swap (inclusive).
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
    
    List<Triangle> aSubList = a.genes.subList(firstTriangle, lastTriangle);
    List<Triangle> bSubList = b.genes.subList(firstTriangle, lastTriangle);
    Iterator<Triangle> aIterator = aSubList.iterator();
    Iterator<Triangle> bIterator = bSubList.iterator();
    
    if (start1 != 0 && aIterator.hasNext() && bIterator.hasNext())
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
   * @param start The start point of the swap (inclusive).
   */
  public static void singlePointCrossoverInPlace(Genome a, Genome b, int start)
  {
    int end = min(a.genes.size(), b.genes.size()) * Triangle.DNA_LENGTH;
    doublePointCrossoverInPlace(a, b, start, end);
  }
  
  /**
   * Clears this Genome's {{@link #genes} and clones each {@link trianglegenome.Triangle}
   * from a different Genome into this Genome's genes. 
   * @param other The other Genome from which to copy the genes.
   */
  public void copyFrom(Genome other)
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
  
  /**
   * Compares this Genome with an Object. If the Object is not an instance
   * of Genome then this automatically returns false. Otherwise, does
   * a element-wise comparison of the two Genome's {@link Genome#genes}
   * fields.
   * @param other The other object to compare with this Genome.
   */
  @Override
  public boolean equals(Object other)
  {
    if (other instanceof Genome)
    {
      if (this.genes.size() != ((Genome)other).genes.size()) return false;
      
      Iterator<Triangle> t1Iterator = this.genes.iterator();
      Iterator<Triangle> t2Iterator = ((Genome)other).genes.iterator();
      boolean isEqual = true;
      while (t1Iterator.hasNext())
      {
        isEqual &= t1Iterator.next().equals(t2Iterator.next());
      }
      return isEqual;
    }
    else return false;
  }
  
  /**
   * Returns a string representation of this genome.
   * @return A string representation of this genome.
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("{ Genome : ");
    for (Triangle t : genes)
    {
      for (int i = 0; i < Triangle.DNA_LENGTH; i++) sb.append(t.dna[i] + " ");
    }
    sb.append("}");
    return sb.toString();
  }
}
