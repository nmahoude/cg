package ww.think;

import java.util.ArrayList;
import java.util.List;

import ww.AgentEvaluator;
import ww.Dir;
import ww.GameState;
import ww.sim.Move;

public class Node {
  static GameState state;
  int depth = 0;
  Move move;
  
  Node(int depth) {
    this.depth = depth;
  }
  
  public List<Node> getChildren() {
    List<Node> nodes = new ArrayList<>();
    if ((depth & 0b1) == 0) {
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
}
