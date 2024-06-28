package spring2022._cgfx;

import java.io.FileNotFoundException;

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
import spring2022.Action;
import spring2022.Player;
import spring2022.Pos;
import spring2022.State;
import spring2022._cgfx.components.S22GameViewer;
import spring2022._cgfx.components.S22GameWrapper;
import spring2022.ag.AG;
import spring2022.ag.AGEvaluator;
import spring2022.ag.AGSolution;
import spring2022.ag.LightState;
import spring2022.ag.Simulator;
import spring2022.ai.TriAction;

public class S22AG extends Application {
  private static final AGEvaluator AG_EVALUATOR = new AGEvaluator();
  AG ag = new AG();
  S22GameWrapper wrapper = new S22GameWrapper();
  S22GameViewer viewer = new S22GameViewer(15, wrapper);
  
  S22GameWrapper wrapper2 = new S22GameWrapper();
  S22GameViewer viewer2 = new S22GameViewer(15, wrapper2);
  
  private ListView<String> listOfSolutions;
  private ObservableList<String> solutionItems = FXCollections.observableArrayList ();

  private ListView<String> listOfSteps;
  private ObservableList<String> stepItems = FXCollections.observableArrayList ();
  
  
  private LightState state;
  private int selectedSolution = -1;
  public static void main(String[] args) throws FileNotFoundException {
    
    Application.launch(args);

  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {

    readGame();
    wrapper2.lightState = new LightState(); // hack hack hack

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
      ag.doOnePly(state);

      updateList(ag);
    });
    buttons.getChildren().add(nextStep);

    Button next100Step = new Button("Next 100");
    next100Step.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      for (int i=0;i<100;i++) {
        ag.doOnePly(state);
      }

      updateList(ag);
    });
    buttons.getChildren().add(next100Step);
    Button next1000Step = new Button("Next 1000");
    next1000Step.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      for (int i=0;i<1000;i++) {
        ag.doOnePly(state);
      }

      updateList(ag);
    });
    buttons.getChildren().add(next1000Step);
    
    
    Button space = new Button("  ");
    buttons.getChildren().add(space);

    
    Button fullRandomStep = new Button("Random");
    fullRandomStep.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      ag.resetAG();
      ag.initFullRandomPopulation(state);

      updateList(ag);
    });
    buttons.getChildren().add(fullRandomStep);
    
    Button direction200Step = new Button("Directions 200");
    direction200Step.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      ag.resetAG();
      ag.initSpeedStraight(state, 200);

      updateList(ag);
    });
    buttons.getChildren().add(direction200Step);

    Button direction400Step = new Button("Directions 400");
    direction400Step.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      ag.resetAG();
      ag.initSpeedStraight(state, 400);

      updateList(ag);
    });
    buttons.getChildren().add(direction400Step);

    Button direction800Step = new Button("Directions 800");
    direction800Step.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      ag.resetAG();
      ag.initSpeedStraight(state, 800);

      updateList(ag);
    });
    buttons.getChildren().add(direction800Step);


    Button directionAndRandomStep = new Button("Directions &random");
    directionAndRandomStep.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      ag.resetAG();
      ag.initSpeedStraightAndRandom(state, 800);
      
      updateList(ag);
    });
    buttons.getChildren().add(directionAndRandomStep);

    Button WindInAllDirectionStep = new Button("Wind all dir");
    WindInAllDirectionStep.setOnAction(value -> {
      Player.start = System.currentTimeMillis();
      ag.resetAG();
      ag.initWindAllDirection(state);
      
      updateList(ag);
    });
    buttons.getChildren().add(WindInAllDirectionStep);

    
    
    depthSlider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
        TriAction actions = new TriAction();
        LightState state = wrapper2.lightState;
        state.createFrom(wrapper.player.state);


        
        Pos[] previousPos = new Pos[] { new Pos(), new Pos() };
        
        previousPos[0].copyFrom(state.hero[0].pos);
        previousPos[1].copyFrom(state.hero[1].pos);

        int maxDepth = newVal.intValue();
        AGSolution solution = ag.solutions[selectedSolution];
        
        for (int depth=0;depth<maxDepth;depth++) {
          actions.actions[0].updateFromAGValues(state.hero[0], solution.c[depth][0].angle, solution.c[depth][0].speed);
          actions.actions[1].updateFromAGValues(state.hero[1], solution.c[depth][1].angle, solution.c[depth][1].speed);
          actions.actions[2].copyFrom(Action.WAIT);
          
          if (depth == maxDepth-1) {
            System.err.println("last for debug point");
          }
          Simulator.apply(state, depth, actions, null);
          
        }
        
        viewer2.update(null, null);
        drawSolution(viewer2, solution, Color.RED, 0.5);
        
        ActionDrawer.drawAction(viewer2, state.hero[0], actions.actions[0], state.findPosById(actions.actions[0].targetEntity));
        ActionDrawer.drawAction(viewer2, state.hero[1], actions.actions[1], state.findPosById(actions.actions[1].targetEntity));

        depthActionsText.setText(actions.actions[0]+" \n\r "+actions.actions[1]);
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
        color = new Color(perc, 0.0, 0, 1.0); // reds
      }
      drawSolution(viewer, solution, color, drawSize);
    }
    
    if (selectedSolution>=0 && selectedSolution< AG.POPULATION_SIZE) {
      drawSolution(viewer, ag.solutions[selectedSolution], Color.RED, 5);
    }

  }

  private void drawSolution(S22GameViewer viewer, AGSolution solution, Color color, double drawSize) {
    TriAction action = new TriAction();
    LightState state = new LightState();
    state.createFrom(wrapper.player.state);
    
    Pos[] previousPos = new Pos[] { new Pos(), new Pos() };
    
    previousPos[0].copyFrom(state.hero[0].pos);
    previousPos[1].copyFrom(state.hero[1].pos);
    
    for (int depth=0;depth<AG.DEPTH;depth++) {
      action.actions[0].updateFromAGValues(state.hero[0], solution.c[depth][0].angle, solution.c[depth][0].speed);
      action.actions[1].updateFromAGValues(state.hero[1], solution.c[depth][1].angle, solution.c[depth][1].speed);
      action.actions[2].copyFrom(Action.WAIT);
      Simulator.apply(state, depth, action, null);
      double score = AG_EVALUATOR.evaluate(state);
      listOfSteps.getItems().set(depth, "Depth "+depth+" => "+score);
      
      for (int a=0;a<2;a++) {
        viewer.board().drawLine(color, drawSize, cgfx.Pos.from(previousPos[a].x, previousPos[a].y), cgfx.Pos.from(state.hero[a].pos.x, state.hero[a].pos.y));
        previousPos[a].copyFrom(state.hero[a].pos);
      }
    }
  }

  private void readGame() {
    wrapper.readFromInput(true, """
^3 4 3 130
^10
^0 1 1128 367 0 2 -1 -1 -1 -1 -1
^1 1 6002 1002 0 2 -1 -1 -1 -1 -1
^2 1 6604 2988 0 2 -1 -1 -1 -1 -1
^3 2 5630 400 0 2 -1 -1 -1 -1 -1
^5 2 5630 400 0 2 -1 -1 -1 -1 -1
^111 0 2156 2290 0 0 7 -274 -291 1 1
^116 0 3208 2733 0 0 15 -304 -259 1 1
^119 0 1606 1780 0 0 24 -267 -296 1 1
^127 0 6554 3407 0 0 23 -133 -376 0 0
^134 0 6169 111 0 0 26 -378 130 0 1
****************************
UNITS IN FOG - debug 'input'
****************************
^3
^126 0 11076 5593 0 0 17 133 376 0 0
^125 0 13087 6851 0 0 24 376 -134 0 1
^118 0 16024 7220 0 0 24 268 297 0 2
turn 169
Starting AG @ 0for depth 10
TODO : implements other pecalculated populations
AG 112 plies in 41 ms
Best ply @ 108 / 112 with score : 2.2051175792129137E7
*************************
*  ATTACKER V2     *
*************************
^turn 169
^mind 1
        """);
    
    // now that data are read, consider we are not inversed anymore !
    
    Player.start = System.currentTimeMillis() + 100;

    //AGSolution.evaluator = new AGEvaluatorDebug();
    this.state = new LightState();
    state.createFrom(wrapper.player.state);
    
    ag.resetAG();
    ag.information.update(wrapper.state());
    ag.initSpeedStraight(state, State.HERO_MAX_MOVE);
    
    //ag.initRandomPopulation(state);
    //ag.initPopulationFromLastTurn(state);
    //ag.initSpeedStraight(state, 400);
    //ag.initSpeedStraightAndRandom(state, 800);
  }
}