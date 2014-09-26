package ImageEvolver;

import java.util.List;
import java.util.LinkedList;

public class Genome
{
  private List<Triangle> genes = new LinkedList<>();
  
  public void addGene(Triangle gene)
  {
    genes.add(gene);
  }
  
  public List<Triangle> getGenes() {
    return genes;
  }
}
