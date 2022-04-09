package othello;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class StateTest {

  @Test
  public void start() throws Exception {
    State state = new State();
    state.grids[0]=34628173824L;
    state.grids[1]=68853694464L;
    state.scores[0]=2;
    state.scores[1]=2;
    

    int swapped = state.putTile(3, 2, 0);

    Assertions.assertThat(swapped).isEqualTo(1);
  }
  
  @Test
  public void hasOneNeighbor() throws Exception {
    State state = new State();
    state.grids[0]=34628173824L;
    state.grids[1]=68853694464L;
    state.scores[0]=2;
    state.scores[1]=2;
    

    boolean neighbor  = state.hasOppNeighbor(Pos.from(3, 2), 0);

    Assertions.assertThat(neighbor).isTrue();
  }
  
  
}
