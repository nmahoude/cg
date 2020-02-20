package ww.paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import ww.GameState;
import ww.TU;

public class VoronoiTest {
  GameState state;

  @Before
  public void setup() {
    state = new GameState();
  }
  
  @Test
  public void voronoi_empty() {
    TU.setHeights(state, 7, 
        "0000000",
        "0000000",
        "0000000",
        "0000000",
        "0000000",
        "0000000",
        "0000000"
        );
    TU.setAgent(state, 0, 0, 0);
    TU.setAgent(state, 1, 0, 6);
    TU.setAgent(state, 2, 6, 0);
    TU.setAgent(state, 3, 6, 6);
    
    Voronoi acc = new Voronoi();
    int count[] = acc.voronoi2(state, state.agents[0], state.agents[2]);
    
    assertThat(count[0], is(33));
    assertThat(count[1], is(14));
  }
  
  @Test
  public void voronoi_blockedTooLow() {
    TU.setHeights(state, 7, 
        "0000020",
        "0000022",
        "0000000",
        "0000000",
        "0000000",
        "0000000",
        "0000000"
        );
    TU.setAgent(state, 0, 0, 0);
    TU.setAgent(state, 1, 0, 6);
    TU.setAgent(state, 2, 6, 0);
    TU.setAgent(state, 3, 6, 6);
    
    Voronoi acc = new Voronoi();
    int count[] = acc.voronoi2(state, state.agents[0], state.agents[2]);
    
    assertThat(count[0], is(44));
    assertThat(count[1], is(0));
  }

  @Test
  public void voronoi_blockedImpossible() {
    TU.setHeights(state, 7, 
        "0000043",
        "0000044",
        "0000000",
        "0000000",
        "0000000",
        "0000000",
        "0000000"
        );
    TU.setAgent(state, 0, 0, 0);
    TU.setAgent(state, 1, 0, 6);
    TU.setAgent(state, 2, 6, 0);
    TU.setAgent(state, 3, 6, 6);
    
    Voronoi acc = new Voronoi();
    int count[] = acc.voronoi2(state, state.agents[0], state.agents[2]);
    
    assertThat(count[0], is(44));
    assertThat(count[1], is(0));
  }
  
  @Test
  public void voronoi_longWayForNearAgent() {
    TU.setHeights(state, 7, 
        "0400000",
        "0404444",
        "0004444",
        "4444444",
        "0000000",
        "0000000",
        "0000000"
        );
    TU.setAgent(state, 0, 0, 0);
    TU.setAgent(state, 1, 0, 6);
    TU.setAgent(state, 2, 6, 0);
    TU.setAgent(state, 3, 6, 6);
    
    Voronoi acc = new Voronoi();
    int count[] = acc.voronoi2(state, state.agents[0], state.agents[2]);
    
    assertThat(count[0], is(6));
    assertThat(count[1], is(3));
  }
  
  @Test
  public void voronoi_4agents() {
    TU.setHeights(state, 7, 
        "0000000",
        "0000000",
        "0000000",
        "0000000",
        "0000000",
        "0000000",
        "0000000"
        );
    TU.setAgent(state, 0, 0, 0);
    TU.setAgent(state, 1, 0, 6);
    TU.setAgent(state, 2, 6, 0);
    TU.setAgent(state, 3, 6, 6);
    
    Voronoi acc = new Voronoi();
    int count[] = acc.voronoi4(state);
    
    assertThat(count[0], is(15));
    assertThat(count[1], is(11));
    assertThat(count[2], is(11));
    assertThat(count[3], is(8));
  }
}
