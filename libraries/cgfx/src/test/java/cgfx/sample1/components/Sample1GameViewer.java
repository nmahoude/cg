package cgfx.sample1.components;

import cgfx.Board;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.GameWrapper;
import javafx.scene.paint.Color;

public class Sample1GameViewer extends GameViewer {

  private GameWrapper wrapper;
  private Board board = new Board(this, 64, 7, 7);
  
  
  public Sample1GameViewer(GameWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  protected void updateView() {
    board.clear();

    board.drawRect(Color.YELLOW, 0, 0, 2*64*7, 0 );
    
    board.drawText(Color.BLACK, "Text: ", 0, 400);
    board.drawText(Color.BLACK, "Under ", 0, 424);
  }

  @Override
  public void setOptionsPane(GameOptionPane options) {
    
  }

}
