package cgfx.sample1.components;

import cgfx.Cell;
import cgfx.CellBoard;
import cgfx.Inset;
import cgfx.Length;
import cgfx.Pos;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.GameWrapper;
import javafx.scene.paint.Color;

public class Sample1GameViewer extends GameViewer {
  private static final int CELL_SIZE = 64;

  private GameWrapper wrapper;
  private CellBoard board = new CellBoard(this, CELL_SIZE, 7, 7);
  
  
  public Sample1GameViewer(GameWrapper wrapper) {
    this.wrapper = wrapper;
  }

  @Override
  protected void updateView() {
      board.clear();
      
      board.drawLine(Color.BLACK, Pos.from(0, 0), Pos.from(100, 100));
      
      for (int y=3;y<5;y++) {
        for (int x=3;x<6;x++) {
          board.drawCellAt(Color.RED, Cell.at(x,y), Inset.of(2));
        }
      }

      for (int y=3;y<5;y++) {
        for (int x=3;x<5;x++) {
          board.drawCellCircle(Color.BLUE, Cell.at(x,y), Inset.of(10));
        }
      }

      
      board.fillCellAt(Color.BLACK, Cell.at(0,0), Inset.of(1));
      board.fillCellAt(Color.BLACK, Cell.at(6,6), Inset.of(1));

      for (int y=5;y<6;y++) {
        for (int x=0;x<2;x++) {
          board.fillCellAt(Color.BLACK, Cell.at(x,y), Inset.of(1));
        }
      }
      
      for (int y=0;y<7;y++) {
        for (int x=0;x<7;x++) {
          board.drawCellText(Color.BLUEVIOLET, Cell.at(x, y), Length.of(4, 2), ""+x+","+y);
        }
      }
      
  }

  @Override
  public void setOptionsPane(GameOptionPane options) {
    
  }

}
