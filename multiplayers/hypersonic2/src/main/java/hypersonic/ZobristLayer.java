<<<<<<< Updated upstream
package hypersonic;

import hypersonic.ai.search.SNode;
import hypersonic.utils.P;

public class ZobristLayer {
  private static final int SIZE = Board.WIDTH * Board.HEIGHT;
  SNode nodes[] = new SNode[SIZE];
  
  public void reset() {
    for (int i=0;i<SIZE;i++) {
      nodes[i] = null;
    }
  }
  
  public SNode getNode(P p) {
    return nodes[p.offset];
  }
  
  public void setNode(SNode s) {
    P position = s.state.players[Player.myId].position;
    nodes[position.offset] = s;
  }

  public SNode getNode(State state) {
    P p = state.players[Player.myId].position;
    return nodes[p.offset];
  }
}
=======
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
>>>>>>> Stashed changes
