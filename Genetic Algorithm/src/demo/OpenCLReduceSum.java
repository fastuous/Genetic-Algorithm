package demo;

import java.io.File;
import java.io.FileInputStream;
import java.nio.IntBuffer;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLMemory;
import com.jogamp.opencl.CLProgram;

import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * Runs an OpenCL kernel which sums all elements in an array in O(log n) time.
 * Only works on arrays with a length that is a power of 2.
 * @author collinsd
 */
public class OpenCLReduceSum
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
      int globalWorkSize = (int)pow(2, ceil(log(elementCount)/log(2) - 1));
      int localWorkSize = min(device.getMaxWorkGroupSize(), globalWorkSize);
      
      File sourceFile = new File("kernels/reduce.cl");
      FileInputStream sourceInputStream = new FileInputStream(sourceFile);
      CLProgram program = context.createProgram(sourceInputStream).build();
      
      CLBuffer<IntBuffer> clBufferA = context.createIntBuffer(1024, CLMemory.Mem.READ_WRITE);
      
      fillIntBuffer(clBufferA.getBuffer());
      
      CLKernel kernel = program.createCLKernel("reduceSum");
      kernel.putArgs(clBufferA).putArg(0);
      
      int stepCount = (int)ceil(log(elementCount)/log(2));
      for (int i = 1; i <= stepCount; i++)
      {
        kernel.setArg(1, i);

        queue.putWriteBuffer(clBufferA, false)
             .put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize)
             .putReadBuffer(clBufferA, true);
        
        globalWorkSize = max(globalWorkSize / 2, 1);
        localWorkSize = min(localWorkSize, globalWorkSize);
      }
        
      int result = clBufferA.getBuffer().get();
      System.out.print("The sum is : " + result);
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
   * Fills an buffer with a sequence 1, 2, 3, ...
   * @param buffer The buffer to fill with the sequence.
   */
  private static void fillIntBuffer(IntBuffer buffer)
  {
    int i = 1;
    while (buffer.remaining() != 0) buffer.put(i++);
    buffer.rewind();
  }
}
