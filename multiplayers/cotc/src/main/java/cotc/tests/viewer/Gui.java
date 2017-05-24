package cotc.tests.viewer;

import cotc.ai.DummyAI;
import cotc.tests.Controller;
import cotc.tests.GameFinished;
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

public class Gui extends Application {
  public static final int ratio = 10;
  private static Controller controller;
  
  Button playPause = new Button();
  boolean pause = true;

  Slider slider = new Slider();
  int currentFrame = 0;
  
  private Pane playfield;
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
    
    updateFrames();

    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(120),
        ae -> update()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();

    
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
      public void handle(ActionEvent event) { } 
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
  }

  private Object update() {
    if (!pause) {
      try {
        controller.playOneTurn();
        updateFrames();
      } catch (GameFinished rf) {
        System.out.println("Race finished "+rf.teamId+" has won");
        Platform.exit();
      } catch(Exception e) {
        e.printStackTrace();
        Platform.exit();
      }
    }
    
    return null;
  }

  public static void main(String[] args) throws Exception {
    controller = new Controller();
    int seed = (int)System.currentTimeMillis();
    System.out.println("Seed : "+seed);
    controller.init(seed);
    
    controller.setAI1(new DummyAI());
    controller.setAI2(new DummyAI());
    
    launch(args);
  }
}
