package demo;

import java.io.File;
import java.io.FileInputStream;
import java.nio.LongBuffer;
import java.util.stream.LongStream;

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
      
      int elementCount = 16777216;
      int globalWorkSize = (int)pow(2, ceil(log(elementCount)/log(2)));
      int localWorkSize = min(device.getMaxWorkGroupSize(), globalWorkSize);
      
      File sourceFile = new File("kernels/reduce.cl");
      FileInputStream sourceInputStream = new FileInputStream(sourceFile);
      CLProgram program = context.createProgram(sourceInputStream).build();
      
      CLBuffer<LongBuffer> clBufferA = context.createLongBuffer(globalWorkSize, CLMemory.Mem.READ_WRITE);
      
      fillIntBuffer(clBufferA.getBuffer());
      
      CLKernel kernel = program.createCLKernel("reduceSum");
      kernel.putArgs(clBufferA).putArg(0);
      
      
      
      long time1 = System.currentTimeMillis();
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
      time1 = System.currentTimeMillis() - time1;
      
      long result = clBufferA.getBuffer().get();
      System.out.println("OpenCL sum is : " + result);
      
      long [] test = LongStream.range(0, 16777217).toArray();
      long time2 = System.currentTimeMillis();
      long sum = 0;
      for (int i = 0; i < test.length; i++) sum += test[i];
      time2 = System.currentTimeMillis() - time2;
      System.out.println("Java sum is   : " + sum);
      
      
      System.out.println("OpenCL Time   : " + time1);
      System.out.println("Java Time     : " + time2);
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
   * @param longBuffer The buffer to fill with the sequence.
   */
  private static void fillIntBuffer(LongBuffer longBuffer)
  {
    long i = 1;
    while (longBuffer.remaining() != 0) longBuffer.put(i++);
    longBuffer.rewind();
  }
}