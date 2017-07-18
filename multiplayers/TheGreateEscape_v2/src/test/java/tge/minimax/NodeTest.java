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
import tge.paths.AccessibleCells;

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
  
  @Test
  public void blockAlmostAll() throws Exception {
    Player.agents[0].position = Point.get(6, 4);
    
    Player.grid.setWall(Point.get(6, 0), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(6, 2), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(6, 4), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(6, 6), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(6, 7), WallOrientation.VERTICAL);

    Player.grid.setWall(Point.get(5, 3), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(7, 2), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(7, 4), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(7, 6), WallOrientation.VERTICAL);

    int countPossibleCells = node.getMaximalPath_iteration2(0);
    
    assertThat(countPossibleCells , is(15));
  }
  
  @Test
  public void onlyOneWay() throws Exception {
    Player.agents[0].position = Point.get(6, 6);
    
    Player.grid.setWall(Point.get(6, 0), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(6, 2), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(6, 4), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(6, 6), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(6, 7), WallOrientation.VERTICAL);

    Player.grid.setWall(Point.get(7, 0), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(7, 2), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(7, 4), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(7, 6), WallOrientation.VERTICAL);

    Player.grid.setWall(Point.get(7, 8), WallOrientation.HORIZONTAL);

    int countPossibleCells = node.getMaximalPath_iteration2(0);
    
    assertThat(countPossibleCells , is(4));
  }
}
