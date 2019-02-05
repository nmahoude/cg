package hypersonic.ai.search;

import java.util.Arrays;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ai.Score;
import hypersonic.entities.Bomb;
import hypersonic.simulation.MoveGenerator;
import hypersonic.simulation.Simulation;
import hypersonic.utils.P;

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
//    SNode best = SNodeCache.nodes[firstChildIndex];
//    double bestScore = Double.NEGATIVE_INFINITY;
//    for (int i=0;i<movesFE;i++) {
//      SNode node = SNodeCache.nodes[firstChildIndex + i];
//      if (node.state.players[Player.myId].isDead) continue;
//      double score = 1.0 * this.visits / node.visits;
//      if (score > bestScore) {
//        bestScore = score;
//        best = node;
//      }
//    }
    // random for now
//    return best;
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
        Simulation.dropEnnemyBombs(node.state);
      }
      sim.state = node.state;
      sim.simulate(moves[i]);
      Search.generatedNodes ++;
      if (node.state.players[Player.myId].isDead) {
        node.score = Score.DEAD_MALUS;
        i = removeChildNode(i);
      } else {
        if (node.state.hash != -1) {
          if (!Search.zobrists[depth].setState(node.state)) {
            // already a state with same ... state
            i = removeChildNode(i);
            Search.collisions++;
            continue;
          }
        }
        node.score = Score.score(node.state, depth, moves[i]);
        node.bestScore = node.score + node.rollout(depth+1);
        node.visits = 1;
      }
    }
  }

  private int removeChildNode(int i) {
    moves[i] = moves[movesFE-1];
    movesFE--;
    return i-1;
  }

  @Override
  public String toString() {
    return ""+moveToHere+" d:"+state.players[Player.myId].isDead;
  }

  public double chooseFlat(int depth, boolean dropEnnemyBombs) {
    SNode current = this;
    double accScore = 0;

    while (depth < Search.MAX_BRUTE_DEPTH) {
      if (current.movesFE == -1) {
        current.generateChildren(depth, dropEnnemyBombs);
      }
      if (current.movesFE == 0) {
        return Score.DEAD_MALUS;
      }
      current = SNodeCache.nodes[current.firstChildIndex + Player.rand.nextInt(current.movesFE)];
      Search.allMoves[depth] = current.moveToHere;
      accScore += current.score;
      depth++;
    }
    accScore+= current.rollout(depth);
    bestScore = Math.max(bestScore, accScore);
    
    return accScore;
  }
  
  public double choose(int depth, boolean dropEnnemyBombs) {
    if (state.players[Player.myId].isDead) {
      return Score.DEAD_MALUS;
    }
    
    double accScore = 0;
    if (depth < Search.MAX_BRUTE_DEPTH) {
      if (movesFE == -1) {
        generateChildren(depth, dropEnnemyBombs);
      }
      if (movesFE == 0) {
        return Score.DEAD_MALUS;
      }
      SNode child = SNodeCache.nodes[firstChildIndex + Player.rand.nextInt(movesFE)];
      Search.allMoves[depth] = child.moveToHere;
      accScore = this.score + child.choose(depth+1, false);
      bestScore = Math.max(bestScore, accScore);
    } else {
      accScore = this.score + rollout(depth);
      bestScore = Math.max(bestScore, accScore);
    }

    return accScore;
  }

  /**
   * bump turn by 8 to see all bombs explode & look at score without player death account
   * @param depth
   * @return
   */
  private double fastRollout(int depth) {
    tmpState.copyFrom(state);
    sim.state = tmpState;
    gen.state = tmpState;

    double score = 0.0;
    tmpState.turn += 10;
    sim.simulate(Move.STAY);
    tmpState.players[Player.myId].isDead = false; // force so we are not dead
    score = Score.score(tmpState, depth, Move.STAY);
    for (;depth<Search.DEPTH;depth++) {
      Search.allMoves[depth] = Move.STAY;
    }
    Search.checkForSurvivableSituation = false;
    return score;
  }
  
  private double rollout(int depth) {
    tmpState.copyFrom(state);
    sim.state = tmpState;
    gen.state = tmpState;

    double score = 0.0;
    while (depth < Search.DEPTH) {
      allowedMovesFE = gen.getPossibleMoves(allowedMoves);
      Move move = allowedMoves[Player.rand.nextInt(allowedMovesFE)];
      Search.allMoves[depth] = move;

      sim.simulate(move);
      score += Score.score(tmpState, depth, move);
      if (tmpState.players[Player.myId].isDead) {
        return Score.DEAD_MALUS;
      }
      depth++;
    }
    
    // score += finishMove(depth);
    
    // end of rollout, if we are still alive, say it !
    Search.checkForSurvivableSituation = false;
    return score;
  }

  private double finishMove(int depth) {
    // last depth we go forward in future to know bombs effect
    tmpState.turn += 10;
    sim.simulate(Move.STAY);
    tmpState.players[Player.myId].isDead = false; // force so we are not dead
    return Score.score(tmpState, depth, Move.STAY);
  }  
  
  
  public void debug() {
    System.err.println("Root ->Best score : " + this.bestScore);
    for (int i=0;i<movesFE;i++) {
      SNode node = SNodeCache.nodes[firstChildIndex + i];
      double score = node.bestScore + Math.sqrt(2.0 * Math.log(this.visits) / node.visits); //- 1.0 * node.visits / this.visits;
      System.err.println("   Node "+node.moveToHere+" "+node.bestScore);
    }
  }

  /**
   * A kind of directed rollout
   */
  public double recalculate(Move[] nextTurnMoves) {
    if (Player.DEBUG_LASTBEST) {
      System.err.println("Recalculate last best path : "+Arrays.toString(nextTurnMoves));
    }
    if (nextTurnMoves[0].dropBomb) {
      if (Player.DEBUG_LASTBEST) {
        System.err.println("Desactive last best path due to bombing");
      }
      return Double.NEGATIVE_INFINITY;
    }
    int depth = 0;
    tmpState.copyFrom(state);
    sim.state = tmpState;
    gen.state = tmpState;

    double score = 0.0;
    while (depth < Search.DEPTH) {
      Move move = nextTurnMoves[depth];
      if (move == null) {
        return Score.DEAD_MALUS;
      }

      P newPos = tmpState.players[Player.myId].position.move(move);
      if (move != Move.STAY && move != Move.STAY_BOMB && !tmpState.canWalkOn(newPos)) {
        System.err.println("Path is not valid anymore, aborting");
        return Score.DEAD_MALUS;
      }
      if (depth == 0) {
        Simulation.dropEnnemyBombs(tmpState);
      }
      sim.simulate(move);
      score += Score.score(tmpState, depth, move);
      if (tmpState.players[Player.myId].isDead) {
        return Score.DEAD_MALUS;
      }
      depth++;
    }
    // end of rollout, if we are still alive, say it !
    for(int i=0;i<Search.DEPTH;i++) {
      Search.bestMoves[i] = nextTurnMoves[i];
    }
    return score;
  }
}
