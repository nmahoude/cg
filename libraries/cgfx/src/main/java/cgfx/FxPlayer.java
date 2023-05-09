package cgfx;

import java.io.FileNotFoundException;

import cgfx.components.ActionsList;
import cgfx.components.CGFactory;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.frames.GameReader;
import cgfx.wrappers.GameWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FxPlayer {
  private final CGFactory factory;

  GameReader gameReader = new GameReader();
  GameWrapper gameWrapper;
  GameWrapper gameWrapper2;
  Replayer replayer;
  
  GameViewer gameViewer2;

  private Label label;
  private Label status;
  private Slider whatIfSlider;
  private Button replay;
  private int currentWhatIfDepth;

  private TextArea stderrView;
  private Canvas evaluator;
  private ActionsList actionList;

  private GameViewer newGV;

  public FxPlayer(CGFactory factory) throws FileNotFoundException {
    this.factory = factory;
    gameWrapper = factory.createGameWrapper();
    gameWrapper2 = factory.createGameWrapper();
    gameViewer2 = factory.createGameViewer(gameWrapper2);

    setupPlayer();

    GameWrapper newGW = factory.createGameWrapper();
    newGV = factory.createGameViewer(newGW);
    replayer = new Replayer(gameReader, newGW, newGV);
    replayer.addViewer(turn -> updateGameTurn(turn));

  }
  
  public void start(Stage primaryStage) throws Exception {
    setupScene(primaryStage);
    replayer.setTurn(1);
  }

  private void setupScene(Stage primaryStage) {
    primaryStage.setTitle("CG - Action comparator");
    VBox vbox = new VBox();

    label = new Label("Status");
    status = new Label("init");

    VBox boards = new VBox();
    stderrView = new TextArea();
    stderrView.setPrefSize(300, 600);

    evaluator = new Canvas();
    evaluator.setWidth(600);
    evaluator.setHeight(400);
    evaluator.setScaleY(-1);

    boards.getChildren().add(new Label("Current"));
    boards.getChildren().add(new HBox(replayer, new VBox(1.0, new Label("stderr:"), stderrView), evaluator));
    boards.getChildren().add(new Label("What if"));

    actionList = new ActionsList(gameWrapper);

    whatIfSlider = new Slider();
    whatIfSlider.setMin(0);
    whatIfSlider.setMax(20);
    whatIfSlider.setValue(0);
    whatIfSlider.setShowTickLabels(true);
    whatIfSlider.setShowTickMarks(true);
    whatIfSlider.setMajorTickUnit(1);
    whatIfSlider.setMinorTickCount(0);
    whatIfSlider.setBlockIncrement(1);
    whatIfSlider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
        currentWhatIfDepth = newVal.intValue();
        redrawWhatIf();
        actionList.getSelectionModel().select(currentWhatIfDepth);
      }
    });

    actionList.getSelectionModel().selectedItemProperty().addListener(event -> {
      currentWhatIfDepth = actionList.getSelectionModel().getSelectedIndex();
      redrawWhatIf();
      whatIfSlider.setValue(currentWhatIfDepth);
    });

    boards.getChildren().add(whatIfSlider);

    HBox bhbox = new HBox(gameViewer2, actionList);
    boards.getChildren().add(bhbox);

    replay = new Button("Replay");
    replay.setOnAction(value -> {
      gameWrapper.think();
      refreshWhatIf();
    });

    Scene scene = new Scene(new HBox(vbox, boards), 1200, 768);

    primaryStage.setScene(scene);
    primaryStage.show();

    vbox.getChildren().add(label);
    vbox.getChildren().add(status);
    vbox.getChildren().add(replay);

    GameOptionPane options = factory.createGameOptionPane();
    if (options != null) {
      options.register(newGV);
      options.register(gameViewer2);

      newGV.setOptionsPane(options);
      gameViewer2.setOptionsPane(options);

      vbox.getChildren().add(options);
    }
  }

  private void refreshWhatIf() {
    actionList.refreshActions();

    // force redraw
    whatIfSlider.setMax(actionList.actions().size()-1);
    whatIfSlider.setValue(1);
    whatIfSlider.setValue(0);
  }

  private void redrawWhatIf() {
    if (currentWhatIfDepth >= 0) {
      gameWrapper2.resetFromBase();
      for (int i = 0; i <= currentWhatIfDepth; i++) {
        gameWrapper2.applyAction(actionList.actions().get(i));
      }
    }
  }

  private void setupPlayer() throws FileNotFoundException {
    gameReader.readReplayFromFile("referee.json");
    gameWrapper.readGlobal(gameReader.getInput(0));
    gameWrapper2.copyFrom(gameWrapper);
  }

  private void updateGameTurn(int turn) {
    stderrView.clear();
    stderrView.appendText(gameReader.getStderr(turn));
    gameWrapper.readTurn(gameReader.getInput(turn));
    gameWrapper2.copyFrom(gameWrapper);
    gameWrapper.think();

    refreshWhatIf();

  }
}
