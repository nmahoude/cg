package tge.minimax;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import tge.Agent;
import tge.Grid;
import tge.Player;
import tge.Point;
import tge.WallOrientation;

public class NodeTest {

  private Node node;

  @Before
  public void setup() {
    Player.grid = new Grid();
    Player.agents[Player.myId] = new Agent(0);
    node = new Node(0);
  }
  
  @Test
  public void countPossibleEmptySpace() throws Exception {
    Player.agents[Player.myId].position = Point.get(0, 1);

    int countPossibleCells = node.countPossibleCells(0);
    
    assertThat(countPossibleCells , is(81));
  }

  @Test
  public void horizontalWallonlyRemove2() throws Exception {
    Player.agents[Player.myId].position = Point.get(5, 1);

    Player.grid.setWall(Point.get(0, 1), WallOrientation.HORIZONTAL);
    int countPossibleCells = node.countPossibleCells(0);
    
    assertThat(countPossibleCells , is(79));
  }
}
