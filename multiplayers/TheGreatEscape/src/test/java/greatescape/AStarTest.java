package greatescape;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class AStarTest {

  @Test
  public void AStar_without_walls() throws Exception {
    Board board = new Board();
    
    List<PathItem> trajectory = new AStar(board, board.cells[0][5], Target.RIGHT).find();
    
    assertThat(trajectory.size(), is(9));
  }
}
