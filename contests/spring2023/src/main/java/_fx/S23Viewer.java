package _fx;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import _fx.modules.S23GameOptionPane;
import _fx.modules.S23GameViewer;
import _fx.modules.S23GameWrapper;
import cgfx.ViewRestorer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import spring2023.Action;
import spring2023.Player;
import spring2023.State;
import spring2023.map.Map;
import spring2023.simulation.TrueSimulation;

public class S23Viewer extends Application {
  S23GameWrapper wrapper = new S23GameWrapper();
  S23GameViewer viewer;
  S23GameOptionPane options = new S23GameOptionPane();
  
  S23GameWrapper wrapperResult = new S23GameWrapper();
  S23GameViewer viewerResult;

  ListView<String> actionsList;
  private ObservableList<String> actionsItems = FXCollections.observableArrayList ();
  
  public static void main(String[] args) throws FileNotFoundException {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    ViewRestorer.applyOn(primaryStage);
    
    readGameInfo();

    viewer = new S23GameViewer(wrapper);
    viewerResult = new S23GameViewer(wrapperResult);
        
    actionsList = new ListView<String>();
    actionsList.setItems(actionsItems);
    
    viewer.setOptionsPane(options);
    viewerResult.setOptionsPane(options);
    
    options.register(viewer);

    Button calculateAI = new Button("Calculate AI");
    calculateAI.setOnAction(e -> {
      calculateAI.setDisable(true);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      
      
      executor.execute(() -> {
        try {
          long start = System.currentTimeMillis();
          
          // clear console ? 
          final String ESC = "\033[";
          System.out.print(ESC + "2J"); 
          
          
          int[] beacons = new int[Map.MAX_CELLS];
          Player.start = Long.MAX_VALUE;
          Player.ai.naiveBeacons(wrapper.state, beacons);
          Player.ai.reworkingBeaconsStrength(wrapper.state, beacons);
          Player.ai.limitingBeaconsStrength(wrapper.state, beacons);
          
          wrapper.setBeacons(beacons);
          
          // execute AI and get the liste of actions
          List<Action> actions = new ArrayList<>();
          for (int i=0;i<Map.MAX_CELLS;i++) {
            if (beacons[i] > 0) {
              actions.add(Action.get(i).withStrength(beacons[i]));
            }
          }
          
          
          
          viewer.setActions(actions);
          viewer.update(null, null);
    
          Platform.runLater(() -> {
            actionsItems.clear();
            actions.stream().map(Action::debugString).forEach(actionsItems::add);
          });
          
        } finally {
          calculateAI.setDisable(false);
        }
      });
      executor.shutdown();
    });
    
    Button optimizeBeacons = new Button("Optimize Beacons");
    optimizeBeacons.setOnAction(e -> {
      optimizeBeacons.setDisable(true);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      
      
      executor.execute(() -> {
        try {
          long start = System.currentTimeMillis();
          
          // clear console ? 
          final String ESC = "\033[";
          System.out.print(ESC + "2J"); 
          
          
          Player.ai.optimizeBeacons(wrapper.state, wrapper.getBeacons());
          
          viewer.setActions(new ArrayList<>());
          viewer.update(null, null);
    
          Platform.runLater(() -> {
            actionsItems.clear();
          });
          
        } finally {
          optimizeBeacons.setDisable(false);
        }
      });
      executor.shutdown();
    });
    
    
    Button simulation = new Button("simulation");
    simulation.setOnAction(e -> {
      simulation.setDisable(true);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      
      
      executor.execute(() -> {
        try {
          wrapperResult.state.copyFrom(wrapper.state);
          for (int i=0;i<State.numberOfCells;i++) {
            wrapperResult.state.cells[i].beacon = wrapper.getBeacons()[i];
          }
          new TrueSimulation().simulate(wrapperResult.state);
          viewerResult.update(null, null);
        } finally {
          simulation.setDisable(false);
        }
      });
      executor.shutdown();
    });
    
    HBox view = new HBox(viewer, new VBox(options, actionsList), new VBox(simulation, viewerResult));
    VBox vbox = new VBox(new HBox(calculateAI, optimizeBeacons), view);

    Scene scene = new Scene(vbox, 1800, 900);

    primaryStage.setScene(scene);
    primaryStage.setTitle("Spring 2023 - Debugger");
    primaryStage.show();

    viewer.update(null, null);
    viewerResult.update(null, null);
  }

  private void readGameInfo() {
    Player.LOCAL_DEBUG = true;
    Player.DEBUG_OPTIMIZER = true;
    Player.DEBUG_AI = true;
    
  String input = """
*** INIT ***
^55
^ 2 0 -1 1 -1 -1 2 -1 
^ 0 0 -1 5 -1 -1 0 -1 0 0 -1 0 -1 -1 6 -1 2 0 11 13 -1 -1 10 -1 2 0 -1 9 -1 12 14 -1 1 0 15 17 -1 -1 1 -1 1 0 -1 2 -1 16 18 -1 0 0 -1 -1 19 -1 9 -1 0 0 -1 10 -1 -1 -1 20 0 0 -1 7 -1 -1 4 -1 0 0 -1 3 -1 -1 8 -1 
^ 0 0 21 -1 13 3 -1 -1 0 0 4 -1 -1 22 -1 14 0 0 -1 23 15 -1 3 11 0 0 -1 4 12 -1 24 16 0 0 23 25 17 5 -1 13 0 0 6 -1 14 24 26 18 0 0 25 -1 -1 -1 5 15 0 0 -1 6 16 26 -1 -1 0 0 -1 -1 -1 27 -1 7 0 0 28 -1 8 -1 -1 -1 
^ 0 0 31 -1 -1 11 -1 42 0 0 12 -1 41 32 -1 -1 2 0 33 35 25 15 13 -1 2 0 16 14 -1 34 36 26 0 0 35 -1 -1 17 15 23 0 0 18 16 24 36 -1 -1 0 0 19 -1 -1 37 29 -1 0 0 38 30 -1 20 -1 -1 0 0 -1 27 37 39 -1 -1 0 0 40 -1 -1 -1 28 38 
^ 0 0 43 45 -1 21 42 54 0 0 22 41 53 44 46 -1 1 0 47 -1 35 23 -1 -1 1 0 24 -1 -1 48 -1 36 2 0 -1 -1 -1 25 23 33 2 0 26 24 34 -1 -1 -1 0 0 27 -1 -1 49 39 29 0 0 50 40 30 28 -1 -1 0 0 29 37 49 51 41 -1 0 0 52 42 -1 30 38 50 
^ 0 0 -1 39 51 53 32 22 0 0 54 31 21 -1 40 52 1 0 -1 -1 45 31 54 -1 1 0 32 53 -1 -1 -1 46 0 0 -1 -1 47 -1 31 43 0 0 -1 32 44 -1 -1 48 0 0 -1 -1 -1 33 -1 45 0 0 34 -1 46 -1 -1 -1 0 0 37 -1 -1 -1 51 39 0 0 -1 52 40 38 -1 -1 
^ 0 0 39 49 -1 -1 53 41 0 0 -1 54 42 40 50 -1 0 0 41 51 -1 -1 44 32 0 0 -1 43 31 42 52 -1 
^2
^ 10 21 9 22 
*** OPTIONAL ***
^11
*** TURN
^ 60 71 
^ 0 400000 3000 500009 16000 500000 4000 0 0 13000 500000 1200000 5000 1000000 14000 200000 5000 0 0 0 
^ 0 1700000 3000 1100028 25036 0 0 0 0 0 0 800000 0 500022 2028 14 14 0 0 0 
^ 0 0 0 9 0 0 0 0 0 0 0 0 0 0 0 
*** END



*** INIT ***
^41
^ 2 0 1 3 -1 2 4 -1 
^ 0 0 5 7 3 0 -1 14 0 0 0 -1 13 6 8 4 1 0 7 9 11 -1 0 1 1 0 -1 0 2 8 10 12 0 0 -1 -1 7 1 14 20 0 0 2 13 19 -1 -1 8 0 0 -1 15 9 3 1 5 0 0 4 2 6 -1 16 10 0 0 15 -1 -1 11 3 7 0 0 12 4 8 16 -1 -1 
^ 0 0 9 -1 -1 -1 -1 3 0 0 -1 -1 4 10 -1 -1 0 0 -1 -1 17 19 6 2 0 0 20 5 1 -1 -1 18 0 0 25 -1 -1 9 7 -1 0 0 10 8 -1 26 -1 -1 0 0 -1 -1 -1 27 19 13 0 0 28 20 14 -1 -1 -1 1 0 13 17 27 29 -1 6 1 0 30 -1 5 14 18 28 
^ 0 0 31 33 23 -1 30 40 0 0 -1 29 39 32 34 24 0 0 33 35 25 -1 -1 21 0 0 -1 -1 22 34 36 26 0 0 35 -1 -1 15 -1 23 0 0 16 -1 24 36 -1 -1 0 0 17 -1 -1 37 29 19 0 0 38 30 20 18 -1 -1 0 0 19 27 37 39 22 -1 0 0 40 21 -1 20 28 38 
^ 0 0 -1 -1 33 21 40 -1 0 0 22 39 -1 -1 -1 34 0 0 -1 -1 35 23 21 31 0 0 24 22 32 -1 -1 36 1 0 -1 -1 -1 25 23 33 1 0 26 24 34 -1 -1 -1 0 0 27 -1 -1 -1 39 29 0 0 -1 40 30 28 -1 -1 1 0 29 37 -1 -1 32 22 1 0 -1 31 21 30 38 -1 
^ 
^1
^ 38 37 
*** OPTIONAL ***
^2
*** TURN
^ 0 0 
^ 43 0 0 17 17 0 0 0 0 0 0 0 0 0 0 0 0 0 0 37 
^ 37 0 0 0 0 0 0 5000 200000 0 0 0 0 0 0 17 17 5000 600000 2013 
^ 500012 
*** END

^ \s
""";

    String cleanInput = Stream.of(input.split("\n"))
                              .map(String::trim)
                              .filter(s -> s.length() != 0 && s.charAt(0) == '^')
                              .map(s -> s.replace("^", " ").concat("\n")) // remove ^
                              .collect(Collectors.joining());

    System.err.println("Cleaned string is " + cleanInput);
    wrapper.readFromInput(cleanInput);
    
    wrapperResult.state = new State();
  }
}
