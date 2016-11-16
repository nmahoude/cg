package stc2.play;

import java.util.Random;

import stc2.BitBoard;
import stc2.Game;
import stc2.MCNode;
import stc2.MCTS;

// a full game between 2 AIs
public class Round {
  Game game = new Game();
  AI player1 = new AI();
  AI player2 = new AI();
  boolean isFinished = false;
  private boolean player1Dead;
  private boolean player2Dead;
  private Random random = new Random();
  
  
  public void play() {
    prepareRound();
    
    while (!isFinished()) {
      doPlayer1Move();
      doPlayer2Move();
      getNewPair();

//      debugBoards();
    }
  }

  private void debugBoards() {
    String p1[] = game.myBoard.getDebugString().split("\n");
    String p2[] = game.otherBoard.getDebugString().split("\n");
    
    for (int i=0;i<12;i++) {
      System.out.println(p1[i]+"  "+p2[i]);
    }
  }

  private void prepareRound() {
    for (int i=0;i<8;i++) {
      getNewPair();
    }
    
    player1.prepare(game, 1);
    player2.prepare(game, 2);
    
  }

  private boolean isFinished() {
    return player1Dead || player2Dead;
  }

  private void doPlayer2Move() {
    MCNode move2 = player2.getMove();
    if (move2 == null) {
      player2Dead = true;
      return;
    }
    game.otherBoard = move2.board;
    if (move2.simulation.points > 0) {
      game.player2Score += move2.simulation.points;
      game.player2Skulls += move2.simulation.points/70;
      while (game.player2Skulls > 6) {
        game.player2Skulls -=6;
        dropSkullLine(game.myBoard);
      }
    }
  }

  private void doPlayer1Move() {
    MCNode move1 = player1.getMove();
    if (move1 == null) {
      player1Dead = true;
      return;
    }
    game.myBoard = move1.board;
    if (move1.simulation.points > 0) {
      game.player1Score += move1.simulation.points;
      game.player1Skulls += move1.simulation.points/70;
      while (game.player1Skulls > 6) {
        game.player1Skulls -=6;
        dropSkullLine(game.otherBoard);
      }
    }
  }

  private void dropSkullLine(BitBoard board) {
    for (int i=0;i<6;i++) {
      board.pushBall(0, i);
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

  public static void main(String[] args) {
    int p1Victory = 0;
    int p2Victory = 0;

    MCTS.AjustementVariables av1 = new MCTS.AjustementVariables();

    // change player 2 default values
    MCTS.AjustementVariables av2 = new MCTS.AjustementVariables();
    av2.THRESOLD_DEPTH_1_COLUMN = 6;
    av2.THRESOLD_DEPTH_2_COLUMN = 2;
    av2.MIN_SKULLS_COLUMNS_TO_DROP = 6;
    av2.SCORE_TO_DESTROY_SKULLS_RAPIDLY = 40;

    
    
    
    for (int i=0;i<100;i++) {
      Round round = new Round();
      round.player1.ajust= av1;
      round.player2.ajust = av2;
      
      round.play();
      if (round.player1Dead) { p2Victory++; }
      if (round.player2Dead) { p1Victory++; }
    }
    System.out.println("p1:"+p1Victory+" / p2:" +p2Victory);
  }
}
