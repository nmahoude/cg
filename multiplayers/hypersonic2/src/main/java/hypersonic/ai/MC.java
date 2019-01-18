package hypersonic.ai;

import java.util.Arrays;

import hypersonic.State;
import hypersonic.Board;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.simulation.MoveGenerator;
import hypersonic.simulation.Simulation;

public class MC {
  private static final int DEPTH = 20;
  
  static double patience[];
  static {
    patience = new double[DEPTH];
    for (int i=0;i<DEPTH;i++) {
      patience[i] =Math.pow(0.7, i);
    }
  }
  State state = new State();
  public Move bestMove = null;
  public String message = "";
  Simulation simulator = new Simulation(state);
  MoveGenerator gen = new MoveGenerator(state);
  
  Move[] moves = new Move[16];
  int movesFE;
  
  public void think(State model) {
    this.state.copyFrom(model);

    Move allMoves[] = new Move[DEPTH];
    double bestScore = Double.NEGATIVE_INFINITY;
    
    int simu = 0;
    while (true) {
      simu++;
      if ((simu & 255) == 0 ) {
        if (System.currentTimeMillis() - Player.startTime > 95) {
          break;
        }
      }
      
      this.state.copyFrom(model);
      
      double score = 0;
      for (int t=0;t<DEPTH;t++) {
        if (t <= DEPTH - Bomb.DEFAULT_TIMER - 1 ) {
          movesFE = gen.getPossibleMoves(moves);
        } else {
          movesFE = gen.getPossibleMovesWithoutBombs(moves);
        }
        Move move = moves[Player.rand.nextInt(movesFE)];
        
        allMoves[t] = move;
        simulator.simulate(move);
        if (this.state.players[Player.myId].isDead) {
          score = -1_000_000 + t; // die the latest
          break;
        } else {
          score += patience[t] * score();
        }
      }
    
      if (score > bestScore) {
        bestScore = score;
        bestMove = allMoves[0];

        if(Player.DEBUG_AI) {
          System.err.println("best move : "+Arrays.asList(allMoves));
          System.err.println("Status pos = "+this.state.players[Player.myId].position);
          System.err.println("Status dead = "+this.state.players[Player.myId].isDead);
        }
      }
    }
    if (Player.DEBUG_AI) {
      System.err.println("Simulations : " + simu);
    }
    message = ""+simu + " / "+(System.currentTimeMillis()-Player.startTime);
  }

  private double score() {
    double score = 0.0;
    
    Bomberman me = state.players[Player.myId];
    
    score += 10_000.0 * me.points;
    score += 1.1 * me.bombCount;
    score += me.currentRange;
    score += me.bombsLeft;
    return score;
  }
}
