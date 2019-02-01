package hypersonic;

import hypersonic.utils.P;

public class ZobristLayer {
  private static final int SIZE = Board.WIDTH * Board.HEIGHT;
  State states[] = new State[SIZE];
  
  public void reset() {
    for (int i=0;i<SIZE;i++) {
      states[i] = null;
    }
  }
  
  public State getState(P p) {
    return states[p.offset];
  }
  
  public boolean setState(State s) {
    P position = s.players[Player.myId].position;
    if (states[position.offset] == null) {
      states[position.offset] = s;
      return true;
    } else {
      return false;
    }
  }
}
