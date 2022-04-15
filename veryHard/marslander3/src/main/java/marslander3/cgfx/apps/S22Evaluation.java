package marslander3.cgfx.apps;

import cgfx.FXEvaluation;
import javafx.application.Application;
import javafx.stage.Stage;
import marslander3.cgfx.components.ML3Factory;

public class S22Evaluation extends Application {
  FXEvaluation player = new FXEvaluation(new ML3Factory());
  
  public static void main(String[] args) {
    
    Application.launch(args);

  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    player.start(primaryStage);  
  }
}
