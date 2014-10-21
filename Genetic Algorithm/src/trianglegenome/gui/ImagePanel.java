package trianglegenome.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import trianglegenome.util.Constants;

/**
 * A custom JPanel that draws the current target image.
 * 
 * @author Truman DeYoung
 */
public class ImagePanel extends JPanel
{
  private static final long serialVersionUID = 2L;

  private BufferedImage targetImage = null;

  /**
   * Construct an ImagePanel of specified width and height.
   * 
   * @param width
   * @param height
   */
  public ImagePanel(int width, int height)
  {
    super();
    super.setSize(width, height);
    super.setPreferredSize(new Dimension(width, height));
  }

  /**
   * Change the image displayed in the ImagePanel. Only accepts image names that are pre-defined in
   * Constants.
   * 
   * @param fileName
   */
  public void updateImage()
  {
    targetImage = Constants.IMAGES[Constants.selectedImage];
    
    super.setSize(targetImage.getWidth(), targetImage.getHeight());
    super.setPreferredSize(new Dimension(targetImage.getWidth(), targetImage.getWidth()));
    this.repaint();
  }
  
  public BufferedImage getSnapshot()
  {
    return targetImage;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if (targetImage != null)
    {
      g.drawImage(targetImage, 0, 0, null);
    }
  }

}
