package hypersonic.ai.bfs;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ai.Score;
import hypersonic.simulation.MoveGenerator;
import hypersonic.simulation.Simulation;

public class BFSNode {
  private static Simulation sim = new Simulation(null);
  private static MoveGenerator gen = new MoveGenerator(null);
  private static State tmpState = new State();
  
  public int movesFE;
  private Move moves[] = new Move[10];
  public State state = new State();
  int firstChildIndex;
  BFSNode parent;
  private int depth;
  Move moveToHere;
  double score;
  double futureScore;
  
  public void generateChildren(boolean dropEnnemyBombs) {
    gen.state = this.state;

    movesFE = gen.getPossibleMoves(moves);
    if (!BFSCache.canReserve(movesFE)) {
      movesFE = -1;
      return;
    }
    firstChildIndex = BFSCache.reserve(movesFE);
    for (int i=0;i<movesFE;i++) {
      BFSNode node = BFSCache.nodes[firstChildIndex+i];
      node.parent = this;
      node.depth = this.depth+1;
      node.state.copyFrom(this.state);
      node.moveToHere = moves[i];
      if (depth == 0 && dropEnnemyBombs) {
        Simulation.dropEnnemyBombs(node.state);
      }
      sim.state = node.state;
      sim.simulate(node.moveToHere);
      node.score = this.score + Score.score(node.state, depth, moves[i]);
      
      // simulate after 10 turns (bomb impact)
      tmpState.copyFrom(node.state);
      sim.state = tmpState;
      tmpState.turn+=10;
      sim.simulate(Move.STAY);
      tmpState.players[Player.myId].isDead =false;
      node.score = node.score + Score.score(tmpState, tmpState.turn, Move.STAY);
    }
  }
  
  private int removeChildNode(int i) {
    moves[i] = moves[movesFE-1];
    movesFE--;
    return i-1;
  }
}
