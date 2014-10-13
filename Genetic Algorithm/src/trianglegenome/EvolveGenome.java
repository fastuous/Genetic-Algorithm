package trianglegenome;

public interface EvolveGenome
{
  public Genome Evolve(Genome genome, int randomTriangle, int dna, int stepSize);
}
