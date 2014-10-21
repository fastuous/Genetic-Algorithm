package trianglegenome;

import static java.lang.Math.abs;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import trianglegenome.util.Constants;

/**
 * Performs crossover on genomes.
 * @author David Collins
 * <br /><br />
 * Example code:<br/>
 * <code><pre>
 *  
 *  // assume genomes is a List&lt;Genome&gt;
 *  // assume hcsp is a HillClimbingSpawner
 *  
 *  java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.Lock();
 *  
 *  GenomeCrossover gc = new GenomeCrossover(genomes);
 *  
 *  // While program is running
 *  {
 *    // if a crossover is needed
 *    {
 *      hcsp.pauseHillClimbers();
 *      gc.crossover(10); // 10 is an arbitrary number
 *    }
 *  }
 *  
 * </pre></code>
 * 
 */
public class GenomeCrossover
{
  /** If set to true, GenomeCrossover will perform an in place crossover. Otherwise,
   * GenomeCrossover will overwrite two random genomes. */
  public static final boolean IN_PLACE = true;
  
  /** A list of genomes on which this GenomeCrossover will perform a crossover.
   * This list should consist of every genome from every tribe. */
  private List<Genome> genomes;
  
  /**
   * Creates a GenomeCrossover given a list of {@link Genome} objects and
   * a {@link java.util.concurrent.locks.Lock}.
   * @param genomes A list of all genomes from every tribe.
   * @param genomeLock A lock for the genomes.
   */
  public GenomeCrossover(List<Genome> genomes)
  {
    this.genomes = genomes;
  }
  
  /**
   * Given a number of times to cross over and a list of genomes, perform some crossovers.
   * This method assumes that the given genomes list is sorted where genomes toward the
   * beginning should be more likely to be parents of the crossover.
   * Note that if {@link #genomeLock} is already locked, this method will throw
   * an {@link IllegalStateException}.
   * @param crossoverCount The number of times to perform a crossover.
   * @param genomes A sorted list of genomes on which the crossover will be performed.
   */
  public void crossover(int crossoverCount)
  {
    Set<Integer> alreadyCrossed = new HashSet<Integer>(crossoverCount * 4);
    Random rand = Constants.rand;
    
    for (int i = 0; i < crossoverCount; i++)
    {
      int parentIndex1;
      int parentIndex2;
      int childIndex1;
      int childIndex2;
      do
      {
        parentIndex1 = (int)(genomes.size()*abs(rand.nextDouble() - rand.nextDouble()));
        parentIndex2 = (int)(genomes.size()*abs(rand.nextDouble() - rand.nextDouble()));
      }
      while (!(alreadyCrossed.contains(parentIndex1) || alreadyCrossed.contains(parentIndex2)));
      
      Genome p1 = genomes.get(parentIndex1);
      Genome p2 = genomes.get(parentIndex2);
      int genomeLength = p1.getGenes().size();
      
      if (!IN_PLACE)
      {
        do
        {
          childIndex1 = (int)(genomes.size()*abs(rand.nextDouble() - rand.nextDouble()));
          childIndex2 = (int)(genomes.size()*abs(rand.nextDouble() - rand.nextDouble()));
        }
        while (!(alreadyCrossed.contains(childIndex1) || alreadyCrossed.contains(childIndex2)));
        Genome c1 = genomes.get(childIndex1);
        Genome c2 = genomes.get(childIndex2);
        Genome.doublePointCrossover(p1, p2, c1, c2, genomeLength, genomeLength * 2);
        alreadyCrossed.add(childIndex1);
        alreadyCrossed.add(childIndex2);
      }
      else Genome.doublePointCrossoverInPlace(p1, p2, genomeLength, genomeLength * 2);
      
      alreadyCrossed.add(parentIndex1);
      alreadyCrossed.add(parentIndex2);
    }
  }
}
