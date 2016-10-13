package stc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class DFSNode {
  final static Deque<DFSNode> nodes = new ArrayDeque<>();
  
  static void add(int n) {
    for (int i=0;i<n;i++) {
      DFSNode node = new DFSNode();
      nodes.offer(node);
    }
  }
  final static DFSNode getNode() {
    DFSNode n = nodes.poll();
    if (n == null) {
      n = new DFSNode();
    }
    return n;
  }
  final static void releaseDFSNode(DFSNode n) {
    nodes.offer(n);
  }
  
  
  Board board = new Board();
  Map<Integer, DFSNode> childs = new HashMap<>();
  int points = 0;
  int score = 0;
  boolean isImpossible = false;
  private int depth;
  
  
  double getScore() {
    int maxHeight = board.getMaxHeights();
    if (isImpossible) {
      return -100;
    } else {
      return points+board.colorBlocksPoint-9*board.skullCount;
    }
  }
  double getBestScore() {
    if (childs.isEmpty()) {
      return getScore();
    } else {
      double maxScore = Integer.MIN_VALUE;
      for ( Entry<Integer, DFSNode> childEntry : childs.entrySet()) {
        DFSNode child = childEntry.getValue();
        double score = child.getBestScore();
        if (score > maxScore) {
          maxScore = score;
        }
      }
      return Math.max(0.6*maxScore, getScore());
    }
  }
  public final void simulate(Game game, int depth) {
    this.depth = depth;
    points = board.points;
    
    if (depth >= 8) {
      return;
    }
    int color1 = game.nextBalls[depth];
    int color2 = game.nextBalls2[depth];
    
    if (depth <=0) {
      // all cases
      int maxRotation = color1 == color2 ? 2 : 4;
      for (int rot = maxRotation;--rot>=0;) {
        for (int x=0;x<6;x++) {
          if (impossibleCases(rot, x)) {
            continue;
          }
          DFSNode child = simulateChild(game, depth, color1, color2, rot, x);
        }
      }
    } else {
      // only some cases
      int maxRotation = color1 == color2 ? 2 : 4;
      for (int i=(int)(22-2.2*depth*depth)-1;--i>=0;) {
        int rot = ThreadLocalRandom.current().nextInt(maxRotation);
        int x = ThreadLocalRandom.current().nextInt(6);
        if (impossibleCases(rot, x)) {
          continue;
        }
        DFSNode child = simulateChild(game, depth, color1, color2, rot, x);
      }
    }
    
  }
  private boolean impossibleCases(int rot, int x) {
    return (x == 0 && rot == 2) || (x == 5 && rot == 0);
  }
  private DFSNode simulateChild(Game game, int depth, int color1, int color2, int rot, int x) {
    DFSNode child = childs.get(x+6*rot);
    if (child == null) {
      child = getNode();
      childs.put(x+6*rot, child);
      board.copy(child.board);
      if (child.board.putBlocks(color1, color2, rot, x)) {
        child.simulate(game, depth+1);
      } else {
        child.isImpossible = true;
      }
    } else {
      if (child.isImpossible) {
        return child;
      } else {
        child.simulate(game, depth+1);
      }
    }
    return child;
  }
  public String debugCourse() {
    if (childs.isEmpty()) {
      return "";
    } else {
      String bestCommand = "";
      DFSNode bestChild = null;
      
      double maxScore = -1;
      for ( Entry<Integer, DFSNode> childEntry : childs.entrySet()) {
        DFSNode child = childEntry.getValue();
        double score = child.getBestScore();
        if (score > maxScore) {
          maxScore = score;
          bestCommand = ""+(childEntry.getKey() % 6)+" "+(childEntry.getKey()/6);
          bestChild = child;
        }
      }
      if (bestChild != null) {
        return bestCommand+"->"+bestChild.debugCourse();
      } else {
        return "NO";
      }
    }
  }
  public void release() {
    for ( Entry<Integer, DFSNode> childEntry : childs.entrySet()) {
      childEntry.getValue().release();
    }    
    childs.clear();
    releaseDFSNode(this);
  }
  public String debugCourse2() {
    this.board.debug();
    System.err.println("For "+board.points+" points");
    if (childs.isEmpty()) {
      return "";
    } else {
      String bestCommand = "";
      DFSNode bestChild = null;
      
      double maxScore = -1;
      for ( Entry<Integer, DFSNode> childEntry : childs.entrySet()) {
        DFSNode child = childEntry.getValue();
        double score = child.getBestScore();
        if (score > maxScore) {
          maxScore = score;
          bestCommand = ""+(childEntry.getKey() % 6)+" "+(childEntry.getKey()/6);
          bestChild = child;
        }
      }
      if (bestChild != null) {
        return bestCommand+"->"+bestChild.debugCourse2();
      } else {
        return "NO";
      }
    }
  }
}
