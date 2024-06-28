package fall2023._fx;

import static javafx.scene.layout.HBox.setMargin;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cgfx.ViewRestorer;
import fall2023.AIInterface;
import fall2023.Action;
import fall2023.Drone;
import fall2023.PathFinder;
import fall2023.Player;
import fall2023.Simulator;
import fall2023.State;
import fall2023._fx.modules.F23GameOptionPane;
import fall2023._fx.modules.F23GameViewer;
import fall2023._fx.modules.F23GameWrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class F23Viewer extends Application {
  F23GameWrapper wrapper = new F23GameWrapper();
  F23GameViewer viewerInit;
  F23GameOptionPane options = new F23GameOptionPane();
  
  F23GameWrapper wrapperResult = new F23GameWrapper();
  F23GameViewer viewerResult;
  
  
  public static void main(String[] args) throws FileNotFoundException {
    Application.launch(args);
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    ViewRestorer.applyOn(primaryStage, this.getClass());
    
    readGameInfo();

    viewerInit = new F23GameViewer(wrapper, options);
    viewerResult = new F23GameViewer(wrapperResult, options);
    
    Button paste = new Button("paste");
    paste.setOnAction(e -> {
      pasteGameData();
    });
    
    
    Button pathButton = new Button("Path");
    pathButton.setOnAction(e -> {
      pathButton.setDisable(true);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.execute(() -> {
        try {
          PathFinder pf = new PathFinder();
          Set<fall2023.Pos> fishes = new HashSet<>();
          for (int i=4;i<16;i++) {
            fishes.add(new fall2023.Pos(wrapper.state.getBestTriangulation(i).center()));
          }
          wrapper.path.clear();
          wrapper.path.addAll(pf.think(wrapper.state.myDrones[0].pos, new fall2023.Pos(2500, 0), fishes));
          viewerInit.update(null, null);
          Platform.runLater(() -> {
          });
        } catch(Exception ex) {
        } finally {
          pathButton.setDisable(false);
        }
      });
      executor.shutdown();
    });
      
    Button aiButton = new Button("AI");
    
    aiButton.setOnAction(e -> {
      aiButton.setDisable(true);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.execute(() -> {
        try {
          long start = System.currentTimeMillis();
          
          calculateAI(wrapper.state, wrapperResult.state);
          
          viewerResult.update(null, null);
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
    
    
    
    HBox commands = new HBox(pathButton, aiButton);
    commands.setAlignment(Pos.BASELINE_RIGHT);
    VBox vbox = new VBox(commands, viewerInit);

    HBox commandsResult = new HBox(new Button("Dummy"));
    VBox vboxResult = new VBox(commandsResult, viewerResult);
    HBox hbox = new HBox(vbox, options, vboxResult);
    setMargin(options, new Insets(10));


    
    
    Scene scene = new Scene(hbox , 1800, 900);
    scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if(e.isShortcutDown() && e.getCode() == KeyCode.V) {
        pasteGameData();
      }
    });
    
    primaryStage.setScene(scene);
    primaryStage.setTitle("Fall 2023 - Debugger");
    primaryStage.show();

    viewerInit.update(null, null);
    viewerResult.update(null, null);

  }

  private void pasteGameData() {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    if (clipboard.hasString()) {
      String text = clipboard.getString();
      if (text.contains("*** INIT") && text.contains("*** END")) {

        // State.setCanBeInitial();
        
        wrapper.readFull(text);
        viewerInit.update(null, null);
        viewerResult.update(null, null);
        
        State state = (State)wrapper.state;
        for (int i=4;i<16;i++) {
          System.out.println("State of fish "+i);
          if (state.myScans.isFirst(i)) System.out.println("    me first");
          if (state.oppScans.isFirst(i)) System.out.println("    opp first");
        }
      } else {
        System.out.println("***************************************");
        System.out.println("Seems like an incorrect game log");
        System.out.println(text);
        System.out.println("***************************************");
      }
    }
  }

  private void calculateAI(State initState, State resultState) {
    AIInterface ai = Player.ai;
    Action[] actions = ai.think(initState);
    
    Drone drone1 = initState.myDrones[0];
    System.out.println(actions[drone1.id].output(drone1));
    Drone drone2 = initState.myDrones[1];
    System.out.println(actions[drone2.id].output(drone2));
    
    
    
    viewerInit.setAction(actions);
    viewerInit.update(null, null);

    resultState.copyFrom(initState);
    resultState.previousState = initState;
    Simulator sim = new Simulator();
    sim.applyJustMe(resultState, actions);
  }
  
  
  private void readGameInfo() {
    wrapper.readFull("""
*** INIT ***
^ 14 4 105 1006 1107 2008 2109 210 311 1212 1313 2214 2315 3416 3417 
*** OPTIONAL ***
^ 3 0
^ 2166 1688 0 30 0 
^ 7933 1698 0 30 0 
^ 6500 1592 0 30 0 
^ 3475 1580 0 30 0 
^ 0
^ 703693171426855030 703693171426859364 703693171590695030 703693171590699364 703689375003446700 703689375003449250 703693171426859364 703693171426855030 703696745003490970 703696745003483136 703696745167330970 703696745167323136 1863373648945217536 1863373648945225370 
^ 0 0
*** TURN ***
^ 0 0
Already scan : 
^0
---
^0
^ 2
^ 0 2083 2282 0 30
^ 2 6750 2138 0 30
^ 2
^ 1 7899 2297 0 30
^ 3 3213 2120 0 30
--- Current Scans ---
^ 0 
--- Fishes --- 
^ 0
--- Blips --- 
^ 28
^ 104 105 106 107 108 109 110 111 112 213 114 215 216 117 2204 2105 2206 2107 2208 2209 2110 2211 2112 2213 2114 2215 2216 2117 
@@@@@@ CHECK SYMMETRY
Doing symmetry between 4 and 5
new rect for 4 => (2083,2500)|1167,2500|
new rect for 5 => (6750,2500)|1167,2500|
Doing symmetry between 6 and 7
new rect for 6 => (2083,5000)|1167,2500|
new rect for 7 => (6750,5000)|1167,2500|
Doing symmetry between 8 and 9
new rect for 8 => (3300,7500)|850,2500|
new rect for 9 => (5850,7500)|850,2500|
Doing symmetry between 10 and 11
new rect for 10 => (6750,2500)|1167,2500|
new rect for 11 => (2083,2500)|1167,2500|
Doing symmetry between 12 and 13
new rect for 12 => (7917,5000)|2083,2500|
new rect for 13 => (0,5000)|2083,2500|
Doing symmetry between 14 and 15
new rect for 14 => (7917,7500)|2083,2500|
new rect for 15 => (0,7500)|2083,2500|
*** END ***
        """);
    
  }
}
