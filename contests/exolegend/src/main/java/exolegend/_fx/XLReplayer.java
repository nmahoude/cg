package exolegend._fx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cgfx.ViewRestorer;
import cgfx.frames.GameReader;
import exolegend.State;
import exolegend._fx.modules.XLGameOptionPane;
import exolegend._fx.modules.XLGameViewer;
import exolegend._fx.modules.XLGameWrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class XLReplayer extends Application {
  private XLGameViewer gameViewer;
  private XLGameWrapper wrapperResult;
  public static void main(String[] args) {

    Application.launch(args);

  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    ViewRestorer.applyOn(primaryStage, this.getClass());
    primaryStage.setTitle("F23 replayer ");
    
    GameReader gameReader = new GameReader();
    gameReader.readReplayFromFile("game1.json");
    
    XLGameWrapper wrapper = new XLGameWrapper();
    wrapper.readFrame(gameReader.getMyFrame(0));
    wrapper.think();

    wrapperResult = new XLGameWrapper();

    
    XLGameOptionPane options = new XLGameOptionPane();
    gameViewer = new XLGameViewer(wrapper, options);
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
    
    gameViewer.update(null, null);

  }

}
