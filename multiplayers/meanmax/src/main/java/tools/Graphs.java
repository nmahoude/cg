package tools;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import meanmax.Game;
import meanmax.ai.ag.AG;

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
    
    stage.setTitle("Line Chart Sample");
    xAxis = new CategoryAxis();
    
    yAxis = new NumberAxis();

    yAxis.setAutoRanging(false);
    yAxis.setLowerBound(000);
    yAxis.setUpperBound(2000);
    
//    yAxis.setAutoRanging(true);
    
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

    lineChart.setTitle("Fitness AG vs random");

    lineChart.setCreateSymbols(false);
    lineChart.setAlternativeRowFillVisible(false);
    series1 = new XYChart.Series();
    series1.setName("Random/max");

    series2 = new XYChart.Series();
    series2.setName("Random/min");

    seriesAG1 = new XYChart.Series();
    seriesAG1.setName("AG");

    seriesAG2 = new XYChart.Series();
    seriesAG2.setName("AG2");

    
    xAxis.setLabel("Generations");

    //fillRandomSeries();
    fillSeriesAG();
    fillSeriesAG2();
    
    Scene scene = new Scene(lineChart);
    lineChart.getData().addAll(series1, series2, seriesAG1, seriesAG2);
    stage.setScene(scene);
    stage.show();
  }

  private void fillSeriesAG() {
    AG ag = new AG();
    ag.setup();

    Game.start = System.currentTimeMillis() + 5_000_000; // give lot of time
    for (int gen=0;gen<ITERATIONS;gen++) {
      ag.nextGeneration();

      seriesAG1.getData().add(new XYChart.Data(""+gen, ag.bestSolution.energy));
    }
  }
  
  private void fillSeriesAG2() {
    meanmax.ai.ag2.AG ag = new meanmax.ai.ag2.AG();
    ag.setup();

    Game.start = System.currentTimeMillis() + 5_000_000; // give lot of time
    for (int gen=0;gen<ITERATIONS;gen++) {
      ag.nextGeneration();

      seriesAG2.getData().add(new XYChart.Data(""+gen, ag.bestSolution.energy));
    }
  }

  private void fillRandomSeries() {
    AG ag = new AG();
    AG.POPULATION = 150;
    AG.SURVIVOR_POP_SIZE = 0;
    AG.POPULATION_RANDOM_START = 0;
    
    Game.start = System.currentTimeMillis() + 5_000_000; // give lot of time
    for (int gen=0;gen<ITERATIONS;gen++) {
      ag.nextGeneration();

      seriesAG2.getData().add(new XYChart.Data(""+gen, ag.bestSolution.energy));
      ag.bestSolution.energy= Double.NEGATIVE_INFINITY;
    }
  }

  public static void main(String[] args) {
    GameSetup.setup();
    launch(args);
  }
}
