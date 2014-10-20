package trianglegenome;

import trianglegenome.gui.DrawPanel;

/**
 * A class to keep track of a mapping between a {@link trianglegenome.Genome} and a
 * {@link trianglegenome.gui.DrawPanel}.
 * <br /><br />
 * Example code:<br/>
 * <code><pre>
 *  Genome g = RandomGenome.generateGenome();
 *  DrawPanel d = new DrawPanel(640, 480); 
 *  
 *  GenomeDrawPanelPair gdpp = new GenomeDrawPanelPair(g, d);
 *  
 * </pre></code>
 * @author collinsd
 */
public class GenomeDrawPanelPair
{
  /** A Genome meant to keep track of the previous state.
   * Use {@link trianglegenome.Genome#clone() to set this initially. */
  
  /** The {@link trianglegenome.Genome} in the genome to draw panel mapping */
  public Genome genome;
  
  /** The {@link trianglegenome.gui.DrawPanel} in the genome to draw panel mapping */
  public DrawPanel drawPanel;
  
  /**
   * Creates a new GenomePanelPair where {@link GenomeDrawPanelPair#previous}
   * is a deep copy of {@link GenomeDrawPanelPair#genome}.
   * @param genome The Genome in the Genome/DrawPanel mapping.
   * @param drawPanel The DrawPanel in the Genome/DrawPanel mapping. 
   */
  public GenomeDrawPanelPair(Genome genome, DrawPanel drawPanel)
  {
    this.genome = genome;
    this.drawPanel = drawPanel;
  }
}
