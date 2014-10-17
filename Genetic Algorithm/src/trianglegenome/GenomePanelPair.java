package trianglegenome;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import trianglegenome.gui.DrawPanel;

/**
 * A class to keep track of a mapping between a {@link trianglegenome.Genome} and a
 * {@link trianglegenome.gui.DrawPanel}. This class also has another Genome member to
 * keep track of a previous state.
 * <br /><br />
 * Example code:<br/>
 * <code><pre>
 *  Genome g = RandomGenome.generateGenome();
 *  DrawPanel d = new DrawPanel(640, 480);
 *  d.setTriangles(g.getGenes());
 *  d.repaint();
 *  
 *  GenomePanelPair p = new GenomePanelPair(g, d);
 *  
 *  // Mutate the Genome g
 *  // p.genome is now mutated but p.previous is preserved.
 *  
 *  p.drawPanel.setTriangles(g.getGenes());
 *  p.drawPanel.repaint();
 *  
 *  // Check if the mutation was good and if it was do the following:
 *  p.previous.copyFrom(p.genome);
 *  
 * </pre></code>
 * @author collinsd
 */
public class GenomePanelPair
{
  /** A Genome meant to keep track of the previous state.
   * Use {@link trianglegenome.Genome#clone() to set this initially. */
  public Genome previous;
  
  /** The {@link trianglegenome.Genome} in the genome to draw panel mapping */
  public Genome genome;
  
  /** The {@link trianglegenome.gui.DrawPanel} in the genome to draw panel mapping */
  public DrawPanel drawPanel;
  
  /**
   * Creates a new GenomePanelPair where {@link GenomePanelPair#previous}
   * is a deep copy of {@link GenomePanelPair#genome}.
   * @param genome The Genome in the Genome/DrawPanel mapping.
   * @param drawPanel The DrawPanel in the Genome/DrawPanel mapping. 
   */
  public GenomePanelPair(Genome genome, DrawPanel drawPanel)
  {
    this.genome = genome;
    this.previous = genome.clone();
    this.drawPanel = drawPanel;
  }
  
  /**
   * Given two lists of equal length, one of {@link trianglegenome.Genome} objects and the
   * other of {@link trianglegenome.gui.DrawPanel} objects, create a list of
   * {@link trianglegenome.GenomePanelPair} objects.
   * @param genomes The Genome objects to zip.
   * @param drawPanels The DrawPanel objects to zip.
   * @return A list of Genomes and DrawPanels zipped together into a list of GenomePanelPairs.
   */
  static List<GenomePanelPair> zip(List<Genome> genomes, List<DrawPanel> drawPanels)
  {
    if (genomes.size() != drawPanels.size())
    {
      throw new IllegalArgumentException("The genomes and drawPanels lists must be the same size");
    }
    
    Iterator<Genome> genomeIt = genomes.iterator();
    Iterator<DrawPanel> drawPanelIt = drawPanels.iterator();
    List<GenomePanelPair> zipped = new ArrayList<GenomePanelPair>(genomes.size());
    
    while (genomeIt.hasNext())
    {
      zipped.add(new GenomePanelPair(genomeIt.next(), drawPanelIt.next()));
    }
    return zipped;
  }
}
