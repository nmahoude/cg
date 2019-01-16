package hypersonic.ai;

import java.util.Arrays;

import hypersonic.Board;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.entities.Bomb;
import hypersonic.simulation.MoveGenerator;
import hypersonic.simulation.Simulation;

public class MC {
  static double patience[];
  static {
    patience = new double[20];
    for (int i=0;i<patience.length;i++) {
      patience[i] = (20-i) / 20.0;
    }
  }
  Board board = new Board();
  public Move bestMove = null;
  Simulation simulator = new Simulation(board);
  MoveGenerator gen = new MoveGenerator(board);
  
  Move[] moves = new Move[16];
  int movesFE;
  
  public void think(Board model) {
    this.board.copyFrom(model);

    Move allMoves[] = new Move[10];
    double bestScore = Double.NEGATIVE_INFINITY;
    
    for (int i=0;i<10_000;i++) {
      this.board.copyFrom(model);
      
      double score = 0;
      for (int t=0;t<10/*DEPTH*/;t++) {
        if (t <= 10 - Bomb.DEFAULT_TIMER) {
          movesFE = gen.getPossibleMoves(moves);
        } else {
          movesFE = gen.getPossibleMovesWithoutBombs(moves);
        }

        Move move = moves[Player.rand.nextInt(movesFE)];
        
        allMoves[t] = move;
        simulator.simulate(move);
        if (this.board.me.isDead) {
          score = -1_000_000 + t; // die the latest
          break;
        } else {
          score += patience[t] * score();
        }
      }
    
      if (score > bestScore) {
        bestScore = score;
        bestMove = allMoves[0];

        System.err.println("best move : "+Arrays.asList(allMoves));
        System.err.println("Status pos = "+this.board.me.position);
        System.err.println("Status dead = "+this.board.me.isDead);
        System.err.println("Bombs : ");
        for (int b=0;b<board.bombsFE;b++) {
          Bomb bomb = board.bombs[b];
          System.err.println(bomb);
        }

      }
    }
  }

  private double score() {
    double score = 0.0;
    
    score += 100.0 * board.me.points;
    score += board.me.currentRange;
    score += board.me.bombCount;
    
    return score;
  }
}
