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
 *  
 *  DrawPanel p1 = new DrawPanel(20, 20);
 *  ImagePanel p2 = new ImagePanel(20, 20);
 *  
 *  // draw image on p2
 *  
 *  FitnessEvaluator f = new FitnessEvaluator(p2.getSnapshot());
 *  
 *  // draw triangles in p1
 *  
 *  // Get fitness with java methods
 *  int f1 = f.differenceSum(p1.getSnapshot());
 *  
 *  // Get fitness with OpenCL kernel
 *  int f2 = f.differenceSumCL(p1.getSnapshot());
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
  CLKernel kernel;
  
  BufferedImage reference;
  
  int elementCount;
  int globalWorkSize;
  int localWorkSize;
  
  CLBuffer<IntBuffer> referenceCLBuffer;
  DataBufferInt referenceBufferInt;
  
  /**
   * Creates a new fitness evaluator.
   */
  public FitnessEvaluator(BufferedImage reference)
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
    
    initializeReferenceBuffers(reference);
  }
  
  /**
   * Initializes the {@link #referenceBufferInt} and {@link #referenceCLBuffer} based on
   * the given reference image.
   * @param reference The reference image to which the triangles image will be compared.
   */
  private void initializeReferenceBuffers(BufferedImage reference)
  {
    referenceBufferInt = (DataBufferInt)reference.getRaster().getDataBuffer();
    
    this.elementCount = referenceBufferInt.getSize();
    this.globalWorkSize = getBufferSize(elementCount);
    this.localWorkSize = device.getMaxWorkGroupSize();
    
    referenceCLBuffer = context.createIntBuffer(globalWorkSize, Mem.READ_ONLY);
    
    referenceCLBuffer.getBuffer().put(referenceBufferInt.getData());
    referenceCLBuffer.getBuffer().rewind();
    
    this.reference = reference;
  }
  
  /**
   * Uses OpenCL to calculate the differences between each red, green and blue value
   * of each pixel in two images.
   * @param reference The reference image against which the triangles will be compared.
   * @param triangles The image containing the triangles to compare to the reference image.
   * @return AThe fitness of the triangles where lower is better. 
   */
  public int differenceSumCL(BufferedImage triangles)
  {
    CLBuffer<IntBuffer> trianglesCLBuffer;
    DataBufferInt trtianglesBufferInt;
    
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
    
    trtianglesBufferInt = (DataBufferInt)triangles.getRaster().getDataBuffer();
    
    trianglesCLBuffer = context.createIntBuffer(globalWorkSize, Mem.READ_WRITE);
    trianglesCLBuffer.getBuffer().put(trtianglesBufferInt.getData());
    
    trianglesCLBuffer.getBuffer().rewind();
    
    kernel.setArgs(referenceCLBuffer, trianglesCLBuffer).setArg(2, elementCount);
    
    queue.putWriteBuffer(referenceCLBuffer, false);
    queue.putWriteBuffer(trianglesCLBuffer, false);
    queue.put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize);
    queue.putReadBuffer(trianglesCLBuffer, true);
    
    int sum = 0;
    IntBuffer differences = trianglesCLBuffer.getBuffer();
    for (int i = 0; i < elementCount; i++) sum += differences.get();
    
    trianglesCLBuffer.release();
    
    return sum;
  }

  /**
   * Uses Java to calculate the differences between each red, green and blue value
   * of each pixel in two images.
   * @param reference The reference image against which the triangles will be compared.
   * @param triangles The image containing the triangles to compare to the reference image.
   * @return AThe fitness of the triangles where lower is better. 
   */
  public int differenceSum(BufferedImage triangles)
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
    
    DataBufferInt trianglesBufferInt = (DataBufferInt)triangles.getRaster().getDataBuffer();
    
    int elementCount = trianglesBufferInt.getSize();
    
    int [] rRGB = referenceBufferInt.getData();
    int [] tRGB = trianglesBufferInt.getData();
    
    int sum = 0;
    for (int i = 0; i < elementCount; i++)
    {
      int rRGBVal = rRGB[i];
      int tRGBVal = tRGB[i];
      int rr = (rRGBVal >> 0x04) & 0xFF;
      int rg = (rRGBVal >> 0x02) & 0xFF;
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
