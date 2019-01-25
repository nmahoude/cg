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
  private static SNode tmpNode = new SNode(); // for mc
  private static Simulation sim = new Simulation(null);
  private static MoveGenerator gen = new MoveGenerator(null);
  private static Move allowedMoves[] = new Move[10];
  private static int allowedMovesFE;

  SNode parent;
  Move moveToHere = Move.STAY;
  SNode children[] = new SNode[10];
  Move moves[] = new Move[10];
  int movesFE = 0;
  
  public State state = new State();
  double score;
  
  public void reset() {
    state.clean();
    score = Double.NEGATIVE_INFINITY;
    movesFE = -1;
    moveToHere = null;
  }

  public SNode chooseChild(int depth, boolean dropEnnemyBombs) {
    if (movesFE == -1) {
      generateChildren(depth, dropEnnemyBombs);
    }
    return children[Player.rand.nextInt(movesFE)];
  }

  
  
  private void generateChildren(int depth, boolean dropEnnemyBombs) {
    gen.state = this.state;
    if (depth <= Search.DEPTH - Bomb.DEFAULT_TIMER - 1 ) {
      movesFE = gen.getPossibleMoves(moves);
    } else {
      movesFE = gen.getPossibleMovesWithoutBombs(moves);
    }
    for (int i=0;i<movesFE;i++) {
      SNode node = SNodeCache.pop();
      node.parent = this;
      node.state.copyFrom(this.state);
      node.moveToHere = moves[i];
      if (depth == 0 && dropEnnemyBombs) {
        dropEnnemyBombs(node.state);
      }
      sim.state = node.state;
      sim.simulate(moves[i]);
      node.score = Score.score(node.state, depth, moves[i]);
      children[i] = node;
    }
  }

  public SNode expand1stMC(int depth, boolean dropEnnemyBombs) {
    tmpNode.state.copyFrom(state);
    expandMC(depth, dropEnnemyBombs);
    return tmpNode;
  }
  
  public  void expandMC(int depth, boolean dropEnnemyBombs) {
    sim.state = tmpNode.state;
    gen.state = tmpNode.state;
    
    if (depth <= Search.DEPTH - Bomb.DEFAULT_TIMER - 1 ) {
      allowedMovesFE = gen.getPossibleMoves(allowedMoves);
    } else {
      allowedMovesFE = gen.getPossibleMovesWithoutBombs(allowedMoves);
    }
    tmpNode.moveToHere = allowedMoves[Player.rand.nextInt(allowedMovesFE)];
    if (depth == 0 && dropEnnemyBombs) {
      dropEnnemyBombs(tmpNode.state);
    }
    sim.simulate(tmpNode.moveToHere);
    
    tmpNode.score = Score.score(tmpNode.state, depth, tmpNode.moveToHere);
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

}
