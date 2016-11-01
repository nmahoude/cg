package hypersonic.montecarlo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
  
  public void simulate(int depth, int tries) {
    if (depth == 0) {
      return;
    }
    for (int i=0;i<tries;i++) {
      List<Move> moves = simulation.getPossibleMoves();
      int choice = ThreadLocalRandom.current().nextInt(moves.size());
      Move move = moves.get(choice);
    
      Node child = childs.get(move);
      if (child == null) {
        child = new Node();
        child.simulation.copyFrom(this.simulation);
        child.simulation.simulate(move);
        childs.put(move, child);
      }
      child.simulate(depth-1, tries);
    }
  }
}
