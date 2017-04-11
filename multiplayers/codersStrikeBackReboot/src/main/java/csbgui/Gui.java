package csbgui;

import java.util.ArrayList;
import java.util.List;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.game.Referee;
import csb.simulation.AGSolution1;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import trigonometry.Point;
import trigonometry.Vector;

public class Gui extends Application {
  public static final int ratio = 10;
  static Referee referee = new Referee();
  
  Button playPause = new Button();
  boolean pause = true;

  Slider slider = new Slider();
  int currentFrame = 0;
  
  Trajectory pod0Trajectory = new Trajectory();
  
  Rectangle[] rectangles = new Rectangle[1_000];
  Line[][] lines = new Line[rectangles.length][AGSolution1.ACTION_SIZE];
  double scores[]  =new double[rectangles.length];
  
  PodRepresentation podRepresentations[] = new PodRepresentation[4];
  {
    for (int i=0;i<referee.pods.length;i++) {
      podRepresentations[i] = new PodRepresentation(referee.pods[i]);
    }
  }
  List<Frame> frames = new ArrayList<>();
  private Pane playfield;
  private Group agSolutions = new Group();
  private Rectangle nextTarget = new Rectangle();

  @Override 
  public void start(Stage stage) {
    VBox root = new VBox();
    Group playfieldRoot = new Group();

    playfield = new Pane();
    playfield.setManaged(false);
    playfield.maxWidth(16000/ratio);
    playfield.maxHeight(9000/ratio);
    playfield.minWidth(16000/ratio);
    playfield.minHeight(9000/ratio);

    playfield.getChildren().add(pod0Trajectory);
    playfield.getChildren().add(agSolutions);
    
    playfieldRoot.setManaged(false);
    playfieldRoot.setAutoSizeChildren(false);
    playfieldRoot.getChildren().add(playfield);
    
    HBox controls = new HBox();
    root.getChildren().add(playfieldRoot);
    root.getChildren().add(controls);
    Scene scene = new Scene(root);
    
    addPlayPauseButton(controls);
    addShowTrajectory(controls);
    
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
      for (int j=0;j<AGSolution1.ACTION_SIZE;j++) {
        lines[i][j] = new Line();
        lines[i][j].setStrokeWidth(1);
        agSolutions.getChildren().add(lines[i][j]);
      }
    }

    nextTarget.setWidth(4);
    nextTarget.setHeight(4);
    nextTarget.setFill(Color.BLACK);
    playfield.getChildren().add(nextTarget);
    
    int i=0;
    for (CheckPoint cp : referee.checkPoints) {
      CheckPointRepresentation cpr = new CheckPointRepresentation(cp);
      playfield.getChildren().add(cpr);
    }
    
    for (Pod pod : referee.pods) {
      playfield.getChildren().add(podRepresentations[pod.id]);
    }
    
    updateFrames();

    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(60),
        ae -> updatePods()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();

    for (i=0;i<referee.playerCount;i++) {
      updatePodRepresentation(i);
    }
    
    scene.setFill(Color.GREY);
    stage.setScene(scene);
    stage.setWidth(1600);
    stage.setHeight(1080);
    stage.show();
  }

  private void addShowTrajectory(HBox controls) {
    CheckBox cb = new CheckBox();
    cb.setText("show traj");
    cb.setSelected(true);
    cb.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
      pod0Trajectory.setVisible(cb.isSelected());
    }
    });
    controls.getChildren().add(cb);
  }

  private void addPlayPauseButton(HBox controls) {
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
  }

  private void updateFrames() {
    Frame frame = new Frame();
    for (int i=0;i<referee.playerCount;i++) {
      frame.pods[i] = referee.pods[i].clone();
      frame.targetPoints[i] = referee.target[i];
    }

    pod0Trajectory.addPoint(frame.pods[0].position);

    frames.add(frame);
    currentFrame = frames.size()-1;
    
    slider.setMin(0);
    slider.setMax(frames.size()-1);
    slider.setValue(currentFrame);
  }

  private void updatePodRepresentation(int index) {
    Pod pod = frames.get(currentFrame).pods[index];
    Point targetPoint = frames.get(currentFrame).targetPoints[index];
    podRepresentations[index].update(targetPoint);
  }

  private Object updatePods() {
    if (!pause) {
      calculateNextFrame();
    }
    
    for (int i=0;i<referee.playerCount;i++) {
      updatePodRepresentation(i);
    }
    return null;
  }

  private void calculateNextFrame() {
    calculateWithAG(referee.pods[0]);
    calculateDirect(referee.pods[1]);
    calculateDirect(referee.pods[2]);
    calculateDirect(referee.pods[3]);
    
    try {
      referee.updateGame(0);
    } catch (Exception e) {
    }
 
    updateFrames();
    //pause = true;
  }

  private void calculateWithAG(Pod pod) {
    AGSolution1 best = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    double minScore =  Double.POSITIVE_INFINITY;
    for (int i=0;i<rectangles.length;i++) {
      AGSolution1 solution = new AGSolution1(referee.pods, referee.checkPoints);
      solution.test();
      
      double score = solution.moveAndEvaluate();;
      rectangles[i].setX(solution.pods[0].position.x / ratio);
      rectangles[i].setY(solution.pods[0].position.y / ratio);
      solution.reset();

      scores[i] = score;
      minScore = Math.min(minScore, score);
      if( score > bestScore) {
        bestScore = score;
        best = solution;
      }
      for (int j=0;j<AGSolution1.ACTION_SIZE-1;j++) {
        lines[i][j].setStartX(solution.points[j].x / ratio);
        lines[i][j].setStartY(solution.points[j].y / ratio);
        lines[i][j].setEndX(solution.points[j+1].x / ratio);
        lines[i][j].setEndY(solution.points[j+1].y / ratio);
      }
    }
    for (int i=0;i<rectangles.length;i++) {
      double colorScore = Math.max(0, Math.min(1, (float) (scores[i]-minScore) / (bestScore-minScore)));
      int index = (int)(colorScore * 10);
      Color color = new Color(colorScore, 1.0-colorScore,  0, 1);
      rectangles[i].setFill(color);
      for (int j=0;j<AGSolution1.ACTION_SIZE-1;j++) {
        lines[i][j].setStroke(color);
        lines[i][j].setVisible(scores[i] > 0.9*bestScore);
        if (scores[i] == bestScore) {
          lines[i][j].setStroke(Color.WHITE);
        }
      }
    }
    
    updateNextCheckPointTarget();
    
    referee.handlePlayerOutput(0, 0, 0, new String[]{best.actionOutput(0)});
    referee.handlePlayerOutput(0, 0, 1, new String[]{best.actionOutput(1)});
  }

  private void updateNextCheckPointTarget() {
    Pod pod = referee.pods[0];
    int lastCheckPoint = pod.nextCheckPointId == 0 ? referee.checkPoints.length-1 : pod.nextCheckPointId-1;
    int nextNextCheckPoint = pod.nextCheckPointId == referee.checkPoints.length-1 ? 0 : pod.nextCheckPointId+1;
    Vector dir = referee.checkPoints[nextNextCheckPoint].position.sub(referee.checkPoints[lastCheckPoint].position).normalize().dot(CheckPoint.RADIUS);
    Point nextTargetPoint = referee.checkPoints[pod.nextCheckPointId].position.sub(dir);
    
    nextTarget .setX(nextTargetPoint.x / ratio);
    nextTarget.setY(nextTargetPoint.y / ratio);
  }

  private void calculateDirect(Pod pod) {
    CheckPoint cp =referee.checkPoints[pod.nextCheckPointId];
    String target = ""+(int)(cp.position.x)+" "+(int)(cp.position.y);
    referee.handlePlayerOutput(0, 0, pod.id, new String[]{target + " 100"});    
  }

  public static void main(String[] args) throws Exception {
    referee.initReferee(5 /**seed*/, 4 /*pods*/);
    
    launch(args);
  }
}
