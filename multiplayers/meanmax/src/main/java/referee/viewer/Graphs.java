package referee.viewer;

import java.util.Locale;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import meanmax.Game;
import meanmax.ai.ag.AG;
import meanmax.ai.ag.AGSolution;
import meanmax.ai.mc.MC;
import meanmax.entities.Entity;

public class Graphs extends Application {
  private static final int ITERATIONS = 500;
  private CategoryAxis xAxis;
  
  private XYChart.Series series1;
  private XYChart.Series series2;

  private XYChart.Series seriesAG1;
  private XYChart.Series seriesAG2;

  private NumberAxis yAxis;
  
  @Override
  public void start(Stage stage) throws Exception {
    initState();
    
    stage.setTitle("Line Chart Sample");
    xAxis = new CategoryAxis();
    
    yAxis = new NumberAxis();
    yAxis.setAutoRanging(false);
    yAxis.setLowerBound(15000);
    yAxis.setUpperBound(30000);
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

    lineChart.setTitle("Fitness AG vs random");

    lineChart.setCreateSymbols(false);
    lineChart.setAlternativeRowFillVisible(false);
    series1 = new XYChart.Series();
    series1.setName("Random/max");

    series2 = new XYChart.Series();
    series2.setName("Random/min");

    seriesAG1 = new XYChart.Series();
    seriesAG1.setName("AG/max");

    seriesAG2 = new XYChart.Series();
    seriesAG2.setName("AG/min");

    
    xAxis.setLabel("Generations");

    fillSeries();
    fillSeriesAG();
    
    Scene scene = new Scene(lineChart);
    lineChart.getData().addAll(series1, series2, seriesAG1, seriesAG2);
    stage.setScene(scene);
    stage.show();
  }

  private void fillSeriesAG() {
    AG ag = new AG();
    ag.setup();
    
    
    Game.start = System.nanoTime() + 35_000_000;
    for (int gen=0;gen<ITERATIONS;gen++) {
      ag.nextGeneration();
      
      seriesAG1.getData().add(new XYChart.Data(""+gen, ag.bestSolution.energy));
    }
  }
  
  private void fillSeries() {
    MC mc = new MC();
    mc.setup();
    
    Game.start = System.nanoTime() + 35_000_000;
    for (int gen=0;gen<ITERATIONS;gen++) {
      for (int i=0;i<AG.POPULATION;i++) {
        mc.oneGeneration();
      }
      
      series1.getData().add(new XYChart.Data(""+gen, mc.bestSolution.energy));
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
  

  private void initState() {
    read(0,0,0,0.5,400.0,-2055.0,4204.0,12.0,-21.0,-1,-1);
    read(1,1,0,1.5,400.0,-483.0,4325.0,19.0,-19.0,-1,-1);
    read(2,2,0,1.0,400.0,3327.0,2692.0,-7.0,-225.0,-1,-1);
    read(3,0,1,0.5,400.0,-2238.0,-4364.0,312.0,-365.0,-1,-1);
    read(4,1,1,1.5,400.0,-3555.0,-2565.0,-28.0,37.0,-1,-1);
    read(5,2,1,1.0,400.0,-3967.0,1331.0,220.0,-47.0,-1,-1);
    read(6,0,2,0.5,400.0,4718.0,278.0,15.0,480.0,-1,-1);
    read(7,1,2,1.5,400.0,4219.0,-1778.0,137.0,-31.0,-1,-1);
    read(8,2,2,1.0,400.0,726.0,-4159.0,-147.0,170.0,-1,-1);
    read(9,3,-1,3.0,650.0,4086.0,6975.0,-172.0,-293.0,1,5);
    read(10,3,-1,3.0,650.0,-8083.0,50.0,340.0,-2.0,1,5);
    read(11,3,-1,3.0,650.0,3998.0,-7025.0,-168.0,296.0,1,5);
    read(12,3,-1,3.0,850.0,-2554.0,7879.0,105.0,-324.0,1,9);
    read(13,3,-1,3.0,850.0,-5547.0,-6151.0,228.0,252.0,1,9);
    read(14,3,-1,3.0,850.0,8101.0,-1728.0,-332.0,71.0,1,9);
    read(15,3,-1,3.0,600.0,-113.0,8032.0,5.0,-340.0,1,4);
    read(16,3,-1,3.0,600.0,-6899.0,-4115.0,292.0,174.0,1,4);
    read(17,3,-1,3.0,600.0,7013.0,-3918.0,-297.0,166.0,1,4);
    Game.backup();
  }
  
  static void read(int unitId, int unitType, int playerId, double mass, double radius, double x, double y, double vx, double vy, int extra, int extra2) {
    Scanner in = new Scanner(""+unitId+" "+unitType+" "+playerId+" "+mass+" "+(int)radius+" "+(int)x+" "+(int)y+" "+(int)vx+" "+(int)vy+" "+extra+" "+extra2);
    in.useLocale(Locale.ENGLISH);
    Game.backup(); // we backup the number of entities each turn ...
  }
}
