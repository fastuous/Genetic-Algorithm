package trianglegenome.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import trianglegenome.Genome;
import trianglegenome.RandomGenome;

/**
 * A class to handle reading and writing genomes to a file of the user's choosing. Assumes good
 * input at this point, may implement more robust validation in the future.
 * 
 * @author Truman DeYoung
 */
public class XMLParser
{
  /**
   * Prompts the user to select a genome file (*.xml) and then reads it in, constructs a genome
   * object, and returns the object.
   * 
   * @return The genome read from the file.
   */
  public static Genome readGenome()
  {
    Genome genome = null;
    String dir = System.getProperty("user.dir");
    JFileChooser fileChooser = new JFileChooser(dir);
    int returnVal = fileChooser.showOpenDialog(null);

    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      File file = fileChooser.getSelectedFile();

      try
      {
        JAXBContext jc = JAXBContext.newInstance(Genome.class);
        Unmarshaller ju = jc.createUnmarshaller();

        genome = (Genome) ju.unmarshal(file);

      }
      catch (JAXBException e)
      {
        e.printStackTrace();
      }
    }

    return genome;
  }

  /**
   * Prompts the user to select a place to save the genome (must be of type '.xml') and then writes
   * the given genome to that file.
   * 
   * @param genome The genome to be written to file.
   */
  public static void writeGenome(Genome genome)
  {
    String dir = System.getProperty("user.dir");
    JFileChooser fileChooser = new JFileChooser(dir);
    int returnVal = fileChooser.showSaveDialog(null);

    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      File file = fileChooser.getSelectedFile();

      try
      {
        JAXBContext jc = JAXBContext.newInstance(Genome.class);
        Marshaller jm = jc.createMarshaller();
        jm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jm.marshal(genome, file);
      }
      catch (JAXBException e)
      {
        e.printStackTrace();
      }
    }
  }

  /**
   * A simple test method to make sure reading and writing genomes is functional.
   * 
   * @param args ignored
   */
  public static void main(String[] args)
  {
    Constants.width = 500;
    Constants.height = 400;

    Genome origGenome = RandomGenome.generateGenome();

    Genome newGenome = origGenome.clone();

    writeGenome(newGenome);
    newGenome = readGenome();

    if (newGenome.equals(origGenome))
    {
      System.out.println("Success!");
    }

  }
}
