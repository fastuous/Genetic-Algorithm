package ImageEvolver;

import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.Math.pow;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import ImageEvolver.util.Constants;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLMemory;
import com.jogamp.opencl.CLProgram;

public class FitnessFunctions
{
  public static int getSimpleFitness(BufferedImage source, BufferedImage target)
  {
    int fitness = 0;
    int numElements = 0;

    CLContext context = CLContext.create();

    try
    {
      CLDevice device = context.getMaxFlopsDevice();
      CLCommandQueue queue = device.createCommandQueue();

      numElements = Constants.width * Constants.height;

      int localWorkSize = Math.min(256, device.getMaxWorkGroupSize());
      int globalWorkSize = (int) pow(2.0, ceil(log(numElements) / log(2.0)));

      File sourceFile = new File("kernels/simpleFitness.cl");
      FileInputStream sourceInputStream = new FileInputStream(sourceFile);
      CLProgram program = context.createProgram(sourceInputStream).build();

      CLBuffer<IntBuffer> clBufferA = context.createIntBuffer(globalWorkSize,
          CLMemory.Mem.READ_ONLY);
      CLBuffer<IntBuffer> clBufferB = context.createIntBuffer(globalWorkSize,
          CLMemory.Mem.READ_ONLY);
      CLBuffer<IntBuffer> clBufferC = context.createIntBuffer(globalWorkSize,
          CLMemory.Mem.WRITE_ONLY);

      fillBuffer(clBufferA.getBuffer(), source);
      fillBuffer(clBufferB.getBuffer(), target);

      CLKernel kernel = program.createCLKernel("simpleFitness");
      kernel.putArgs(clBufferA, clBufferB, clBufferC).putArg(numElements);

      queue.putWriteBuffer(clBufferA, true);
      queue.putWriteBuffer(clBufferB, true);
      queue.put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize);
      queue.putReadBuffer(clBufferC, true);

      int[] result = new int[numElements];
      clBufferC.getBuffer().get(result);
      for (int i = 0; i < numElements; i++)
        fitness += result[i];
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      context.release();
    }

    // int sourceWidth = source.getWidth();
    // int sourceHeight = source.getHeight();
    // int targetWidth = target.getWidth();
    // int targetHeight = target.getHeight();
    //
    // int fitness = 0;
    //
    // if (sourceWidth != targetWidth || sourceHeight != targetHeight)
    // {
    // System.err.println("Fitness error: dimension mismatch");
    // return 0;
    // }
    //
    // for (int i = 0; i < sourceWidth; i++)
    // {
    // for (int j = 0; j < sourceHeight; j++)
    // {
    // int sourceColor = source.getRGB(i, j);
    // int targetColor = target.getRGB(i, j);
    //
    // int dRed = Math.abs((sourceColor >> 16) & 0xFF - (targetColor >> 16) & 0xFF);
    // int dGreen = Math.abs((sourceColor >> 8) & 0xFF - (targetColor >> 8) & 0xFF);
    // int dBlue = Math.abs(sourceColor & 0xFF - targetColor & 0xFF);
    //
    // fitness += dRed + dGreen + dBlue;
    // }
    // }
    // fitness = fitness / (sourceWidth * sourceHeight);
    //
    //
    fitness = fitness / numElements;
    return fitness;
  }

  private static void fillBuffer(IntBuffer intBuffer, BufferedImage image)
  {
    for (int i = 0; i < image.getWidth(); i++)
    {
      for (int j = 0; j < image.getHeight(); j++)
      {
        intBuffer.put(image.getRGB(i, j) & 0xFFFFFF);
      }
    }

  }
}
