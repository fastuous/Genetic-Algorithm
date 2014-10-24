package trianglegenome;

import java.awt.image.BufferedImage;
import java.util.List;
import trianglegenome.util.Constants;

/**
 * Performs Hill Climbing on a specific genome. Hill climbing is a thread that
 * runs on a genome
 * 
 * @author masonbanning
 *
 */
public class HillClimbing extends Thread
{
  private int fitnessBefore = 0;
  private int fitnessAfter = 0;
  private int successfulDNA = Constants.rand.nextInt(10);
  private int successfulMultiplier = 1;
  private int evenOrOdd = Constants.rand.nextInt(100000) % 2; // used for
                                                              // switching
                                                              // between
                                                              // operations
  private int stepSize = Constants.rand.nextInt(2) + 1;
  private int triangle = Constants.rand.nextInt(Constants.TRIANGLE_COUNT);
  private BufferedImage targetImage;
  private FitnessEvaluator fitnessEvaluator;
  private List<GenomeDrawPanelPair> genomeStates; // List of Genome/DrawPanel
                                                  // pair
  private volatile boolean paused = false;
  private volatile boolean interrupted = false;
  private volatile long stepCount;

  /**
   * This is the constructor for the hill climbing thread.
   * 
   * @param genomeStates
   * @param target
   */
  public HillClimbing(List<GenomeDrawPanelPair> genomeStates, BufferedImage target)
  {
    super("HillClimbing-Thread");
    targetImage = target;
    fitnessEvaluator = new FitnessEvaluator(targetImage);
    this.genomeStates = genomeStates;
    stepCount = 0;
  }

  /*
   * (non-Javadoc) performs evolutions on a genome and catched an interrupted
   * exception
   * 
   * @see java.lang.Thread#run()
   */
  @Override
  public void run()
  {
    // performEvolution on all genomeStates
    while (!interrupted)
    {
      if (!this.paused)
      {
        for (GenomeDrawPanelPair state : genomeStates)
        {
          if (super.isInterrupted()) break;
          performEvolution(state);
        }
      }
      else
      {
        try
        {
          synchronized (this)
          {
            this.notify();
          }
          Thread.sleep(100);
        }
        catch (Exception e)
        {
        }
      }
    }
    synchronized (this)
    {
      this.notify();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Thread#interrupt()
   */
  public void interrupt()
  {
    interrupted = true;
  }

  /**
   * Pauses the current hill climbing thread
   */
  public void pause()
  {
    this.paused = true;
  }

  /**
   * unpauses the current hill climbing thread
   */
  public void unpause()
  {
    this.paused = false;
  }

  /**
   * this method returns true if the thread is paused or false if it isn't.
   * 
   * @return
   */
  public boolean isPaused()
  {
    if (this.paused) return true;
    return false;
  }

  /**
   * returns the current generation
   * 
   * @return
   */
  public long getGenerationCount()
  {
    return stepCount;
  }

  /**
   * performs a single evolution on a genome if the thread is paused.
   */
  public void performOneEvolution()
  {
    stepCount++;
    if (isPaused())
    {
      for (GenomeDrawPanelPair state : genomeStates)
      {
        performEvolution(state);
      }
    }
  }

  /**
   * Performs an evolution on the selected genome
   * 
   * @param genome
   */
  public void evolve(Genome genome)
  {
    synchronized (genome.getGenes())
    {
      List<Triangle> genes = genome.getGenes();
      Triangle tri = genes.get(triangle);// triangle to evolve
      int val;
      int newVal;
      int upperBound;
      int lowerBound;
      do
      {
        if (interrupted) return;
        val = tri.dna[successfulDNA];
        upperBound = 0;
        lowerBound = 0;
        if (successfulDNA >= 0 && successfulDNA < 3)
          upperBound = Constants.width;
        else if (successfulDNA >= 3 && successfulDNA < 6)
          upperBound = Constants.height;
        else if (successfulDNA >= 6 && successfulDNA < 10) upperBound = Constants.MAX_RGBA;
        if (evenOrOdd == 0)
          newVal = val + successfulMultiplier * stepSize;
        else
          newVal = val - successfulMultiplier * stepSize;
        if (newVal > upperBound || newVal < lowerBound)// checks to see if the
                                                       // selected value is
                                                       // out-of-bounds
        {
          //changes paramaters before grabbing a new value to check.
          evenOrOdd = (evenOrOdd + 1) % 2;
          successfulMultiplier = 1;
          successfulDNA = Constants.rand.nextInt(10);
          evenOrOdd = Constants.rand.nextInt(100000) % 2;
          stepSize = Constants.rand.nextInt(2) + 1;
          triangle = Constants.rand.nextInt(Constants.TRIANGLE_COUNT);
        }
        else
          tri.dna[successfulDNA] = newVal;//keep the evolution
      }
      while (newVal > upperBound || newVal < lowerBound);
    }
  }

  /**
   * This method undoes an unsuccessful evolution
   * @param genome
   */
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
      if (successfulDNA >= 0 && successfulDNA < 3)
        upperBound = Constants.width;
      else if (successfulDNA >= 3 && successfulDNA < 6)
        upperBound = Constants.height;
      else if (successfulDNA >= 6 && successfulDNA < 10) upperBound = Constants.MAX_RGBA;
      if (evenOrOdd == 0)
        newVal = val - successfulMultiplier * stepSize;
      else
        newVal = val + successfulMultiplier * stepSize;
      if (!(newVal > upperBound || newVal < lowerBound)) tri.dna[successfulDNA] = newVal;
    }
  }

  /**
   * This function performs evolutions on genomes, and, if they aren't successful, 
   * devolves them and tries again with new values 
   * @param genomeState
   */
  public void performEvolution(GenomeDrawPanelPair genomeState)
  {
    genomeState.drawPanel.setTriangles(genomeState.genome.getGenes());
    BufferedImage drawPanelSnapshot = genomeState.drawPanel.getSnapshot();
    fitnessBefore = fitnessEvaluator.differenceSum(drawPanelSnapshot);
    genomeState.genome.setFitness(fitnessBefore);
    do
    {
      if (interrupted) return;
      evolve(genomeState.genome);
      Triangle t = genomeState.genome.getGenes().get(triangle);
      genomeState.drawPanel.updateRegion(t.getBoundingBox());
      genomeState.drawPanel.repaint();
      drawPanelSnapshot = genomeState.drawPanel.getSnapshot();
      fitnessAfter = fitnessEvaluator.differenceSum(drawPanelSnapshot);
      if (fitnessAfter > fitnessBefore)
      {
        devolve(genomeState.genome);
        successfulMultiplier = 1;
        successfulDNA = Constants.rand.nextInt(10);
        evenOrOdd = Constants.rand.nextInt(100000) % 2;
        stepSize = Constants.rand.nextInt(2) + 1;
        triangle = Constants.rand.nextInt(Constants.TRIANGLE_COUNT);
      }
    }
    while (fitnessAfter >= fitnessBefore);
    genomeState.genome.setFitness(fitnessAfter);
    successfulMultiplier += .5;
    stepCount++;
  }
}