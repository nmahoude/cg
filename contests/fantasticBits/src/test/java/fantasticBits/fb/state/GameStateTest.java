package fantasticBits.fb.state;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class GameStateTest {

  @Test
  public void duplicateIsNotNull() {
    GameState state = new GameState();
    
    assertThat(state.duplicate(), is(not(nullValue())));
  }
}
