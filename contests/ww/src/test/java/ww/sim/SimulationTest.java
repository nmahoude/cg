package ww.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.Dir;
import ww.GameState;

public class SimulationTest {
  GameState state ;
  Simulation simulation;
  
  @Before 
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));
  
    simulation = new Simulation();
    
  }
  
  @Test
  public void canPushTowardAKnownAgent() {
    state.size = 5;
    setAgent(0,3,0);
    setAgent(1,4,2);
    setAgent(2,3,1);
    setAgent(3,-1,-1);
    setHeights(
      "00303",
      "00233",
      "00003",
      "00003",
      "00000"
    );
    
    Move move = getMove(1, Dir.NW, Dir.N);

    boolean valid = simulation.simulate(move, state);
    
    assertThat(valid, is(false));
  }
  private Move getMove(int i, Dir dir1, Dir dir2) {
    Move move = new Move();
    move.index = i;
    move.dir1 = dir1;
    move.dir2 = dir2;
    return move;
  }

  private void setHeights(String... rows) {
    for (int y=0;y<rows.length;y++) {
      for (int x=0;x<rows.length;x++) {
        char c = rows[y].charAt(x);
        if (c == '.') {
          state.grid.setHole(x, y);
        } else {
          state.setHeight(x, y, c-'0');
        }
      }
    }
  }
  private void setAgent(int id, int x, int y) {
    state.agents[id].x = x;
    state.agents[id].y = y;
  }
  
  
}
