package demo;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Creates a DrawPanel (whose class definition is in the same file) which
 * uses a VolatileImage as an offscreen buffer.
 * @author collinsd
 */
public class VolatileImageTest
{
  JFrame frame = new JFrame("VolatileImage Test");
  DrawPanel panel = new DrawPanel(640, 480);
  
  /**
   * Creates a VolatileImageTest which creates a DrawPanel and draws a
   * series of squares on it.
   * @throws InterruptedException I have no shame in rolling checked
   * exceptions downhill.
   */
  public VolatileImageTest() throws InterruptedException
  {
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.add(panel);
    panel.setVisible(true);
    frame.pack();
    frame.setVisible(true);
    
    for (int x = 5, y = 5; x < 200 && y < 200; x += 5, y += 5)
    {
      panel.addShape(new Rectangle(x, x, 405 - 2*x, 405 - 2*y));
      Thread.sleep(500);
    }
  }
  
  /**
   * Creates a VolatileImageTest
   * @param args ignored
   * @throws InterruptedException If main throws checked exceptions,
   * I guess that means that the user is downhill from here.
   */
  public static void main(String[] args) throws InterruptedException
  {
    new VolatileImageTest();
  }
}

/**
 * A JPanel that uses a VolatileImage. 
 * @author collinsd
 */
@SuppressWarnings("serial")
class DrawPanel extends JPanel
{
  private VolatileImage offscreenBuffer;
  private Collection<Shape> shapes;
  GraphicsConfiguration graphicsConfiguration;
  
  /**
   * Creates a new DrawPanel with the specified width and height.
   * @param width The width of the new DrawPanel.
   * @param height The height of the new DrawPanel.
   */
  public DrawPanel(int width, int height)
  {
    super();
    this.setSize(width, height);
    this.setPreferredSize(new Dimension(width, height));
    createOffScreenBuffer(this.getWidth(), this.getHeight());
    shapes = new ArrayList<Shape>();
    
    graphicsConfiguration = super.getGraphicsConfiguration();
  }
  
  /**
   * Initializes the offscreenBuffer
   * @param width The width of the offscreen buffer. 
   * @param height The height of the offscreen buffer. 
   */
  private void createOffScreenBuffer(int width, int height)
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    offscreenBuffer = gc.createCompatibleVolatileImage(width, height);
  }
  
  /**
   * Adds to the list of shapes to be drawn.
   * @param shape The new shape to be drawn.
   */
  public void addShape(Shape shape)
  {
    shapes.add(shape);
    repaint();
  }

  /**
   * Removes a shape from the list of shapes to be drawn.
   */
  public void removeShape(Shape shape)
  {
    shapes.remove(shape);
    repaint();
  }
  
  /**
   * Sets internal shapes collection to the given collection.
   * @param shapes The new shapes collection that this DrawPanel will use.
   */
  public void setShapes(Collection<Shape> shapes)
  {
    this.shapes = shapes;
    repaint();
  }
  
  /**
   * Returns the shapes collection that this DrawPanel is using.
   * @return The shapes collection that this DrawPanel is using.
   */
  public Collection<Shape> getShapes() { return shapes; }
  
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
      if (offscreenBuffer == null) 
      {
        createOffScreenBuffer(this.getWidth(), this.getHeight());
      }
      
      int validationCode = offscreenBuffer.validate(graphicsConfiguration);
      if (validationCode == VolatileImage.IMAGE_INCOMPATIBLE)
      {
        createOffScreenBuffer(this.getWidth(), this.getHeight());
      }
      
      Graphics2D offscreenGraphics = offscreenBuffer.createGraphics();
      for (Shape s : shapes) offscreenGraphics.draw(s);
      offscreenGraphics.dispose();
      
      g.drawImage(offscreenBuffer, 0, 0, this);
      
    } while (offscreenBuffer.contentsLost());
  }
}