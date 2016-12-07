package stc2.play;

import stc2.BitBoard;
import stc2.Game;
import stc2.mcts.MCTS;

public class NewAi implements IAI {
  AdjustementFactors factors = new AdjustementFactors();
  
  MCTS mcts = new MCTS();
  int player;
  private Game game;
  AdjustementFactors ajust = null;

  @Override
  public Move getMove() {
    factors.apply();
    
    BitBoard board;
    if (player == 2) {
      board = game.otherBoard;
    } else {
      board = game.myBoard;
    }
    mcts.run(board, 50_000, 8);
    if (mcts.bestChild == null) {
      return null;
    }
    return new Move(mcts.bestChild.rotation, mcts.bestChild.column);
  }

  @Override
  public void prepare(Game game, int player) {
    this.game = game;
    mcts.game = game;
    this.player = player;
  }

}
