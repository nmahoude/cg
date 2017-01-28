package greatescape;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class PlayerTest {

  @Test
  public void WallOrintations() throws Exception {
    assertThat(WallOrientation.H.toString(), is ("H"));
    assertThat(WallOrientation.V.toString(), is ("V"));
  }
  
  @Test
  public void horizontalToBlockProgression() throws Exception {
    Board board = new Board();
    
    List<PathItem> trajectory = new AStar(board, board.cells[8][3], Player.getTargetFromId(1)).find();
    assertThat(trajectory.size(), is(9));

    board.addWall(7, 3, WallOrientation.H);
    List<PathItem> trajectory2 = new AStar(board, board.cells[8][3], Player.getTargetFromId(1)).find();
    assertThat(trajectory2.size(), is(9));
  }
  
  @Test
  public void hitWallTooEasily() throws Exception {
    Board board = new Board();
    board.addWall(2, 0, WallOrientation.V);
    board.addWall(1, 2, WallOrientation.V);
    board.addWall(1, 2, WallOrientation.H);
    
    List<PathItem> trajectory = new AStar(board, board.cells[0][2], Player.getTargetFromId(0)).find();
    Player.debugTrajectory(trajectory);
    assertThat(board.cells[0][2].canGoRight, is(false));
    assertThat(trajectory.get(1).pos, is(not(board.cells[1][2])));
  }
}
