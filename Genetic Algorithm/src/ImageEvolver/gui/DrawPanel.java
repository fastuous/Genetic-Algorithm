package ImageEvolver.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import ImageEvolver.Triangle;

/**
 * A panel which extends {@link JPanel} and uses a {@link VolatileImage} to
 * to render graphics.
 * <br /><br />
 * Example code:
 * <code><br /> <br />
 * DrawPanel p = new DrawPanel(640, 480);<br />
 * <br />
 * // Add DrawPanel p to a JFrame or something similar.<br />
 * <br />
 * Triangle t = new Triangle(new int[] {5, 5, 20}, new int[] {5, 20, 20}, 0, 0, 0, 255);<br />
 * p.setTriangleDrawLimit(200); // Default value is 0<br />
 * <br />
 * p.getTirangles().add(t);<br />
 * p.repaint(); // Must manually repaint after adding a triangle.<br />
 * <br />
 * t.dna[0] = 10;<br />
 * p.repaint(); // Must manually repaint after changing a triangle.<br />
 * <br />
 * List&lt;Triangle&gt; ts = new ArrayList&lt;Triangle&gt;();<br /> 
 * ts.add(t);<br />
 * p.setTriangles(ts); // Automatically repaints the DrawPanel<br />
 * <br />
 * <br />
 * </code>
 * 
 * @author David Collins
 */
public class DrawPanel extends JPanel
{
  /** The version of this draw panel (starting with 1). */
  private static final long serialVersionUID = 1L;

  /** A {@link VolatileImage} that this panel will
   * draw in the {@link DrawPanel#paintComponent(Graphics)} method.
   * method */
  private VolatileImage offscreenBuffer;
  
  /** The GraphicsConfiguration used to validate the
   * {@link DrawPanel#offscreenBuffer} */
  private GraphicsConfiguration graphicsConfiguration;
  
  /** Determines how many triangles will be drawn on the
   * {@link DrawPanel} */
  private int triangleDrawLimit = 0;
  
  /** A {@link List} of {@link Triangle} objects for drawing on the
   * {@link DrawPanel#offscreenBuffer} */
  private List<Triangle> triangles;
  
  /**
   * Creates a new {@link DrawPanel} with a given width and height.
   * @param width The width of the draw panel.
   * @param height The height of the draw panel.
   */
  public DrawPanel(int width, int height)
  {
    super();
    super.setSize(width, height);
    super.setPreferredSize(new Dimension(width, height));
    
    triangles = new ArrayList<Triangle>();
    createOffScreenBuffer(width, height);
    graphicsConfiguration = super.getGraphicsConfiguration();
  }
  
  /**
   * Sets {@link DrawPanel#triangles} to the given value. 
   * @param triangles A list of triangles to draw on the DrawPanel.
   */
  public void setTriangles(List<Triangle> triangles)
  {
    this.triangles = triangles;
    this.repaint();
  }
  
  /**
   * Gets the field {@link DrawPanel#triangles} which is a list of triangles
   * that this {@link DrawPanel} is currently drawing.
   * @return The field {@link DrawPanel#triangles} which is a list of triangles
   * that this {@link DrawPanel} is currently drawing.
   */
  public List<Triangle> getTriangles() { return triangles; }
  
  /**
   * Sets the number of triangles from {@link DrawPanel#triangles}
   * (set by {@link DrawPanel#setTriangles(List)}) that this DrawPanel will draw.
   * @param count The number of triangles to draw.
   */
  public void setTriangleDrawLimit(int count)
  {
    triangleDrawLimit = count;
    this.repaint();
  }
  
  /**
   * Returns the number of triangles that this DrawPanel is currently
   * drawing.
   * @return The number of triangles that this DrawPanel is currently
   * drawing.
   */
  public int getTriangleDrawLimit() { return triangleDrawLimit; }
  
  /**
   * Initializes the {@link DrawPanel#offscreenBuffer}
   * @param width The width of the offscreen buffer. 
   * @param height The height of the offscreen buffer. 
   */
  private void createOffScreenBuffer(int width, int height)
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    offscreenBuffer = gc.createCompatibleVolatileImage(width, height);
    updateOffScreenBuffer();
  }
  
  /**
   * Paints each of the triangles on the {@link DrawPanel#offscreenBuffer}.
   */
  private void updateOffScreenBuffer()
  {
    Graphics2D offscreenGraphics = offscreenBuffer.createGraphics();
    offscreenGraphics.clearRect(0, 0, getWidth(), getHeight());
    triangles
        .stream()
        .limit(triangleDrawLimit)
        .forEach(t -> drawTriangle(offscreenGraphics, t));
    
    offscreenGraphics.dispose();
  }
  
  /**
   * Draws a given {@link Triangle} on the given {@link Graphics2D}.
   * @param t The triangle to draw on the given Graphics2D.
   * @param g The Graphics2D on which to draw the triangle.
   */
  private void drawTriangle(Graphics2D g, Triangle t)
  {
    Color c = new Color(t.dna[6], t.dna[7], t.dna[8], t.dna[9]);
    g.setColor(c);
    int [] xs = Arrays.copyOfRange(t.dna, 0, 3);
    int [] ys = Arrays.copyOfRange(t.dna, 3, 6);
    Polygon p = new Polygon(xs, ys, 3);
    g.draw(p);
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
    
    do
    {
      if (offscreenBuffer == null) createOffScreenBuffer(getWidth(), getHeight());
      
      int validationCode = offscreenBuffer.validate(graphicsConfiguration);
      if (validationCode == VolatileImage.IMAGE_INCOMPATIBLE)
      {
        createOffScreenBuffer(getWidth(), getHeight());
      }
      
      g.drawImage(offscreenBuffer, 0, 0, this);
      
    } while (offscreenBuffer.contentsLost());
  }
}
