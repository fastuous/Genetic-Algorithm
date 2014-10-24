package trianglegenome.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.swing.JPanel;

import trianglegenome.Triangle;
import trianglegenome.util.Constants;

/**
 * A panel which extends {@link JPanel} and uses a {@link VolatileImage} to
 * to render graphics.
 * <br /><br />
 * Example code:<br/>
 * <code><pre>
 *  // Assume DrawPanel p is initialized.
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
public abstract class DrawPanel extends JPanel
{
  /** The version of this draw panel (starting with 1). */
  protected static final long serialVersionUID = 1L;

  /** An {@link Image} that this panel will
   * draw in the {@link DrawPanel#paintComponent(Graphics)} method.
   * method */
  protected Image offscreenBuffer;
  
  /** The graphics of the {@link DrawPanel#offscreenBuffer} */
  protected Graphics2D offscreenGraphics;
  
  /** An image for use with {@link #getFXImage()} to get the triangles as
   * a WritableImage. */
  protected WritableImage fxImage;
  
  /** The GraphicsConfiguration used to validate the
   * {@link DrawPanel#offscreenBuffer} */
  protected GraphicsConfiguration graphicsConfiguration;
  
  /** Determines how many triangles will be drawn on the
   * {@link DrawPanel} */
  protected int triangleDrawLimit = Constants.TRIANGLE_COUNT;
  
  /** A {@link List} of {@link Triangle} objects for drawing on the
   * {@link DrawPanel#offscreenBuffer} */
  protected List<Triangle> triangles;
  
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
    
    fxImage = SwingFXUtils.toFXImage(getSnapshot(), null);
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
   * Returns a snapshot containing all of the triangles.
   * @return A snapshot containing all of the triangles.
   */
  public abstract BufferedImage getSnapshot();
  
  /**
   * Returns the triangles painted to a {@link javafx.scene.image.Image}.
   * @return The triangles painted to a {@link javafx.scene.image.Image}.
   */
  public abstract Image getFXImage();
  
  /**
   * Initializes the {@link DrawPanel#offscreenBuffer}
   * @param width The width of the offscreen buffer. 
   * @param height The height of the offscreen buffer. 
   */
  protected abstract void createOffScreenBuffer(int width, int height);
  
  /**
   * Paints each of the triangles on the {@link DrawPanel#offscreenBuffer}.
   */
  protected abstract void updateOffScreenBuffer();
  
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
  
  /**
   * Updates the DrawPanel only in the specified bound
   * @param bound The bound in the DrawPanel to update.
   */
  public void updateRegion(Rectangle bound)
  {
    offscreenGraphics.setClip(bound);
    offscreenGraphics.clearRect(bound.x, bound.y, bound.width, bound.height);
    
    Predicate<Triangle> intersectsBound = t -> t.getBoundingBox().intersects(bound);
    
    triangles
        .stream()
        .filter(intersectsBound)
        .forEach(t -> drawTriangle(t));
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
  }
}
