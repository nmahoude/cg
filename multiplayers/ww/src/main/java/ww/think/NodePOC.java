package ww.think;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ww.AgentEvaluator2;
import ww.Dir;
import ww.GameState;
import ww.sim.Move;

public class NodePOC {
  long transposition[] = new long[5];

  static GameState state;
  
  int depth = 0;
  public Move move;
  public double score = 0.0;
  
  public NodePOC(int depth) {
    this.depth = depth;
  }
  
  public List<NodePOC> getChildren() {
    List<NodePOC> nodes = new ArrayList<>();
    if ((depth % 2) == 0) {
      if (depth == 0) {
        //System.err.println("Depth is 0, sending cache with "+state.legalActionDepth0NodeCache.size()+" moves");
        return state.legalActionDepth0NodeCache;
      }
      // my moves
      for (int i=0;i<2;i++) {
        for (Dir dir1 : Dir.getValues()) {
          if (!state.agents[i].cell.get(dir1).isValid()) continue; // rearly cutoff
          for (Dir dir2 : Dir.getValues()) {
            Move move = new Move(state.agents[i]);
            move.dir1 = dir1;
            move.dir2 = dir2;

            NodePOC node = new NodePOC(depth+1);
            node.copyTransposition(this); // will be updated by simulation
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
            
            NodePOC node = new NodePOC(depth+1);
            node.copyTransposition(this); // will be updated by simulation
            node.move = move;
            nodes.add(node);    
          }
        }
      }
      if (nodes.isEmpty()) {
        // add fake node (no move)
        NodePOC e = new NodePOC(depth+1);
        e.fakeTransposition();
        nodes.add(e);
      }
      return nodes;
    }
  }

  private void fakeTransposition() {
    Node.testedNodes++;
    // unique number !
    transposition[0] = Node.testedNodes;
    transposition[1] = Node.testedNodes;
    transposition[2] = Node.testedNodes;
    transposition[3] = Node.testedNodes;
    transposition[4] = Node.testedNodes;
  }

  private void copyTransposition(NodePOC node) {
    transposition[0] = node.transposition[0];
    transposition[1] = node.transposition[1];
    transposition[2] = node.transposition[2];
    transposition[3] = 0L; // position of us
    transposition[4] = 0L; // position of them
  }

  public double evaluate(int dontsee0, int dontsee1) {
    return AgentEvaluator2.score(state); // + 18.0 * (dontsee0 + dontsee1);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(transposition);
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
    if (!Arrays.equals(transposition, other.transposition))
      return false;
    return true;
  }
  

}
