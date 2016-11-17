package stc2.mcts;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import stc2.BitBoard;
import stc2.Game;

public class MCTS {
  ThreadLocalRandom random = ThreadLocalRandom.current();
  Game game;
  
  MCTS() {
  }
  
  public void run(final BitBoard startingBoard, final int runs, final int maxDepth) {
    Node root = new Node(startingBoard);
    for (int i=runs;i>=0;i++) {
      Map.Entry<BitBoard, Node> expandedNode = select(root.board, root);
    }
  }

  private Map.Entry<BitBoard, Node> select(BitBoard board, Node root) {
    return treePolicy(board, root);
  }

  private Entry<BitBoard, Node> treePolicy(BitBoard board, Node node) {
    if (node.unvisitedChildren == null) {
      node.expand(board);
    }
    if (!node.unvisitedChildren.isEmpty()) {
      Node temp = node.unvisitedChildren.remove(random.nextInt(node.unvisitedChildren.size()));
      node.children.add(temp);
      temp.makeMove(game.nextBalls[node.depth], game.nextBalls2[node.depth]);
      return new AbstractMap.SimpleEntry<>(temp.board, temp);
    } else {
      Node temp = findBestNode(node);
      return treePolicy(temp.board, temp);
    }
  }

  private Node findBestNode(Node node) {
    return node.children.get(random.nextInt(node.children.size()));
  }
  
}
