package trianglegenome.gui;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

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
 *  DrawPanel p = new DrawPanelVolatileImage(640, 480);
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
public class DrawPanelVolatileImage extends DrawPanel
{
  private static final long serialVersionUID = 1;
  
  /** A {@link VolatileImage} that this panel will
   * draw in the {@link DrawPanelBufferedImage#paintComponent(Graphics)} method.
   * method */
  protected VolatileImage offscreenBuffer;
  
  /**
   * Creates a new {@link DrawPanelVolatileImage} with a given width and height.
   * @param width The width of the draw panel.
   * @param height The height of the draw panel.
   */
  public DrawPanelVolatileImage(int width, int height)
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
   * Initializes the {@link DrawPanelVolatileImage#offscreenBuffer}
   * @param width The width of the offscreen buffer. 
   * @param height The height of the offscreen buffer. 
   */
  protected void createOffScreenBuffer(int width, int height)
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    offscreenBuffer = gc.createCompatibleVolatileImage(width, height);
    offscreenGraphics = offscreenBuffer.createGraphics();
  }
  
  /**
   * Paints each of the triangles on the {@link DrawPanelVolatileImage#offscreenBuffer}.
   */
  protected void updateOffScreenBuffer()
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
          drawTriangle(t);
          trianglesDrawn++;
        }
      }
      
    } while (offscreenBuffer.contentsLost());
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
