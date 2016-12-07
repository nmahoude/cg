package stc3.mcts;

import java.util.concurrent.ThreadLocalRandom;

import stc3.game.Pair;
import stc3.game.PlayerInfo;

public class MCTS {
  private ThreadLocalRandom random = ThreadLocalRandom.current();
  private PlayerInfo playerInfo;
  private ScoreHeuristics scoreHeuristic = new ScoreHeuristics();
  public int maxDepth = 8;
  
  public Node run(PlayerInfo playerInfo, int run) {
    this.playerInfo = playerInfo;
    Node root = new Node(0, -1, -1, -1, -1);
    root.board.copyFrom(playerInfo.board);
    
    for (int i=0;i<run;i++) {
      selectAndExpand(root, 0);
    }
    return root;
  }

  private void selectAndExpand(Node parent, int depth) {
    if (depth == maxDepth || parent == null) {
      return;
    }
    if (parent.visited == null) {
      Pair pair = playerInfo.pairs[depth];
      parent.expand(pair.color1, pair.color2, scoreHeuristic);
    } else {
      Node child = parent.findBestChildForConstruction();
      selectAndExpand(child, depth+1);
    }
//    if (!parent.unvisited.isEmpty()) {
//      Node child = parent.visitRandomChild();
//      return child;
//    } else {
//      Node bestChild = parent.findBestChild();
//      if (bestChild == null) {
//        return null;
//      }
//      return select(bestChild, depth+1);
//    }
  }
}
