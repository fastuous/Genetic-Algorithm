package trianglegenome;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import trianglegenome.gui.DrawPanel;
import trianglegenome.gui.ImagePanel;
import trianglegenome.util.Constants;

public class MainController extends Control implements Initializable
{

  private List<Genome> globalPopulation = new ArrayList<Genome>();
  private int threadCount;
  private DrawPanel drawPanel;
  private ImagePanel imagePanel;
  private HillClimberSpawner hillClimberSpawner;
  private Genome selectedGenome;
  private boolean started = false;

  // private MainGUI GUI

  @FXML private ImageView drawPanelContainer;
  @FXML private ImageView imagePanelContainer;
  @FXML private Label nTriangles;
  @FXML private Slider triangleSlider;
  @FXML private ComboBox<String>imageSelect;

  @FXML
  private void toggleRunning()
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

  @FXML
  private void next()
  {
    Genome seed = SeedGenome.generateSeed(Constants.IMAGES[Constants.selectedImage]);
    
    drawPanel.setTriangles(seed.getGenes());
    drawPanelContainer.setImage(drawPanel.getFXImage());
    
    //    hillClimberSpawner.performOneEvolution();
  }

  private List<Genome> getSortedPopulation()
  {
    List<Genome> genomes = globalPopulation;
    genomes.sort((Genome g1, Genome g2) -> Long.compare(g1.getFitness(), g2.getFitness()));
    return genomes;
  }

  @FXML
  private void showGenomeTable()
  {
    // TODO Take global genome table, sort it
  }

  @FXML
  private void imageSelected(ActionEvent event)
  {
    SelectionModel<String> test = imageSelect.getSelectionModel();
    Constants.selectedImage = test.getSelectedIndex();
    imagePanelContainer.setImage(SwingFXUtils.toFXImage(Constants.IMAGES[Constants.selectedImage], null));
  }

  private void setup()
  {
    BufferedImage target = Constants.IMAGES[Constants.selectedImage];
    Constants.width = target.getWidth();
    Constants.height = target.getHeight();
    drawPanel = new DrawPanel(Constants.width, Constants.height);
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
  private void triangleSliderUpdate()
  {
    nTriangles.setText("Triangles: " + (int) triangleSlider.getValue() + "/200");
    drawPanel.setTriangleDrawLimit((int) triangleSlider.getValue());
    drawPanelContainer.setImage(drawPanel.getFXImage());
  }

  @FXML
  private void tribeSelectorUpdate(ActionEvent event)
  {
    // TODO changes selected genome
  }

  @FXML
  private void genomeSliderUpdate(ActionEvent event)
  {
    // TODO changes selected genome
  }

  @FXML
  private void readGenome()
  {
    // TODO read genome from XML into currently selected
  }

  @FXML
  private void writeGenome()
  {
    // TODO write selected genome to XML file
  }

  @FXML
  private void test()
  {
    System.out.println("Test");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    imagePanelContainer.setImage(SwingFXUtils.toFXImage(Constants.IMAGES[Constants.selectedImage], null));
    imageSelect.getItems().addAll(Constants.IMAGE_FILES);
    triangleSlider.valueProperty().addListener(e -> triangleSliderUpdate());
    
    setup();
  }

}
