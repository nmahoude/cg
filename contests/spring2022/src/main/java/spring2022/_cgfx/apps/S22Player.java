package spring2022._cgfx.apps;

import java.io.FileNotFoundException;

import cgfx.FxPlayer;
import javafx.application.Application;
import javafx.stage.Stage;
import spring2022._cgfx.components.S22Factory;

public class S22Player extends Application {
  static FxPlayer player;
  
  public static void main(String[] args) throws FileNotFoundException {
    player = new FxPlayer(new S22Factory());
    
    Application.launch(args);

  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    player.start(primaryStage);  
  }

}
