package demo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXFrame extends Application
{

  @Override
  public void start(Stage stage) {
    try
    {
      Parent root = FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
      
      Scene scene = new Scene(root, 816, 480);
      
      stage.setTitle("Main GUI");
      stage.setScene(scene);
      stage.show();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    launch(args);
  }

}
