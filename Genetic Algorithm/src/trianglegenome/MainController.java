package trianglegenome;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

import javax.swing.JOptionPane;

import trianglegenome.gui.DrawPanel;
import trianglegenome.gui.DrawPanelBufferedImage;
import trianglegenome.gui.DrawPanelVolatileImage;
import trianglegenome.gui.GenomeTable;
import trianglegenome.util.Constants;
import trianglegenome.util.XMLParser;

/**
 * A centralized controller class, with methods that are called by JavaFX GUI controls.
 * 
 * @author Truman DeYoung
 */
public class MainController extends Control implements Initializable
{

  private List<Genome> globalPopulation = new ArrayList<Genome>();
  List<Genome> selectedTribePopulation;
  private DrawPanel drawPanel;
  private Genome selectedGenome;
  private EvolutionModel evolutionModel;
  private boolean running = false;
  private Thread guiUpdater;
  private int minuteCounter = 0;
  private long nPreviousGenerations = 0;
  private long tribeFitness;
  private long totalFitness;

  // private MainGUI GUI

  @FXML
  private ImageView drawPanelContainer;
  @FXML
  private ImageView imagePanelContainer;
  @FXML
  private Label nTriangles, fitness;
  @FXML
  private Slider triangleSlider, genomeSlider;
  @FXML
  private ComboBox<String> imageSelect, tribeSelect;
  @FXML
  private Button toggleRunning, nextGeneration, genomeTable, readGenome, writeGenome;
  @FXML
  private Label elapsedTime, totalGen, hillClimbGen, crossGen, genPerSecond, tribeFitPerMin, totalFitPerMin,
      tribeDiversity, totalDiversity;

  @FXML
  private void toggleRunning()
  {
    running = !running;
    toggleControls();

    if (!evolutionModel.isPaused())
    {
      evolutionModel.pause();
    }
    else if (evolutionModel.isPaused())
    {
      evolutionModel.unpause();
    }

  }

  @FXML
  private void next()
  {
    Genome seed = SeedGenome.generateSeed(Constants.IMAGES[Constants.selectedImage]);

    drawPanel.setTriangles(seed.getGenes());
    drawPanelContainer.setImage(drawPanel.getFXImage());

    // hillClimberSpawner.performOneEvolution();
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
    Genome genome = getSortedPopulation().get(0);
    new GenomeTable(genome);
  }

  @FXML
  private void imageSelected(ActionEvent event)
  {
    SelectionModel<String> test = imageSelect.getSelectionModel();
    Constants.selectedImage = test.getSelectedIndex();

    BufferedImage target = Constants.IMAGES[Constants.selectedImage];

    imagePanelContainer.setImage(SwingFXUtils.toFXImage(target, null));

    evolutionModel.interrupt();
    try
    {
      synchronized (evolutionModel)
      {
        evolutionModel.wait();
      }
    }
    catch (Exception e)
    {
    }

    setup();
  }

  private void setup()
  {
    BufferedImage target = Constants.IMAGES[Constants.selectedImage];
    Constants.width = target.getWidth();
    Constants.height = target.getHeight();
    Constants.threadCount = getThreadCount();
    drawPanel = (Constants.useVolatileImage) ? new DrawPanelVolatileImage(Constants.width, Constants.height)
        : new DrawPanelBufferedImage(Constants.width, Constants.height);
    globalPopulation.clear();
    for (int i = 0; i < 40; ++i)
    {
      globalPopulation.add(SeedGenome.generateSeed(target));
    }
    selectedGenome = globalPopulation.get(0);
    tribeSelect.itemsProperty().get().clear();

    for (int i = 0; i < Constants.threadCount; i++)
    {
      tribeSelect.itemsProperty().get().add("Tribe " + i);
    }

    if (evolutionModel != null) evolutionModel.interrupt();
    evolutionModel = new EvolutionModel(Constants.threadCount, globalPopulation, target);
    evolutionModel.pause();
    evolutionModel.start();
    selectedTribePopulation = evolutionModel.getGenomesFromTribe(0);
    genomeSlider.setMax(selectedTribePopulation.size() - 1);
    genomeSlider.setMajorTickUnit(selectedTribePopulation.size() - 1);
    genomeSlider.setMinorTickCount(selectedTribePopulation.size() - 2);
    genomeSlider.setMin(0);

  }

  @FXML
  private void triangleSliderUpdate()
  {
    nTriangles.setText("Triangles: " + (int) triangleSlider.getValue() + "/200");
    drawPanel.setTriangleDrawLimit((int) triangleSlider.getValue());
    drawPanelContainer.setImage(drawPanel.getFXImage());
  }

  @FXML
  private void tribeSelectorUpdate()
  {
    SelectionModel<String> selectedTribe = tribeSelect.getSelectionModel();
    selectedTribePopulation = evolutionModel.getGenomesFromTribe(selectedTribe.getSelectedIndex());
    genomeSlider.setMax(selectedTribePopulation.size() - 1);
    genomeSlider.setMajorTickUnit(selectedTribePopulation.size() - 1);
    genomeSlider.setMinorTickCount(selectedTribePopulation.size() - 2);
    genomeSlider.setMin(0);
    genomeSliderUpdate();
  }

  @FXML
  private void genomeSliderUpdate()
  {
    selectedGenome = selectedTribePopulation.get((int) genomeSlider.getValue());
    updateGUI();
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

  private void updateGUI()
  {
   
    long generationDelta = (evolutionModel.getTotalGenerations() - nPreviousGenerations) / 5;

    drawPanel.setTriangles(selectedGenome.getGenes());
    drawPanelContainer.setImage(drawPanel.getFXImage());
    fitness.textProperty().set("fitness: " + selectedGenome.getFitness());

    int elapsedMinutes = (int) (evolutionModel.getElapsedTime() / 1000 / 60);
    int elapsedSeconds = (int) ((evolutionModel.getElapsedTime() / 1000) % 60);
    elapsedTime.setText("Elapsed Time: " + elapsedMinutes + "m " + elapsedSeconds + "s");
    totalGen.setText("Total Generations: " + evolutionModel.getTotalGenerations());
    hillClimbGen.setText("HillClimb Gens.: " + evolutionModel.getHillClimbGenerations());
    crossGen.setText("Crossover Gens.: " + evolutionModel.getCrossoverGenerations());
    genPerSecond.setText("Gens. Per Second: " + generationDelta);

    if (minuteCounter == 12)
    {
      minuteCounter = 0;
      tribeFitPerMin.setText("Test " + nPreviousGenerations);
    }

  }

  private void toggleControls()
  {
    if (!running)
    {
      toggleRunning.setText("Start");

      imageSelect.setDisable(false);
      triangleSlider.setDisable(false);
      tribeSelect.setDisable(false);
      genomeSlider.setDisable(false);
      nextGeneration.setDisable(false);
      genomeTable.setDisable(false);
      readGenome.setDisable(false);
      writeGenome.setDisable(false);
    }
    else
    {
      toggleRunning.setText("Pause");

      imageSelect.setDisable(true);
      triangleSlider.setDisable(true);
      tribeSelect.setDisable(true);
      genomeSlider.setDisable(true);
      nextGeneration.setDisable(true);
      genomeTable.setDisable(true);
      readGenome.setDisable(true);
      writeGenome.setDisable(true);
    }
  }

  private int getThreadCount()
  {
    int threadCount = 0;
    while (true)
    {
      String input = JOptionPane.showInputDialog("Input the number of threads:");

      try
      {
        threadCount = Integer.parseInt(input);
      }
      catch (NumberFormatException e)
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

  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    imagePanelContainer.setImage(SwingFXUtils.toFXImage(Constants.IMAGES[Constants.selectedImage], null));
    imageSelect.getItems().addAll(Constants.IMAGE_FILES);
    triangleSlider.valueProperty().addListener(e -> triangleSliderUpdate());
    genomeSlider.valueProperty().addListener(e -> genomeSliderUpdate());
    genomeSlider.setShowTickLabels(true);

    guiUpdater = new Thread(() ->
    {
      while (true)
      {
        try
        {
          Thread.sleep(5000);
        }
        catch (Exception e)
        {
        }
        minuteCounter++;
        Platform.runLater(() -> updateGUI());
        nPreviousGenerations = evolutionModel.getTotalGenerations();
      }
    });
    guiUpdater.start();

    setup();
  }

  public List<Thread> getThreads()
  {
    List<Thread> threadList = new LinkedList<>();
    threadList.add(evolutionModel);
    threadList.add(guiUpdater);

    return threadList;
  }

}
