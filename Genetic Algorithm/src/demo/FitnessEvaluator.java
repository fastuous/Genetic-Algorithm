package demo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.IntBuffer;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;

import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static com.jogamp.opencl.CLMemory.Mem;

public class FitnessEvaluator
{
  CLContext context;
  CLDevice device;
  CLCommandQueue queue;
  File sourceFile;
  FileInputStream sourceInputStream;
  CLProgram program;
  
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
  }
  
  public int differenceSumCL(BufferedImage reference, BufferedImage triangles)
  {
    int rWidth = reference.getWidth();
    int rHeight = reference.getHeight();
    int tWidth = triangles.getWidth();
    int tHeight = triangles.getHeight();
    if (tWidth != rWidth || tHeight != rHeight)
    {
      throw new IllegalArgumentException("Reference and triangle images must be the same size.");
    }
    
    CLKernel kernel = program.createCLKernel("fitness");
    
    int elementCount = tWidth * tHeight;
    int globalWorkSize = (int)pow(2, ceil(log(elementCount)/log(2)));
    int localWorkSize = min(device.getMaxWorkGroupSize(), globalWorkSize);
    
    CLBuffer<IntBuffer> rBuf = context.createIntBuffer(globalWorkSize, Mem.READ_ONLY);
    CLBuffer<IntBuffer> tBuf = context.createIntBuffer(globalWorkSize, Mem.READ_ONLY);
    CLBuffer<IntBuffer> oBuf = context.createIntBuffer(globalWorkSize, Mem.WRITE_ONLY);
    rBuf.getBuffer().put(reference.getRGB(0, 0, rWidth, rHeight, null, 0, rWidth));
    tBuf.getBuffer().put(triangles.getRGB(0, 0, tWidth, tHeight, null, 0, tWidth));
    rBuf.getBuffer().rewind();
    tBuf.getBuffer().rewind();
    
    kernel.putArgs(rBuf, tBuf, oBuf).putArg(elementCount);
    
    queue.putWriteBuffer(rBuf, false);
    queue.putWriteBuffer(tBuf, false);
    queue.put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize);
    queue.putReadBuffer(oBuf, true);
    
    int sum = 0;
    IntBuffer differences = oBuf.getBuffer();
    for (int i = 0; i < elementCount; i++) sum += differences.get();
    
    return sum;
  }
  
  public int differenceSum(BufferedImage reference, BufferedImage triangles)
  {
    int rWidth = reference.getWidth();
    int rHeight = reference.getHeight();
    int tWidth = triangles.getWidth();
    int tHeight = triangles.getHeight();
    if (tWidth != rWidth || tHeight != rHeight)
    {
      throw new IllegalArgumentException("Reference and triangle images must be the same size.");
    }
    
    int elementCount = tWidth * tHeight;
    
    int [] rRGB = reference.getRGB(0, 0, rWidth, rHeight, null, 0, rWidth);
    int [] tRGB = triangles.getRGB(0, 0, tWidth, tHeight, null, 0, tWidth);
    
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
}
