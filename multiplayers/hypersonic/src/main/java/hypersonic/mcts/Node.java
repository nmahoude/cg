package hypersonic.montecarlo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import hypersonic.Board;
import hypersonic.Move;
import hypersonic.Simulation;
import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.utils.Cache;

public class Node {
  public static Cache<Node> cache = new Cache<>();
  static {
    for (int i=0;i<10000;i++) {
      cache.push(new Node());
    }
  }
  
  // state
  int depth = 0;
  int count = 0;
  
  Simulation simulation = new Simulation();
  
  Map<Move, Node> childs = new HashMap<>();
  List<Move> moves;
  Move move;
  
  public double getBestScore() {
    if (childs.isEmpty()) {
      return getScore();
    } else {
      double bestScore = Integer.MIN_VALUE;
      for (final Node child : childs.values()) {
        final double score = child.getBestScore();
        if (bestScore < score) {
          bestScore = score;
        }
      }
      return getScore()+bestScore;
    }
  }

  double getScore() {
    return simulation.getScoreHeuristic();
  }
  
  public void simulate(final int depth) {
    count++;
    this.depth = depth;
    if (depth == MonteCarlo.MAX_DEPTH) {
      return;
    }
    
    final Move nextMove = findPossibleRandomMove();

//    System.err.println("depth = "+(remainingDepth));
//    System.err.println("me : "+simulation.board.me);
//    System.err.println("choosing move : "+move+ " from "+moves.toString());
    Node child = childs.get(nextMove);
    if (child == null) {
      child = createChild(nextMove);
    }
    if (child.simulation.board.me.isDead) {
      return;
    }
    child.simulate(depth+1);
  }

  private void simulateWorstCaseScenario(final Simulation simulation) {
    for (final Bomberman bomberman : simulation.board.players) {
      if (bomberman != simulation.board.me && bomberman.bombsLeft > 0 && bomberman.position.manhattanDistance(simulation.board.me.position) < 3) {
        simulation.board.addBomb(Bomb.create(simulation.board, bomberman.owner, bomberman.position, Bomb.DEFAULT_TIMER, bomberman.currentRange));
      }
    }
  }

  private Node createChild(final Move nextMove) {
    Node child;
    if (cache.isEmpty()) {
      child = new Node();
    } else {
      child = cache.pop();
    }
    child.move = nextMove;
    child.simulation.copyFrom(this.simulation);
    if (depth == 0) {
      simulateWorstCaseScenario(child.simulation);
    }
    child.simulation.simulate(nextMove);
    childs.put(nextMove, child);
    return child;
  }

  private Move findPossibleRandomMove() {
    if (moves == null) {
      moves = simulation.getPossibleMoves();
    }
    final int choice = ThreadLocalRandom.current().nextInt(moves.size());
    final Move nextMove = moves.get(choice);
    return nextMove;
  }

  public void retrocedRoot() {
    for (final Node child : childs.values()) {
      child.retroced();
    }
  }
  public void retroced() {
    if (simulation.board != null) {
      Board.retrocede(simulation.board);
    }
    for (final Node child : childs.values()) {
      child.retroced();
    }
    clear();
    cache.retrocede(this);
  }

  public void clear() {
    childs.clear();
    moves = null;
    move = null;
    count = 0;
  }

  public void getNodeList(final List<Node> nodes) {
    if (childs.isEmpty()) {
      return;
    } else {
      double bestScore = Integer.MIN_VALUE;
      Node bestNode = null;
      for (final Node child : childs.values()) {
        final double score = child.getBestScore();
        if (bestScore < score) {
          bestScore = score;
          bestNode = child;
        }
      }
      nodes.add(bestNode);
      bestNode.getNodeList(nodes);
    }
  }
}
