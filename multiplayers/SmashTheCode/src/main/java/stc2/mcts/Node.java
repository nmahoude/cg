package stc2.mcts;

import java.util.ArrayList;
import java.util.List;

import stc2.BitBoard;
import stc2.Simulation;

public class Node {
  static Simulation simulation = new Simulation();

  BitBoard board = new BitBoard();
  public List<Node> unvisitedChildren = null;
  public List<Node> children;

  private int column;
  private int rotation;
  public int depth;
  
  int points;
  double score;
  Node parent;

  double bestChildScore = Double.NEGATIVE_INFINITY;
  Node bestChild;
  
  public Node(BitBoard boardModel) {
    simulation.board = this.board;
    this.board.copyFrom(boardModel);
  }

  public Node() {
  }
  
  public void expand(BitBoard board) {
    unvisitedChildren = new ArrayList<>();
    children = new ArrayList<>();
    for (int rot=0;rot<4;rot++) {
      for (int column=0;column<6;column++) {
        if ((column == 0 && rot == 2) || (column == 5 && rot == 0)) { continue; }
        if (board.canPutBalls(rot, column)) {
          Node node = new Node();
          node.parent = this;
          node.column = column;
          node.rotation = rot;
          node.depth = depth+1;
          unvisitedChildren.add(node);
        }
      }
    }
  }

  public void makeMove(int color1, int color2) {
    simulation.board = board;
    simulation.clear();
    simulation.putBallsNoCheck(color1, color2, rotation, column);
    
    this.points = simulation.points;
    this.score = getScore();
    if (parent != null) {
      parent.backPropagate(this, score);
    }
  }

  public void backPropagate(Node node, double childNodeScore) {
    if (childNodeScore > bestChildScore) {
      bestChildScore = childNodeScore;
      bestChild = node;
      if (parent != null) {
        parent.backPropagate(this, this.score + bestChildScore);
      }
    }
  }

  private double getScore() {
    return 
        - 20*simulation.groupsCount[1] 
        + 10*simulation.groupsCount[2]
        + 40*simulation.groupsCount[3];
  }
}
