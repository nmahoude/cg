package tge.paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import tge.Agent;
import tge.Grid;
import tge.Player;
import tge.Point;

public class AccessibleCellsTest {

  @Test
  public void test1() throws Exception {
    Player.grid = new Grid();
    Player.agents[0] = new Agent(0);
    Player.agents[0].position = Point.get(0, 0);
    
    AccessibleCells ac = new AccessibleCells();
    int cells = ac.go(0);
    
    assertThat(cells, is(81));
  }
}
