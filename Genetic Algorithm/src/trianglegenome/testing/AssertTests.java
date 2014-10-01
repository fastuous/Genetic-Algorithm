/*
 * @author masonbanning
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
    // TODO Auto-generated constructor stub
  }

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
  public boolean isValidGenome(Genome genome)
  {
    List<Triangle> genes = genome.getGenes();
    for(Triangle tri : genes)
    {
      if(!isValidTriangle(tri)) return false;
    }
    return true;
  }

  public static void main(String[] args)
  {
    
    Constants.height = 413;
    Constants.width = 512;
    Genome distanceTestA = new Genome();
    Genome distanceTestB = new Genome();
    Genome distanceTestC = new Genome();
    Genome distanceTestD = new Genome();
    int[] dna1 = {0,0,0,0,0,0,0,0,0,0};
    int[] dna2 = {1,1,1,1,1,1,1,1,1,1};
    int[] dna3 = {0,1,1,1,1,1,1,1,1,1};
    int[] dna4 = {1,1,0,0,0,0,0,0,0,0};
    for(int i = 0; i < 200; ++i)
    {
      if(i < 100)
      {
        distanceTestA.addGene(new Triangle(dna1));
        distanceTestB.addGene(new Triangle(dna2));
        distanceTestC.addGene(new Triangle(dna1));
        distanceTestD.addGene(new Triangle(dna2));
      }
      else if(i >= 100)
      {
        distanceTestA.addGene(new Triangle(dna2));
        distanceTestB.addGene(new Triangle(dna1));
        if(i != 199){
          distanceTestC.addGene(new Triangle(dna2));
          distanceTestD.addGene(new Triangle(dna1));
        }
      }
    }
    distanceTestC.addGene(new Triangle(dna3));
    distanceTestD.addGene(new Triangle(dna4));
    Genome cloneA = distanceTestA.clone();
    Genome testA = RandomGenome.generateGenome();
    Genome testB = RandomGenome.generateGenome();
    AssertTests test = new AssertTests();
    assert test.isValidGenome(testA);
    assert test.isValidGenome(testB);
    assert Genome.getHammingDistance(distanceTestA, distanceTestB) == 2000;
    assert Genome.getHammingDistance(cloneA, distanceTestA) == 0;
    assert Genome.getHammingDistance(distanceTestA, distanceTestC) == 1;
    assert Genome.getHammingDistance(distanceTestB, distanceTestD) == 2;
  }
}
