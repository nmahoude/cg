package cgfx;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import cgfx.components.GameViewer;
import cgfx.frames.GameReader;
import cgfx.wrappers.GameWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Replayer extends VBox {

  private GameViewer gameViewer;
  private HBox buttons;
  
  private int turn;
  private Button first, next, prev, last;
  
  private int maxTurns = 200;
  private GameReader gameReader;
  private GameWrapper wrapper;
  private Label turnLabel;
  
  Set<Consumer<Integer>> viewers = new HashSet<>();
  
  public Replayer(GameReader gameReader, GameWrapper wrapper, GameViewer gameViewer) {
    this.gameReader = gameReader;
    this.wrapper = wrapper;
    this.gameViewer = gameViewer;
    

    buttons = new HBox();

    this.setMargin(gameViewer, new Insets(10.0));
    this.setMargin(buttons, new Insets(10.0));
    
    first = new Button("<<");
    first.setOnAction(value -> setTurn(0));

    prev = new Button("<");
    prev.setOnAction(value -> setTurn(turn-1));

    next = new Button(">");
    next.setOnAction(value -> setTurn(turn+1));

    last = new Button(">>");
    last.setOnAction( value -> setTurn(maxTurns));
    
    
    turnLabel = new Label();
    
    buttons.getChildren().add(first);
    buttons.getChildren().add(prev);
    buttons.getChildren().add(next);
    buttons.getChildren().add(last);
    
    buttons.getChildren().add(turnLabel);
    
    this.getChildren().add(gameViewer);
    this.getChildren().add(buttons);

    setTurn(0);
    setMaxturn(gameReader.getMaxTurn()-1);
  }

  public int getCurrentTurn() {
    return turn;
  }
  
  private void setMaxturn(int maxTurn) {
    this.maxTurns = maxTurn;
  }

  public void updateView() {
    gameViewer.update(null, null);
  }
  
  public void setTurn(int turn) {
    this.turn = turn;
    turnLabel.setText(""+turn);
    try {
      wrapper.readFrame(gameReader.getMyFrame(turn));
      updateView();
    } catch (Exception e) {
      e.printStackTrace();
    }

    first.setDisable(turn == 1);
    prev.setDisable(turn == 1);
    next.setDisable(turn == maxTurns);
    last.setDisable(turn == maxTurns);
    
    
    viewers.forEach(v -> v.accept(turn));
  }
  
  public void addViewer(Consumer<Integer> c) {
    viewers.add(c);
  }
}
