package fall2023._fx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cgfx.ViewRestorer;
import cgfx.frames.GameReader;
import fall2023.AI;
import fall2023.AIInterface;
import fall2023.Action;
import fall2023.Drone;
import fall2023.Simulator;
import fall2023.State;
import fall2023._fx.modules.F23GameOptionPane;
import fall2023._fx.modules.F23GameViewer;
import fall2023._fx.modules.F23GameWrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class F23Replayer extends Application {
  private F23GameViewer gameViewer;
  private F23GameWrapper wrapperResult;
  public static void main(String[] args) {

    Application.launch(args);

  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    ViewRestorer.applyOn(primaryStage, this.getClass());
    primaryStage.setTitle("F23 replayer ");
    
    
    GameReader gameReader = new GameReader();
    gameReader.readReplayFromFile("game1.json");
    
    F23GameWrapper wrapper = new F23GameWrapper();
    wrapper.readFrame(gameReader.getMyFrame(0));
    wrapper.think();

    wrapperResult = new F23GameWrapper();

    
    F23GameOptionPane options = new F23GameOptionPane();
    gameViewer = new F23GameViewer(wrapper, options);
    gameViewer.setOptionsPane(options);
    options.register(gameViewer);
    
    cgfx.Replayer replayer = new cgfx.Replayer(gameReader, wrapper, gameViewer);
    replayer.updateView();
    
    Button aiButton = new Button("AI");
    
    aiButton.setOnAction(e -> {
      aiButton.setDisable(true);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.execute(() -> {
        try {
          long start = System.currentTimeMillis();
          
          calculateAI(wrapper.state, wrapperResult.state);
          
          Platform.runLater(() -> {
          });
          long end= System.currentTimeMillis();
          System.out.println("AI in "+(end-start)+" ms");
        } finally {
          aiButton.setDisable(false);
        }
      });
      executor.shutdown();
    });
    
    Scene scene = new Scene(new HBox(aiButton, replayer, options), 1800, 1024);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  private void calculateAI(State initState, State resultState) {
    AIInterface ai = new AI();
    Action[] actions = ai.think(initState);
    
    Drone drone1 = initState.myDrones[0];
    System.out.println(actions[drone1.id].output(drone1));
    Drone drone2 = initState.myDrones[1];
    System.out.println(actions[drone2.id].output(drone2));
    
    
    
    gameViewer.setAction(actions);
    gameViewer.update(null, null);

    resultState.copyFrom(initState);
    Simulator sim = new Simulator();
    sim.applyJustMe(resultState, actions);
  }

}
