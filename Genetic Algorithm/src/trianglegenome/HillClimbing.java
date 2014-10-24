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
  private BufferedImage targetImage;

  private FitnessEvaluator fitnessEvaluator;
  private List<GenomeDrawPanelPair> genomeStates;
  private volatile boolean paused = false;
  
  private volatile long stepCount;

  public HillClimbing(List<GenomeDrawPanelPair> genomeStates, BufferedImage target)
  {
    super("HillClimbing-Thread");
    targetImage = target;
    fitnessEvaluator = new FitnessEvaluator(targetImage);
    this.genomeStates = genomeStates;
    stepCount = 0;
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
        catch (Exception e) {}
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
  
  public long getGenerationCount()
  {
    return stepCount;
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
    synchronized (genome.getGenes())
    {
      List<Triangle> genes = genome.getGenes();
      Triangle tri = genes.get(triangle);
      
      int val;
      int newVal;
      int upperBound;
      int lowerBound;
      
      do
      {
        if (super.isInterrupted() || this.isPaused()) return;
        val = tri.dna[successfulDNA];
        upperBound = 0;
        lowerBound = 0;
        
        if (successfulDNA >= 0 && successfulDNA < 3) upperBound = Constants.width;
        else if (successfulDNA >= 3 && successfulDNA < 6) upperBound = Constants.height;
        else if (successfulDNA >= 6 && successfulDNA < 10) upperBound = Constants.MAX_RGBA;
        
        if (evenOrOdd == 0) newVal = val + successfulMultiplier * stepSize;
        else newVal = val - successfulMultiplier * stepSize;
        if(newVal > upperBound || newVal < lowerBound)
        {
          evenOrOdd = (evenOrOdd+1) % 2;
          successfulMultiplier = 1;
          successfulDNA = Constants.rand.nextInt(10);
          evenOrOdd = Constants.rand.nextInt(100000) % 2;
          stepSize = Constants.rand.nextInt(2)+1;
          triangle = Constants.rand.nextInt(Constants.TRIANGLE_COUNT);
        }
        else tri.dna[successfulDNA] = newVal;
      } while (newVal > upperBound || newVal < lowerBound);
    }
  }
  
  public void devolve(Genome genome)
  {
    synchronized (genome.getGenes())
    {
      List<Triangle> genes = genome.getGenes();
      Triangle tri = genes.get(triangle);
      
      int val = tri.dna[successfulDNA];
      int newVal;
      int upperBound = 0;
      int lowerBound = 0;
      if (successfulDNA >= 0 && successfulDNA < 3) upperBound = Constants.width;
      else if (successfulDNA >= 3 && successfulDNA < 6) upperBound = Constants.height;
      else if (successfulDNA >= 6 && successfulDNA < 10) upperBound = Constants.MAX_RGBA;
      
      if (evenOrOdd == 0) newVal = val - successfulMultiplier * stepSize;
      else newVal = val + successfulMultiplier * stepSize;
      if (!(newVal > upperBound || newVal < lowerBound)) tri.dna[successfulDNA] = newVal;
    }
  }
  
  public void performEvolution(GenomeDrawPanelPair genomeState)
  {
    genomeState.drawPanel.setTriangles(genomeState.genome.getGenes());
    BufferedImage drawPanelSnapshot = genomeState.drawPanel.getSnapshot();
    fitnessBefore = fitnessEvaluator.differenceSum(drawPanelSnapshot);
    genomeState.genome.setFitness(fitnessBefore);
    do
    {
      if (super.isInterrupted() || this.isPaused()) return;
      evolve(genomeState.genome);
      //Triangle t = genomeState.genome.getGenes().get(triangle);
      //genomeState.drawPanel.updateRegion(t.getBoundingBox());
      genomeState.drawPanel.repaint();
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
    } while (fitnessAfter > fitnessBefore);
    genomeState.genome.setFitness(fitnessAfter);
    successfulMultiplier += .5;
    stepCount++;
  }
}