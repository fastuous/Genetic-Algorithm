package trianglegenome;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import trianglegenome.util.Constants;

/**
 * Our main entry-point class. Its only job is to instantiate the JavaFX application, which in turn
 * instantiates the MainController.
 * 
 * @author Truman DeYoung
 */
public class TriangleGenome extends Application
{
  private MainController controller;

  /* (non-Javadoc)
   * @see javafx.application.Application#start(javafx.stage.Stage)
   */
  @Override
  public void start(Stage stage)
  {

    Parent root;

    try
    {
      FXMLLoader loader = new FXMLLoader();

      root = loader.load(getClass().getResource("/trianglegenome/gui/MainGUI.fxml").openStream());

      controller = loader.getController();

      Scene scene = new Scene(root, 1100, 700);

      stage.setTitle("Image Evolver");
      stage.setScene(scene);
      stage.setOnCloseRequest(e -> onClose(e));
      stage.show();

    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Handles shutting down the threads before exiting the application.
   * 
   * @param event
   */
  private void onClose(WindowEvent event)
  {
    controller.getThreads().forEach(t -> t.interrupt());
    System.exit(0);
  }

  public static void main(String[] args)
  {
    Constants.width = Constants.IMAGES[Constants.selectedImage].getWidth();
    Constants.height = Constants.IMAGES[Constants.selectedImage].getHeight();
    launch(args);

  }

}
