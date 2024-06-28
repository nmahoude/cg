package spring2021.s21.applications;

import cgfx.FxComparator;
import javafx.application.Application;
import javafx.stage.Stage;
import spring2021.s21.components.S21Factory;

public class S21Compator extends Application {
  FxComparator player = new FxComparator(new S21Factory());
  
  public static void main(String[] args) {
    
    Application.launch(args);

  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    player.start(primaryStage);  
  }
}
