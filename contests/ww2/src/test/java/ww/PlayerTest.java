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
  
}
