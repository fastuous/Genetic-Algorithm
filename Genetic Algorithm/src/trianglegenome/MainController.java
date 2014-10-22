package trianglegenome;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;

import trianglegenome.gui.DrawPanel;
import trianglegenome.gui.ImagePanel;
import trianglegenome.util.Constants;

public class MainController extends Application implements Initializable
{

  private List<Genome> globalPopulation = new ArrayList<Genome>();
  private int threadCount;
  private DrawPanel drawPanel;
  private ImagePanel imagePanel;
  private HillClimberSpawner hillClimberSpawner;
  private Genome selectedGenome;
  private boolean started = false;

  // private MainGUI GUI

  @FXML
  private ImageView drawPanelContainer;
  @FXML
  private Pane imagePanelContainer;
  @FXML
  private Text nTriangles;
  @FXML
  private Slider triangleSlider;

  @FXML
  public void toggleRunning()
  {
    if (!started)
    {
      if (hillClimberSpawner.hillClimbersArePaused())
      {
        hillClimberSpawner.unpauseHillClimbers();
      }
      else if (!hillClimberSpawner.hillClimbersArePaused())
      {
        hillClimberSpawner.pauseHillClimbers();
      }
    }
    else
    {
      started = true;
      hillClimberSpawner.startHillClimbing();
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

  public void imageSelected(ActionEvent event)
  {
    setup();
  }

  private void setup()
  {
    BufferedImage target = Constants.IMAGES[Constants.selectedImage];
    Constants.width = target.getWidth();
    Constants.height = target.getHeight();
    globalPopulation.clear();
    for (int i = 0; i < 10; ++i)
    {
      globalPopulation.add(SeedGenome.generateSeed(target));
    }
    List<Triangle> test = globalPopulation.get(Constants.rand.nextInt(10)).getGenes();
    drawPanel.setTriangles(test);
    hillClimberSpawner = new HillClimberSpawner(10, globalPopulation, target);

    // Instantiate the Image and Draw Panels based on the Global Constants
    // Set The draw Panel and Image Panel for the GUI
    //
    // TODO instantiate and setup everything necessary for problem space
  }

  @FXML
  public void triangleSliderUpdate(ActionEvent event)
  {
    nTriangles.setText("Triangles: " + triangleSlider.getValue());
  }

  public void tribeSelectorUpdate(ActionEvent event)
  {
    // TODO changes selected genome
  }

  public void genomeSliderUpdate(ActionEvent event)
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

  @SuppressWarnings("unchecked")
  @Override
  public void start(Stage stage)
  {
    imagePanel = new ImagePanel(512, 413);
    drawPanel = new DrawPanel(512, 413);
    final SwingNode drawPanelNode = new SwingNode();
    final SwingNode imagePanelNode = new SwingNode();
    try
    {
      Parent root = FXMLLoader.load(getClass().getResource("/trianglegenome/gui/MainGUI.fxml"));

      createAndSetSwingContent(drawPanelNode, imagePanelNode);

      Scene scene = new Scene(root, 1100, 700);

      drawPanelContainer = (ImageView) scene.lookup("#drawPanelContainer");
      imagePanelContainer = (Pane) scene.lookup("#imagePanelContainer");
      final ComboBox<String> imageSelect = (ComboBox<String>) scene.lookup("#imageSelect");
      imagePanelContainer.getChildren().add(imagePanelNode);
      imageSelect.getItems().addAll(Constants.IMAGE_FILES);
      

      stage.setTitle("Image Evolver");
      stage.setScene(scene);
      stage.show();
    }
    catch (IOException e)
    {
    }
    setup();

    try
    {
      Thread.sleep(200);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    drawPanel.setTriangles(globalPopulation.stream().findFirst().get().getGenes());
    drawPanel.repaint();
    drawPanelContainer.setImage(SwingFXUtils.toFXImage(drawPanel.getSnapshot(), null));
    imagePanel.updateImage();

    return;

  }

  private void createAndSetSwingContent(final SwingNode drawPanelNode, final SwingNode imagePanelNode)
  {
    try
    {
      SwingUtilities.invokeAndWait(new Runnable()
      {
        @Override
        public void run()
        {
          drawPanelNode.setContent(drawPanel);
          imagePanelNode.setContent(imagePanel);
        }
      });
    }
    catch (InvocationTargetException | InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    Constants.selectedImage = 0;
    Constants.width = 512;
    Constants.height = 413;
    try
    {
      launch(args);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @FXML
  private void test()
  {
    System.out.println("Test");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources)
  {

  }
}
