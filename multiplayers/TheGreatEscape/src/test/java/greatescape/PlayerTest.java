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

    board.addWall(14, 7, 3, WallOrientation.H);
    List<PathItem> trajectory2 = new AStar(board, board.cells[8][3], Player.getTargetFromId(1)).find();
    assertThat(trajectory2.size(), is(9));
  }
  
  @Test
  public void hitWallTooEasily() throws Exception {
    Board board = new Board();
    board.addWall(1, 2, 0, WallOrientation.V);
    board.addWall(2, 1, 2, WallOrientation.V);
    board.addWall(3, 1, 2, WallOrientation.H);
    
    List<PathItem> trajectory = new AStar(board, board.cells[0][2], Player.getTargetFromId(0)).find();
    Player.debugTrajectory(trajectory);
    assertThat(board.cells[0][2].wallRight, is(not(0)));
    assertThat(trajectory.get(1).pos, is(not(board.cells[1][2])));
  }
  
  
  @Test
  public void directDansLeMur() throws Exception {
    Board board = new Board();
    board.addWall(1,8, 0, WallOrientation.V);
    board.addWall(2,1, 1, WallOrientation.V);
    board.addWall(3,7, 8, WallOrientation.H);
    board.addWall(4,5, 8, WallOrientation.H);
    board.addWall(5,8, 2, WallOrientation.V);
    board.addWall(6,8, 4, WallOrientation.V);
    board.addWall(7,3, 8, WallOrientation.H);
    board.addWall(8,8, 6, WallOrientation.V);
    
    List<PathItem> trajectory = new AStar(board, board.cells[7][6], Player.getTargetFromId(0)).find();

    assertThat(trajectory.get(1).pos, is(not(board.cells[8][6])));
  }
}
