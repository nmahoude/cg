package marslander3.cgfx.apps;

import java.io.FileNotFoundException;

import cgfx.FxPlayer;
import javafx.application.Application;
import javafx.stage.Stage;
import marslander3.cgfx.components.ML3Factory;

public class ML3Player extends Application {
  static FxPlayer player;
  
  public static void main(String[] args) throws FileNotFoundException {
    player = new FxPlayer(new ML3Factory());
    
    Application.launch(args);

  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    player.start(primaryStage);  
  }

}
