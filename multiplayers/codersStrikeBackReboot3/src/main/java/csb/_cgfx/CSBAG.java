package csb._cgfx;

import java.io.FileNotFoundException;

import csb.Player;
import csb.State;
import csb.ai.AG;
import csb.ai.AGSolution;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CSBAG extends Application {
  CSBGameWrapper wrapper = new CSBGameWrapper();
  CSBGameViewer viewer = new CSBGameViewer(wrapper);

  CSBGameWrapper wrapper2 = new CSBGameWrapper();
  CSBGameViewer viewer2 = new CSBGameViewer(wrapper2);

  AG ag = new AG();
  
  private ListView<String> listOfSolutions;
  private ObservableList<String> solutionItems = FXCollections.observableArrayList ();

  private ListView<String> listOfSteps;
  private ObservableList<String> stepItems = FXCollections.observableArrayList ();
  
  private int selectedSolution = -1;
  public static void main(String[] args) throws FileNotFoundException {
    
    Application.launch(args);

  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {

    readGame();

    listOfSolutions = new ListView<>();
    for (int i=0;i<AG.POPULATION_SIZE;i++) {
      solutionItems.add("Sol "+i);
    }
    listOfSolutions.setItems(solutionItems);

    listOfSteps = new ListView<>();
    for (int i=0;i<AG.DEPTH;i++) {
      stepItems.add("Depth "+i);
    }
    listOfSteps.setItems(stepItems);

    VBox viewer2Group = new VBox ();
    viewer2Group.getChildren().add(viewer2);
    Text depthActionsText = new Text();
    
    Slider depthSlider = new Slider();
    depthSlider.setMin(0);
    depthSlider.setMax(AG.DEPTH);
    depthSlider.setShowTickLabels(true);
    depthSlider.setShowTickMarks(true);
    depthSlider.setMajorTickUnit(1);
    depthSlider.setMinorTickCount(0);
    depthSlider.setBlockIncrement(1);

    
    HBox buttons = new HBox();
    
    Button nextStep = new Button("Next");
    nextStep.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      ag.doOnePly(wrapper.state);

      updateList(ag);
    });
    buttons.getChildren().add(nextStep);

    Button next100Step = new Button("Next 100");
    next100Step.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      for (int i=0;i<100;i++) {
        ag.doOnePly(wrapper.state);
      }

      updateList(ag);
    });
    buttons.getChildren().add(next100Step);
    Button next1000Step = new Button("Next 1000");
    next1000Step.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      for (int i=0;i<1000;i++) {
        ag.doOnePly(wrapper.state);
      }

      updateList(ag);
    });
    buttons.getChildren().add(next1000Step);
    
    
    Button space = new Button("  ");
    buttons.getChildren().add(space);

    
    Button fullRandomStep = new Button("Random");
    fullRandomStep.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      wrapper.state.restore();
      ag.resetAG();
      ag.initFullRandomPopulation(wrapper.state);
      
      updateList(ag);
    });
    buttons.getChildren().add(fullRandomStep);
    
//    Button direction200Step = new Button("Directions 200");
//    direction200Step.setOnAction(value -> {
//      Player.start = System.currentTimeMillis();
//      ag.resetAG();
//      ag.initSpeedStraight(state, 200);
//
//      updateList(ag);
//    });
//    buttons.getChildren().add(direction200Step);
//
//    Button direction400Step = new Button("Directions 400");
//    direction400Step.setOnAction(value -> {
//      Player.start = System.currentTimeMillis();
//      ag.resetAG();
//      ag.initSpeedStraight(state, 400);
//
//      updateList(ag);
//    });
//    buttons.getChildren().add(direction400Step);
//
//    Button direction800Step = new Button("Directions 800");
//    direction800Step.setOnAction(value -> {
//      Player.start = System.currentTimeMillis();
//      ag.resetAG();
//      ag.initSpeedStraight(state, 800);
//
//      updateList(ag);
//    });
//    buttons.getChildren().add(direction800Step);
//
//
//    Button directionAndRandomStep = new Button("Directions &random");
//    directionAndRandomStep.setOnAction(value -> {
//      Player.start = System.currentTimeMillis();
//      ag.resetAG();
//      ag.initSpeedStraightAndRandom(state, 800);
//      
//      updateList(ag);
//    });
//    buttons.getChildren().add(directionAndRandomStep);
//
//    Button WindInAllDirectionStep = new Button("Wind all dir");
//    WindInAllDirectionStep.setOnAction(value -> {
//      Player.start = System.currentTimeMillis();
//      ag.resetAG();
//      ag.initWindAllDirection(state);
//      
//      updateList(ag);
//    });
//    buttons.getChildren().add(WindInAllDirectionStep);

    
    
    depthSlider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
      	wrapper.state.restore();
        State state = wrapper2.player.state;
        state.copyFrom(wrapper.player.state);

        Pos[] previousPos = new Pos[] { new Pos(), new Pos() };
        
        previousPos[0].copyFrom(state.pods[0].x, state.pods[0].y);
        previousPos[1].copyFrom(state.pods[1].x, state.pods[1].y);

        int maxDepth = newVal.intValue();
        AGSolution solution = ag.solutions[selectedSolution];
        
        for (int depth=0;depth<maxDepth;depth++) {
          solution.apply(state, depth);
        }
        
        viewer2.update(null, null);
        drawSolution(viewer2, solution, Color.RED, 0.5);
        
//        ActionDrawer.drawAction(viewer2, state.hero[0], actions.actions[0], state.findPosById(actions.actions[0].targetEntity));
//        ActionDrawer.drawAction(viewer2, state.hero[1], actions.actions[1], state.findPosById(actions.actions[1].targetEntity));
//
//        depthActionsText.setText(actions.actions[0]+" \n\r "+actions.actions[1]);
      }
    });

    listOfSolutions.getSelectionModel().selectedItemProperty().addListener(event -> {
      selectedSolution = listOfSolutions.getSelectionModel().getSelectedIndex();
      updateView();
      depthSlider.setValue(1);
      depthSlider.setValue(0);
    });
    viewer2Group.getChildren().add(depthSlider);
    viewer2Group.getChildren().add(depthActionsText);

    listOfSolutions.getSelectionModel().select(0);

    
    
    HBox view = new HBox(viewer, viewer2Group);
    HBox solutions = new HBox(listOfSolutions, listOfSteps);
    VBox vbox = new VBox(buttons, view, solutions);
    
    Scene scene = new Scene(vbox, 1920, 1080);

    
    primaryStage.setScene(scene);
    primaryStage.setTitle("Spring22 - AG Debugger");
    primaryStage.show();
  }

  private void updateList(AG ag) {

    for (int i=0;i<ag.POPULATION_SIZE;i++) {
      listOfSolutions.getItems().set(i, "Sol "+i+" => "+ag.solutions[i].score);
    }

    
    
    listOfSolutions.getSelectionModel().select(1);
    listOfSolutions.getSelectionModel().select(0);
  }

  private void updateView() {
    
    viewer.update(null, null);
    viewer2.update(null, null);
    debugAG();
    
  }


  private void debugAG() {
    
    double minScore = Double.POSITIVE_INFINITY;
    double maxScore = Double.NEGATIVE_INFINITY;
    

    for (int i=AG.POPULATION_SIZE-1;i>=0;i--) {
      AGSolution solution = ag.solutions[i];
      minScore = Math.min(minScore, solution.score);
      maxScore = Math.max(maxScore, solution.score);
    }
    
    for (int i=AG.POPULATION_SIZE-1;i>=0;i--) {
      AGSolution solution = ag.solutions[i];
      double perc = (solution.score  - minScore) / (maxScore-minScore);
      Color color;
      double drawSize;

      
      if (i == selectedSolution) {
        continue;
      } else if (i >= AG.SURVIVOR_SIZE) {
        drawSize = 0.1;
        color = new Color(0, 0.0, perc, 1.0); // blues
      } else {
        drawSize = 1;
        color = new Color(0.0, perc, 0, 1.0); // reds
      }
      drawSolution(viewer, solution, color, drawSize);
    }
    
    if (selectedSolution>=0 && selectedSolution< AG.POPULATION_SIZE) {
      drawSolution(viewer, ag.solutions[selectedSolution], Color.RED, 5);
    }

  }

  private void drawSolution(CSBGameViewer viewer, AGSolution solution, Color color, double drawSize) {
    wrapper.player.state.restore();
    State state = new State();
    state.copyFrom(wrapper.player.state);
    
    Pos[] previousPos = new Pos[] { new Pos(), new Pos() };
    
    previousPos[0].copyFrom(state.pods[0]);
    previousPos[1].copyFrom(state.pods[1]);
    
    for (int depth=0;depth<AG.DEPTH;depth++) {
      solution.apply(state, depth);
      listOfSteps.getItems().set(depth, "Depth "+depth+" => "+solution.score);
      
      for (int a=0;a<2;a++) {
        viewer.board().drawLine(color, drawSize, cgfx.Pos.from(previousPos[a].x, previousPos[a].y), cgfx.Pos.from(state.pods[a].x, state.pods[a].y));
        previousPos[a].copyFrom(state.pods[a]);
      }
    }
  }

  private void readGame() {
    wrapper.readFromInput(true, """
				^ 3 6
				^ 7654 5991
				^ 3139 7542
				^ 9496 4377
				^ 14495 7808
				^ 6339 4314
				^ 7821 839
        
				^ 53 0 0 
				^ 1228 7814 100 0 0 2
				^ 4866 5128 100 0 180 1
				^ 13839 4005 226 222 77 3
				^ 14575 6776 205 342 84 3
        """);
    
    
    // now that data are read, consider we are not inversed anymore !
    
    Player.start = System.currentTimeMillis() + 100;

    ag.resetAG();
    ag.initFullRandomPopulation(wrapper.state);
    wrapper.state.restore();
    
//    ag.information.update(wrapper.state());
//    ag.initSpeedStraight(state, State.HERO_MAX_MOVE);
    
    //ag.initRandomPopulation(state);
    //ag.initPopulationFromLastTurn(state);
    //ag.initSpeedStraight(state, 400);
    //ag.initSpeedStraightAndRandom(state, 800);
  }
}