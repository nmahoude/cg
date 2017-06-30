package ww.think;

import java.util.ArrayList;
import java.util.List;

import ww.AgentEvaluator;
import ww.Dir;
import ww.GameState;
import ww.sim.Move;

public class NodePOC {
  long l1;
  long l2;
  long p1;
  long p2;
  
  static GameState state;
  
  int depth = 0;
  public Move move;
  public double score = 0.0;
  
  public NodePOC(int depth) {
    this.depth = depth;
  }
  
  public List<Node> getChildren() {
    List<Node> nodes = new ArrayList<>();
    if ((depth % 2) == 0) {
      if (depth == 0) {
        //System.err.println("Depth is 0, sending cache with "+state.legalActionDepth0NodeCache.size()+" moves");
        return state.legalActionDepth0NodeCache;
      }
      // my moves
      for (int i=0;i<2;i++) {
        for (Dir dir1 : Dir.getValues()) {
          for (Dir dir2 : Dir.getValues()) {
            Move move = new Move(state.agents[i]);
            move.dir1 = dir1;
            move.dir2 = dir2;
            
            Node node = new Node(depth+1);
            node.move = move;
            nodes.add(node);    
          }
        }
      }
      return nodes;
    } else {
      // his moves
      for (int i=2;i<4;i++) {
        if (state.agents[i].inFogOfWar()) continue;
        for (Dir dir1 : Dir.getValues()) {
          for (Dir dir2 : Dir.getValues()) {
            Move move = new Move(state.agents[i]);
            move.dir1 = dir1;
            move.dir2 = dir2;
            
            Node node = new Node(depth+1);
            node.move = move;
            nodes.add(node);    
          }
        }
      }
      if (nodes.isEmpty()) {
        // add fake node (no move)
        nodes.add(new Node(depth+1));
      }
      return nodes;
    }
  }

  public double evaluate() {
    return AgentEvaluator.score(state);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (l1 ^ (l1 >>> 32));
    result = prime * result + (int) (l2 ^ (l2 >>> 32));
    result = prime * result + (int) (p1 ^ (p1 >>> 32));
    result = prime * result + (int) (p2 ^ (p2 >>> 32));
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NodePOC other = (NodePOC) obj;
    if (l1 != other.l1)
      return false;
    if (l2 != other.l2)
      return false;
    if (p1 != other.p1)
      return false;
    if (p2 != other.p2)
      return false;
    return true;
  }
}
