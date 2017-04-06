package csbgui;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.game.Referee;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import trigonometry.Point;

public class Gui extends Application {
  static final int ratio = 10;
  static Referee referee = new Referee();
  
  Button playPause = new Button();
  boolean pause = true;
  
  Circle[] cps = new Circle[100];
  Circle[] pods = new Circle[4];
  Line[] podTargets = new Line[4];
  Line[] podSpeeds = new Line[4];
  Line[] podDirections = new Line[4];
  
  @Override 
  public void start(Stage stage) {
    Group root = new Group();
    Scene scene = new Scene(root, 16000/ratio, 9000/ratio);

    playPause.setLayoutX(10);
    playPause.setLayoutY(10);
    playPause.setText(">>");
    playPause.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        pause = !pause;
      }
    });
    root.getChildren().add(playPause);
    
    
    int i=0;
    for (CheckPoint cp : referee.checkPoints) {
      Text text = new Text(cp.position.x / ratio, cp.position.y / ratio, ""+i);
      text.setFill(Color.BLUE);
      root.getChildren().add(text);
      
      Circle circle = new Circle();
      circle.setCenterX(cp.position.x / ratio);
      circle.setCenterY(cp.position.y / ratio);
      circle.setRadius(cp.radius / ratio);
      circle.setFill(Color.TRANSPARENT);
      circle.setStroke(Color.BLUE);
      cps[i++] = circle;
      root.getChildren().add(circle);
    }
    
    i=0;
    for (Pod pod : referee.pods) {
      Circle circle = new Circle();
      circle.setRadius(pod.radius / ratio);
      circle.setFill(Color.TRANSPARENT);
      if (i<2) {
        circle.setStroke(Color.ORANGE);
      } else {
        circle.setStroke(Color.PINK);
      }
      pods[i] = circle;

      Line line = new Line();
      line.setFill(Color.ORANGE);
      line.setStroke(Color.ORANGE);
      podTargets[i] = line;
      
      Line speedLine = new Line();
      speedLine.setStroke(Color.DARKVIOLET);
      podSpeeds[i] = speedLine;

      Line directionLine = new Line();
      directionLine.setStroke(Color.RED);
      podDirections[i] = directionLine;

      updatePodRepresentation(i);
      
      root.getChildren().add(circle);
      root.getChildren().add(line);
      root.getChildren().add(speedLine);
      root.getChildren().add(directionLine);
      i++;
    }
    
    
    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(120),
        ae -> updatePods()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
    
    scene.setFill(Color.GREY);
    stage.setScene(scene);
    stage.show();
  }

  private void updatePodRepresentation(int index) {
    Pod pod = referee.pods[index];
    Point targetPoint = referee.target[index];

    Circle circle = pods[index];
    circle.setCenterX(pod.position.x / ratio);
    circle.setCenterY(pod.position.y / ratio);

    Line line = podTargets[index];
    line.setStartX(pod.position.x / ratio);
    line.setStartY(pod.position.y / ratio);
    line.setEndX(targetPoint.x / ratio);
    line.setEndY(targetPoint.y / ratio);

    line = podSpeeds[index];
    line.setStartX(pod.position.x / ratio);
    line.setStartY(pod.position.y / ratio);
    line.setEndX((pod.position.x + pod.speed.vx) / ratio);
    line.setEndY((pod.position.y + pod.speed.vy) / ratio);
    
    line = podDirections[index];
    line.setStartX(pod.position.x / ratio);
    line.setStartY(pod.position.y / ratio);
    line.setEndX((pod.position.x + pod.radius*pod.direction.vx) / ratio);
    line.setEndY((pod.position.y + pod.radius*pod.direction.vy) / ratio);
    
  }
  private Object updatePods() {
    if (pause) return null;
    
    
    for (int i=0;i<4;i++) {
      Pod pod = referee.pods[i];
      CheckPoint cp =referee.checkPoints[pod.nextCheckPointId];
      String target = ""+(int)(cp.position.x)+" "+(int)(cp.position.y);
      referee.handlePlayerOutput(0, 0, i, new String[]{target + " 100"});
    }
    
    try {
      //int ncp = referee.pods[0].nextCheckPointId;
      referee.updateGame(0);
//      if (ncp != referee.pods[0].nextCheckPointId) {
//        pause = true;
//      }
    } catch (Exception e) {
    }

    for (int i=0;i<4;i++) {
      updatePodRepresentation(i);
    }
    return null;
  }

  public static void main(String[] args) throws Exception {
    referee.initReferee(2 /**seed*/, 2 /*players*/);
    
    launch(args);
  }
}
