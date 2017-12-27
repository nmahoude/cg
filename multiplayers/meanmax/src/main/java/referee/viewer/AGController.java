package referee.viewer;

import java.util.Locale;
import java.util.Scanner;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import meanmax.Game;
import meanmax.ai.ag.AG;
import referee.Referee;

public class AGController extends Application {
  private static Referee controller;
  private PlayField playfield;
  static AG ag = new AG();
  private AGSolutionNode[] solutionNodes;
  private VBox root;
  Button restartSim = new Button();
  
  private void addRestartSim(HBox controls) {
    restartSim.setLayoutX(10);
    restartSim.setLayoutY(10);
    restartSim.setText("restart");
    restartSim.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        ag.setup();
      }
    });
    controls.getChildren().add(restartSim);
  }
  
  @Override
  public void start(Stage stage) throws Exception {
    root = new VBox();
    
    playfield = new PlayField();
    double scale = 0.05d;
    playfield.setScaleX(scale);
    playfield.setScaleY(scale);
    playfield.setTranslateX(Game.MAP_RADIUS * scale);
    playfield.setTranslateY(Game.MAP_RADIUS * scale); 
    root.getChildren().add(playfield);

    HBox controls = new HBox();
    addRestartSim(controls);
    root.getChildren().add(controls);

    initSolutions();
    
    Scene scene = new Scene(root);
    //scene.setFill(Color.BLACK);

    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(500),
        ae -> update()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
    
    
    stage.setScene(scene);
    stage.setWidth(1600);
    stage.setHeight(1080);
    stage.show();
  }

  
  private void initSolutions() {
    solutionNodes = new AGSolutionNode[AG.POPULATION];
    for (int i=0;i<AG.POPULATION;i++) {
      solutionNodes[i] = new AGSolutionNode();
      playfield.getChildren().add(solutionNodes[i]);
      solutionNodes[i].update(ag.solutions[i]);
    }
  }

  private void updateSolutions() {
    Game.start = System.nanoTime();
    ag.nextGeneration();
    for (int i=0;i<AG.POPULATION;i++) {
      solutionNodes[i].update(ag.solutions[i]);
    }
  }

  private Object update() {
    updateSolutions();
    return null;
  }
  
  public static void main(String[] args) throws Exception {
    setupGame();
    ag.setup();
    launch(args);
  }


  private static void setupGame() {
    read(0,0,0,0.5,400.0,2577.0,4227.0,669.0,-8.0,-1,-1);
    read(1,1,0,1.5,400.0,-2713.0,4875.0,-26.0,-65.0,-1,-1);
    read(2,2,0,1.0,400.0,-786.0,-951.0,178.0,-216.0,-1,-1);
    read(3,0,1,0.5,400.0,-4038.0,3637.0,198.0,153.0,-1,-1);
    read(4,1,1,1.5,400.0,-2645.0,3038.0,-18.0,-42.0,-1,-1);
    read(5,2,1,1.0,400.0,190.0,4653.0,564.0,152.0,-1,-1);
    read(6,0,2,0.5,400.0,1625.0,4127.0,378.0,-221.0,-1,-1);
    read(7,1,2,1.5,400.0,2280.0,3119.0,507.0,-240.0,-1,-1);
    read(8,2,2,1.0,400.0,-2229.0,2196.0,20.0,-164.0,-1,-1);
    read(32,3,-1,6.5,800.0,3063.0,-178.0,113.0,-7.0,8,8);
    read(34,3,-1,7.0,850.0,437.0,-2224.0,20.0,-95.0,9,9);
    read(36,3,-1,4.5,600.0,3640.0,2748.0,72.0,55.0,4,4);
    read(38,3,-1,6.5,800.0,-1363.0,-2135.0,-24.0,-35.0,8,8);
    read(45,3,-1,3.0,600.0,-4652.0,2603.0,152.0,-215.0,1,4);
    read(47,3,-1,3.0,850.0,-3641.0,5812.0,143.0,-229.0,1,9);
    read(48,5,-1,-1.0,1000.0,3495.0,3411.0,0.0,0.0,1,-1);
  }

  static void read(int unitId, int unitType, int playerId, double mass, double radius, double x, double y, double vx, double vy, int extra, int extra2) {
    Scanner in = new Scanner(""+unitId+" "+unitType+" "+playerId+" "+mass+" "+(int)radius+" "+(int)x+" "+(int)y+" "+(int)vx+" "+(int)vy+" "+extra+" "+extra2);
    in.useLocale(Locale.ENGLISH);
    Game.readOneUnit(in);
    Game.backup(); // we backup the number of entities each turn ...
  }
}
