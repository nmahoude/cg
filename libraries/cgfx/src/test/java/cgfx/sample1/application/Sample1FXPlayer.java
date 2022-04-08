package cgfx.sample1.application;

import cgfx.FxPlayer;
import cgfx.sample1.components.Sample1Factory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Sample1FXPlayer extends Application {
  static FxPlayer player;
  
  public static void main(String[] args) throws Exception {
    player = new FxPlayer(new Sample1Factory());
    Application.launch(args);
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    player.start(primaryStage);  
  }

}
