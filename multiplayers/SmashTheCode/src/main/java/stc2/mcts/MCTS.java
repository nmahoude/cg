package stc2.mcts;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import stc2.BitBoard;
import stc2.Game;

public class MCTS {
  ThreadLocalRandom random = ThreadLocalRandom.current();
  public Game game;
  public Node bestChild;
  
  public MCTS() {
  }
  
  public void run(final BitBoard startingBoard, final int runs, final int maxDepth) {
    Node root = new Node(startingBoard);
    root.board.copyFrom(startingBoard);
    for (int i=runs;i>=0;i--) {
      Map.Entry<BitBoard, Node> expandedNode = select(root.board, root);
    }
    bestChild = root.bestChild;
    root.children.remove(bestChild);
    root.release();
    if (bestChild != null) {
      bestChild.release();
    }
  }

  private Map.Entry<BitBoard, Node> select(BitBoard board, Node root) {
    return treePolicy(board, root);
  }

  private Entry<BitBoard, Node> treePolicy(BitBoard board, Node node) {
    if (node.depth == 8) {
      return new AbstractMap.SimpleEntry<>(node.board, node); 
    }
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
      if (temp != null) {
        return treePolicy(temp.board, temp);
      } else {
        return new AbstractMap.SimpleEntry<>(node.board, node); 
      }
    }
  }

  private Node findBestNode(Node node) {
    if (node.children.size() == 0) {
      return null;
    }
    return node.children.get(random.nextInt(node.children.size()));
  }
  
}
