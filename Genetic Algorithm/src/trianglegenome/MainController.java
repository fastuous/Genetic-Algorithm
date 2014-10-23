package trianglegenome;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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

import javax.swing.JOptionPane;

import trianglegenome.gui.DrawPanel;
import trianglegenome.gui.ImagePanel;
import trianglegenome.util.Constants;
import trianglegenome.util.XMLParser;

public class MainController extends Control implements Initializable
{

  private List<Genome> globalPopulation = new ArrayList<Genome>();
  private int threadCount;
  private DrawPanel drawPanel;
  private ImagePanel imagePanel;
  private HillClimberSpawner hillClimberSpawner;
  private Genome selectedGenome;
  private boolean started = false;
  private EvolutionManager evolutionManager;

  // private MainGUI GUI

  @FXML
  private ImageView drawPanelContainer;
  @FXML
  private ImageView imagePanelContainer;
  @FXML
  private Label nTriangles;
  @FXML
  private Label fitness;
  @FXML
  private Slider triangleSlider;
  @FXML
  private ComboBox<String> imageSelect;

  @FXML
  private void toggleRunning()
  {
    if (started)
    {
      if (!evolutionManager.isPaused())
      {
        evolutionManager.pause();
      }
      else if (evolutionManager.isPaused())
      {
        evolutionManager.unpause();
      }
    }
    else
    {
      started = true;
      evolutionManager.run();
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

    evolutionManager.performOneEvolution();
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

    setup();
  }

  private void setup()
  {
    Constants.threadCount = getThreadCount();
    started = false;
    BufferedImage target = Constants.IMAGES[Constants.selectedImage];
    Constants.width = target.getWidth();
    Constants.height = target.getHeight();
    globalPopulation.clear();
    for (int i = 0; i < 100 * threadCount; ++i)
    {
      globalPopulation.add(SeedGenome.generateSeed(target));
    }
    evolutionManager = new EvolutionManager(Constants.threadCount, globalPopulation, target);
    drawPanel = new DrawPanel(Constants.width, Constants.height);

    // Instantiate the Image and Draw Panels based on the Global Constants
    // Set The draw Panel and Image Panel for the GUI
    //
    // TODO instantiate and setup everything necessary for problem space
  }

  private int getThreadCount()
  {
    int threadCount = 0;
    while (true)
    {
     String input = JOptionPane.showInputDialog("Input the number of threads:"); 
     
     try {
       threadCount = Integer.parseInt(input);
     }
     catch(NumberFormatException e)
     {
       continue;
     }
     
     if (threadCount < 1 || threadCount > 1000)
     {
       continue;
     }
     
     break;
    }
    
    return threadCount;
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
    selectedGenome = XMLParser.readGenome();
  }

  @FXML
  private void writeGenome()
  {
    XMLParser.writeGenome(selectedGenome);
  }

  @FXML
  private void test()
  {
    System.out.println("Test");
  }

  public void updateDisplay()
  {
    drawPanel.setTriangles(selectedGenome.getGenes());
    drawPanelContainer.setImage(drawPanel.getFXImage());
    fitness.setText("Fitness: " + selectedGenome.getFitness());
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
