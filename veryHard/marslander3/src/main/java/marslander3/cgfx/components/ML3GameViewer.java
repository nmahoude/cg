package marslander3.cgfx.components;

import cgfx.Board;
import cgfx.Pos;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.GameWrapper;
import javafx.scene.paint.Color;
import marslander3.Mars;
import marslander3.Player;

public class ML3GameViewer extends GameViewer{

  private ML3GameWrapper wrapper;
  private Board board = new Board(this, 700, 300);
  
  public ML3GameViewer(GameWrapper wrapper) {
    this.wrapper = (ML3GameWrapper)wrapper;
    
    board.getCanvas().setScaleY(-1.0);
  }

  @Override
  protected void updateView() {
    draw(this.wrapper.player);
  }

  private void draw(Player player) {
    board.clear();
    
    Mars mars = player.mars;
    
    for (int i=0;i<mars.pointsX.length-1;i++) {
      board.drawLine(Color.RED, Pos.from(mars.pointsX[i] / 10, mars.pointsY[i] / 10), 
                                Pos.from(mars.pointsX[i+1] / 10, mars.pointsY[i+1] / 10));
    }

    
    board.drawCircle(Color.BLACK, Pos.from(player.lander.getXAsInt() / 10, player.lander.getYAsInt() / 10), 8);
    
  }

  @Override
  public void setOptionsPane(GameOptionPane options) {
    // TODO Auto-generated method stub
    
  }

}
