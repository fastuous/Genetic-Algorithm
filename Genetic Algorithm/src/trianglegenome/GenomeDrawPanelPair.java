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
 *  Genome gg = gdpp.genome;
 *  DrawPanel dd = gdpp.drawPanel;
 *  
 * </pre></code>
 * @author collinsd
 */
public class GenomeDrawPanelPair
{
  /** The {@link trianglegenome.Genome} in the genome to draw panel mapping */
  public final Genome genome;
  
  /** The {@link trianglegenome.gui.DrawPanel} in the genome to draw panel mapping */
  public final DrawPanel drawPanel;
  
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
