package spring2021.s21.applications;

import java.io.FileNotFoundException;

import cgfx.FxPlayer;
import javafx.application.Application;
import javafx.stage.Stage;
import spring2021.s21.components.S21Factory;

public class S21Player extends Application {
  static FxPlayer player;
  
  public static void main(String[] args) throws FileNotFoundException {
    player = new FxPlayer(new S21Factory());
    
    Application.launch(args);

  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    player.start(primaryStage);  
  }

}
