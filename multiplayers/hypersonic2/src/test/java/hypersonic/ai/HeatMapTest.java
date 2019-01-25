package hypersonic.ai;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import hypersonic.Board;
import hypersonic.State;

public class HeatMapTest {

  @Test
  public void emptyBoard_allHeatAt0() throws Exception {
    State state = new State();
    
    HeatMap.calculate(state);
    
    for (int y=0;y<Board.HEIGHT;y++) {
      for (int x=0;x<Board.WIDTH;x++) {
        assertThat(score(x,y), is(0.0));
      }
    }
  }
  
  @Test
  public void oneBoxAt_0_0() throws Exception {
    State state = new State();
    state.board.cells[0+Board.WIDTH*0] = Board.BOX;
    
    HeatMap.calculate(state);
    
    assertThat(score(0,0), is(1.0));
    assertThat(score(12, 10), is(0.0));
    
  }
  
  @Test
  public void allBoxes() throws Exception {
    State state = new State();
    for (int y=0;y<Board.HEIGHT;y++) {
      for (int x=0;x<Board.WIDTH;x++) {
        state.board.cells[x+Board.WIDTH*y] = Board.BOX;
      }
    }
    
    HeatMap.calculate(state);
    
    assertThat(score(12,10), is(16.0));
    assertThat(score(0,0), is(16.0));
  }
  
  @Test
  public void allBoxesExcept_0_0() throws Exception {
    State state = new State();
    for (int y=0;y<Board.HEIGHT;y++) {
      for (int x=0;x<Board.WIDTH;x++) {
        if (x == 0 && y == 0) continue;
        state.board.cells[x+Board.WIDTH*y] = Board.BOX;
      }
    }
    
    HeatMap.calculate(state);
    
    assertThat(score(12,10), is(16.0));
    assertThat(score(0,0), is(15.0));
  }

  private double score(int x, int y) {
    return Math.round(HeatMap.score[x+Board.WIDTH*y]);
  }

}
