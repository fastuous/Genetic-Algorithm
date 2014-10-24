package trianglegenome.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Arrays;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.swing.JPanel;

import trianglegenome.Triangle;
import trianglegenome.util.Constants;

/**
 * A panel which extends {@link JPanel} and uses a {@link VolatileImage} to
 * to render graphics.
 * <br /><br />
 * Example code:<br/>
 * <code><pre>
 *  DrawPanel p = new DrawPanelBufferedImage(640, 480);
 *  
 *  // Add DrawPanel p to a JFrame or something similar.
 *  
 *  Triangle t = new Triangle(new int[] {5, 5, 20}, new int[] {5, 20, 20}, 0, 0, 0, 255);
 *  p.setTriangleDrawLimit(200); // Default value is 0
 *  
 *  p.getTirangles().add(t);
 *  p.repaint(); // Must manually repaint after adding a triangle.
 *  
 *  t.dna[0] = 10;
 *  p.repaint(); // Must manually repaint after changing a triangle.
 *  
 *  List&lt;Triangle&gt; ts = new ArrayList&lt;Triangle&gt;(); 
 *  ts.add(t);
 *  p.setTriangles(ts); // Automatically repaints the DrawPanel
 *  
 *  
 * </pre></code>
 * 
 * @author David Collins
 */
public class DrawPanelBufferedImage extends DrawPanel
{
  /** The version of this draw panel (starting with 1). */
  private static final long serialVersionUID = 1L;

  /** A {@link BufferedImage} that this panel will
   * draw in the {@link DrawPanelBufferedImage#paintComponent(Graphics)} method.
   * method */
  private BufferedImage offscreenBuffer;
  
  /**
   * Creates a new {@link DrawPanelBufferedImage} with a given width and height.
   * @param width The width of the draw panel.
   * @param height The height of the draw panel.
   */
  public DrawPanelBufferedImage(int width, int height)
  {
    super(width, height);
  }
  
  /**
   * Returns a snapshot containing all of the triangles.
   * @return A snapshot containing all of the triangles.
   */
  public BufferedImage getSnapshot()
  {
    int oldTriangleDrawLimit = triangleDrawLimit;
    
    triangleDrawLimit = Constants.TRIANGLE_COUNT;
    updateOffScreenBuffer();
    
    triangleDrawLimit = oldTriangleDrawLimit;
    updateOffScreenBuffer();
    
    BufferedImage croppedSnapshot = new BufferedImage(
        Constants.width, Constants.height, BufferedImage.TYPE_INT_RGB);
    
    croppedSnapshot.getGraphics().drawImage(offscreenBuffer, 0, 0, null);
    
    return croppedSnapshot;
  }
  
  /**
   * Returns the triangles painted to a {@link javafx.scene.image.Image}.
   * @return The triangles painted to a {@link javafx.scene.image.Image}.
   */
  public Image getFXImage()
  {
    
    BufferedImage croppedSnapshot = new BufferedImage(
        Constants.width, Constants.height, BufferedImage.TYPE_INT_RGB);
    
    croppedSnapshot.getGraphics().drawImage(offscreenBuffer, 0, 0, null);
    
    return SwingFXUtils.toFXImage(croppedSnapshot, fxImage);
  }
  
  /**
   * Initializes the {@link DrawPanelBufferedImage#offscreenBuffer}
   * @param width The width of the offscreen buffer. 
   * @param height The height of the offscreen buffer. 
   */
  protected void createOffScreenBuffer(int width, int height)
  {
    offscreenBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    offscreenGraphics = (Graphics2D) offscreenBuffer.getGraphics();
  }
  
  /**
   * Paints each of the triangles on the {@link DrawPanelBufferedImage#offscreenBuffer}.
   */
  protected void updateOffScreenBuffer()
  {
    offscreenGraphics.clearRect(0, 0, getWidth(), getHeight());
    synchronized (triangles)
    {
      int trianglesDrawn = 0;
      for (Triangle t : triangles)
      {
        if (trianglesDrawn == triangleDrawLimit) break;
        drawTriangle(t);
        trianglesDrawn++;
      }
    }
  }
  
  /**
   * Draws a given {@link Triangle} on the given {@link Graphics2D}.
   * @param t The triangle to draw on the given Graphics2D.
   */
  protected void drawTriangle(Triangle t)
  {
    Color c = new Color(t.dna[6], t.dna[7], t.dna[8], t.dna[9]);
    offscreenGraphics.setColor(c);
    int [] xs = Arrays.copyOfRange(t.dna, 0, 3);
    int [] ys = Arrays.copyOfRange(t.dna, 3, 6);
    offscreenGraphics.fillPolygon(xs, ys, 3);
  }
  
  /*
   * (non-Javadoc)
   * @see java.awt.Component#repaint()
   */
  @Override
  public void repaint()
  {
    if (offscreenBuffer != null) updateOffScreenBuffer();
    super.repaint();
  }
  
  /*
   * (non-Javadoc)
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    g.drawImage(offscreenBuffer, 0, 0, this);
  }
}
