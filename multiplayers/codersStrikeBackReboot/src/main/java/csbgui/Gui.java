package csbgui;

import java.util.ArrayList;
import java.util.List;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.game.Referee;
import csb.simulation.AGSolution;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import trigonometry.Point;

public class Gui extends Application {
  public static final int ratio = 10;
  static Referee referee = new Referee();
  
  Button playPause = new Button();
  boolean pause = true;

  Slider slider = new Slider();
  int currentFrame = 0;
  
  Circle[] cps = new Circle[100];
  Rectangle[] rectangles = new Rectangle[10_000];
  double scores[]  =new double[rectangles.length];
  
  PodRepresentation podRepresentations[] = new PodRepresentation[4];
  {
    for (int i=0;i<4;i++) {
      podRepresentations[i] = new PodRepresentation();
    }
  }
  List<Frame> frames = new ArrayList<>();

  @Override 
  public void start(Stage stage) {
    VBox root = new VBox();
    Rectangle rectangle = new Rectangle(16000/ratio, 9000/ratio);
    Group playfieldRoot = new Group(rectangle);
    Pane playfield = new Pane(rectangle);
    playfield.setManaged(false);
    playfield.maxWidth(16000/ratio);
    playfield.maxHeight(9000/ratio);
    playfield.minWidth(16000/ratio);
    playfield.minHeight(9000/ratio);
    playfieldRoot.setManaged(false);
    playfieldRoot.setAutoSizeChildren(false);
    playfieldRoot.getChildren().add(playfield);
    
    HBox controls = new HBox();
    root.getChildren().add(playfieldRoot);
    root.getChildren().add(controls);
    Scene scene = new Scene(root);
    
    // play/pause button
    playPause.setLayoutX(10);
    playPause.setLayoutY(10);
    playPause.setText(">>");
    playPause.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        pause = !pause;
      }
    });
    controls.getChildren().add(playPause);
    
    // frame slider
    slider.setLayoutX(100);
    slider.setLayoutY(10);
    slider.setMajorTickUnit(10);
    slider.setMinorTickCount(1);
    slider.setBlockIncrement(1);
    slider.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        currentFrame = newValue.intValue();
      }
    });    
    controls.getChildren().add(slider);

    for (int i=0;i<rectangles.length;i++) {
      rectangles[i] = new Rectangle();
      rectangles[i].setWidth(1);
      rectangles[i].setHeight(1);
      playfield.getChildren().add(rectangles[i]);
    }

    
    int i=0;
    for (CheckPoint cp : referee.checkPoints) {
      Text text = new Text(cp.position.x / ratio, cp.position.y / ratio, ""+i);
      text.setFill(Color.BLUE);
      playfield.getChildren().add(text);
      
      Circle circle = new Circle();
      circle.setCenterX(cp.position.x / ratio);
      circle.setCenterY(cp.position.y / ratio);
      circle.setRadius(cp.radius / ratio);
      circle.setFill(Color.TRANSPARENT);
      circle.setStroke(Color.BLUE);
      cps[i++] = circle;
      playfield.getChildren().add(circle);
    }
    
    for (Pod pod : referee.pods) {
      podRepresentations[pod.id].build(playfield, pod);
    }
    
    updateFrames();
    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(120),
        ae -> updatePods()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();

    for (i=0;i<referee.playerCount;i++) {
      updatePodRepresentation(i);
    }
    
    scene.setFill(Color.GREY);
    stage.setScene(scene);
    stage.show();
  }

  private void updateFrames() {
    Frame frame = new Frame();
    for (int i=0;i<referee.playerCount;i++) {
      frame.pods[i] = referee.pods[i].clone();
      frame.targetPoints[i] = referee.target[i];
    }
    frames.add(frame);
    currentFrame = frames.size()-1;
    
    slider.setMin(0);
    slider.setMax(frames.size()-1);
    slider.setValue(currentFrame);
  }

  private void updatePodRepresentation(int index) {
    Pod pod = frames.get(currentFrame).pods[index];
    Point targetPoint = frames.get(currentFrame).targetPoints[index];
    podRepresentations[index].update(pod, targetPoint);
  }

  private Object updatePods() {
    if (!pause) {
      // case 1
      {
        int AGSteps[] = new int[11];
        AGSolution best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        double minScore =  Double.POSITIVE_INFINITY;
        for (int i=0;i<rectangles.length;i++) {
          AGSolution solution = new AGSolution(referee.pods, referee.checkPoints);
          solution.zero();
          solution.moveAndEvaluate();
          double score = solution.score1;
          rectangles[i].setX(solution.pods[0].position.x / ratio);
          rectangles[i].setY(solution.pods[0].position.y / ratio);
          solution.reset();

          scores[i] = score;
          minScore = Math.min(minScore, score);
          if( score > bestScore) {
            bestScore = score;
            best = solution;
          }
        }
        for (int i=0;i<rectangles.length;i++){
          double colorScore = Math.max(0, Math.min(1, (float) (scores[i]-minScore) / (bestScore-minScore)));
          int index = (int)(colorScore * 10);
          AGSteps[index]++;    
          rectangles[i].setFill(new Color(colorScore, 1.0-colorScore,  0, 1));
        }
        System.out.println("steps: ");
        for (int i=0;i<11;i++) {
          System.out.print(AGSteps[i]+ " ");
        }
        System.out.println();
        referee.handlePlayerOutput(0, 0, 0, new String[]{best.actionOutput(0)});
      }
      for (int i=1;i<referee.playerCount;i++) {
        Pod pod = referee.pods[i];
        CheckPoint cp =referee.checkPoints[pod.nextCheckPointId];
        String target = ""+(int)(cp.position.x)+" "+(int)(cp.position.y);
        referee.handlePlayerOutput(0, 0, i, new String[]{target + " 100"});
      }
      
      try {
        referee.updateGame(0);
        if (referee.collisionOccur) {
          //pause = true;
        }
      } catch (Exception e) {
      }
  
      updateFrames();
      pause = true;
    }
    
    for (int i=0;i<referee.playerCount;i++) {
      updatePodRepresentation(i);
    }
    return null;
  }

  public static void main(String[] args) throws Exception {
    referee.initReferee(2 /**seed*/, 4 /*pods*/);
    
    launch(args);
  }
}
