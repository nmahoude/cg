package fall2022._fx;

import cgfx.frames.GameReader;
import fall2022._fx.modules.F22GameOptionPane;
import fall2022._fx.modules.F22GameViewer;
import fall2022._fx.modules.F22GameWrapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Replayer extends Application {
  public static void main(String[] args) {

    Application.launch(args);

  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    primaryStage.setTitle("F22 replayer ");
    
    GameReader gameReader = new GameReader();
    gameReader.readReplayFromFile("referee.json");
    
    F22GameWrapper wrapper = new F22GameWrapper();
    wrapper.readGlobal(gameReader.getInput(0));
    wrapper.readTurn(gameReader.getInput(1));
    wrapper.think();
    
    F22GameOptionPane options = new F22GameOptionPane();
    F22GameViewer gameViewer = new F22GameViewer(wrapper);
    gameViewer.setOptionsPane(options);
    options.register(gameViewer);
    
    cgfx.Replayer replayer = new cgfx.Replayer(gameReader, wrapper, gameViewer);
    replayer.updateView();
    
    Scene scene = new Scene(new HBox(replayer, options), 1800, 1024);
    primaryStage.setScene(scene);
    primaryStage.show();
    
    
  }

}
