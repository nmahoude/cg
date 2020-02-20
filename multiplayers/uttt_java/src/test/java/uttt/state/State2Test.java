package uttt.state;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class State2Test {

  @Test
  public void emptyGridSetForPlayer0() throws Exception {
    State2 state = new State2();
    
    state.set(true, 4, 0b10000);
    
    assertThat(state.cells[4], is(0b10000));
    assertThat(state.nextPlayGrid, is(4));
  }
  
  @Test
  public void emptyGridSetForPlayer1() throws Exception {
    State2 state = new State2();
    
    state.set(false, 4, 0b10000);
    
    assertThat(state.cells[4], is(0b10000_0000000000000000));
    assertThat(state.nextPlayGrid, is(4));
  }
  
}
