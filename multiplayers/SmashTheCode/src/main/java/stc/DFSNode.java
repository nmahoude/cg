package stc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class DFSNode {

  Board board = new Board();
  Map<Integer, DFSNode> childs = new HashMap<>();
  int points = 0;
  int score = 0;
  boolean isImpossible = false;
  
  double getBestScore() {
    if (childs.isEmpty()) {
      if (isImpossible) {
        return -100;
      } else {
        return score;
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
      return Math.max(0.8*maxScore, score);
    }
  }
  public final void simulate(Game game, int depth) {
    points = board.points;
    score = board.points;
    
    int sumHeights=0;
    for (int x=6;--x>0;) {
      sumHeights+=board.heights[x];
    }
    score = score + (72-sumHeights);
    if (depth >= 8) {
      return;
    }
    int color1 = game.nextBalls[depth];
    int color2 = game.nextBalls2[depth];
    
    if (depth <=0) {
      // all cases
      for (int rot = 0;rot<4;rot++) {
        for (int x=0;x<6;x++) {
          if ((x== 0 && rot == 2) || (x==5 && rot==0)) {
            continue;
          }
          DFSNode child = simulateChild(game, depth, color1, color2, rot, x);
        }
      }
    } else {
      // only some cases
      for (int i=4;--i>0;) {
        int rot = ThreadLocalRandom.current().nextInt(4);
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
      String command = "";
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
      return bestCommand+"->"+bestChild.debugCourse();
    }
  }
}
