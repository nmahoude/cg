package csbgui;

import java.util.Random;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.game.Referee;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Gui extends Application {
  static final int ratio = 10;
  static Referee referee = new Referee();
  
  Circle[] cps = new Circle[100];
  Circle[] pods = new Circle[4];
  @Override 
  public void start(Stage stage) {
    Group root = new Group();
    
    Scene scene = new Scene(root, 16000/ratio, 9000/ratio);
    
    int i=0;
    for (CheckPoint cp : referee.checkpoints) {
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
      circle.setCenterX(pod.position.x / ratio);
      circle.setCenterY(pod.position.y / ratio);
      circle.setRadius(pod.radius / ratio);
      circle.setFill(Color.TRANSPARENT);
      if (i<2) {
        circle.setStroke(Color.ORANGE);
      } else {
        circle.setStroke(Color.PINK);
      }
      pods[i++] = circle;
      root.getChildren().add(circle);
    }
    
    
    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(60),
        ae -> updatePods()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
    
    stage.setScene(scene);
    stage.show();
  }
  
  private Object updatePods() {
//    for (int i=0;i<4;i++) {
//      map.pods[i].apply(0, 0);
//      map.pods[i].position = map.pods[i].position.add(map.pods[i].speed);
//    }
//    
//    for (int i=0;i<4;i++) {
//      Pod pod = map.pods[i];
//      pods[i].setCenterX(pod.position.x / ratio);
//      pods[i].setCenterY(pod.position.y / ratio);
//    }
    return null;
  }

  public static void main(String[] args) throws Exception {
    referee.initReferee(new Random().nextInt(), /**seed*/ 2 /**players*/, 5/*checkpoints*/);
    
    launch(args);
  }
}
