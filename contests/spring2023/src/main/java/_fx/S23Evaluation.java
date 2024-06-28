package _fx;

import _fx.modules.S23GameViewer;
import _fx.modules.S23GameWrapper;
import cgfx.components.EvalChart;
import cgfx.components.GameViewer;
import cgfx.frames.FrameChooser;
import cgfx.frames.GameReader;
import cgfx.wrappers.GameWrapper;
import fast.read.FastReader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import spring2023.State;

public class S23Evaluation extends Application {
  
  private GameReader reader = new GameReader();
  private GameWrapper wrapper;
  private GameViewer viewer;
  private EvalChart chart;
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    
    reader.readReplayFromFile("game1.json");
    State.readInit(FastReader.fromString(reader.getCleanInput(1)));
    
    wrapper = new S23GameWrapper();
    viewer = new S23GameViewer((S23GameWrapper)wrapper);
    
    setupScene(primaryStage);
  }

  private void setupScene(Stage primaryStage) {
    
    chart = new EvalChart(S23EvalNode.names());
    
    //wrapper.readTurn(reader.getInput(0));
    
    for (int index=0;index < reader.getMaxTurn();index++) {
      wrapper.readTurn(reader.getInput(index));
      chart.addNode(new S23EvalNode((S23GameWrapper)wrapper));
    }

    wrapper.readTurn(reader.getInput(0));
//    for(Node legend : chart.getNode().lookupAll(".chart-legend-item")){
//
//      Label label = (Label)legend;
//
//      label.setOnMouseClicked(e -> {
//        System.out.println("click on "+label);
//        Platform.runLater(() -> {
//          chart.showOnOff(label.getText());
//        });
//      });
//    }
    
    viewer.update(null, null);
    
    FrameChooser frameChooser = new FrameChooser(0, reader.getMaxTurn()-1);
    frameChooser.addViewer(i -> this.chooseFrame(i));
    
    VBox view = new VBox(chart, frameChooser);
    Scene scene = new Scene(new HBox(view, viewer));
    
    primaryStage.setTitle("S2023 - Evaluation");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  private void chooseFrame(int i) {
    wrapper.readTurn(reader.getInput(i));
    viewer.update(null, null);
    
    chart.displayVerticalAt(i);
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
