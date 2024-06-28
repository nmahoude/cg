package spring2022._cgfx.apps;

import cgfx.FxComparator;
import javafx.application.Application;
import javafx.stage.Stage;
import spring2022._cgfx.components.S22Factory;

public class S22Compator extends Application {
  FxComparator player = new FxComparator(new S22Factory());
  
  public static void main(String[] args) {
    
    Application.launch(args);

  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    player.start(primaryStage);  
  }
}
