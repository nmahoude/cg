package utils;
import java.util.concurrent.ThreadLocalRandom;

import stc2.Game;
import stc2.MCTSOld;

public class AG {
  ThreadLocalRandom random = ThreadLocalRandom.current();
  Game game = new Game();
  
  MCTSOld player1MCTS = new MCTSOld();
  MCTSOld player2MCTS = new MCTSOld();
  
  public void prepare() {
    game.myBoard.clear();
    game.otherBoard.clear();
    
    player1MCTS.game = game;
    player1MCTS.myBoard = game.myBoard;
    player1MCTS.otherBoard = game.otherBoard;

    player2MCTS.game = game;
    player2MCTS.myBoard = game.otherBoard;
    player2MCTS.otherBoard = game.myBoard;

    for (int i=0;i<8;i++) {
      getNewPair();
    }
  }
  
  public void doOneTurn() {
    player1MCTS.simulate(false);
    player2MCTS.simulate(false);
    
    updatePlayer(player1MCTS, player2MCTS);
    updatePlayer(player2MCTS, player1MCTS);
    
  }

  public void updatePlayer(MCTSOld player, MCTSOld otherPlayer) {
    int points = player.bestNode.simulation.points;
    if (points > 420) {
      int skulls = points / 420;
      for (int i=0;i<skulls;i++) {
        for (int c=0;c<6;c++) {
          if (otherPlayer.bestNode.board.getColHeight(c) < 12 ) {
            otherPlayer.bestNode.board.pushBall(0, c);
          }
        }
      }
    }
  }
  
  
  public void getNewPair() {
    colorShift();
    game.nextBalls[7] = random.nextInt(5)+1;
    game.nextBalls2[7] = random.nextInt(5)+1;
  }

  private void colorShift() {
    System.arraycopy(game.nextBalls, 1, game.nextBalls, 0, 7);
    System.arraycopy(game.nextBalls2, 1, game.nextBalls2, 0, 7);
  }
}
