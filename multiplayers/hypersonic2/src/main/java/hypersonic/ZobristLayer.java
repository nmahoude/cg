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
