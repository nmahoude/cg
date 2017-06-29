package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Move;
import ww.sim.Simulation;
import ww.think.Think;

public class PlayerTest {

  Divination divination;
  private GameState currentState;
  
  @Before
  public void setup() {
    currentState = new GameState();
    
  }
  
  @Test
  public void compareOldThinkWithNewThink() throws Exception {

    TU.setHeights(currentState, 5,
      "00000",
      "00000",
      "00000",
      "00000",
      "00010"
      );
    TU.setAgent(currentState, 0,1,4);
    TU.setAgent(currentState, 1,2,2);
    TU.setAgent(currentState, 2,1,4);
    TU.setAgent(currentState, 3,2,3);
    Simulation sim = new Simulation();
    
    Player.state = currentState;
    Player.sim = sim;
        
    Move best1 = Player.think(sim);
    
    GameState.startTime = System.currentTimeMillis()+100_000;
    Move best2 = new Think(currentState).think(1);
    
    assertThat(best2.agent, is(best1.agent));
    assertThat(best2.dir1, is(best1.dir1));
    assertThat(best2.dir2, is(best1.dir2));
    
  }
}
