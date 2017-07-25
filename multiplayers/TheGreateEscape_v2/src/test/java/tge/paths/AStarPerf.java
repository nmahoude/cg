package tge.paths;

import java.util.List;

import org.junit.Test;

import tge.Agent;
import tge.Cell;
import tge.Grid;
import tge.Player;
import tge.Point;
import tge.WallOrientation;

public class AStarPerf {
  @Test
  public void astar() throws Exception {
    Player.grid = new Grid();
    Player.agents[0] = new Agent(0);
    Player.agents[0].position = Point.get(0,4);

    Player.grid.setWall(Point.get(5, 3), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(4, 3), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(4, 5), WallOrientation.VERTICAL);
    
    double path;
    for (int i=0;i<81*81*81;i++) {
      AStar.astar(Player.grid.get(Point.get(0, 0)), 0);
    }
  }
}
