package marslander3.cgfx.apps;

import cgfx.GameReader;
import cgfx.Replayer;
import cgfx.components.GameViewer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import marslander3.cgfx.components.ML3GameViewer;
import marslander3.cgfx.components.ML3GameWrapper;

public class S22SimpleReplayer extends Application {
  public static void main(String[] args) {

    Application.launch(args);

  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    primaryStage.setTitle("Test ");
    
    GameReader gameReader = new GameReader();
    gameReader.readReplayFromFile("referee.json");
    
    ML3GameWrapper wrapper = new ML3GameWrapper();
    wrapper.readGlobal(gameReader.getInput(0));
    wrapper.readTurn(gameReader.getInput(1));
    wrapper.think();
    
    GameViewer gameViewer = new ML3GameViewer(wrapper);

    
    Replayer replayer = new Replayer(gameReader, wrapper, gameViewer);
    replayer.updateView();
    
    Scene scene = new Scene(new HBox(replayer), 1560, 1024);
    primaryStage.setScene(scene);
    primaryStage.show();
    
    
  }

}
