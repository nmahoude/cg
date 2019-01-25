package hypersonic.ai.sa;

import hypersonic.Cache;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ai.MC;
import hypersonic.ai.Score;
import hypersonic.entities.Bomberman;

public class SANode {
  private static Move[] allowedMoves = new Move[10];
  private static int movesFE;

  
  Move moves[] = new Move[PseudoSA.DEPTH];
  double scores[] = new double[PseudoSA.DEPTH];
  double score;
  
  public void copyFrom(SANode model) {
    for (int i=0;i<PseudoSA.DEPTH;i++) {
      this.moves[i] = model.moves[i];
      this.scores[i] = model.scores[i];
    }
  }
  
  void build(boolean dropBombs, State state, int from) {
    score = 0.0;
    
    for (int step=0;step<from;step++) {
      PseudoSA.simulator.simulate(moves[step]);
      score += scores[step];
      if (state.players[Player.myId].isDead) {
        score = -1_000_000 + step;
        return;
      }
    }
    
    for (int step=from;step<PseudoSA.DEPTH;step++) {
      movesFE = PseudoSA.gen.getPossibleMoves(allowedMoves);
      Move move = allowedMoves[Player.rand.nextInt(movesFE)];
      moves[step] = move;

      if (dropBombs && step == 0) {
        allPlayersExceptMeDropBombs(state);
      }
      
      PseudoSA.simulator.simulate(move);
      if (state.players[Player.myId].isDead) {
        scores[step] = -1_000_000 + step; // die the latest
        score = -1_000_000;
        return;
      } else {
        scores[step] = Score.score(state, step, move); 
        score += scores[step];
      }
    }
  }

  private void allPlayersExceptMeDropBombs(State state) {
    for (int i=0;i<4;i++) {
      if (i == Player.myId) continue;
      Bomberman b = state.players[i];
      if (b.isDead || b.bombsLeft == 0) continue;
      state.addBomb(Cache.popBomb(i, b.position, 8, b.currentRange));
    }
  }
}
