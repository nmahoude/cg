package stc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class DFSNode {
  final static Deque<Board> boards = new ArrayDeque<>();
  
  static void add(int n) {
    for (int i=0;i<n;i++) {
      Board board = new Board();
      boards.offer(board);
    }
  }
  static {
    add(100_000);
  }
  final static Board getBoard() {
    Board b = boards.poll();
    if (b == null) {
      b = new Board();
    }
    return b;
  }
  final static void releaseBoard(Board b) {
    boards.offer(b);
  }
  
  
  Board board = getBoard();
  Map<Integer, DFSNode> childs = new HashMap<>();
  int points = 0;
  int score = 0;
  boolean isImpossible = false;
  
  
  double getBestScore() {
    int sumHeights=0;
    for (int x=6;--x>0;) {
      sumHeights+=board.heights[x];
    }
    
    if (childs.isEmpty()) {
      if (isImpossible) {
        return -100;
      } else {
        return points+72-sumHeights;
      }
    } else {
      double maxScore = -1;
      for ( Entry<Integer, DFSNode> childEntry : childs.entrySet()) {
        DFSNode child = childEntry.getValue();
        double score = child.getBestScore();
        if (score > maxScore) {
          maxScore = score;
        }
      }
      return Math.max(0.6*maxScore, 0);
    }
  }
  public final void simulate(Game game, int depth) {
    points = board.points;
    
    if (depth >= 8) {
      return;
    }
    int color1 = game.nextBalls[depth];
    int color2 = game.nextBalls2[depth];
    
    if (depth <=0) {
      // all cases
      int maxRotation = color1 == color2 ? 2 : 4;
      for (int rot = maxRotation;--rot>0;) {
        for (int x=0;x<6;x++) {
          if ((x== 0 && rot == 2) || (x==5 && rot==0)) {
            continue;
          }
          DFSNode child = simulateChild(game, depth, color1, color2, rot, x);
        }
      }
    } else {
      // only some cases
      int maxRotation = color1 == color2 ? 2 : 4;
      for (int i=22-6*depth;--i>0;) {
        int rot = ThreadLocalRandom.current().nextInt(maxRotation);
        int x = ThreadLocalRandom.current().nextInt(6);
        if ((x== 0 && rot == 2) || (x==5 && rot==0)) {
          continue;
        }
        DFSNode child = simulateChild(game, depth, color1, color2, rot, x);
      }
    }
    
  }
  private DFSNode simulateChild(Game game, int depth, int color1, int color2, int rot, int x) {
    DFSNode child = childs.get(x+6*rot);
    if (child == null) {
      child = new DFSNode();
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
    releaseBoard(board);
    for ( Entry<Integer, DFSNode> childEntry : childs.entrySet()) {
      childEntry.getValue().release();
    }    
  }
}
