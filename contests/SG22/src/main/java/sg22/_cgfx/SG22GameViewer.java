package sg22._cgfx;

import cgfx.Board;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.GameWrapper;

public class SG22GameViewer extends GameViewer {

  private Board board;
  private SG22GameWrapper wrapper;

  public SG22GameViewer(GameWrapper wrapper) {
    this(10, wrapper);
  }
  
  public SG22GameViewer(int scale, GameWrapper wrapper) {
    board = new Board(this, 17630 , 9000, 1.0 / scale);
    
    this.wrapper = (SG22GameWrapper)wrapper;
  }
  
  @Override
  protected void updateView() {
    
  }

  @Override
  public void setOptionsPane(GameOptionPane options) {
  }

}
