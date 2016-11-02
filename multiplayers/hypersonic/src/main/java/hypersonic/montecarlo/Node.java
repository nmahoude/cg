package hypersonic.montecarlo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import hypersonic.Move;
import hypersonic.Simulation;

public class Node {
  // state
  int depth = 0;
  int count = 0;
  
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
      return getScore()+bestScore;
    }
  }

  int getScore() {
    return simulation.getScoreHeuristic();
  }
  
  public void simulate(int depth) {
    count++;
    this.depth = depth;
    if (depth == MonteCarlo.MAX_DEPTH) {
      return;
    }
    
    List<Move> moves = simulation.getPossibleMoves();
    int choice = ThreadLocalRandom.current().nextInt(moves.size());
    Move move = moves.get(choice);

//    System.err.println("depth = "+(remainingDepth));
//    System.err.println("me : "+simulation.board.me);
//    System.err.println("choosing move : "+move+ " from "+moves.toString());
    
    Node child = childs.get(move);
    if (child == null) {
      child = new Node();
      child.simulation.copyFrom(this.simulation);
      child.simulation.simulate(move);
      childs.put(move, child);
    }
    if (child.simulation.board.me.isDead) {
      return;
    }
    child.simulate(depth+1);
  }
}
