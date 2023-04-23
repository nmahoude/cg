package othello;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class StateTest {
  State state;

  @Before
  public void setup() {
    state = new State();
    
  }
  
  @Test
  public void start() throws Exception {
    state.grids[0]=34628173824L;
    state.grids[1]=68853694464L;
    state.scores[0]=2;
    state.scores[1]=2;
    

    int swapped = state.putTile(3, 2, 0);

    assertThat(swapped).isEqualTo(1);
  }
  
  @Test
  public void hasOneNeighbor() throws Exception {
    state.grids[0]=34628173824L;
    state.grids[1]=68853694464L;
    state.scores[0]=2;
    state.scores[1]=2;
    

    boolean neighbor  = state.hasOppNeighbor(Pos.from(3, 2), 0);

    assertThat(neighbor).isTrue();
  }
  
  
  @Test
  public void has4MovesPossibles() throws Exception {
    state.grids[0]=403177472L;
    state.grids[1]=120259084288L;
    state.scores[0]=3;
    state.scores[1]=3;
    
    
    state.putTile(3, 5, 0);
    state.debug();
    
    
    Depth1AI ai = new Depth1AI();
    ai.maxTilesNextPlay(state, state.oppId, false);
    
    assertThat(ai.oppMoveCount).isEqualTo(4);
    
  }
  
}
