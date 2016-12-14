package charts;

import ag.AG;
import ag.AGSolution;
import ag.FastRand;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import stc2.BitBoard;
import stc2.Game;
 
 
public class LineChartSample extends Application {
 
    private static final int GENERATION_COUNT = 1000;
    private XYChart.Series series1;
    private XYChart.Series series2;
    private XYChart.Series series3;
    private XYChart.Series series4;
    
    private CategoryAxis xAxis;

    double bestMax = Double.NEGATIVE_INFINITY;

    public void fillSeries() {
      series1.getData().clear();
      series2.getData().clear();
      series3.getData().clear();
      series4.getData().clear();
      
      Game game = new Game();
      setNextBlocks2(game,
          "34 54 13 15 54 11 11 13"
          );
      
      BitBoard board = new BitBoard();
      prepareBoard(board ,
          "......",
          "......",
          "...1..",
          "...X..",
          "...X.3",
          ".X.X52",
          "XXXXXX",
          "XXXXXX",
          "XXXXXX",
          "XXX2XX",
          "XXX2XX",
          "X1X2XX");
      
      AG ag = new AG();
      ag.simulate(game, board, 8, 0, null); // one turn
      
      double total = 0;
      int count = 0;
      for (int i=1;i<GENERATION_COUNT;i++) { 
        ag.doOneGeneration(game);
        reportGeneration(i, ag, total, count);
      }

      System.err.println("Best solution with score "+ag.bestSolution.energy+" / pts="+ag.bestSolution.points);
      System.err.println("pop : "+ag.bestSolution);
    }
    
    private void setNextBlocks2(Game game, String string) {
      String[] blocks = string.split(" ");
      setNextBlocks(game, blocks);
    }

    private void reportGeneration(int generation, AG ag, double total, int count) {
      //System.err.println("Generation "+generation);
      double max = Double.NEGATIVE_INFINITY;
      double min = Double.MAX_VALUE;
      int maxPoints  = Integer.MIN_VALUE;
      
      for (int i=0;i<AG.POPULATION_COUNT;i++) {
        if (ag.populations2[i].energy > -1_000_000) {
          total+=ag.populations2[i].energy;
          count++;
      
          maxPoints = (int)Math.max(maxPoints, ag.populations2[i].points);
          max = Math.max(max, ag.populations2[i].energy);
          min = Math.min(min, ag.populations2[i].energy);
        }
      }
      double average = total/count;
//      System.err.println("count : "+count);
//      System.err.println("energy mean : "+(total/count));
//      System.err.println("champion: " +ag.bestSolution.energy);
      addData(generation, max, min, average, maxPoints);
    }
  
    
    public void addData(int generation, double max, double min, double average, int maxPoints) {
      if (max > bestMax) {
        bestMax = max;
      }
      series1.getData().add(new XYChart.Data(""+generation, max));
      series2.getData().add(new XYChart.Data(""+generation, average));
      series3.getData().add(new XYChart.Data(""+generation, min));
      series4.getData().add(new XYChart.Data(""+generation, maxPoints));
      
    }
    
    @Override 
    public void start(Stage stage) {
    stage.setTitle("Line Chart Sample");
    xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

    lineChart.setTitle("AG, convergence");

    lineChart.setCreateSymbols(false);
    lineChart.setAlternativeRowFillVisible(false);
    series1 = new XYChart.Series();
    series1.setName("Top");

    series2 = new XYChart.Series();
    series2.setName("Avg");

    series3 = new XYChart.Series();
    series3.setName("Min");

    series4 = new XYChart.Series();
    series4.setName("Points");
    fillSeries();

    xAxis.setLabel("Generation. Max=" + bestMax + " known max = 1000");

    Slider slider = new Slider();
    slider.setMin(0);
    slider.setMax(100);
    slider.setValue(42);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(50);
    slider.setMinorTickCount(5);
    slider.setBlockIncrement(1);
    slider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov,
          Number oldVal, Number newVal) {

        AGSolution.rand = new FastRand(newVal.intValue());
        fillSeries();
      }
    });

    GridPane grid = new GridPane();
    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(100);
    grid.getColumnConstraints().add(column1);

    RowConstraints row1 = new RowConstraints();
    row1.setPercentHeight(100);
    grid.getRowConstraints().add(row1);
    
    grid.add(lineChart, 0, 0);
    grid.add(slider, 0, 1);

    Scene scene = new Scene(grid);
    lineChart.getData().addAll(series1, series2, series3, series4);
    // scene.getStylesheets().add("linechartsample/Chart.css");
    stage.setScene(scene);
    stage.show();
  }
 
 
  public static void main(String[] args) {
    launch(args);
  }
    
    static public void prepareBoard(BitBoard board, String... rows) {
      if (rows.length != 12) {
        throw new UnsupportedOperationException("12 rows for board");
      }
      int index = 0;
      for (String row : rows) {
        board.updateRow(index++, row);
      }
      board.buildCompleteLayerMask();
    }
    public static void setNextBlocks(Game game, String... blocks) {
      int index = 0;
      for (String block : blocks) {
        game.nextBalls[index]  = block.charAt(0)-'0';
        game.nextBalls2[index] = block.charAt(1)-'0';
        index++;
      }
    }
}