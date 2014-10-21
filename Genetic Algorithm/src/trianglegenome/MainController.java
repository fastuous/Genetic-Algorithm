package trianglegenome;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;
import trianglegenome.gui.DrawPanel;
import trianglegenome.gui.ImagePanel;
import trianglegenome.util.Constants;

public class MainController extends Application
{

  private List<Genome> globalPopulation;
  private int threadCount;
  private DrawPanel drawPanel;
  private ImagePanel imagePanel;
  private HillClimberSpawner hillClimberSpawner;
  private Genome selectedGenome;
  
  //private MainGUI GUI
  
  @FXML
  public Pane drawPanelContainer;
  @FXML
  public Pane imagePanelContainer;


  public void toggleRunning()
  {
    if(hillClimberSpawner.hillClimbersArePaused())
    {
      hillClimberSpawner.unpauseHillClimbers();
    }
    else if(!hillClimberSpawner.hillClimbersArePaused())
    {
      hillClimberSpawner.pauseHillClimbers(); 
    }
    // TODO if hill climbing, pause hill-climbing threads, if performing crossover, finish crossover
    // and stop
  }

  public void next()
  {
    hillClimberSpawner.performOneEvolution();
  }

  private List<Genome> getSortedPopulation()
  {
    List<Genome> genomes = globalPopulation;
    genomes.sort((Genome g1, Genome g2) -> Long.compare(g1.getFitness(), g2.getFitness()));
    return genomes;
  }


  public void showGenomeTable()
  {
    // TODO Take global genome table, sort it
  }

  public void imageSelected()
  {
    setup();
  }

  private void setup()
  {
    BufferedImage target = Constants.IMAGES[Constants.selectedImage];
    imagePanel = new ImagePanel(target.getWidth(),target.getHeight());
    drawPanel = new DrawPanel(target.getWidth(),target.getHeight());
    globalPopulation.clear();
    for(int i = 0; i < 1250; ++i)
    {
      globalPopulation.add(SeedGenome.generateSeed(target));
    }
    drawPanel.setTriangles(globalPopulation.get(Constants.rand.nextInt(1250)).getGenes());
    hillClimberSpawner = new HillClimberSpawner(1250,globalPopulation, target);
    
    //Instantiate the Image and Draw Panels based on the Global Constants
    //Set The draw Panel and Image Panel for the GUI
    //
    // TODO instantiate and setup everything necessary for problem space
  }

  public void triangleSliderUpdate(int value)
  {
    // TODO you know what to do
  }

  public void tribeSelectorUpdate(int index)
  {
    // TODO changes selected genome
  }

  public void genomeSliderUpdate(int index)
  {
    // TODO changes selected genome
  }

  public void readGenome()
  {
    // TODO read genome from XML into currently selected
  }

  public void writeGenome()
  {
    // TODO write selected genome to XML file
  }

  @Override
  public void start(Stage stage) throws Exception
  {
    imagePanel = new ImagePanel(512, 413);
    drawPanel = new DrawPanel(512, 413);
    try
    {
      Parent root = FXMLLoader.load(getClass().getResource("/trianglegenome/gui/MainGUI.fxml"));

      final SwingNode drawPanelNode = new SwingNode();
      final SwingNode imagePanelNode = new SwingNode();
      
      createAndSetSwingContent(drawPanelNode, imagePanelNode);
      
      drawPanelContainer = new Pane(drawPanelNode);
      imagePanelContainer = new Pane(imagePanelNode);
      
      Scene scene = new Scene(root, 1100, 600);

      stage.setTitle("Image Evolver");
      stage.setScene(scene);
      stage.show();
    }
    catch (IOException e)
    {

    }
    imagePanel.updateImage();
  }

  private void createAndSetSwingContent(final SwingNode drawPanelNode, final SwingNode imagePanelNode)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        drawPanelNode.setContent(drawPanel);
        imagePanelNode.setContent(imagePanel);
      }
    });
  }

  public static void main(String[] args)
  {
    Constants.selectedImage = 0;
    Constants.width = 512;
    Constants.height = 413;
    launch(args);
  }
  
  @FXML
  public void test()
  {
    System.out.println("Test");
  }
}
