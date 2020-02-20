package csbgui2;

import csbgui.CheckPointRepresentation;
import csbgui.PodRepresentation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Gui extends Application {
  public static final int ratio = 10;

  Controller controller;

  
  private Pane playfield;
  PodRepresentation podRepresentations[];
  CheckPointRepresentation cpr[];
  
  @Override
  public void start(Stage stage) throws Exception {
    VBox root = new VBox();
    Scene scene = new Scene(root);

    controller = new DuelController();

    playfield = new Pane();
    playfield.setManaged(false);
    playfield.maxWidth(16000/ratio);
    playfield.maxHeight(9000/ratio);
    playfield.minWidth(16000/ratio);
    playfield.minHeight(9000/ratio);
    root.getChildren().add(playfield);
    
    createPodRepresentation();
    createCPRepresentation();
    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(60),
        ae -> update()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();

    
    scene.setFill(Color.GREY);
    stage.setScene(scene);
    stage.setWidth(1600);
    stage.setHeight(1080);
    stage.show();
  }


  private void createCPRepresentation() {
    cpr = new CheckPointRepresentation[controller.checkPoints.length];
    for (int i=0;i<controller.checkPoints.length;i++) {
      cpr[i] = new CheckPointRepresentation(controller.checkPoints[i]);
      playfield.getChildren().add(cpr[i]);
    }
  }


  private Object update() {
    controller.update();
    updatePodResentation();
    return null;
  }


  private void updatePodResentation() {
    for (PodRepresentation pr : podRepresentations) {
      pr.update(pr.pod.position);
    }
  }


  private void createPodRepresentation() {
    podRepresentations = new PodRepresentation[controller.pods.length];
    for (int i=0;i<controller.pods.length;i++) {
      podRepresentations[i] = new PodRepresentation(controller.pods[i]);
      playfield.getChildren().add(podRepresentations[i]);
    }
  }

  public static void main(String[] args) throws Exception {
    launch(args);
  }
}
