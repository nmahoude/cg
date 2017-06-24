package ww;

import java.util.Scanner;

import ww.sim.Move;
import ww.sim.Simulation;

public class Player {
  static GameState state = new GameState();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    state.readInit(in);

    int round = 0;
    int s = 0;
    // game loop
    while (true) {
      state.readRound(in);
      state.toTDD();
      round++;

      String best[] = new String[state.unitsPerPlayer];
      double bestScore[] = new double[state.unitsPerPlayer];
      
      Simulation simulation = new Simulation();
      state.backup();
      for (int i = 0; i < state.unitsPerPlayer; i++) {
        Move move = new Move();
        move.index = i;
        move.currentHeight = state.getHeight(state.agents[i].x, state.agents[i].y);
        bestScore[i] = Double.NEGATIVE_INFINITY;
        for (Dir dir1 : Dir.values()) {
          for (Dir dir2 : Dir.values()) {
            move.dir1 = dir1;
            move.dir2 = dir2;
            boolean valid = simulation.simulate(move, state);
            if (!valid) continue;
            double score = calculateScore(move);
            if (score > bestScore[i]) {
              bestScore[i] = score;
              if (move.isPushed) {
                best[i] = "PUSH&BUILD "+i+" "+dir1.toString()+" "+dir2.toString();
              } else {
                best[i] = "MOVE&BUILD "+i+" "+dir1.toString()+" "+dir2.toString();
              }
            }
            state.restore();
          }
        }
      }
        

      if (bestScore[0] > bestScore[1]) {
        System.out.println(best[0]);
      } else {
        System.out.println(best[1]);
      }
      s++;
    }
  }
  
  private static double calculateScore(Move move) {
    if (move.isPushed) {
      return calculatePushScore(move);
    } else {
      return calculateMoveScore(move);
    }
  }
  public static double calculateMoveScore(Move move) {
    int moveScore = 0
        // our next height
        + 20*move.dir1Height
        // if we go on a lvl 3 height next, it's a big bonus (score 1 point)
        + (move.dir1Height == 3 ? 1000 : 0);
    
    int deltaY = move.dir2Height + 1 - move.dir1Height;
    int buildScore = 0
        // height of the next block we put
        + move.dir2Height 
        // Malus if we go to height 4
        + (move.dir2Height == 3 ? -5 : 0)
        // Malus if the block is higher than our next position
        - (deltaY > 1 ? 100 : 0);

    return moveScore+ buildScore;
  }
  
  public static double calculatePushScore(Move move) {
    int moveScore = 0
        // juste get our current height as there is no score++ on push 
        + 20*move.currentHeight;
    
    int deltaY = move.dir1Height-move.dir2Height;
    int pushScore = 0
        // just under 1 floor up for me so I prefer climbing, but 2 stairs fall is better
        + 15 * deltaY
        // If opp is on lvl 3 and we push it <= than lvl 1, it's a very good move
        + (move.dir1Height == 3 ? 1000*Math.max(deltaY-1,0) : 0);
    
    return moveScore + pushScore;
  }
}