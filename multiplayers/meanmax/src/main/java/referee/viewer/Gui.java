package referee.viewer;

import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import meanmax.Game;
import referee.Referee;

public class Gui extends Application {
  //public static final int ratio = 20;
  private static Referee controller;
  
  Button playPause = new Button();
  boolean pause = true;

  Slider slider = new Slider();
  int currentFrame = 0;
  
  PlayerScore ps0 = new PlayerScore();
  PlayerScore ps1 = new PlayerScore();
  PlayerScore ps2 = new PlayerScore();
  
  List<Frame> frames = new ArrayList<>();
  private Rectangle nextTarget = new Rectangle();
  private static AGAIRepresentation agaiUI;

  private PlayField playfield;

  @Override 
  public void start(Stage stage) {
    VBox root = new VBox();

    Group playfieldRoot = new Group();
    playfieldRoot.setManaged(false);
    playfieldRoot.setAutoSizeChildren(false);
    
    playfield = new PlayField();
    playfieldRoot.getChildren().add(playfield);
    double scale = 0.08d;
    playfield.setScaleX(scale);
    playfield.setScaleY(scale);
    playfield.setTranslateX(Game.MAP_RADIUS * scale);
    playfield.setTranslateY(Game.MAP_RADIUS * scale);

    HBox controls = new HBox();
    root.getChildren().add(playfieldRoot);
    
    VBox scores = new VBox();
    scores.getChildren().add(ps0);
    scores.getChildren().add(ps1);
    scores.getChildren().add(ps2);
    
    root.getChildren().add(controls);
    root.getChildren().add(scores);
    Scene scene = new Scene(root);
    scene.setFill(Color.BLACK);
    
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

    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(200),
        ae -> update()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();

    stage.setScene(scene);
    stage.setWidth(1600);
    stage.setHeight(1080);
    stage.show();
  }

  private void addShowTrajectory(HBox controls) {
    CheckBox cb = new CheckBox();
    cb.setText("show traj");
    cb.setSelected(true);
//    cb.setOnAction(new EventHandler<ActionEvent>() {
//    @Override
//    public void handle(ActionEvent event) {
//      for (PodRepresentation pr :podRepresentations) {
//        pr.trajectory.setVisible(cb.isSelected());
//      }
//    }
//    });
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

  private Object update() {
    if (!pause) {
      try {
        controller.playOneTurn();
        playfield.update();
        
        ps0.update(Game.players[0]);
        ps1.update(Game.players[1]);
        ps2.update(Game.players[2]);
        if (controller.gameOver() || controller.turn >= 600) {
          System.out.println("Game finished");
          pause = true;
        }
      } catch(Exception e) {
        e.printStackTrace();
        Platform.exit();
      }
    }
    //agaiUI.update(agai);
    //pause =true;
    return null;
  }

  public static void main(String[] args) throws Exception {
    controller = new Referee();
    int seed = (int)System.currentTimeMillis();
    System.out.println("Seed : "+seed);
    controller.init();
    
    agaiUI = new AGAIRepresentation();
    launch(args);
  }
}
