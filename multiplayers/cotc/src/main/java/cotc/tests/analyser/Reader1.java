package cotc.tests.analyser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class Reader1 extends Application {
  static List<String> shipsLines = new ArrayList<>();
  static List<String> cannonballLines = new ArrayList<>();
  static List<String> minesLines = new ArrayList<>();
  static List<String> barrelLines = new ArrayList<>();
  static List<String> damageLines = new ArrayList<>();
  
  static Game game = new Game();
  
  public static void main(String[] args) {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(
            Reader1.class.getClassLoader().getResourceAsStream("example.json")))) {
      String sCurrentLine;
      String viewerLine = "";
      while ((sCurrentLine = br.readLine()) != null) {
        if (sCurrentLine.contains("viewer")) {
          viewerLine = sCurrentLine;
        }
      }

      System.out.println(viewerLine);
      
      doFrameRegexp(viewerLine);
      
      launch(args);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void doFrameRegexp(String viewerLine) {
    // regexp for frames
    Pattern pattern = Pattern.compile("frames: \\[(.*)\\]");
    Matcher matcher = pattern.matcher(viewerLine);
    matcher.find();
    String frames = matcher.group(1);
    
    System.out.println(frames);
    
    doKeyFrames(frames);
    
  }

  private static void doKeyFrames(String frames) {
    Pattern pattern = Pattern.compile("\\\\\\\"KEY_FRAME (\\d*)(.+?)]");
    Matcher matcher = pattern.matcher(frames);
    while (matcher.find()) {
//      System.out.println(matcher.group());
      doKeyFrameEntry(matcher.group());
    }
  }

  static String inputs[] = new String[256];
  static int playerCount = 2;
  static int shipCount = 3;
  private static void doKeyFrameEntry(String group) {
    Pattern pattern = Pattern.compile("\\\\\\\"(.+?);*\\\\\\\"");
    Matcher matcher = pattern.matcher(group);
    
    int i=0;
    while (matcher.find()) {
      inputs[i++] = matcher.group(1);
    }
    
    game.readFrame(inputs);
    
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
    XYChart.Series series = new XYChart.Series();
    series.setName("score");
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
      
      series.getData().add(new XYChart.Data(turn++, (int)(score-score2)));
    }
    
    Scene scene  = new Scene(lineChart,800,600);
    lineChart.getData().add(series);
   
    stage.setScene(scene);
    stage.show();    
  }
}
