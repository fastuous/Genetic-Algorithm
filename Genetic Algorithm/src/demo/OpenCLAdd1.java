package demo;

import java.io.File;
import java.io.FileInputStream;
import java.nio.FloatBuffer;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLMemory;
import com.jogamp.opencl.CLProgram;

/**
 * Runs an OpenCL kernel which adds one to all elements in an array.
 * @author collinsd
 */
public class OpenCLAdd1
{
  /**
   * A demonstration of OpenCL on Java.
   * @param args ignored
   */
  public static void main(String[] args)
  {
    CLContext context = CLContext.create();
    
    try
    {
      CLDevice device = context.getMaxFlopsDevice();
      CLCommandQueue queue = device.createCommandQueue();
      
      int elementCount = 1024;
      int localWorkSize = 256;
      int globalWorkSize = 1024; // Must be a multiple of localWorkSize
      
      File sourceFile = new File("kernels/add1.cl");
      FileInputStream sourceInputStream = new FileInputStream(sourceFile);
      CLProgram program = context.createProgram(sourceInputStream).build();
      
      CLBuffer<FloatBuffer> clBufferA = context.createFloatBuffer(globalWorkSize, CLMemory.Mem.READ_ONLY);
      CLBuffer<FloatBuffer> clBufferC = context.createFloatBuffer(globalWorkSize, CLMemory.Mem.WRITE_ONLY);

      fillFloatBuffer(clBufferA.getBuffer());
      
      CLKernel kernel = program.createCLKernel("add1");
      kernel.putArgs(clBufferA, clBufferC).putArg(elementCount);

      queue.putWriteBuffer(clBufferA, false)
           .put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize)
           .putReadBuffer(clBufferC, true);
      
      float [] result = new float[1024];
      clBufferC.getBuffer().get(result);
      for (int i = 0; i < 1024; i++) System.out.print(result[i] + " ");
    }
    catch (Exception e)
    { 
      e.printStackTrace();
    }
    finally
    {
      context.release();
    }
  }
  
  /**
   * Fills a float buffer with a sequence 1.0, 2.0, 3.0, ...
   * @param buffer The buffer to fill with the sequence.
   */
  private static void fillFloatBuffer(FloatBuffer buffer)
  {
    float i = 1.0f;
    while (buffer.remaining() != 0) buffer.put(i++);
    buffer.rewind();
  }
}
