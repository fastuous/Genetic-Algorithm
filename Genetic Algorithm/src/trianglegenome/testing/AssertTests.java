package trianglegenome.testing;

import java.util.List;
import trianglegenome.Genome;
import trianglegenome.RandomGenome;
import trianglegenome.Triangle;
import trianglegenome.util.Constants;

/**
 * Unit testing for Lab 5 specifications.
 * 
 * @author Mason Banning
 */
public class AssertTests
{
  public AssertTests()
  {
    Constants.height = 413;
    Constants.width = 512;
    testValidity();
    testHammingDistance();
    testCrossover();
  }

  /**
   * This method Tests a triangle's dna to make sure all the values are in the correct bounds.
   * Returns false if not valid, else it returns true.
   * 
   * @param Triangle
   * @return Boolean
   */
  public boolean isValidTriangle(Triangle tri)
  {
    for (int i = 0; i <= 2; ++i)
    {
      if (tri.dna[i] < 0 || tri.dna[i] > Constants.width) return false;
    }
    for (int i = 3; i <= 5; ++i)
    {
      if (tri.dna[i] < 0 || tri.dna[i] > Constants.height) return false;
    }
    for (int i = 6; i <= 9; ++i)
    {
      if (tri.dna[i] < 0 || tri.dna[i] > 255) return false;
    }
    return true;
  }

  /**
   * This method Tests a genomes's triangles to make sure all the triangles dna values are in the
   * correct bounds. Returns false if not valid, else it returns true.
   * 
   * @param Triangle
   * @return Boolean
   */
  public boolean isValidGenome(Genome genome)
  {
    List<Triangle> genes = genome.getGenes();
    for (Triangle tri : genes)
    {
      if (!isValidTriangle(tri)) return false;
    }
    return true;
  }

  /**
   * Assert tests that make two random genomes and test their validity
   */
  public void testValidity()
  {
    Genome testA = RandomGenome.generateGenome();
    Genome testB = RandomGenome.generateGenome();

    assert isValidGenome(testA);
    assert isValidGenome(testB);
  }

  /**
   * Assert tests that make four different genomes and perform different tests on their hamming
   * distance
   */
  public void testHammingDistance()
  {
    Genome distanceTestA = new Genome();// first Genome
    Genome distanceTestB = new Genome();// second Genome
    Genome distanceTestC = new Genome();// Genome made to have one difference from the first
    Genome distanceTestD = new Genome();// Genome made to have two differences from the second
    int[] dna1 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    int[] dna2 = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

    int[] dna3 = { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 };// different dna for C
    int[] dna4 = { 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 };// different dna for D

    for (int i = 0; i < 200; ++i)
    {
      if (i < 100) // Makes the first 100 triangles for each genome
      {
        distanceTestA.addGene(new Triangle(dna1));
        distanceTestC.addGene(new Triangle(dna1));
        distanceTestB.addGene(new Triangle(dna2));
        distanceTestD.addGene(new Triangle(dna2));
      }
      else if (i >= 100)// Makes the second hundred
      {
        distanceTestA.addGene(new Triangle(dna2));
        distanceTestB.addGene(new Triangle(dna1));
        if (i != 199) // leaves the final genome available for C and D
        {
          distanceTestC.addGene(new Triangle(dna2));
          distanceTestD.addGene(new Triangle(dna1));
        }
      }
    }
    // adds the dna that will make it different from the first
    distanceTestC.addGene(new Triangle(dna3));

    // adds the dna that will make it different from the second
    distanceTestD.addGene(new Triangle(dna4));

    Genome cloneA = distanceTestA.clone(); // the clone of the first genome

    // This tests the two completely different genomes
    assert Genome.getHammingDistance(distanceTestA, distanceTestB) == 2000;

    // This tests the cloned Genomes
    assert Genome.getHammingDistance(cloneA, distanceTestA) == 0;

    // This is the test for the genomes with one difference in the DNA
    assert Genome.getHammingDistance(distanceTestA, distanceTestC) == 1;

    // This is the test for the genomes with two differences in the DNA
    assert Genome.getHammingDistance(distanceTestB, distanceTestD) == 2;
  }

  /**
   * Run a series of six tests based on single point crossover mutation.
   */
  public void testCrossover()
  {
    Triangle t1 = new Triangle(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    Triangle t2 = new Triangle(1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    Triangle t3 = new Triangle(2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
    Triangle t4 = new Triangle(3, 3, 3, 3, 3, 3, 3, 3, 3, 3);
    Triangle t5 = new Triangle(4, 4, 4, 4, 4, 4, 4, 4, 4, 4);
    Triangle t6 = new Triangle(5, 5, 5, 5, 5, 5, 5, 5, 5, 5);

    // Constructs genes resulting in a crossover at the 12th dna (2nd gene)
    Triangle t2Crosst5 = new Triangle(1, 1, 4, 4, 4, 4, 4, 4, 4, 4);
    Triangle t5Crosst2 = new Triangle(4, 4, 1, 1, 1, 1, 1, 1, 1, 1);

    Genome parent1 = new Genome(t1, t2, t3);
    Genome parent2 = new Genome(t4, t5, t6);
    Genome child1 = new Genome();
    Genome child2 = new Genome();

    Genome parent1Untouched = new Genome(t1, t2, t3);
    Genome parent2Untouched = new Genome(t4, t5, t6);
    Genome expectedChild1 = new Genome(t1, t2Crosst5, t6);
    Genome expectedChild2 = new Genome(t4, t5Crosst2, t3);

    // First test case: random point in genome (12), using manually constructed expected results
    assert isSinglePointCrossover(parent1, parent2, expectedChild1, expectedChild2);

    // children don't contain same genes
    assert !expectedChild1.equals(expectedChild2);

    // parents aren't same address
    assert parent1 != parent2;

    // children aren't same address
    assert child1 != child2;

    // Second test case: random point in genome (12), using our method to crossover
    // NOTE our method changes the dna contained in the children passed to it and leaves the parents
    // unchanged
    Genome.singlePointCrossover(parent1, parent2, child1, child2, 12);
    
    assert isSinglePointCrossover(parent1, parent2, child1, child2);

    // check against manually constructed results
    assert child1.equals(expectedChild1);
    assert child2.equals(expectedChild2);

    // check that parents genes haven't been changed
    assert parent1.equals(parent1Untouched);
    assert parent2.equals(parent2Untouched);

    // children don't contain same genes
    assert !child1.equals(child2);

    // parents aren't same address
    assert parent1 != parent2;

    // children aren't same address
    assert child1 != child2;

    // Third test case: crossover after first dna
    Genome.singlePointCrossover(parent1, parent2, child1, child2, 1);

    assert isSinglePointCrossover(parent1, parent2, child1, child2);

    // children don't contain same genes
    assert !child1.equals(child2);

    // parents aren't same address
    assert parent1 != parent2;

    // children aren't same address
    assert child1 != child2;

    // Fourth test case: last dna in genome sequence
    Genome.singlePointCrossover(parent1, parent2, child1, child2, 29);

    assert isSinglePointCrossover(parent1, parent2, child1, child2);

    // children don't contain same genes
    assert !child1.equals(child2);

    // parents aren't same address
    assert parent1 != parent2;

    // children aren't same address
    assert child1 != child2;

    // Fifth test case: copy the parents genes into the children and verify a crossover didn't take
    // place
    child1 = parent1.clone();
    child2 = parent2.clone();

    assert !isSinglePointCrossover(parent1, parent2, child1, child2);

    // children contain same genes as parents
    assert child1.equals(parent1);
    assert child2.equals(parent2);

    // children don't contain same genes
    assert !child1.equals(child2);

    // parents aren't same address
    assert parent1 != parent2;

    // children aren't same address
    assert child1 != child2;

    // Sixth test case: let isSinglePointCrossover() test for duplicate children
    child2 = child1.clone();
    assert !isSinglePointCrossover(parent1, parent2, child1, child2);

    // children contain same genes as each other
    assert child1.equals(child2);

    // parents aren't same address
    assert parent1 != parent2;

    // children aren't same address
    assert child1 != child2;

  }

  /**
   * Given two parent genomes and two children genomes, tests whether the children are the result of
   * a single point crossover mutation of the parents. <br />
   * <br />
   * <strong>Note: </strong> We chose to let this test figure out where the crossover point is on
   * its own, rather than explicitly specify a crossover point as a parameter.
   * 
   * @param parent1 The first parent.
   * @param parent2 The second parent.
   * @param child1 The first child.
   * @param child2 The second child.
   * @return The result of the test.
   */
  private boolean isSinglePointCrossover(Genome parent1, Genome parent2, Genome child1,
      Genome child2)
  {
    if (parent1.equals(parent2)) return false;
    if (child1.equals(child2)) return false;

    int indexAfterCrossover = 0;
    List<Triangle> parent1Genes = parent1.getGenes();
    List<Triangle> parent2Genes = parent2.getGenes();
    List<Triangle> child1Genes = child1.getGenes();
    List<Triangle> child2Genes = child2.getGenes();

    if (parent1Genes.size() != parent2Genes.size()) return false;
    if (parent2Genes.size() != child1Genes.size()) return false;
    if (child1Genes.size() != child2Genes.size()) return false;

    int numGenes = parent1Genes.size();
    int totalDNA = parent1Genes.size() * Triangle.DNA_LENGTH;

    for (int i = 0; i < numGenes; i++)
    {
      if (indexAfterCrossover != 0) break;
      for (int j = 0; j < Triangle.DNA_LENGTH; j++)
      {
        if (parent1Genes.get(i).dna[j] != child1Genes.get(i).dna[j])
        {
          indexAfterCrossover = i * Triangle.DNA_LENGTH + j;
          break;
        }
      }
    }

    if (indexAfterCrossover == 0) return false;

    // OK technically all these '10's should be Triangle.DNA_LENGTH, but that makes this code super
    // messy...
    for (int i = 0; i < indexAfterCrossover; i++)
    {
      if (child1Genes.get(i / 10).dna[i % 10] != parent1Genes.get(i / 10).dna[i % 10])
        return false;
      if (child2Genes.get(i / 10).dna[i % 10] != parent2Genes.get(i / 10).dna[i % 10])
        return false;
    }

    for (int i = indexAfterCrossover; i < totalDNA; i++)
    {
      if (child1Genes.get(i / 10).dna[i % 10] != parent2Genes.get(i / 10).dna[i % 10])
        return false;
      if (child2Genes.get(i / 10).dna[i % 10] != parent1Genes.get(i / 10).dna[i % 10])
        return false;
    }

    return true;
  }

  public static void main(String[] args)
  {
    new AssertTests();
  }
}