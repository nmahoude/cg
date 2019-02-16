package hypersonic.ai.bfs;

import hypersonic.Board;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.utils.P;

public class BFSZobristLayer<T> {
  private static final int SIZE = Board.WIDTH * Board.HEIGHT;
  T nodes[];
  
  public BFSZobristLayer(T nodes[]) {
    this.nodes = nodes;
  }
  public void reset() {
    for (int i=0;i<SIZE;i++) {
      nodes[i] = null;
    }
  }
  
  public T getNode(P p) {
    return nodes[p.offset];
  }
  
  public void setNode(T s, P p) {
    nodes[p.offset] = s;
  }

  public T getNode(State state) {
    P p = state.players[Player.myId].position;
    return nodes[p.offset];
  }
}

