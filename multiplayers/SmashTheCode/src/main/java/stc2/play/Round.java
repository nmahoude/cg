package stc2.play;

import java.util.Random;

import stc2.BitBoard;
import stc2.Game;
import stc2.MCTSOld;
import stc2.Simulation;

// a full game between 2 AIs
public class Round {
  Game game = new Game();
  Simulation simulation = new Simulation();
  AI player1 = new AI();
  AI player2 = new AI();
  private boolean player1Dead;
  private boolean player2Dead;
  private Random random = new Random();
  
  private IAI.Move move1;
  private int lastP1Points;
  private IAI.Move move2;
  private int lastP2Points;
  
  public void play() {
    prepareRound();
    
    while (!isFinished()) {
      doPlayer1Move();
      doPlayer2Move();
      getNewPair();
      
      if (!isFinished()) {
        updatePlayer1();
        updatePlayer2();
      }

      debugBoards();
    }
  }

  private void updatePlayer2() {
    if (lastP2Points > 0) {
      game.player2Score += lastP2Points;
      game.player2Skulls += lastP2Points/70;
      while (game.player2Skulls > 6) {
        game.player2Skulls -=6;
        dropSkullLine(game.myBoard);
      }
    }
  }

  private void updatePlayer1() {
    if (lastP1Points > 0) {
      game.player1Score += lastP1Points;
      game.player1Skulls += lastP1Points/70;
      while (game.player1Skulls > 6) {
        game.player1Skulls -=6;
        dropSkullLine(game.otherBoard);
      }
    }
  }

  private void debugBoards() {
    String p1[] = game.myBoard.getDebugString().split("\n");
    String p2[] = game.otherBoard.getDebugString().split("\n");
    
    for (int i=0;i<12;i++) {
      System.out.println(p1[i]+"  "+p2[i]);
    }
    System.out.println("");
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
    move2 = player2.getMove();
    if (move2 == null) {
      player2Dead = true;
      return;
    }
    simulation.clear();
    simulation.board = game.otherBoard;
    simulation.putBallsNoCheck(game.nextBalls[0], game.nextBalls2[0], move2.rotation, move2.column);
    lastP2Points = simulation.points;
  }

  private void doPlayer1Move() {
    move1 = player1.getMove();
    if (move1 == null) {
      player1Dead = true;
      return;
    }
    
    simulation.clear();
    simulation.board = game.myBoard;
    simulation.putBallsNoCheck(game.nextBalls[0], game.nextBalls2[0], move1.rotation, move1.column);
    lastP1Points = simulation.points;
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

    MCTSOld.AjustementVariables av1 = new MCTSOld.AjustementVariables();

    // change player 2 default values
    MCTSOld.AjustementVariables av2 = new MCTSOld.AjustementVariables();
//    av2.THRESOLD_DEPTH_1_COLUMN = 6;
//    av2.THRESOLD_DEPTH_2_COLUMN = 2;
//    av2.MIN_SKULLS_COLUMNS_TO_DROP = 4;
//    av2.SCORE_TO_DESTROY_SKULLS_RAPIDLY = 40;

    
    
    
    for (int i=0;i<1;i++) {
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
