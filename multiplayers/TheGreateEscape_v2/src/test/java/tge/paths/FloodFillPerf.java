package tge.paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import tge.Agent;
import tge.Grid;
import tge.Player;
import tge.Point;
import tge.WallOrientation;

public class FloodFillPerf {
  @Before
  public void setup() {
    Player.grid = new Grid();
    Player.agents[0] = new Agent(0);
    Player.agents[1] = new Agent(1);
  }
  
  @Test
  public void perfs() throws Exception {
    Player.agents[0].position = Point.get(7, 3);
    Player.agents[1].position = Point.get(2, 4);
    
    Player.grid.setWall(Point.get(2, 3), WallOrientation.VERTICAL);
    Player.grid.setWall(Point.get(6 ,7), WallOrientation.HORIZONTAL);
    Player.grid.setWall(Point.get(8, 7), WallOrientation.VERTICAL);
    
    int cells = 0;
    FloodFill floodFill = new FloodFill();
    for (int i=0;i<81*81*81;i++) {
      cells = floodFill.floodFillFromExit(1);
    }
    
    assertThat(cells, is(80));
  }
}
