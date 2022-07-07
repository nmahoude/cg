package connect4;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class StateTest {

  @Test
  void emptyStateHasNoWinner() throws Exception {
    State state = State.emptyState();
    
    assertThat(state.winner).isEqualTo(-1);
  }
  
  @Test
  void winner0_WhenColumnOf4Consecutive() throws Exception {
    State state = State.emptyState();
    
    state.put(4, 0);
    state.put(4, 0);
    state.put(4, 0);
    state.put(4, 0);
    
    assertThat(state.winner).isEqualTo(0);
  }
  
  @Test
  void winner1_WhenColumnOf4Consecutive() throws Exception {
    State state = State.emptyState();
    
    state.put(6, 1);
    state.put(6, 1);
    state.put(6, 1);
    state.put(6, 1);
    
    assertThat(state.winner).isEqualTo(1);
  }
  
  @Test
  void noWinnerWhenNoConsecutiveColumn() throws Exception {
    State state = State.emptyState();
    
    state.put(6, 1);
    state.put(6, 1);
    state.put(6, 1);
    state.put(6, 0);
    state.put(6, 1);
    
    assertThat(state.winner).isEqualTo(-1);
  }
  
  @Test
  void winnerWhenRow() throws Exception {
    State state = State.emptyState();
    
    state.put(6, 0);
    state.put(5, 0);
    state.put(4, 0);
    state.put(7, 0);
    
    assertThat(state.winner).isEqualTo(0);
  }
  
  @Test
  void winnerWhenDiagonalUpRight() throws Exception {
    State state = State.emptyState();
    

    state.put(1, 1);
    state.put(1, 0);
    
    
    state.put(2, 1);
    state.put(2, 1);
    state.put(2, 0);

    state.put(3, 1);
    state.put(3, 1);
    state.put(3, 1);
    state.put(3, 0);
    
    state.put(0, 0);

    assertThat(state.winner).isEqualTo(0);
    
  }
  
  @Test
  void winnerWhenDiagonalDownLeft() throws Exception {
    State state = State.emptyState();
    
    state.put(0, 0);

    state.put(1, 1);
    state.put(1, 0);
    
    
    state.put(2, 1);
    state.put(2, 1);
    state.put(2, 0);

    state.put(3, 1);
    state.put(3, 1);
    state.put(3, 1);
    state.put(3, 0);
    
    assertThat(state.winner).isEqualTo(0);
    
  }

  @Test
  void winnerWhenDiagonalMiddleUpRight() throws Exception {
    State state = State.emptyState();
    
    state.put(0, 0);

    state.put(1, 1);
    state.put(1, 0);
    
    
    state.put(2, 1);
    state.put(2, 1);

    state.put(3, 1);
    state.put(3, 1);
    state.put(3, 1);
    state.put(3, 0);

    state.put(2, 0);

    assertThat(state.winner).isEqualTo(0);
  }

  @Test
  void winnerWhenDiagonalDownRight() throws Exception {
    State state = State.emptyState();
    
    state.put(0, 1);
    state.put(0, 1);
    state.put(0, 1);
    state.put(0, 0);

    state.put(1, 1);
    state.put(1, 1);
    state.put(1, 0);
    
    
    state.put(2, 1);
    state.put(2, 0);

    state.put(3, 0);
    
    assertThat(state.winner).isEqualTo(0);
    
  }

  @Test
  void winnerWhenDiagonalDownRightFromFarRight() throws Exception {
    State state = State.emptyState();
    
    state.put(8, 1);
    state.put(8, 1);
    state.put(8, 1);

    state.put(7, 1);
    state.put(7, 1);
    state.put(7, 0);
    
    
    state.put(6, 1);
    state.put(6, 0);

    state.put(5, 0);
    
    state.put(8, 0);
    assertThat(state.winner).isEqualTo(0);
    
  }
  
  @Test
  void debug() throws Exception {
    String input = "58 "
        + "OXOO.OOXX\r\n"
        + "XXOX.OXOO\r\n"
        + "XOXO.XOXX\r\n"
        + "XOOO.OOXO\r\n"
        + "OXXX.XXOX\r\n"
        + "XXOX.OOXO\r\n"
        + "XXXOOXOOX\r\n"
        
        + "0 0 ";
    FastReader in = new FastReader(input.getBytes());
    
    State state = State.emptyState();
    state.read(in);
    
    state.checkAndPut(4, true);
    state.checkAndPut(4, true);
    state.checkAndPut(4, true);
    state.checkAndPut(4, true);
    state.checkAndPut(4, true);
    state.debugColumns();
    state.checkAndPut(4, true);
    state.debugColumns();

    assertThat(state.winner).isEqualTo(2);
    
    
  }
  
}
