package stc2.play;

import stc2.BitBoard;
import stc2.Game;
import stc2.MCNode;
import stc2.MCTS;
import stc2.MCTS.AjustementVariables;

public class AI {
  Game game;
  int player;
  MCTS mcts;
  AjustementVariables ajust;
  
  public MCNode getMove() {
    mcts = new MCTS();
    mcts.ajust = this.ajust;
    if (player == 2) {
      mcts.attachGame(game, game.otherBoard, game.myBoard);
    } else {
      mcts.attachGame(game, game.myBoard, game.otherBoard);
    }
    
    mcts.simulate(false);
    return mcts.bestNode;
  }

  public void prepare(Game game, int player) {
    this.game = game;
    this.player = player;
  }
  
}
