package stc2.mcts;

import java.util.ArrayList;
import java.util.List;

import stc2.BitBoard;
import stc2.Simulation;

public class Node {
  public static double COL_HEIGHT_1 = -1;
  public static double COL_HEIGHT_2 =  0;
  public static double COL_HEIGHT_3 = +1;
  public static double COL_HEIGHT_4 = +1;
  public static double COL_HEIGHT_5 =  0;
  public static double COL_HEIGHT_6 = -1;
  
  public static double GROUP_COUNT_1 = -40;
  public static double GROUP_COUNT_2 = 10;
  public static double GROUP_COUNT_3 = 40;

  public static double POINTS_BONUS = 1; 
  public static double COLOR_GROUP_BONUS = 1;
  public static double COLUMN_BONUS = 1;
  public static double SKULLS_BONUS = 1;

  static Simulation simulation = new Simulation();

  BitBoard board = new BitBoard();
  public List<Node> unvisitedChildren = null;
  public List<Node> children;

  public int column;
  public int rotation;
  public int depth;
  
  int points;
  double score;
  Node parent;

  double bestChildScore = Double.NEGATIVE_INFINITY;
  Node bestChild;
  
  public Node(BitBoard boardModel) {
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
    this.board.copyFrom(parent.board);
    simulation.board = this.board;
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

  public double getScore() {
      return POINTS_BONUS * simulation.points 
          + COLOR_GROUP_BONUS *getColorGroupScore()
          + COLUMN_BONUS * getColumnScore()
          + SKULLS_BONUS * getSkullsScore();
  }
  
  public int getSkullsScore() {
    return simulation.board.layers[BitBoard.SKULL_LAYER].bitCount();
  }


  public double getColorGroupScore() {
        return 
            GROUP_COUNT_1*simulation.groupsCount[1] 
            + GROUP_COUNT_2*simulation.groupsCount[2]
            + GROUP_COUNT_3*simulation.groupsCount[3];
  }


  public double getColumnScore() {
    return 
        COL_HEIGHT_1*simulation.board.getColHeight(0)
        +COL_HEIGHT_2*simulation.board.getColHeight(1)
        +COL_HEIGHT_3*simulation.board.getColHeight(2)
        +COL_HEIGHT_4*simulation.board.getColHeight(3)
        +COL_HEIGHT_5*simulation.board.getColHeight(4)
        +COL_HEIGHT_6*simulation.board.getColHeight(5);
  }

}
