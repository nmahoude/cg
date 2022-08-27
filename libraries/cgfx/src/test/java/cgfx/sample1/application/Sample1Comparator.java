package cgfx.sample1.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cgfx.sample1.application.compartors.Evaluator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * compare evaluation results on 2 series of actions
 * 
 * graph shows values of each evaluation criteria based on simulation DEPTH
 * 
 * 
 *
 */

public class Sample1Comparator extends Application {
  private LineChart chart;
  private CheckBox cumulative;
  private CheckBox depthFactor;
  
  
  
  
  // TODO find another way ....
  public static Evaluator evaluator = new Evaluator();
  private Map<Integer, XYChart.Series> series = new HashMap<>();
  private BorderPane root;
  
  public Sample1Comparator() {
    build();

  }

  private void build() {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();

    xAxis.setLabel("depth");
    xAxis.setAutoRanging(false);
    xAxis.setLowerBound(0);
    xAxis.setUpperBound(evaluator.depth());
    yAxis.setLabel("score");
    chart = new LineChart(xAxis, yAxis);
    chart.setAnimated(false);
    
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    root = new BorderPane();
    Scene scene = new Scene(root, 1200, 768);
    
    cumulative = new CheckBox("Cumulative");
    cumulative.selectedProperty().addListener(event -> {
      evaluator.cumulative = cumulative.isSelected();
      redraw();
    });
    cumulative.setSelected(true);
    
    depthFactor = new CheckBox("Depth factor");
    depthFactor.selectedProperty().addListener(event -> {
      evaluator.useDepthFactor = depthFactor.isSelected();
      redraw();
    });
    depthFactor.setSelected(true);
    
    HBox group = new HBox(cumulative, depthFactor);
    
    VBox seriesBox = new VBox();
    List<String> names = evaluator.names();
    for (int i = 0; i < names.size(); i++) {
      String name = names.get(i);
      CheckBox serieBox = new CheckBox(name);
      seriesBox.getChildren().add(serieBox);

      Series serie = new XYChart.Series();
      fillSerie(i, serie);
      series.put(i, serie);
      
      
      chart.getData().add(serie);
      serie.getData().forEach((item) -> {
        String color = "blue";
        ((XYChart.Data) item).getNode().setStyle("-fx-background-color: " + color + ", white;");
      });
      chart.getData().remove(serie);
      
      serieBox.selectedProperty().addListener(event -> {
        if (serieBox.isSelected()) {
          chart.getData().add(serie);
        } else {
          chart.getData().remove(serie);
        }
        
//        serie.getData().forEach(d ->  {
//          if (((XYChart.Data)d).getNode() != null) 
//            ((XYChart.Data) d).getNode().setVisible(serieBox.isSelected()); 
//          
//        });
      });

      if (i == 0) {
        serieBox.setSelected(true);
      }
      
      
      
    }
    

    
    
    root.setTop(group);
    root.setCenter(chart);
    root.setLeft(seriesBox);
    
    
    primaryStage.setScene(scene);
    primaryStage.show();
    
    
  }

  
  private void redraw() {

    updateSeries();
  
  }

  private void fillSerie(int index, Series serie) {
    serie.setName(evaluator.names().get(index));
    for (int d=0;d<evaluator.depth();d++) {
      serie.getData().add(new XYChart.Data(d, evaluator.value(index, d)));
    }
  }

  private void updateSeries() {

    for (Entry<Integer, Series> serieEntry : series.entrySet()) {
      int index = serieEntry.getKey();
      Series serie = serieEntry.getValue();
          
      
      for (int d=0;d<evaluator.depth();d++) {
        ((XYChart.Data)serie.getData().get(d)).setYValue(evaluator.value(index, d));
      }
    }
  }
  
  
  public static void main(String[] args) {
    Application.launch(args);
  }
}
