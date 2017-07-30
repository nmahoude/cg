package tron;

import org.junit.Before;
import org.junit.Test;

import tron.algorithms.MultiBFS;
import tron.common.Point;

public class PlayerPerf {

  @Before
  public void setup() {
    Player.init();
  }
  
  @Test
  public void perf() throws Exception {
    Player.initAgents[0].currentCell = Player.grid.get(Point.get(0, 0));
    Player.initAgents[1].currentCell = Player.grid.get(Point.get(29, 19));
    Player.initAgents[2].currentCell = Player.grid.get(Point.get(15, 10));
    
    Player.agents[Player.playerFE++] = Player.initAgents[0]; 
    Player.agents[Player.playerFE++] = Player.initAgents[1]; 
    Player.agents[Player.playerFE++] = Player.initAgents[2]; 
    
    for (int i=0;i<100_000;i++) {
      MultiBFS.bfs();
    }
  }
}
