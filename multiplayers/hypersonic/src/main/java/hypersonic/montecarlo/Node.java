package hypersonic.montecarlo;

import java.util.HashMap;
import java.util.Map;

import hypersonic.Move;
import hypersonic.Simulation;

public class Node {
  // state
  Simulation simulation = new Simulation();
  
  Map<Move, Node> childs = new HashMap<>();
  
  public int getBestScore() {
    if (childs.isEmpty()) {
      return getScore();
    } else {
      int bestScore = Integer.MIN_VALUE;
      for (Node child : childs.values()) {
        int score = child.getBestScore();
        if (bestScore < score) {
          bestScore = score;
        }
      }
      return bestScore;
    }
  }

  int getScore() {
    return simulation.getScoreHeuristic();
  }
  
  public void simulate(int depth) {
    if (depth == 0) {
      return;
    }
    for (Move move : Move.values()) {
      if (!simulation.isMovePossible(move)) {
        continue;
      }
      Node child = childs.get(move);
      if (child == null) {
        child = new Node();
        child.simulation.copyFrom(this.simulation);
        child.simulation.simulate(move);
        childs.put(move, child);
      }
      child.simulate(depth-1);
    }
  }
}
