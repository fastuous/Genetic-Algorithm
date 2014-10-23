package trianglegenome;

import java.awt.image.BufferedImage;
import java.util.List;
import trianglegenome.util.Constants;

public class HillClimbing extends Thread
{
  private int fitnessBefore = 0;
  private int fitnessAfter = 0;
  private int successfulDNA = Constants.rand.nextInt(10);
  private int successfulMultiplier = 1;
  private int evenOrOdd = Constants.rand.nextInt(100000) % 2;
  private int stepSize = Constants.rand.nextInt(2)+1;
  private int triangle = Constants.rand.nextInt(Constants.TRIANGLE_COUNT);
  private int upperBound;
  private int lowerBound = 0;
  private BufferedImage targetImage;

  private FitnessEvaluator fitnessEvaluator;
  private List<GenomeDrawPanelPair> genomeStates;
  private volatile boolean paused = false;

  public HillClimbing(List<GenomeDrawPanelPair> genomeStates, BufferedImage target)
  {
    targetImage = target;
    fitnessEvaluator = new FitnessEvaluator(targetImage);
    this.genomeStates = genomeStates;
  }

  @Override
  public void run()
  {
    // performEvolution on all genomeStates
    while (!super.isInterrupted())
    {
      if (!this.paused)
      {
        for (GenomeDrawPanelPair state : genomeStates)
        {
          performEvolution(state);
        }
      }
      else
      {
        try
        {
          synchronized (this) { this.notify(); }
          Thread.sleep(100);
        }
        catch (Exception e)
        {
          this.pause();
          e.printStackTrace();
        }
      }
    }
  }

  public void pause()
  {
    this.paused = true;
  }

  public void unpause()
  {
    this.paused = false;
  }

  public boolean isPaused()
  {
    if (this.paused) return true;
    return false;
  }
  
  public void performOneEvolution()
  {
    if(isPaused())
    {
      for (GenomeDrawPanelPair state : genomeStates)
      {
        performEvolution(state);
      }
    }
  }
  
  public void evolve(Genome genome)
  {
      if (successfulDNA >= 0 && successfulDNA < 3) upperBound = Constants.width;
      else if (successfulDNA >= 3 && successfulDNA < 6) upperBound = Constants.height;
      else if (successfulDNA >= 6 && successfulDNA < 10) upperBound = Constants.MAX_RGBA;
    synchronized (genome.getGenes())
    {
      List<Triangle> genes = genome.getGenes();
      Triangle tri = genes.get(triangle);
      int location = genes.indexOf(tri);
      do
      {
        if (evenOrOdd == 0)
        {
          tri.dna[successfulDNA] += successfulMultiplier * stepSize;
        }
        else
        {
          tri.dna[successfulDNA] -= successfulMultiplier * stepSize;
        }
        if(tri.dna[successfulDNA] > upperBound || tri.dna[successfulDNA] < lowerBound)
        {
          if (evenOrOdd == 1)
          {
            tri.dna[successfulDNA] += successfulMultiplier * stepSize;
          }
          else
          {
            tri.dna[successfulDNA] -= successfulMultiplier * stepSize;
          }
          evenOrOdd = (evenOrOdd+1) % 2;
          successfulMultiplier = 1;
          successfulDNA = Constants.rand.nextInt(10);
          evenOrOdd = Constants.rand.nextInt(100000) % 2;
          stepSize = Constants.rand.nextInt(2)+1;
          triangle = Constants.rand.nextInt(Constants.TRIANGLE_COUNT);
        }
      } while (tri.dna[successfulDNA] > upperBound || tri.dna[successfulDNA] < lowerBound);
      genes.set(location, tri);
    }
  }
  
  public void devolve(Genome genome)
  {
    synchronized (genome.getGenes())
    {
      List<Triangle> genes = genome.getGenes();
      Triangle tri = genes.get(triangle);
      int location = genes.indexOf(tri);
      if (evenOrOdd == 1)
      {
        tri.dna[successfulDNA] += successfulMultiplier * stepSize;
      }
      else
      {
        tri.dna[successfulDNA] -= successfulMultiplier * stepSize;
      }
      genes.set(location, tri);
    }
  }
  
  public void performEvolution(GenomeDrawPanelPair genomeState)
  {
    genomeState.drawPanel.setTriangles(genomeState.genome.getGenes());
    BufferedImage drawPanelSnapshot = genomeState.drawPanel.getSnapshot();
    fitnessBefore = fitnessEvaluator.differenceSum(drawPanelSnapshot);
    do
    {
      evolve(genomeState.genome);
      genomeState.drawPanel.setTriangles(genomeState.genome.getGenes());
      drawPanelSnapshot = genomeState.drawPanel.getSnapshot();
      fitnessAfter = fitnessEvaluator.differenceSum(drawPanelSnapshot);
      if (fitnessAfter > fitnessBefore)
      {
        devolve(genomeState.genome);
        successfulMultiplier = 1;
        successfulDNA = Constants.rand.nextInt(10);
        evenOrOdd = Constants.rand.nextInt(100000) % 2;
        stepSize = Constants.rand.nextInt(2)+1;
        triangle = Constants.rand.nextInt(Constants.TRIANGLE_COUNT);
      }
    }
    while (fitnessAfter > fitnessBefore);
    successfulMultiplier += .5;
  }
}