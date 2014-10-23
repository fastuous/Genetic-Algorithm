package trianglegenome;

import java.io.IOException;

import trianglegenome.util.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TriangleGenome extends Application
{
  static MainController controller;

  @Override
  public void start(Stage stage)
  {

    Parent root;

    try
    {
      root = FXMLLoader.load(getClass().getResource("/trianglegenome/gui/MainGUI.fxml"));

      Scene scene = new Scene(root, 1100, 700);

      stage.setTitle("Image Evolver");
      stage.setScene(scene);
      stage.show();

    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }

  public static void main(String[] args)
  {
    Constants.width = Constants.IMAGES[Constants.selectedImage].getWidth();
    Constants.height = Constants.IMAGES[Constants.selectedImage].getHeight();
    launch(args);

  }

}
