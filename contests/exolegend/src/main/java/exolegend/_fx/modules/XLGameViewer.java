package exolegend._fx.modules;

import cgfx.Board;
import cgfx.Length;
import cgfx.Pos;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import exolegend.State;
import javafx.scene.Group;
import javafx.scene.paint.Color;

public class XLGameViewer extends GameViewer {
  private final static double boardScale = 0.07;
  
  private Board board;
  
  private final XLGameWrapper wrapper;
  private final State state;
  private final XLGameOptionPane options;

  public XLGameViewer(XLGameWrapper wrapper, XLGameOptionPane options) {
    this.wrapper = wrapper;
    this.options = options;
    this.state = wrapper.state;
    
    var gBoard = new Group();
    board = new Board(gBoard, 10_000, 10_000, boardScale);
    
    
    this.getChildren().add(gBoard);
    options.register(this);
  }

  @Override
  protected void updateView() {
    
    board.clear();
    board.drawRect(Color.BLACK, Pos.from(0, 0), Length.of(10_000, 10_000));
    board.drawCircle(Color.BLUE, Pos.from(5000, 5000), 4000);
    
  }


  @Override
  public void setOptionsPane(GameOptionPane options) {
  }


  
}
