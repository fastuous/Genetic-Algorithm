package trianglegenome.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 *  DrawPanel p = new DrawPanel(640, 480);
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
public class DrawPanel extends JPanel
{
  /** The version of this draw panel (starting with 1). */
  private static final long serialVersionUID = 1L;

  /** A {@link VolatileImage} that this panel will
   * draw in the {@link DrawPanel#paintComponent(Graphics)} method.
   * method */
  private VolatileImage offscreenBuffer;
  
  /** The graphics of the {@link DrawPanel#offscreenBuffer} */
  private Graphics2D offscreenGraphics;
  
  /** An image for use with {@link #getFXImage()} to get the triangles as
   * a WritableImage. */
  private WritableImage fxImage;
  
  /** The GraphicsConfiguration used to validate the
   * {@link DrawPanel#offscreenBuffer} */
  private GraphicsConfiguration graphicsConfiguration;
  
  /** Determines how many triangles will be drawn on the
   * {@link DrawPanel} */
  private int triangleDrawLimit = Constants.TRIANGLE_COUNT;
  
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
  public BufferedImage getSnapshot()
  {
    int oldTriangleDrawLimit = triangleDrawLimit;
    
    triangleDrawLimit = Constants.TRIANGLE_COUNT;
    updateOffScreenBuffer();
    BufferedImage snapshot = offscreenBuffer
        .getSnapshot()
        .getSubimage(0, 0, Constants.width, Constants.height);
    
    triangleDrawLimit = oldTriangleDrawLimit;
    updateOffScreenBuffer();
    
    BufferedImage croppedSnapshot = new BufferedImage(
        Constants.width, Constants.height, BufferedImage.TYPE_INT_RGB);
    
    croppedSnapshot.getGraphics().drawImage(snapshot, 0, 0, null);
    
    return croppedSnapshot;
  }
  
  /**
   * Returns the triangles painted to a {@link javafx.scene.image.Image}.
   * @return The triangles painted to a {@link javafx.scene.image.Image}.
   */
  public Image getFXImage()
  {
    updateOffScreenBuffer();
    
    BufferedImage snapshot = offscreenBuffer
        .getSnapshot()
        .getSubimage(0, 0, Constants.width, Constants.height);
    
    BufferedImage croppedSnapshot = new BufferedImage(
        Constants.width, Constants.height, BufferedImage.TYPE_INT_RGB);
    
    croppedSnapshot.getGraphics().drawImage(snapshot, 0, 0, null);
    
    return SwingFXUtils.toFXImage(croppedSnapshot, fxImage);
  }
  
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
    offscreenGraphics = offscreenBuffer.createGraphics();
  }
  
  /**
   * Paints each of the triangles on the {@link DrawPanel#offscreenBuffer}.
   */
  private void updateOffScreenBuffer()
  {
    do
    {
      if (offscreenBuffer == null) createOffScreenBuffer(getWidth(), getHeight());
      
      int validationCode = offscreenBuffer.validate(graphicsConfiguration);
      if (validationCode == VolatileImage.IMAGE_INCOMPATIBLE)
      {
        createOffScreenBuffer(getWidth(), getHeight());
      }

      if (offscreenBuffer.contentsLost()) continue; 
      
      offscreenGraphics.clearRect(0, 0, getWidth(), getHeight());
      synchronized (triangles)
      {
        int trianglesDrawn = 0;
        for (Triangle t : triangles)
        {
          if (trianglesDrawn == triangleDrawLimit) break;
          drawTriangle(offscreenGraphics, t);
          trianglesDrawn++;
        }
      }
      
    } while (offscreenBuffer.contentsLost());
    
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
    g.fillPolygon(xs, ys, 3);
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
