package stc2.play;

import java.util.Arrays;
import java.util.Random;

import stc2.BitBoard;
import stc2.Game;
import stc2.MCTSOld;
import stc2.Simulation;

// a full game between 2 AIs
public class Round {
  Game game = new Game();
  Simulation simulation = new Simulation();
  NewAi player1 = new NewAi();
  NewAi player2 = new NewAi();
  private boolean player1Dead;
  private boolean player2Dead;
  private Random random = new Random();
  
  private Move move1;
  private int lastP1Points;
  private Move move2;
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

      //debugBoards();
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
    
    for (int i=0;i<8;i++) {
      System.out.print("["+game.nextBalls[i]+","+game.nextBalls2[i]+"] ");
    }
    System.out.println("");
    
    System.out.println("Points "+game.player1Score+" / "+game.player2Score);
    System.out.println("Skulls "+game.player1Skulls+" / "+game.player2Skulls);
    for (int i=0;i<12;i++) {
      System.out.print(p1[i]+"  "+p2[i]);
      System.out.println("");
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
    simulation.board = game.otherBoard;
    simulation.putBallsNoCheck( game.nextBalls[0], game.nextBalls2[0], move2.rotation, move2.column);
    lastP2Points = simulation.points;
  }

  private void doPlayer1Move() {
    move1 = player1.getMove();
    if (move1 == null) {
      player1Dead = true;
      return;
    }
    
    simulation.board = game.otherBoard;
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

  static  class Candidate implements Comparable<Candidate> {
    int victoryCount = 0;
    AdjustementFactors adjustementFactor = AdjustementFactors.random();
    
    @Override
    public int compareTo(Candidate o) {
      return Integer.compare(o.victoryCount, victoryCount);
    }
  }
  
  static Candidate[] candidates = new Candidate[10];
  static {
    for (int i=0;i<candidates.length;i++) {
      candidates[i] = new Candidate();
    }
  }
  
  
  
  public static void main(String[] args) {

    while (true) {
      System.out.println("New generation");
      System.out.println("--------------");
      
      for (int i=0;i<10;i++) {
        for (int j=i+1;j<10;j++) {
          System.out.println("Match "+i+" vs "+j);
          oneMatch(candidates[i], candidates[j]);
        }
      }
      
      Arrays.sort(candidates);
      outputGenerationResults();
      
      Darwin.mutate(candidates);
    }
  }

  private static void outputGenerationResults() {
    for (int i=0;i<candidates.length;i++) {
      System.out.println("Candidates victory : "+candidates[i].victoryCount);
      candidates[i].adjustementFactor.print();
      System.out.println("-----------------------------");
    }
  }

  private static void oneMatch(Candidate candidate1, Candidate candidate2) {
    int p1Victory = 0;
    int p2Victory = 0;
    for (int i=0;i<500;i++) {
      Round round = new Round();
      round.player1.ajust = candidate1.adjustementFactor;
      round.player2.ajust = candidate2.adjustementFactor;
      round.play();
      if (round.player1Dead) { candidate2.victoryCount++; }
      if (round.player2Dead) { candidate1.victoryCount++; }
    }
  }
}
