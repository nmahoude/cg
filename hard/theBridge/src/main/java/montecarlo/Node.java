package montecarlo;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import theBridge.Move;
import theBridge.Simulation;

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
    if (simulation.aliveMoto() == 0) {
      return Integer.MIN_VALUE;
    }
    if (simulation.isFinished()) {
      return Integer.MAX_VALUE;
    }
    return simulation.aliveMoto()*(1+simulation.getMotosX());
  }
  
  public void simulate(int depth) {
    if (depth == 0 || simulation.isFinished()) {
      return;
    }
    for (Move move : Move.values()) {
      if (move == Move.Up && !simulation.canMoveUp()) {
        continue;
      }
      if (move == Move.Down && !simulation.canMoveDown()) {
        continue;
      }
      if (move == Move.Slow && !simulation.canSlow()) {
        continue;
      }
      if ((move == Move.Wait || move == Move.Jump) && simulation.getMotosSpeed() == 0) {
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

  private Move findPlausibleMoves() {
    if (simulation.getMotosSpeed() == 0) {
      return Move.Speed;
    } else {
      return Move.values()[ThreadLocalRandom.current().nextInt(Move.values().length)];
    }
  }
}
