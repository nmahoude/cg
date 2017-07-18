package tge.paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import tge.Agent;
import tge.Grid;
import tge.Player;
import tge.Point;
import tge.WallOrientation;

public class AccessibleCellsTest {

  @Before
  public void setup() {
    Player.grid = new Grid();
    Player.agents[0] = new Agent(0);
  }

  @Test
  public void test1() throws Exception {
    Player.agents[0].position = Point.get(0, 0);
    
    AccessibleCells ac = new AccessibleCells();
    int cells = ac.go(0);
    
    assertThat(cells, is(81));
  }

  @Test
  @Ignore
  public void ineffectiveWall() throws Exception {
    Player.agents[0].position = Point.get(5, 0);
    
    Player.grid.setWall(Point.get(5, 0), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(5, 2), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(5, 4), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(5, 6), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(5 ,7), WallOrientation.VERTICAL);

    
    Player.grid.setWall(Point.get(5, 4), WallOrientation.HORIZONTAL);
    AccessibleCells ac = new AccessibleCells();
    int cells = ac.go(0);
    
    assertThat(cells, is(36));
  }
  
  @Test
  public void blockHalfGrid() throws Exception {
    Player.agents[0].position = Point.get(0, 0);
    
    Player.grid.setWall(Point.get(0, 4), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(2, 4), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(4, 4), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(6, 4), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(7, 3), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(8, 3), WallOrientation.VERTICAL);
    AccessibleCells ac = new AccessibleCells();
    int cells = ac.go(0);
    
    assertThat(cells, is(40));
  }

  @Test
  public void blockAlmostAll() throws Exception {
    Player.agents[0].position = Point.get(7, 3);
    
    Player.grid.setWall(Point.get(7, 0), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(7, 2), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(7, 4), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(7, 6), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(7, 7), WallOrientation.VERTICAL);

    Player.grid.setWall(Point.get(8, 0), WallOrientation.VERTICAL);

    AccessibleCells ac = new AccessibleCells();
    int cells = ac.go(0);
    
    assertThat(cells, is(16));
  }
}
