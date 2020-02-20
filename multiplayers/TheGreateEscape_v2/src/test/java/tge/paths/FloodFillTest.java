package tge.paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import tge.Agent;
import tge.Grid;
import tge.Player;
import tge.Point;
import tge.WallOrientation;

public class FloodFillTest {
  @Before
  public void setup() {
    Player.grid = new Grid();
    Player.agents[0] = new Agent(0);
    Player.agents[1] = new Agent(1);
  }

  
  @Test
  public void blockAlmostAll() throws Exception {
    Player.agents[0].position = Point.get(7, 3);
    Player.agents[1].position = Point.get(2, 4);
    
    Player.grid.setWall(Point.get(2, 3), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(2, 5), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(4, 7), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(6 ,7), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(8, 7), WallOrientation.VERTICAL);
    
    int cells = new FloodFill().floodFillFromExit_dfs(1);
    
    assertThat(cells, is(80));
  }

  @Test
  public void bigPathOnBottom() throws Exception {
    Player.agents[0].position = Point.get(7, 3);
    Player.agents[1].position = Point.get(3, 5);
    
    Player.grid.setWall(Point.get(1, 0), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(1, 2), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(1, 3), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(3, 3), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(2, 4), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(2, 6), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(0, 8), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(2, 6), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(4, 4), WallOrientation.VERTICAL);
    
    int cells = new FloodFill().floodFillFromExit_dfs(1);
    
    assertThat(cells, is(not(16)));
  }
}
