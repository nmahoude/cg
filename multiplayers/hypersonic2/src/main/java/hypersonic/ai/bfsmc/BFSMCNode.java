package hypersonic.ai.bfsmc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ai.Score;
import hypersonic.entities.Bomb;
import hypersonic.simulation.MoveGenerator;
import hypersonic.simulation.Simulation;

public class BFSMCNode {
  private static State tmpState = new State(); // for rollout
  private static Simulation sim = new Simulation(null);
  private static MoveGenerator gen = new MoveGenerator(null);
  private static Move allowedMoves[] = new Move[10];
  private static int allowedMovesFE;
  
  public int movesFE;
  Move moves[] = new Move[10]; // TODO use a cache of possible moves ? 2^10 = 1024 only ?
  public int firstChildIndex;

  public State state = new State();
  public int depth;
  public BFSMCNode parent;
  public Move moveToHere;
  public double score;
  private int visits;
  
  public BFSMCNode navigate(Move move) {
    for (int i=0;i<movesFE;i++) {
      if (moves[i] == move) {
        return BFSMCNodeCache.nodes[firstChildIndex + i];
      }
    }
    return null;
  }
  
  public List<BFSMCNode> ns(Move[] allMoves) {
    List<BFSMCNode> result = new ArrayList<>();
    BFSMCNode current = this;
    int depth = 0;
    while (current != null) {
      if (allMoves[depth] == null) return result;
      result.add(current);
      current = current.navigate(allMoves[depth++]);
    }
    return result;
  }
  
  public boolean generateChildren(boolean dropEnnemyBombs) {
    if (depth > 7) {
      BFSMC.checkForSurvivableSituation = false;
    }
    gen.state = this.state;
    if (depth <= Player.DEPTH - Bomb.DEFAULT_TIMER - 1 ) {
      movesFE = gen.getPossibleMoves(moves);
    } else {
      movesFE = gen.getPossibleMovesWithoutBombs(moves);
    }
    if (!BFSMCNodeCache.canReserve(movesFE)) {
      return false;
    }
    firstChildIndex = BFSMCNodeCache.reserve(movesFE);
    for (int i=0;i<movesFE;i++) {
      BFSMCNode node = BFSMCNodeCache.nodes[firstChildIndex+i];
      node.parent = this;
      node.depth = this.depth+1;
      node.state.copyFrom(this.state);
      node.moveToHere = moves[i];
      if (depth == 0 && dropEnnemyBombs) {
        Simulation.dropEnnemyBombs(node.state);
      }
      sim.state = node.state;
      sim.simulate(node.moveToHere);
      BFSMC.generatedNodes ++;
      if (node.state.players[Player.myId].isDead) {
        node.score = Score.DEAD_MALUS;
        i = removeChildNode(i);
      } else {
        if (node.state.hash != -1) {
          if (!BFSMC.zobrists[depth].setState(node.state)) {
            // already a state with same ... state
            i = removeChildNode(i);
            BFSMC.collisions++;
            continue;
          }
        }
        node.score = this.score + Score.score(node.state, depth, moves[i]);
        node.visits = 1;
      }
    }
    if (movesFE == 0) {
      // we don't have any valid children, so we are a dead node 
      this.score = Score.DEAD_MALUS;
      this.state.players[Player.myId].isDead = true;
    }
    return true;
  }
  
  private int removeChildNode(int i) {
    moves[i] = moves[movesFE-1];
    movesFE--;
    return i-1;
  }

  
  public double rollout(Move allMoves[], int maxDepth) {
    tmpState.copyFrom(state);
    sim.state = tmpState;
    gen.state = tmpState;
    
    double score = 0.0;

    BFSMCNode current = this;
    int depth = 0;
    
    while (current.movesFE > 0) {
      int rand = Player.rand.nextInt(current.movesFE);
      if (depth < maxDepth) {
        allMoves[depth] = current.moves[rand];
      }
      current = BFSMCNodeCache.nodes[current.firstChildIndex + rand];
      current.visits++;
      score = current.score; // only = as the node keeps the sum of all preceding parents
      depth++;
    }
    if (current.state.players[Player.myId].isDead == true) {
      return Score.DEAD_MALUS;
    }
    tmpState.copyFrom(current.state);
    // ok end of precalculated steps, need to actually rollout
    while (depth < maxDepth) {
      allowedMovesFE = gen.getPossibleMoves(allowedMoves);
      Move move = allowedMoves[Player.rand.nextInt(allowedMovesFE)];
      allMoves[depth] = move;

      sim.simulate(move);
      score += Score.score(tmpState, depth, move);
      if (tmpState.players[Player.myId].isDead) {
        return Score.DEAD_MALUS;
      }
      depth++;
    }
    
    BFSMC.checkForSurvivableSituation = false;
    return score;    
  }
  
  public double rolloutMoves(Move allMoves[], int maxDepth) {
    tmpState.copyFrom(state);
    sim.state = tmpState;
    gen.state = tmpState;
    
    double score = 0.0;
    int depth = 0;

    BFSMCNode current = this;
    while (current.movesFE > 0) {
      Move move = allMoves[depth];
      if (move == null) {
        return Score.DEAD_MALUS;
      }
      BFSMCNode next = null;
      for (int i=0;i<current.movesFE;i++) {
        if (current.moves[i] == move) {
          next = BFSMCNodeCache.nodes[current.firstChildIndex + i];
          break;
        }
      }
      current = next;
      current.visits++;
      score = current.score; // only = as the node keeps the sum of all preceding parents
      depth++;
    }
    tmpState.copyFrom(current.state);
  
    while (depth < maxDepth) {
      Move move = allMoves[depth];
      if (move == null) break;
      sim.simulate(move);
      score += Score.score(tmpState, depth, move);
      if (tmpState.players[Player.myId].isDead) {
        return Score.DEAD_MALUS;
      }
      depth++;
    }
    
    BFSMC.checkForSurvivableSituation = false;
    return score;    
  }
  
  @Override
  public String toString() {
    List<Move> moves = new ArrayList<>();
    BFSMCNode current = this;
    while (current.parent != null) {
      moves.add(0,current.moveToHere);
      current = current.parent;
    }
    return "s:"+score+" m: "+Arrays.asList(moves);
  }
  
}
