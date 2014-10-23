package trianglegenome;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
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
import javafx.scene.input.MouseEvent;
import trianglegenome.gui.DrawPanel;
import trianglegenome.gui.ImagePanel;
import trianglegenome.util.Constants;

public class MainController extends Control implements Initializable
{

  private List<Genome> globalPopulation = new ArrayList<Genome>();
  List<Genome> selectedTribePopulation;
  private int threadCount;
  private DrawPanel drawPanel;
  private ImagePanel imagePanel;
  private Genome selectedGenome;
  private EvolutionManager evolutionManager;
  private boolean started = false;
  private Thread guiUpdater;

  // private MainGUI GUI

  @FXML private ImageView drawPanelContainer;
  @FXML private ImageView imagePanelContainer;
  @FXML private Label nTriangles;
  @FXML private Slider triangleSlider;
  @FXML private Slider genomeSlider;
  @FXML private ComboBox<String> imageSelect;
  @FXML private ComboBox<Integer> tribeSelect;

  @FXML
  private void toggleRunning()
  {
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

    BufferedImage target = Constants.IMAGES[Constants.selectedImage];
    
    imagePanelContainer.setImage(SwingFXUtils.toFXImage(target, null));
    
    evolutionManager.interrupt();
    try { synchronized (evolutionManager) { evolutionManager.wait(); } }
    catch (Exception e) {}
    
    setup();
  }

  private void setup()
  {
    BufferedImage target = Constants.IMAGES[Constants.selectedImage];
    Constants.width = target.getWidth();
    Constants.height = target.getHeight();
    drawPanel = new DrawPanel(Constants.width, Constants.height);
    globalPopulation.clear();
    for (int i = 0; i < 40; ++i)
    {
      globalPopulation.add(SeedGenome.generateSeed(target));
    }
    selectedGenome = globalPopulation.stream().findFirst().get();
    
    // TODO: set this elsewhere
    threadCount = 4;
    
    for (int i = 0; i < threadCount; i++) tribeSelect.itemsProperty().get().add(i);
    
    if (evolutionManager != null) evolutionManager.interrupt();
    evolutionManager = new EvolutionManager(threadCount, globalPopulation, target);
    evolutionManager.start();

    selectedTribePopulation = evolutionManager.getGenomesFromTribe(0);
    genomeSlider.setMax(selectedTribePopulation.size() - 1);
    genomeSlider.setMajorTickUnit(selectedTribePopulation.size() - 1);
    genomeSlider.setMinorTickCount(selectedTribePopulation.size() - 2);
    genomeSlider.setMin(0);
    
    return;
    
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
    selectedTribePopulation = evolutionManager.getGenomesFromTribe(tribeSelect.getValue());
    genomeSlider.setMax(selectedTribePopulation.size() - 1);
    genomeSlider.setMajorTickUnit(selectedTribePopulation.size() - 1);
    genomeSlider.setMinorTickCount(selectedTribePopulation.size() - 2);
    genomeSlider.setMin(0);
    genomeSliderUpdate();
  }

  @FXML
  private void genomeSliderUpdate(MouseEvent event)
  {
    genomeSliderUpdate();
  }
  
  private void genomeSliderUpdate()
  {
    selectedGenome = selectedTribePopulation.get((int)genomeSlider.getValue());
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
  
  private void updateDrawPanel()
  {
    drawPanel.setTriangles(selectedGenome.getGenes());
    drawPanelContainer.setImage(drawPanel.getFXImage());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    imagePanelContainer.setImage(SwingFXUtils.toFXImage(Constants.IMAGES[Constants.selectedImage], null));
    imageSelect.getItems().addAll(Constants.IMAGE_FILES);
    triangleSlider.valueProperty().addListener(e -> triangleSliderUpdate());
    genomeSlider.valueProperty().addListener(e -> genomeSliderUpdate());
    
    guiUpdater = new Thread(
        () ->
        {
          while (true)
          {
            Platform.runLater(() -> updateDrawPanel());
            try { Thread.sleep(200); } catch (Exception e) {}
          }
        });
    guiUpdater.start();
    
    setup();
  }

}
