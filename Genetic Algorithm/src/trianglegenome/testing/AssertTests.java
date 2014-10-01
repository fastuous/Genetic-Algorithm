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
    validityTests();
    hammingDistanceTests();
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
  public void validityTests()
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
  public void hammingDistanceTests()
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

  public void testCrossover()
  {
    Triangle t1 = new Triangle(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
    Triangle t2 = new Triangle(new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
    Triangle t3 = new Triangle(new int[] { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 });
    Triangle t4 = new Triangle(new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 });
    Triangle t5 = new Triangle(new int[] { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 });
    Triangle t6 = new Triangle(new int[] { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 });

    Triangle t2Crosst5 = new Triangle(new int[] { 1, 1, 4, 4, 4, 4, 4, 4, 4, 4 });
    Triangle t5Crosst2 = new Triangle(new int[] { 4, 4, 1, 1, 1, 1, 1, 1, 1, 1 });

    Genome parent1 = new Genome(t1, t2, t3);
    Genome parent2 = new Genome(t4, t5, t6);
    Genome child1 = new Genome();
    Genome child2 = new Genome();

    Genome expectedChild1 = new Genome(t1, t2Crosst5, t6);
    Genome expectedChild2 = new Genome(t4, t5Crosst2, t3);

    // Genome.singlePointCrossOver(parent1, parent2, child1, child2, 1);

    assert isSinglePointCrossover(parent1, parent1, expectedChild1, expectedChild2);

    // assert child1.equals(expectedChild1);
    // assert child2.equals(expectedChild2);
    //
    // assert !child1.equals(child2);
    //
    // assert !child1.equals(parent1);
    // assert !child1.equals(parent2);
    // assert !child2.equals(parent1);
    // assert !child2.equals(parent2);

    return;
  }

  /**
   * Given two parent genomes and two children genomes, tests whether the children are the result of
   * a single point crossover mutation of the parents.
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

    int totalGenes = parent1Genes.size() * Triangle.DNA_LENGTH;

    for (int i = 0; i < totalGenes; i++)
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
      if (child1Genes.get(i / 10).dna[i % 10] != parent1Genes.get(i / 10).dna[i % 10]) return false;
      if (child2Genes.get(i / 10).dna[i % 10] != parent2Genes.get(i / 10).dna[i % 10]) return false;
    }

    for (int i = indexAfterCrossover; i < totalGenes; i++)
    {
      if (child1Genes.get(i / 10).dna[i % 10] != parent2Genes.get(i / 10).dna[i % 10]) return false;
      if (child2Genes.get(i / 10).dna[i % 10] != parent1Genes.get(i / 10).dna[i % 10]) return false;
    }

    return true;
  }

  public static void main(String[] args)
  {
    new AssertTests();
  }
}
