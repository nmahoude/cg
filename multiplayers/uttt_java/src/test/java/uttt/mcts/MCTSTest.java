<<<<<<< Updated upstream
package uttt.mcts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uttt.Player;
import uttt.state.State2;

public class MCTSTest {

  private static final int DEBUG_TIME = 1_000_000;
  @Before
  public void setup() {
    NodeCache.init();
    NodeCache.resetCache();
  }
  @Test
  public void simple() throws Exception {
    MCTS mcts = new MCTS();
    
    Player.start = System.currentTimeMillis() + 1000;
    
    mcts.think();
    
    mcts.output();
  }

  @Test
  public void twoPlys() throws Exception {
    MCTS mcts = new MCTS();
    
    State2 state = mcts.getCurrentState();
    state.set(true, 0, 1);
    state.set(false, 2, 3);
    
    Player.start = System.currentTimeMillis();
    
    mcts.think();
    
    mcts.output();
  }

  @Test
  @Ignore
  public void debugSituation() throws Exception {
    MCTS mcts = new MCTS();
    
    State2 state = mcts.getCurrentState();
    // XOO
    // XXO
    // O
    setState(state, 0, "X");
    setState(state, 1, "O");
    setState(state, 2, "O");
    setState(state, 3, "X");
    setState(state, 4, "X");
    setState(state, 5, "O");
    setState(state, 6, "O");
    
    setState(state, 7, "XOX"
                     + "OOX"
                     + "X  ");
    
    setState(state, 8, "XOO"
                     + "XXO"
                     + "OO ");

    state.nextPlayGrid = 7;
    
    Player.start = System.currentTimeMillis(); //+DEBUG_TIME;
    
    mcts.think();
    
    mcts.output();
  }
  
  @Test
  public void findProvenLoss() throws Exception {
    MCTS mcts = new MCTS();
    
    State2 state = mcts.getCurrentState();
    setState(state, 0, "X");
    setState(state, 1, "O  O X   ");
    setState(state, 2, " XO   X O");
    setState(state, 3, "X X      ");
    setState(state, 4, "    X  XO");
    setState(state, 5, "O");
    setState(state, 6, "O X O    ");
    
    setState(state, 7, "O      OX");
    
    setState(state, 8, "O");

    state.nextPlayGrid = 4;
    
    Player.start = System.currentTimeMillis()+DEBUG_TIME;
    
    mcts.think();
    
    mcts.output();
  }
  
  @Test
  @Ignore
  public void findProvenWin() throws Exception {
    MCTS mcts = new MCTS();
    State2 state = mcts.getCurrentState();
    setState(state, 0, "X");
    setState(state, 1, "O");
    setState(state, 2, "X");
    setState(state, 3, "O");
    setState(state, 4, "X OOXXO  ");
    setState(state, 5, "O");
    setState(state, 6, "XO OOXX O");
    setState(state, 7, "O");
    setState(state, 8, "X");

    state.nextPlayGrid = 6;
    
    Player.start = System.currentTimeMillis();
    
    mcts.think();
    
    mcts.output();
  }
  
  @Test
  public void thisIsNotAWinForMe() throws Exception {
    State2 state = new State2();
    setState(state, 0, "X");
    setState(state, 1, "O");
    setState(state, 2, "X");
    setState(state, 3, "O");
    setState(state, 4, "X OOXXO  ");
    setState(state, 5, "O");
    setState(state, 6, "XO OOXX O");
    setState(state, 7, "O");
    setState(state, 8, "X");

/*
      ME : Node : (6,7) => 2147483647 / 18
        HIM: Node : (4,8) => -2147483648 / 7
          ME : Node : (4,7) => 2147483647 / 5
            HIM: Node : (4,1) => -2147483648 / 2
              ME : Node : (6,2) => 2147483647 / 0
 */
    state.set(true, 6, 1 << 7);
    state.set(false, 4, 1 << 8);
    state.set(true, 4, 1 << 7);
    state.set(false, 4, 1 << 1);
    state.set(true, 6, 1<< 2);
    
    assertThat(state.winner(), is(not(0)));
  }
  
  
  private void setState(State2 state, int gDecal, String board) {
    if (board.length() == 1) {
      if (board.charAt(0) == 'O') makeHimWin(state, gDecal);
      else if (board.charAt(0) == 'X') makeMeWin(state, gDecal);
      else throw new RuntimeException("unknown player "+ board.charAt(0));
      return;
    }
    for (int i=0;i<9;i++) {
      if (board.charAt(i) == 'X') {
        state.set(true, gDecal, 1 << i);
      } else if (board.charAt(i) == 'O') {
        state.set(false,  gDecal,  1 << i);
      }
    }
  }
  
  private void makeHimWin(State2 state, int gDecal) {
    state.set(false, gDecal,0b1);
    state.set(false, gDecal,0b10);
    state.set(false, gDecal,0b100);
  }
  private void makeMeWin(State2 state, int gDecal) {
    state.set(true, gDecal,0b1);
    state.set(true, gDecal,0b10);
    state.set(true, gDecal,0b100);
  }
}
=======
package uttt.mcts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uttt.Player;
import uttt.state.State2;

public class MCTSTest {

  private static final int DEBUG_TIME = 1_000_000;
  @Before
  public void setup() {
    NodeCache.init();
    NodeCache.resetCache();
  }
  @Test
  public void simple() throws Exception {
    MCTS mcts = new MCTS();
    
    Player.start = System.currentTimeMillis() + 1000;
    
    mcts.think();
    
    mcts.output();
  }

  @Test
  public void twoPlys() throws Exception {
    MCTS mcts = new MCTS();
    
    State2 state = mcts.getCurrentState();
    state.set(true, 0, 1);
    state.set(false, 2, 3);
    
    Player.start = System.currentTimeMillis();
    
    mcts.think();
    
    mcts.output();
  }

  @Test
  public void debugSituation() throws Exception {
    MCTS mcts = new MCTS();
    
    State2 state = mcts.getCurrentState();
    // XOO
    // XXO
    // O
    setState(state, 0, "X");
    setState(state, 1, "O");
    setState(state, 2, "O");
    setState(state, 3, "X");
    setState(state, 4, "X");
    setState(state, 5, "O");
    setState(state, 6, "O");
    
    setState(state, 7, "XOX"
                     + "OOX"
                     + "X  ");
    
    setState(state, 8, "XOO"
                     + "XXO"
                     + "OO ");

    state.nextPlayGrid = 7;
    
    Player.start = System.currentTimeMillis()+DEBUG_TIME;
    
    mcts.think();
    
    mcts.output();
  }
  
  @Test
  public void findProvenLoss() throws Exception {
    MCTS mcts = new MCTS();
    
    State2 state = mcts.getCurrentState();
    setState(state, 0, "X");
    setState(state, 1, "O  O X   ");
    setState(state, 2, " XO   X O");
    setState(state, 3, "X X      ");
    setState(state, 4, "    X  XO");
    setState(state, 5, "O");
    setState(state, 6, "O X O    ");
    
    setState(state, 7, "O      OX");
    
    setState(state, 8, "O");

    state.nextPlayGrid = 4;
    
    Player.start = System.currentTimeMillis()+DEBUG_TIME;
    
    mcts.think();
    
    mcts.output();
  }
  
  @Test
  public void findProvenWin() throws Exception {
    MCTS mcts = new MCTS();
    State2 state = mcts.getCurrentState();
    setState(state, 0, "X");
    setState(state, 1, "O");
    setState(state, 2, "X");
    setState(state, 3, "O");
    setState(state, 4, "X OOXXO  ");
    setState(state, 5, "O");
    setState(state, 6, "XO OOXX O");
    setState(state, 7, "O");
    setState(state, 8, "X");

    state.nextPlayGrid = 6;
    
    Player.start = System.currentTimeMillis();
    
    mcts.think();
    
    mcts.output();
  }
  
  @Test
  public void thisIsNotAWinForMe() throws Exception {
    State2 state = new State2();
    setState(state, 0, "X");
    setState(state, 1, "O");
    setState(state, 2, "X");
    setState(state, 3, "O");
    setState(state, 4, "X OOXXO  ");
    setState(state, 5, "O");
    setState(state, 6, "XO OOXX O");
    setState(state, 7, "O");
    setState(state, 8, "X");

/*
      ME : Node : (6,7) => 2147483647 / 18
        HIM: Node : (4,8) => -2147483648 / 7
          ME : Node : (4,7) => 2147483647 / 5
            HIM: Node : (4,1) => -2147483648 / 2
              ME : Node : (6,2) => 2147483647 / 0
 */
    state.set(true, 6, 1 << 7);
    state.set(false, 4, 1 << 8);
    state.set(true, 4, 1 << 7);
    state.set(false, 4, 1 << 1);
    state.set(true, 6, 1<< 2);
    
    assertThat(state.winner(), is(not(0)));
  }
  
  
  private void setState(State2 state, int gDecal, String board) {
    if (board.length() == 1) {
      if (board.charAt(0) == 'O') makeHimWin(state, gDecal);
      else if (board.charAt(0) == 'X') makeMeWin(state, gDecal);
      else throw new RuntimeException("unknown player "+ board.charAt(0));
      return;
    }
    for (int i=0;i<9;i++) {
      if (board.charAt(i) == 'X') {
        state.set(true, gDecal, 1 << i);
      } else if (board.charAt(i) == 'O') {
        state.set(false,  gDecal,  1 << i);
      }
    }
  }
  
  private void makeHimWin(State2 state, int gDecal) {
    state.set(false, gDecal,0b1);
    state.set(false, gDecal,0b10);
    state.set(false, gDecal,0b100);
  }
  private void makeMeWin(State2 state, int gDecal) {
    state.set(true, gDecal,0b1);
    state.set(true, gDecal,0b10);
    state.set(true, gDecal,0b100);
  }
}
>>>>>>> Stashed changes
