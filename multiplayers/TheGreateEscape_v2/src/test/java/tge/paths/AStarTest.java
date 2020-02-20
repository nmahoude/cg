package tge.paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import tge.Agent;
import tge.Cell;
import tge.Grid;
import tge.Player;
import tge.Point;
import tge.WallOrientation;

public class AStarTest {

  @Test
  public void astar() throws Exception {
    Player.grid = new Grid();
    Player.agents[0] = new Agent(0);
    Player.agents[0].position = Point.get(0,0);
    
    List<Cell> path = AStar.astar(Player.grid.get(Point.get(0, 0)), 0);
    
    assertThat(path.size(), is(9));
  }

  @Test
  public void astar_withWall() throws Exception {
    Player.grid = new Grid();

    Player.grid.setWall(Point.get(7, 0), WallOrientation.VERTICAL);
    List<Cell> path = AStar.astar(Player.grid.get(Point.get(0, 0)), 0);
    
    assertThat(path.size(), is(11));
  }

  @Test
  public void astar_withWallInFront() throws Exception {
    Player.grid = new Grid();

    Player.grid.setWall(Point.get(5, 3), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(7, 3), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(5, 4), WallOrientation.HORIZONTAL);
    List<Cell> path = AStar.astar(Player.grid.get(Point.get(6, 3)), 0);
    assertThat(path.size()-1, is(7));
  }
  
  @Test
  public void astar_findAWay() throws Exception {
    Player.grid = new Grid();

    Player.grid.setWall(Point.get(4, 2), WallOrientation.VERTICAL);
    List<Cell> path = AStar.astar(Player.grid.get(Point.get(0, 3)), 0);
    assertThat(path.size()-1, is(9));
  }
}
