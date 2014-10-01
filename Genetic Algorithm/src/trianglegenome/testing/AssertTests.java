/*
 * @author masonbanning
 */
package trianglegenome.testing;

import trianglegenome.Genome;
import trianglegenome.Triangle;
import trianglegenome.util.Constants;

public class AssertTests
{
  public AssertTests()
  {
    // TODO Auto-generated constructor stub
  }

  public boolean checkValidTriangle(Triangle tri)
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
  public boolean checkValidGenome(Genome genome)
  {
    
  }

  public static void main(String[] args)
  {
    // TODO Auto-generated method stub
  }
}
