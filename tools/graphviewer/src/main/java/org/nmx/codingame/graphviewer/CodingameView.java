package org.nmx.codingame.graphviewer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CodingameView extends Application {
  
  private static GraphPane graphPane = new GraphPane();
  private static GameNode rootGameNode;
  
	@Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Codingame javafx viewer");

    graphPane.init(rootGameNode);
    
    
    Scene scene = new Scene(graphPane, 1024, 768);
    primaryStage.setScene(scene);

    
    primaryStage.show();
  }

  public static void execute(GameNode rootGameNode) throws Exception {
    CodingameView.rootGameNode = rootGameNode;
    Application.launch();
  }

}
