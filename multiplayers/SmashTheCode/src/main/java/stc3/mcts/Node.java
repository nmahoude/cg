package stc3.mcts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import stc2.BitBoard;
import stc2.Simulation;

public class Node {
  private static Simulation simulator = new Simulation();
  private static ThreadLocalRandom random = ThreadLocalRandom.current();
  
  Node parent;
  public BitBoard board;
  public double constructionScore = 1.0;
  
  public double totalChildScore = 1.0;
  public int simCount;
  
  public int column;
  public int rotation;
  public List<Node> unvisited = null;
  public List<Node> visited = null;

  private int color1; // TODO don't save colors, find a good design to get them back from playerinfo or game
  private int color2;

  public Node(int color1, int color2, int column, int rotation) {
    this.color1 = color1;
    this.color2 = color2;
    this.column = column;
    this.rotation = rotation;
    board = new BitBoard();
  }

  public void expand(int color1, int color2) {
    unvisited = new ArrayList<>();
    visited = new ArrayList<>();
    
    for (int rotation=0;rotation<4;rotation++) {
      for (int column=0;column<6;column++) {
        if (board.canPutBalls(rotation, column)) {
          Node newChild = new Node(color1, color2, column, rotation);
          newChild.parent = this;
          unvisited.add(newChild);
        }
      }
    }
  }


  public Node findBestChild() {
    Node bestNode = null;
    double score = Double.NEGATIVE_INFINITY;
    for (Node node : visited) {
      if (node.constructionScore > score) {
        score = node.constructionScore;
        bestNode = node;
      }
    }
    return bestNode;
  }


  public void simulate(ScoreHeuristics scoreHeuristic) {
    this.board.copyFrom(parent.board);
    simulator.putBallsNoCheck(board, color1, color2, rotation, column);
    constructionScore = 
         scoreHeuristic.getConstructionScore(simulator, this)
        +scoreHeuristic.getPoints(simulator, this);
  }


  public void backpropagate() {
    if (parent == null) {
      return;
    }
    parent.simCount++;
    parent.totalChildScore +=this.constructionScore;
    parent.backpropagate();
  }

  public Node visitRandomChild() {
    Node node = unvisited.remove(random.nextInt(unvisited.size()));
    visited.add(node);
    return node;
  }

  public Node randomVisitedChild() {
    return visited.get(random.nextInt(visited.size()));
  }
}
