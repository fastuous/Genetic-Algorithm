package trianglegenome;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileInputStream;
import java.nio.IntBuffer;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static com.jogamp.opencl.CLMemory.Mem;

/**
 * Calculates fitness using java methods and by calling an opencl kernel,
 * kernels/fitness.cl
 * <br /><br />
 * Example code:<br/>
 * <code><pre>
 *  FitnessEvaluator f = new FitnessEvaluator();
 *  
 *  DrawPanel p1 = new DrawPanel(20, 20);
 *  ImagePanel p2 = new ImagePanel(20, 20);
 *  
 *  // put triangles in p1
 *  // set image on p2
 *  
 *  // Get fitness with java methods
 *  int f1 = f.differenceSum(drawPanelSnapshot, imagePanelSnapshot);
 *  
 *  // Get fitness with OpenCL kernel
 *  int f2 = f.differenceSumCL(drawPanelSnapshot, imagePanelSnapshot);
 *  
 *  assert f1 == f2;
 *  
 * </pre></code>
 * 
 * @author David Collins
 */
public class FitnessEvaluator
{
  CLContext context;
  CLDevice device;
  CLCommandQueue queue;
  File sourceFile;
  FileInputStream sourceInputStream;
  CLProgram program;
  BufferedImage reference;
  CLKernel kernel;
  
  /**
   * Creates a new fitness evaluator.
   */
  public FitnessEvaluator()
  {
    context = CLContext.create();
    device = context.getMaxFlopsDevice();
    queue = device.createCommandQueue();
    sourceFile = new File("kernels/fitness.cl");
    
    try
    {
      sourceInputStream = new FileInputStream(sourceFile);
      program = context.createProgram(sourceInputStream).build();
    }
    catch (Exception e) { e.printStackTrace(); }
    
    kernel = program.createCLKernel("fitness");
  }
  
  /**
   * Uses OpenCL to calculate the differences between each red, green and blue value
   * of each pixel in two images.
   * @param reference The reference image against which the triangles will be compared.
   * @param triangles The image containing the triangles to compare to the reference image.
   * @return AThe fitness of the triangles where lower is better. 
   */
  public int differenceSumCL(BufferedImage reference, BufferedImage triangles)
  {
    int rWidth = reference.getWidth();
    int rHeight = reference.getHeight();
    int tWidth = triangles.getWidth();
    int tHeight = triangles.getHeight();
    if (tWidth != rWidth || tHeight != rHeight)
    {
      final String ERROR = "Reference and triangle images must be the same size"; 
      throw new IllegalArgumentException(ERROR);
    }
    if (reference.getType() != BufferedImage.TYPE_INT_RGB ||
        triangles.getType() != BufferedImage.TYPE_INT_RGB)
    {
      final String ERROR = "Reference and triangle images must be BufferedImage.TYPE_INT_RGB"; 
      throw new IllegalArgumentException(ERROR);
    }
    
    DataBufferInt rBufInt = (DataBufferInt)reference.getRaster().getDataBuffer();
    DataBufferInt tBufInt = (DataBufferInt)triangles.getRaster().getDataBuffer();
    
    int elementCount = rBufInt.getSize();
    int globalWorkSize = getBufferSize(elementCount);
    int localWorkSize = device.getMaxWorkGroupSize();
    
    CLBuffer<IntBuffer> rBuf = context.createIntBuffer(globalWorkSize, Mem.READ_ONLY);
    CLBuffer<IntBuffer> tBuf = context.createIntBuffer(globalWorkSize, Mem.READ_WRITE);
    rBuf.getBuffer().put(rBufInt.getData());
    tBuf.getBuffer().put(tBufInt.getData());
    rBuf.getBuffer().rewind();
    tBuf.getBuffer().rewind();
    
    kernel.setArgs(rBuf, tBuf).setArg(2, elementCount);
    
    queue.putWriteBuffer(rBuf, false);
    queue.putWriteBuffer(tBuf, false);
    queue.put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize);
    queue.putReadBuffer(tBuf, true);
    
    int sum = 0;
    IntBuffer differences = tBuf.getBuffer();
    for (int i = 0; i < elementCount; i++) sum += differences.get();
    
    return sum;
  }

  /**
   * Uses Java to calculate the differences between each red, green and blue value
   * of each pixel in two images.
   * @param reference The reference image against which the triangles will be compared.
   * @param triangles The image containing the triangles to compare to the reference image.
   * @return AThe fitness of the triangles where lower is better. 
   */
  public int differenceSum(BufferedImage reference, BufferedImage triangles)
  {
    int rWidth = reference.getWidth();
    int rHeight = reference.getHeight();
    int tWidth = triangles.getWidth();
    int tHeight = triangles.getHeight();
    if (tWidth != rWidth || tHeight != rHeight)
    {
      final String ERROR = "Reference and triangle images must be the same size"; 
      throw new IllegalArgumentException(ERROR);
    }
    if (reference.getType() != BufferedImage.TYPE_INT_RGB ||
        triangles.getType() != BufferedImage.TYPE_INT_RGB)
    {
      final String ERROR = "Reference and triangle images must be BufferedImage.TYPE_INT_RGB"; 
      throw new IllegalArgumentException(ERROR);
    }
    
    DataBufferInt rBuf = (DataBufferInt)reference.getRaster().getDataBuffer();
    DataBufferInt tBuf = (DataBufferInt)triangles.getRaster().getDataBuffer();
    
    int elementCount = tBuf.getSize();
    
    int [] rRGB = rBuf.getData();
    int [] tRGB = tBuf.getData();
    
    int sum = 0;
    for (int i = 0; i < elementCount; i++)
    {
      int rRGBVal = rRGB[i];
      int tRGBVal = tRGB[i];
      int rr = (rRGBVal >> 0x04) & 0xFF;
      int rg = (rRGBVal >> 0x02) & 0xFF ;
      int rb = rRGBVal & 0xFF;
      int tr = (tRGBVal >> 0x04) & 0xFF;
      int tg = (tRGBVal >> 0x02) & 0xFF;
      int tb = tRGBVal & 0xFF;
      
      double dr2 = pow(rr - tr, 2.0);
      double dg2 = pow(rg - tg, 2.0);
      double db2 = pow(rb - tb, 2.0);
      
      sum += sqrt(dr2 + dg2 + db2);
    }
    return sum;
  }
  
  private int getBufferSize(int elementCount)
  {
    int mwSize = device.getMaxWorkGroupSize();
    int wgs = mwSize / elementCount + 1; 
    return wgs * elementCount;
  }
}
