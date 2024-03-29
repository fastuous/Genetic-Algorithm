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
import javafx.scene.control.SingleSelectionModel;
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
  private boolean started = false;
  private boolean running = false;
  private Thread guiUpdater;
  private int minuteCounter = 12;
  private long nPreviousGenerations = 0;
  private int[] tribeFitnesses;
  private int totalFitness;

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

  /**
   * Called whenever the start/pause button is pressed.
   */
  @FXML
  private void toggleRunning()
  {
    running = !running;
    toggleControls();

    if (!started)
    {
      started = true;
      for (int i = 0; i < Constants.threadCount; i++)
      {
        tribeFitnesses[i] = (int) (evolutionModel.getBestFitnessValue(i) / (Constants.width * Constants.height));
      }
    }

    if (!evolutionModel.isPaused())
    {
      evolutionModel.pause();
    }
    else if (evolutionModel.isPaused())
    {
      evolutionModel.unpause();
    }

    int elapsedMinutes = (int) (evolutionModel.getElapsedTime() / 1000 / 60);
    int elapsedSeconds = (int) ((evolutionModel.getElapsedTime() / 1000) % 60);
    elapsedTime.setText("Elapsed Time: " + elapsedMinutes + "m " + elapsedSeconds + "s");
  }

  /**
   * Performs one hill climbing evolution and updates the global population.
   */
  @FXML
  private void next()
  {
    if (!running)
    {
      evolutionModel.performOneEvolution();
    }
  }

  /**
   * A simple helper method to get the global population sorted by fitness.
   * 
   * @return The sorted global population
   */
  private List<Genome> getSortedPopulation()
  {
    List<Genome> genomes = globalPopulation;
    genomes.sort((Genome g1, Genome g2) -> Long.compare(g1.getFitness(), g2.getFitness()));
    return genomes;
  }

  /**
   * Called when the Show Genome button is pressed. Constructs a new genome table.
   */
  @FXML
  private void showGenomeTable()
  {
    Genome genome = getSortedPopulation().get(0);
    new GenomeTable(genome);
  }

  /**
   * Called whenever the selected image is changed. Resets all problem-space variables so that we
   * are ready to start evolving a new image.
   * 
   * @param event
   */
  @FXML
  private void imageSelected(ActionEvent event)
  {
    SelectionModel<String> test = imageSelect.getSelectionModel();
    Constants.selectedImage = test.getSelectedIndex();

    BufferedImage target = Constants.IMAGES[Constants.selectedImage];

    imagePanelContainer.setImage(SwingFXUtils.toFXImage(target, null));

    try
    {
      synchronized (evolutionModel)
      {
        evolutionModel.interrupt();
        evolutionModel.wait();
      }
    }
    catch (Exception e)
    {
    }

    setup();
  }

  /**
   * Helper method that's called whenever we start evolving a new image. Necessary to set up all
   * necessary environment variables.
   */
  private void setup()
  {
    BufferedImage target = Constants.IMAGES[Constants.selectedImage];
    Constants.width = target.getWidth();
    Constants.height = target.getHeight();
    Constants.threadCount = getThreadCount();
    tribeFitnesses = new int[Constants.threadCount];
    drawPanel = (Constants.useVolatileImage) ? new DrawPanelVolatileImage(Constants.width, Constants.height)
        : new DrawPanelBufferedImage(Constants.width, Constants.height);
    globalPopulation.clear();
    for (int i = 0; i < 10 * Constants.threadCount; ++i)
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

    for (int i = 0; i < Constants.threadCount; i++)
    {
      tribeFitnesses[i] = (int) (evolutionModel.getBestFitnessValue(i) / (Constants.width * Constants.height));
    }

    totalFitness = (int) (evolutionModel.getBestFitnessValue() / (Constants.width * Constants.height));
  }

  /**
   * Sets the tribe population to look at when considering the currently selected genome.
   * Additionally updates the genome slider to reflect the number of genomes in a tribe.
   */
  @FXML
  private void triangleSliderUpdate()
  {
    nTriangles.setText("Triangles: " + (int) triangleSlider.getValue() + "/200");
    drawPanel.setTriangleDrawLimit((int) triangleSlider.getValue());
    drawPanelContainer.setImage(drawPanel.getFXImage());
  }

  /**
   * Sets the tribe population to look at when considering the currently selected genome.
   * Additionally updates the genome slider to reflect the number of genomes in a tribe.
   */
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

  /**
   * Changes the selected genome that is drawn based on the genome slider.
   */
  @FXML
  private void genomeSliderUpdate()
  {
    selectedGenome = selectedTribePopulation.get((int) genomeSlider.getValue());
    updateDrawPanel();
  }

  /**
   * Read the currently selected genome into an XML file.
   */
  @FXML
  private void readGenome()
  {
    selectedGenome = XMLParser.readGenome();
  }

  /**
   * Write the currently selected genome into an XML file.
   */
  @FXML
  private void writeGenome()
  {
    XMLParser.writeGenome(selectedGenome);
  }

  /**
   * Update the draw area to show the currently selected genome and its fitness.
   */
  private void updateDrawPanel()
  {
    drawPanel.setTriangles(selectedGenome.getGenes());
    drawPanelContainer.setImage(drawPanel.getFXImage());
    fitness.textProperty().set("Fitness: " + selectedGenome.getFitness() / (Constants.height * Constants.width));

  }

  /**
   * Called every five seconds to update statistics on the GUI.
   */
  private void updateGUI()
  {
    minuteCounter++;
    long generationDelta = (evolutionModel.getTotalGenerations() - nPreviousGenerations) / 5;
    nPreviousGenerations = evolutionModel.getTotalGenerations();
    updateDrawPanel();

    int elapsedMinutes = (int) (evolutionModel.getElapsedTime() / 1000 / 60);
    int elapsedSeconds = (int) ((evolutionModel.getElapsedTime() / 1000) % 60);
    elapsedTime.setText("Elapsed Time: " + elapsedMinutes + "m " + elapsedSeconds + "s");
    totalGen.setText("Total Generations: " + evolutionModel.getTotalGenerations());
    hillClimbGen.setText("HillClimb Gens.: " + evolutionModel.getHillClimbGenerations());
    crossGen.setText("Crossover Gens.: " + evolutionModel.getCrossoverGenerations());
    genPerSecond.setText("Gens. Per Second: " + generationDelta);
    SelectionModel<String> tribeSelector = tribeSelect.getSelectionModel();
    int selectedTribe = tribeSelector.getSelectedIndex();
    tribeDiversity.setText("Tribe Diversity*: "
        + (evolutionModel.getWorstFitnessValue(selectedTribe) - evolutionModel.getBestFitnessValue(selectedTribe)));
    totalDiversity.setText("Total Diversity*: "
        + (evolutionModel.getWorstFitnessValue() - evolutionModel.getBestFitnessValue()));

    // called to only update once every minute
    if (minuteCounter >= 12)
    {

      minuteCounter = 0;
      int currentTribeFitness = (int) (evolutionModel.getBestFitnessValue(selectedTribe) / (Constants.width * Constants.height));
      int tribeFitnessDelta = (currentTribeFitness - tribeFitnesses[selectedTribe]);

      int totalFitnessDelta = (int) (totalFitness / (evolutionModel.getBestFitnessValue() / (Constants.width * Constants.height)));

      // dirty fix to prevent displaying erroenous values within the first minute of runtime
      if (tribeFitnessDelta < 1000 && tribeFitnessDelta > -10000)
      {
        tribeFitPerMin.setText("Tribe Fitness per Min.:  " + tribeFitnessDelta);
        totalFitPerMin.setText("Total Fitness per Min.: " + totalFitnessDelta);
      }
      for (int i = 0; i < Constants.threadCount; i++)
      {
        tribeFitnesses[i] = (int) (evolutionModel.getBestFitnessValue(i) / (Constants.width * Constants.height));
      }
      totalFitness = (int) (evolutionModel.getBestFitnessValue() / (Constants.width * Constants.height));

    }

  }

  /**
   * Simple helper method to enable/disable GUI controls based on the running state of the program.
   */
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

  /* (non-Javadoc)
   * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
   */
  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    imagePanelContainer.setImage(SwingFXUtils.toFXImage(Constants.IMAGES[Constants.selectedImage], null));
    imageSelect.getItems().addAll(Constants.IMAGE_FILES);
    triangleSlider.valueProperty().addListener(e -> triangleSliderUpdate());
    genomeSlider.valueProperty().addListener(e -> genomeSliderUpdate());
    genomeSlider.setShowTickLabels(true);
    SelectionModel<String> selection = tribeSelect.getSelectionModel();
    selection.select(0);
    tribeSelect.setSelectionModel((SingleSelectionModel<String>) selection);

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
        Platform.runLater(() -> updateGUI());

      }
    });
    guiUpdater.start();

    setup();
  }

  /**
   * Helper method to return the active threads called by this class, used to shut down the
   * application.
   * 
   * @return
   */
  public List<Thread> getThreads()
  {
    List<Thread> threadList = new LinkedList<>();
    threadList.add(evolutionModel);
    threadList.add(guiUpdater);

    return threadList;
  }

}
