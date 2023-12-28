package cgfx.components;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * A chart that display double values
 * 
 * You need to add @EvalNode to the chart to display values
 */
public class EvalChart extends BorderPane {

  private MyLineChart chart;
  private List<String> names = new ArrayList<>();
  private List<String> hidden= new ArrayList<>();
  private List<EvalNode> nodes = new ArrayList<>();
  
  static class MyLineChart extends LineChart {
    private Line line;
    int currentIndex;
    public MyLineChart(Axis xAxis, Axis yAxis) {
      super(xAxis, yAxis);

      line = new Line();
      this.getPlotChildren().add(line);

      setCurrentIndex(0);
    }
    
    public void setCurrentIndex(int index) {
      this.currentIndex = index;
      this.layoutPlotChildren();
    }
    
    @Override
      protected void layoutPlotChildren() {
        super.layoutPlotChildren();

        line.setStroke(new Color(0, 0, 0, 0.3));
        line.setStartX(getXAxis().getDisplayPosition(currentIndex)+0.5);
        line.setEndX(line.getStartX());
        line.setStartY(0);
        line.setEndY(getBoundsInLocal().getHeight());
        line.toFront();
      }
  }
  
  
  public EvalChart(List<String> names) {
    this.names.addAll(names);
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();

    xAxis.setLabel("turns");
    yAxis.setLabel("values");

    chart = new MyLineChart(xAxis, yAxis);
    
    this.setCenter(chart);
    for (String name : names) {
      XYChart.Series serie = new XYChart.Series();
      serie.setName(name);
      
      chart.getData().add(serie);
      //serie.getNode().setStyle("-fx-stroke: "+"rgb(255,0,0)"+";"+"-fx-node: #0181e2;");
    }
  }

  public void displayVerticalAt(int index) {
    this.chart.setCurrentIndex(index);
  }
  
  public void addNode(EvalNode node) {
    nodes.add(node);
    update();
  }
  
  public void clearNodes() {
    nodes.clear();
    update();
  }
  
  public void showOnOff(String name) {
    if (hidden.contains(name)) {
      hidden.remove(name); 
    } else {
      hidden.add(name);
    }
    update();
  }
  
  public void update() {
    for (int i=0;i<chart.getData().size();i++) {
      ((XYChart.Series) chart.getData().get(i)).getData().clear();
    }
    
    for (int n=0;n<nodes.size();n++) {
      EvalNode node = nodes.get(n);
      for (int i = 0; i < names.size(); i++) {
        if (hidden.contains(names.get(i))) {
          ((XYChart.Series) chart.getData().get(i)).getData().add(new XYChart.Data(n, 0.0));
        } else {
          ((XYChart.Series) chart.getData().get(i)).getData().add(new XYChart.Data(n, node.value(i)));
        }
      }
    }
  }

  public LineChart getNode() {
    return chart;
  }
}
