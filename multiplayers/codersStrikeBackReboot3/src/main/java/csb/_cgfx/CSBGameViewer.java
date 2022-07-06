package csb._cgfx;

import cgfx.Board;
import cgfx.Length;
import cgfx.Pos;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import csb.State;
import javafx.scene.paint.Color;

public class CSBGameViewer extends GameViewer {

  Board board = new Board(this, 16000, 9000, 0.1);
  private CSBGameWrapper wrapper;
  
  public CSBGameViewer(CSBGameWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  protected void updateView() {
    board.clear();
    board.getGc().setLineWidth(1);
    
    board.drawRect(Color.BLACK, cgfx.Pos.from(0, 0), Length.of(16000,  9000));
    
    for (int i=0;i<State.checkPoints.length;i++) {
      board.fillCircle(Color.LIGHTBLUE, Pos.from(State.checkPoints[i].x, State.checkPoints[i].y), 600);
    }
    
    for (int i=0;i<2;i++) {
      board.fillCircle(Color.ORANGE, Pos.from(wrapper.state.pods[i].x, wrapper.state.pods[i].y), 400);
    }

    for (int i=2;i<4;i++) {
      board.fillCircle(Color.ROSYBROWN, Pos.from(wrapper.state.pods[i].x, wrapper.state.pods[i].y), 400);
    }
  }

  @Override
  public void setOptionsPane(GameOptionPane options) {
    // TODO Auto-generated method stub
    
  }

  public Board board() {
    return board;
  }

}
