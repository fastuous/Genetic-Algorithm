package trianglegenome;

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
 *  GenomeState s = new GenomeState(g, d);
 *  
 *  // Mutate the Genome g
 *  // p.genome is now mutated but p.previous is preserved.
 *  
 *  s.drawPanel.setTriangles(g.getGenes());
 *  s.drawPanel.repaint();
 *  
 *  // Check if the mutation was good and if it was do the following:
 *  s.previous.copyFrom(p.genome);
 *  
 * </pre></code>
 * @author collinsd
 */
public class GenomeState
{
  /** A Genome meant to keep track of the previous state.
   * Use {@link trianglegenome.Genome#clone() to set this initially. */
  public Genome previous;
  
  /** The {@link trianglegenome.Genome} in the genome to draw panel mapping */
  public Genome genome;
  
  /** The {@link trianglegenome.gui.DrawPanel} in the genome to draw panel mapping */
  public DrawPanel drawPanel;
  
  /**
   * Creates a new GenomePanelPair where {@link GenomeState#previous}
   * is a deep copy of {@link GenomeState#genome}.
   * @param genome The Genome in the Genome/DrawPanel mapping.
   * @param drawPanel The DrawPanel in the Genome/DrawPanel mapping. 
   */
  public GenomeState(Genome genome, DrawPanel drawPanel)
  {
    this.genome = genome;
    this.previous = genome.clone();
    this.drawPanel = drawPanel;
  }
}
