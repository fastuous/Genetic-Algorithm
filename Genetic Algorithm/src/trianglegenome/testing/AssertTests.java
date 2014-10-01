/*
 * @author masonbanning
 * This is the class for lab5's assert tests
 */
package trianglegenome.testing;

import java.util.List;
import trianglegenome.Genome;
import trianglegenome.RandomGenome;
import trianglegenome.Triangle;
import trianglegenome.util.Constants;

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
   * This method Tests a triangle's dna to make sure all the values are in 
   * the correct bounds. Returns false if not valid, else it returns true.
   * 
   * @param Triangle
   * @return Boolean
   */
  private boolean isValidTriangle(Triangle tri)
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
   * This method Tests a genomes's triangles to make sure all the triangles dna values are in 
   * the correct bounds. Returns false if not valid, else it returns true.
   * 
   * @param Triangle
   * @return Boolean
   */
  private boolean isValidGenome(Genome genome)
  {
    List<Triangle> genes = genome.getGenes();
    for(Triangle tri : genes)
    {
      if(!isValidTriangle(tri)) return false;
    }
    return true;
  }
  /**
   * Assert tests that make two random genomes and test their validity
   */
  private void validityTests()
  {
    Genome testA = RandomGenome.generateGenome();
    Genome testB = RandomGenome.generateGenome();
    
    assert isValidGenome(testA);
    assert isValidGenome(testB);
  }
  /**
   * Assert tests that make four different genomes and perform different tests
   * on their hamming distance
   */
  private void hammingDistanceTests()
  {
    Genome distanceTestA = new Genome();//first Genome 
    Genome distanceTestB = new Genome();//second Genome
    Genome distanceTestC = new Genome();//Genome made to have one difference from the first
    Genome distanceTestD = new Genome();//Genome made to have two differences from the second
    int[] dna1 = {0,0,0,0,0,0,0,0,0,0};
    int[] dna2 = {1,1,1,1,1,1,1,1,1,1};
    
    int[] dna3 = {0,1,1,1,1,1,1,1,1,1};//different dna for C
    int[] dna4 = {1,1,0,0,0,0,0,0,0,0};//different dna for D
    
    for(int i = 0; i < 200; ++i)
    {
      if(i < 100) //Makes the first 100 triangles for each genome
      {
        distanceTestA.addGene(new Triangle(dna1));
        distanceTestC.addGene(new Triangle(dna1));
        distanceTestB.addGene(new Triangle(dna2));
        distanceTestD.addGene(new Triangle(dna2));
      }
      else if(i >= 100)//Makes the second hundred
      {
        distanceTestA.addGene(new Triangle(dna2));
        distanceTestB.addGene(new Triangle(dna1));
        if(i != 199) //leaves the final genome available for C and D
        {
          distanceTestC.addGene(new Triangle(dna2));
          distanceTestD.addGene(new Triangle(dna1));
        }
      }
    }
    
    distanceTestC.addGene(new Triangle(dna3));//adds the dna that will make it different from the first
    distanceTestD.addGene(new Triangle(dna4));//adds the dna that will make it different from the second
    
    Genome cloneA = distanceTestA.clone(); //the clone of the first genome
    
    assert Genome.getHammingDistance(distanceTestA, distanceTestB) == 2000; //This tests the two completely different genomes
    assert Genome.getHammingDistance(cloneA, distanceTestA) == 0; //This tests the cloned Genomes
    assert Genome.getHammingDistance(distanceTestA, distanceTestC) == 1;//This is the test for the genomes with one difference in the DNA
    assert Genome.getHammingDistance(distanceTestB, distanceTestD) == 2;//This is the test for the genomes with two differences in the DNA
  }
  
  private void testCrossover()
  {
    Triangle t1 = new Triangle(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    Triangle t2 = new Triangle(new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
    Triangle t3 = new Triangle(new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2});
    Triangle t4 = new Triangle(new int[] {3, 3, 3, 3, 3, 3, 3, 3, 3, 3});
    Triangle t5 = new Triangle(new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4, 4});
    Triangle t6 = new Triangle(new int[] {5, 5, 5, 5, 5, 5, 5, 5, 5, 5});

    Triangle t2Crosst5 = new Triangle(new int[] {1, 1, 4, 4, 4, 4, 4, 4, 4, 4});
    Triangle t5Crosst2 = new Triangle(new int[] {4, 4, 1, 1, 1, 1, 1, 1, 1, 1});
    
    Genome parent1 = new Genome(t1, t2, t3);
    Genome parent2 = new Genome(t4, t5, t6);
    Genome child1 = new Genome();
    Genome child2 = new Genome();
    
    Genome expectedChild1 = new Genome(t1, t2Crosst5, t6);
    Genome expectedChild2 = new Genome(t4, t5Crosst2, t3);

    Genome.singlePointCrossOver(parent1, parent2, child1, child2, 12);
    
    assert child1.equals(expectedChild1);
    assert child2.equals(expectedChild2);
    
    assert !child1.equals(child2);
    
    assert !child1.equals(parent1);
    assert !child1.equals(parent2);
    assert !child2.equals(parent1);
    assert !child2.equals(parent2);
    
    return;
  }

  public static void main(String[] args)
  {
    new AssertTests();
  }
}
