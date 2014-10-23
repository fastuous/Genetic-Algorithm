package trianglegenome.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import trianglegenome.Genome;
import trianglegenome.Triangle;
import trianglegenome.util.Constants;

public class GenomeTable
{
  TableView<TriangleData> table = new TableView<TriangleData>();

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public GenomeTable(Genome genome)
  {
    Stage stage = new Stage();
    try
    {
      Parent root = FXMLLoader.load(getClass().getResource("GenomeTable.fxml"));

      Scene scene = new Scene(root, 870, 500);

      List<Triangle> triangles = genome.getGenes();
      List<TriangleData> triangleData = new LinkedList<>();

      for (int i = 0; i < Constants.TRIANGLE_COUNT; i++)
      {
        triangleData.add(new TriangleData(i + 1, triangles.get(i)));
      }

      table = (TableView) scene.lookup("#table");

      ((TableColumn) table.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "index"));
      ((TableColumn) table.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "x1"));
      ((TableColumn) table.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "x2"));
      ((TableColumn) table.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "x3"));
      ((TableColumn) table.getColumns().get(4)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "y1"));
      ((TableColumn) table.getColumns().get(5)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "y2"));
      ((TableColumn) table.getColumns().get(6)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "y3"));
      ((TableColumn) table.getColumns().get(7)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "red"));
      ((TableColumn) table.getColumns().get(8)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "green"));
      ((TableColumn) table.getColumns().get(9)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "blue"));
      ((TableColumn) table.getColumns().get(10)).setCellValueFactory(new PropertyValueFactory<TriangleData, Integer>(
          "alpha"));

      final ObservableList data = FXCollections.observableArrayList(triangleData);

      table.setItems(data);

      stage.setScene(scene);
      stage.show();

    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

  }

  public static class TriangleData
  {
    private final SimpleStringProperty index;
    private final SimpleIntegerProperty x1;
    private final SimpleIntegerProperty x2;
    private final SimpleIntegerProperty x3;
    private final SimpleIntegerProperty y1;
    private final SimpleIntegerProperty y2;
    private final SimpleIntegerProperty y3;
    private final SimpleIntegerProperty red;
    private final SimpleIntegerProperty green;
    private final SimpleIntegerProperty blue;
    private final SimpleIntegerProperty alpha;

    private TriangleData(int index, Triangle triangle)
    {
      this.index = new SimpleStringProperty("Triangle: " + index);
      x1 = new SimpleIntegerProperty(triangle.dna[0]);
      x2 = new SimpleIntegerProperty(triangle.dna[1]);
      x3 = new SimpleIntegerProperty(triangle.dna[2]);
      y1 = new SimpleIntegerProperty(triangle.dna[3]);
      y2 = new SimpleIntegerProperty(triangle.dna[4]);
      y3 = new SimpleIntegerProperty(triangle.dna[5]);
      red = new SimpleIntegerProperty(triangle.dna[6]);
      green = new SimpleIntegerProperty(triangle.dna[7]);
      blue = new SimpleIntegerProperty(triangle.dna[8]);
      alpha = new SimpleIntegerProperty(triangle.dna[9]);
    }

    public String getIndex()
    {
      return index.get();
    }

    public Integer getX1()
    {
      return x1.get();
    }

    public Integer getX2()
    {
      return x2.get();
    }

    public Integer getX3()
    {
      return x3.get();
    }

    public Integer getY1()
    {
      return y1.get();
    }

    public Integer getY2()
    {
      return y2.get();
    }

    public Integer getY3()
    {
      return y3.get();
    }

    public Integer getRed()
    {
      return red.get();
    }

    public Integer getGreen()
    {
      return green.get();
    }

    public Integer getBlue()
    {
      return blue.get();
    }

    public Integer getAlpha()
    {
      return alpha.get();
    }
  }
}
