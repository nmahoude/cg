package cotc.tests.analyser;

import cotc.GameState;
import cotc.Team;
import cotc.ai.ag.Feature;
import cotc.ai.ag.FeatureWeight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Reader2 extends Application {
  
  private static Game game;
  
  public static void main(String[] args) {
//    GameReader1 battleReader = new GameReader1();
//    battleReader.read("findById.json");
//    game = battleReader.game;
    
    BattlesReader br = new BattlesReader();
    game = br.game;
    br.readOneBattle("212955176");
    
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    FeatureWeight weights = new FeatureWeight();
    
    
    stage.setTitle("Battle viewer");
    //defining the axes
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Turns");
    //creating the chart
    final LineChart<Number,Number> lineChart = 
            new LineChart<Number,Number>(xAxis,yAxis);
            
    lineChart.setTitle("Eval over turns");
    //defining a series
    XYChart.Series myScore = new XYChart.Series();
    XYChart.Series hisScore = new XYChart.Series();
    XYChart.Series combinedScore = new XYChart.Series();
    
    myScore.setName("My score");
    hisScore.setName("his score");
    combinedScore.setName("Combined");
    
    int turn = 0;
    for (int i=0;i<game.frames.size();i++) {
      Frame frame = game.frames.get(i);
      GameState state = frame.frameToState();
      Feature feature= new Feature();
      feature.calculateFeaturesFinal(state);
      double score = feature.applyWeights(weights);
      
      Team swap = state.teams[0];
      state.teams[0] = state.teams[1];
      state.teams[1] = swap;
      Feature feature2= new Feature();
      feature2.calculateFeaturesFinal(state);
      double score2 = feature2.applyWeights(weights);
      
      myScore.getData().add(new XYChart.Data(turn, (int)(score)));
      hisScore.getData().add(new XYChart.Data(turn, (int)(score2)));
      combinedScore.getData().add(new XYChart.Data(turn, (int)(score-score2)));
      turn++;
    }
    
    Scene scene  = new Scene(lineChart,800,600);
    lineChart.getData().add(myScore);
    lineChart.getData().add(hisScore);
    lineChart.getData().add(combinedScore);

    stage.setScene(scene);
    stage.show();    
  }


}
