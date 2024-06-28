package spring2022._cgfx.apps;

import cgfx.Replayer;
import cgfx.components.GameViewer;
import cgfx.frames.GameReader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import spring2022._cgfx.components.S22GameViewer;
import spring2022._cgfx.components.S22GameWrapper;

public class S22SimpleReplayer extends Application {
  public static void main(String[] args) {

    Application.launch(args);

  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    primaryStage.setTitle("Test ");
    
    GameReader gameReader = new GameReader();
    gameReader.readReplayFromFile("referee.json");
    
    S22GameWrapper wrapper = new S22GameWrapper();
    wrapper.readGlobal(gameReader.getInput(0));
    wrapper.readTurn(gameReader.getInput(1));
    wrapper.think();
    
    GameViewer gameViewer = new S22GameViewer(wrapper);

    
    Replayer replayer = new Replayer(gameReader, wrapper, gameViewer);
    replayer.updateView();
    
    Scene scene = new Scene(new HBox(replayer), 1560, 1024);
    primaryStage.setScene(scene);
    primaryStage.show();
    
    
  }

}
