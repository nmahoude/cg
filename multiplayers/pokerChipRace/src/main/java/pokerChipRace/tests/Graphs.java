package pokerChipRace.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import pokerChipRace.GameState;
import pokerChipRace.ai.AG;
import pokerChipRace.ai.AGSolution;
import pokerChipRace.entities.Entity;

public class Graphs extends Application {
  private static final int ITERATIONS = 500;
  private CategoryAxis xAxis;
  private XYChart.Series series1;
  private XYChart.Series series2;

  private XYChart.Series seriesAG1;
  private XYChart.Series seriesAG2;

  private GameState state = new GameState();
  
  @Override
  public void start(Stage stage) throws Exception {
    initState();
    
    stage.setTitle("Line Chart Sample");
    xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

    lineChart.setTitle("AG, convergence");

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
    ag.setState(state);

    ag.createPopulations();

    ag.initFirstPopulation();
    
    for (int gen=0;gen<ITERATIONS;gen++) {
      ag.nextPopulation();
      ag.swapPopulations();
      
      seriesAG1.getData().add(new XYChart.Data(""+gen, ag.best.energy));
      // get the min
      double min = Double.POSITIVE_INFINITY;
      for (int i=0;i<AG.POP_SIZE;i++) {
        if (ag.population[i].energy < min) {
          min = Math.max(-11, ag.population[i].energy);
        }
      }
      seriesAG2.getData().add(new XYChart.Data(""+gen, min));
    }
    
  }

  private void fillSeries() {
    AG ag = new AG();
    ag.setState(state);

    for (int gen=0;gen<ITERATIONS;gen++) {
      double best = Double.NEGATIVE_INFINITY;
      double min = Double.POSITIVE_INFINITY;
      for (int i=0;i<AG.POP_SIZE;i++) {
        AGSolution sol = new AGSolution(state.myChips.length);
        sol.randomize();
        ag.play(sol);
        if (sol.energy > best) {
          best = sol.energy;
        }
        if (sol.energy < min) {
          min = Math.max(-10, sol.energy);
        }
      }
      series1.getData().add(new XYChart.Data(""+gen, best));
      series2.getData().add(new XYChart.Data(""+gen, min));
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
  

  private void initState() {
    
    readEntity(4,1,28.269495010375977, 62.118377685546875, 0.0, 0.0,24.0);
    readEntity(5,1,117.4542236328125, 250.11058044433594, 0.0, 0.0,24.0);
    readEntity(6,1,170.80821228027344, 149.8305206298828, 0.0, 0.0,24.0);
    readEntity(7,1,240.35940551757812, 484.6694030761719, 0.0, 0.0,24.0);
    readEntity(0,0,696.939208984375, 48.049530029296875, 0.0, 0.0,24.0);
    readEntity(1,0,277.2272033691406, 321.293701171875, 0.0, 0.0,24.0);
    readEntity(2,0,450.101318359375, 200.92941284179688, 0.0, 0.0,24.0);
    readEntity(3,0,320.15869140625, 134.68077087402344, 0.0, 0.0,24.0);
    readEntity(8,2,742.5584716796875, 155.23597717285156, 0.0, 0.0,24.0);
    readEntity(9,2,640.8668823242188, 339.8033142089844, 0.0, 0.0,24.0);
    readEntity(10,2,726.5615234375, 420.1160583496094, 0.0, 0.0,24.0);
    readEntity(11,2,540.5764770507812, 43.98273468017578, 0.0, 0.0,24.0);
    readEntity(12,-1,19.674983978271484, 415.80462646484375, -3.0675508975982666, 1.7326430082321167,11.872926712036133);
    readEntity(13,-1,396.2111511230469, 31.345638275146484, 1.0081160068511963, 1.521314024925232,19.83572006225586);
    readEntity(14,-1,563.132080078125, 489.2593994140625, -1.898347020149231, 1.3789730072021484,20.985376358032227);
    readEntity(15,-1,749.16748046875, 313.90936279296875, 0.0, 0.0,24.28710174560547);
    readEntity(16,-1,525.1819458007812, 311.83209228515625, 0.0, 0.0,24.641206741333008);
    readEntity(17,-1,641.3917846679688, 224.97232055664062, 0.0, 0.0,24.581615447998047);
    readEntity(18,-1,470.74676513671875, 425.7611999511719, 5.390584945678711, 1.5201740264892578,20.421085357666016);
    readEntity(19,-1,343.8877868652344, 225.65264892578125, 0.5115640163421631, 0.16506700217723846,13.233302116394043);
    readEntity(20,-1,381.271240234375, 465.5465087890625, -7.854773998260498, 2.5974628925323486,13.728981018066406);
    readEntity(21,-1,98.2670669555664, 375.3666076660156, -0.3384070098400116, 2.7375710010528564,10.88565444946289);
    readEntity(22,-1,601.2674560546875, 139.14471435546875, -0.0, 0.0,24.88776206970215);
    readEntity(23,-1,779.8758544921875, 496.51177978515625, -0.24723899364471436, 0.34254199266433716,16.830278396606445);
    readEntity(24,-1,368.77459716796875, 378.5141906738281, -0.29928600788116455, 0.36537298560142517,22.916175842285156);
    readEntity(25,-1,35.50079345703125, 277.7336730957031, 3.397490978240967, 0.19216600060462952,14.336982727050781);
    readEntity(26,-1,455.89501953125, 60.501060485839844, 3.3983609676361084, 9.134573936462402,12.034594535827637);
    readEntity(27,-1,26.668434143066406, 191.44644165039062, -0.0, 0.0,24.493167877197266);
    readEntity(28,-1,177.0920867919922, 311.4151306152344, -1.3511240482330322, 0.5259280204772949,15.178531646728516);
    readEntity(29,-1,424.77911376953125, 87.85401916503906, 0.5526109933853149, 3.853355884552002,14.01296329498291);
    readEntity(30,-1,391.0923156738281, 312.0578308105469, -9.291930198669434, 1.9998719692230225,17.876035690307617);
    readEntity(31,-1,213.81735229492188, 68.8546142578125, 5.717473030090332, 2.8549630641937256,18.6209716796875);
    readEntity(32,-1,69.01516723632812, 488.6913146972656, 1.5735169649124146, 0.45401298999786377,13.372647285461426);
    state.backup();
  }

  private void readEntity(int id, int owner, double x, double y, double vx, double vy, double radius) {
    Entity entity = state.getInitialChip(id);
    entity.update(owner, x,y, radius, vx, vy);
    if (owner == state.myId) {
      state.myChips.add(entity);
    }
  }
}
