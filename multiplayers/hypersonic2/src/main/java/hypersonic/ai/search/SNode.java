package hypersonic.ai.search;

import hypersonic.Cache;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ai.Score;
import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.simulation.MoveGenerator;
import hypersonic.simulation.Simulation;

public class SNode {
  public static State tmpState = new State(); // for rollout
  private static Simulation sim = new Simulation(null);
  private static MoveGenerator gen = new MoveGenerator(null);
  private static Move allowedMoves[] = new Move[10];
  private static int allowedMovesFE;

  SNode parent;
  Move moveToHere = Move.STAY;
  int firstChildIndex = -1;
  Move moves[] = new Move[10]; // TODO use a cache of possible moves ? 2^10 = 1024 only ?
  int movesFE = 0;
  
  public State state = new State();
  double score;
  double bestScore = Double.NEGATIVE_INFINITY; // me + childs
  int visits = 0;
  
  private SNode chooseChild() {
    // random for now
    return SNodeCache.nodes[firstChildIndex + Player.rand.nextInt(movesFE)];
  }
  
  private void generateChildren(int depth, boolean dropEnnemyBombs) {
    gen.state = this.state;
    if (depth <= Search.DEPTH - Bomb.DEFAULT_TIMER - 1 ) {
      movesFE = gen.getPossibleMoves(moves);
    } else {
      movesFE = gen.getPossibleMovesWithoutBombs(moves);
    }
    firstChildIndex = SNodeCache.reserve(movesFE);
    for (int i=0;i<movesFE;i++) {
      SNode node = SNodeCache.nodes[firstChildIndex+i];
      node.parent = this;
      node.state.copyFrom(this.state);
      node.moveToHere = moves[i];
      if (depth == 0 && dropEnnemyBombs) {
        dropEnnemyBombs(node.state);
      }
      sim.state = node.state;
      sim.simulate(moves[i]);
      node.score = Score.score(node.state, depth, moves[i]);
      node.visits = 0;
    }
  }

  private static void dropEnnemyBombs(State state) {
    // for all players different than me and who can, drop a bomb at first one
    for (int i=0;i<4;i++) {
      if (i == Player.myId) continue;
      Bomberman b = state.players[i];
      if (b.isDead || b.bombsLeft == 0) continue;
      state.addBomb(Cache.popBomb(i, b.position, 8, b.currentRange));
    }
  }

  @Override
  public String toString() {
    return ""+moveToHere+" d:"+state.players[Player.myId].isDead;
  }

  public double choose(int depth, boolean dropEnnemyBombs) {
    if (state.players[Player.myId].isDead) {
      return score;
    }
    double accumulatedScore;
    if (depth < Search.MAX_BRUTE_DEPTH) {
      if (movesFE == -1) {
        generateChildren(depth, dropEnnemyBombs);
      }
      SNode child = chooseChild();
      Search.allMoves[depth] = child.moveToHere;
      accumulatedScore = this.score + child.choose(depth+1, false);
    } else {
      accumulatedScore = this.score + rollout(depth);
    }

    if (accumulatedScore > bestScore) {
      bestScore = accumulatedScore;
    }
    return accumulatedScore;
  }

  private double rollout(int depth) {
    tmpState.copyFrom(state);
    sim.state = tmpState;
    gen.state = tmpState;

    double score = 0.0;
    while (depth < Search.DEPTH) {
      if (depth <= Search.DEPTH - Bomb.DEFAULT_TIMER - 1 ) {
        allowedMovesFE = gen.getPossibleMoves(allowedMoves);
      } else {
        allowedMovesFE = gen.getPossibleMovesWithoutBombs(allowedMoves);
      }
      Move move = allowedMoves[Player.rand.nextInt(allowedMovesFE)];
      Search.allMoves[depth] = move;

      sim.simulate(move);
      score += Score.score(tmpState, depth, move);
      if (tmpState.players[Player.myId].isDead) {
        break;
      }
      depth++;
    }
    // end of rollout, if we are still alive, say it !
    Search.survivableSituation = Search.survivableSituation || !tmpState.players[Player.myId].isDead;
    return score;
  }  
}
