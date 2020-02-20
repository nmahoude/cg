package tests.viewer;

import java.util.ArrayList;
import java.util.List;

import csb.ai.ag.AGAI;
import csb.entities.CheckPoint;
import csb.entities.Pod;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import tests.Controller;
import tests.RaceFinished;
import trigonometry.Point;

public class Gui extends Application {
  public static final int ratio = 10;
  private static Controller controller;
  
  Button playPause = new Button();
  boolean pause = true;

  Slider slider = new Slider();
  int currentFrame = 0;
  
  PodRepresentation podRepresentations[] = new PodRepresentation[4];
  List<Frame> frames = new ArrayList<>();
  private Pane playfield;
  private Rectangle nextTarget = new Rectangle();
  private static AGAI agai;
  private static AGAIRepresentation agaiUI;

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

    playfield.getChildren().add(agaiUI);
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

    nextTarget.setWidth(4);
    nextTarget.setHeight(4);
    nextTarget.setFill(Color.BLACK);
    playfield.getChildren().add(nextTarget);
    
    int i=0;
    for (CheckPoint cp : controller.getCheckPoints()) {
      CheckPointRepresentation cpr = new CheckPointRepresentation(cp);
      playfield.getChildren().add(cpr);
    }
    
    for (Pod pod : controller.getPods()) {
      podRepresentations[pod.id] = new PodRepresentation(pod);
      playfield.getChildren().add(podRepresentations[pod.id]);
    }
    
    updateFrames();

    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(120),
        ae -> update()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();

    for (i=0;i<controller.getPods().length;i++) {
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
      for (PodRepresentation pr :podRepresentations) {
        pr.trajectory.setVisible(cb.isSelected());
      }
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
    for (int i=0;i<controller.getPods().length;i++) {
      frame.pods[i] = controller.getPods()[i].clone();
      frame.targetPoints[i] = new Point(controller.getPods()[i].x, controller.getPods()[i].y);
    }

    frames.add(frame);
    currentFrame = frames.size()-1;
    
    slider.setMin(0);
    slider.setMax(frames.size()-1);
    slider.setValue(currentFrame);
  }

  private void updatePodRepresentation(int index) {
    podRepresentations[index].update();
  }

  private Object update() {
    if (!pause) {
      try {
        controller.playOneTurn();
        updateFrames();
      } catch (RaceFinished rf) {
        System.out.println("Race finished "+rf.team+" has won");
        Platform.exit();
      } catch(Exception e) {
        e.printStackTrace();
        Platform.exit();
      }
    }
    
    for (int i=0;i<controller.getPods().length;i++) {
      updatePodRepresentation(i);
    }
    agaiUI.update(agai);
    //pause =true;
    return null;
  }

  public static void main(String[] args) throws Exception {
    controller = new Controller();
    int seed = (int)System.currentTimeMillis();
    System.out.println("Seed : "+seed);
    controller.init(seed);
//    controller.referee.physics.collisionSimualtion = false;
    
    controller.setAI1(new AGAI(System.currentTimeMillis()+100));
    
    agai = new AGAI(System.currentTimeMillis()+100);
    controller.setAI2(agai);
    
    agaiUI = new AGAIRepresentation();
    launch(args);
  }
}
