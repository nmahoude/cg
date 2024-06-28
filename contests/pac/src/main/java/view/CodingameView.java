package view;

import java.util.Random;
import java.util.function.Function;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;

public class CodingameView extends Application {

  public static boolean wait = true;
  
  private ToolBar toolbar;

  public static Agent game;
  public static Agent agents[] = new Agent[5];
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Codingame javafx viewer");

    Group root = new Group();
    Scene scene = new Scene(root, 1024, 768);
    primaryStage.setScene(scene);

    Group gameView = new Group();
    gameView.setLayoutX(32);
    gameView.setLayoutY(32);
    root.getChildren().add(gameView);

    Label label = new Label("Hello Codingame !");
    label.setTranslateX(200);
    label.setTranslateY(300);
    root.getChildren().add(label);

    String[] colors = new String[] { null, null, null, "#FF0000", "#00FF00", "#0000FF", "#FF00FF" };
    
    game = new Agent("Game", gameView);
    game.setFunction( coords -> {
      if (coords[0] == 0 || coords[0] == 29 || coords[1] == 0 || coords[1] == 19) {
        return "#000000";
      } else {
        return "#FFFFFF";
      }
    });
    game.updateCells();
    
    toolbar = new ToolBar();
    root.getChildren().add(toolbar);

    for (int i=0;i<5;i++) {
      agents[i] = buildPlayer("Bot "+i, gameView, coords -> {
        if (coords[0] == 0 || coords[0] == 29 || coords[1] == 0 || coords[1] == 19) {
          return null;
        }
        return colors[new Random().nextInt(colors.length)];
      });
    }

    
    
    Button nextBtn = new Button("Next");
    nextBtn.setOnAction(event -> {
      wait = false;
    });
    toolbar.getItems().add(nextBtn);
    primaryStage.show();
  }

  private Agent buildPlayer(String name, Group gameView, Function<int[], String> function) {
    Group player1_1 = new Group();
    gameView.getChildren().add(player1_1);

    
    Agent agent = new Agent(name, player1_1);
    //agent.setFunction(function);
    agent.updateCells();

    ToggleButton toggleButton1 = new ToggleButton(name);
    toggleButton1.setSelected(true);
    toggleButton1.setOnAction(event -> {
      player1_1.setVisible(toggleButton1.isSelected());
    });
    toolbar.getItems().add(toggleButton1);

    return agent;
  }

  public static void main(String[] args) {
    Application.launch(args);
  }

  public void execute() {
    Application.launch();
  }
  
  public static void waitNext() {
    game.updateCells();
    for (int i=0;i<5;i++) {
      agents[i].updateCells();
    }
    
    // wait for javafx push ?
    while (CodingameView.wait) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    CodingameView.wait = true;
  }
}
