package ww;

import ww.sim.Move;

public class Eval {
  private GameState state;
  private Move move;
  
  public static int feature=0;
  public static final int POSITION_FEATURE = feature++;
  public static final int MOVABILITY_FEATURE = feature++;
  public static final int PUSH_FEATURE = feature++;
  public static final int MOVE_FEATURE = feature++;
  public static final int LAST_FEATURE =feature++;
  
  public double features[] = new double[LAST_FEATURE];
  
  public void debug(String action) {
    System.err.println("Eval Features for ("+action+") :");
    System.err.println("   positioning  "+features[POSITION_FEATURE] );
    System.err.println("   movability   "+features[MOVABILITY_FEATURE] );
    System.err.println("   push         "+features[PUSH_FEATURE] );
    System.err.println("   move         "+features[MOVE_FEATURE] );
  }
  public double calculateScore(GameState state, Move move) {
    this.state = state;
    this.move = move;
    
    features[POSITION_FEATURE] = positioningScore();
    features[MOVABILITY_FEATURE] = calculateMovabilityBonus();

    if (move.isPush) {
      features[MOVE_FEATURE] = 0.0;
      features[PUSH_FEATURE] =  calculatePushScore();
    } else {
      features[MOVE_FEATURE] = calculateMoveScore();
      features[PUSH_FEATURE] =  0.0;
    }
    
    double score = 0.0;
    for (int i=0;i<feature-1;i++) {
      score+= features[i];
    }
    return score;
  }

  private double positioningScore() {
    // 1 try : manhattan distance between my agents
    double score = 0.0;
    for (int i=0;i<2;i++) {
      int manhattanDistance = Math.abs(state.agents[i].x - GameState.size/2)
          +Math.abs(state.agents[i].y - GameState.size/2);
      score += 5.0*(GameState.size - manhattanDistance);
    }
    return score;
  }

  /**
   * Bonus if we have a lot of move possible, malus for the opp
   * @return
   */
  private double calculateMovabilityBonus() {
    double movability = 0;
    for (int i=0;i<GameState.unitsPerPlayer;i++) {
      int possibleMoves = state.agents[i].getPossibleMoves(state);
      if (possibleMoves == 0) {
        movability -= 10_000; // big malus if we can't move
      }
      movability += 5.0 * possibleMoves;
    }
    for (int i=0;i<GameState.unitsPerPlayer;i++) {
      if (state.agents[GameState.unitsPerPlayer+i].inFogOfWar()) continue;
      
      int possibleMoves = state.agents[GameState.unitsPerPlayer+i].getPossibleMoves(state);
      if (possibleMoves == 0) {
        movability += 10_000; // high bonus if he can't move
      }
      movability -= 1.0 * possibleMoves;
    }
    return movability;
  }
  
  public double calculateMoveScore() {
    double moveScore = 0
        // our next height
        + 20.0*move.dir1Height
        // if we go on a lvl 3 height next, it's a big bonus (score 1 point)
        + (move.dir1Height == 3 ? 1000 : 0);
    
    int deltaY = move.dir2Height + 1 - move.dir1Height;
    double buildScore = 0
        // height of the next block we put
        + move.dir2Height 
        // Malus if we go to height 4
        + (move.dir2Height == 3 ? -5 : 0)
        // Malus if the block is higher than our next position
        - (deltaY > 1 ? 100 : 0);

    return moveScore+ buildScore;
  }
  
  public double calculatePushScore() {
    double moveScore = 0
        + 20*move.currentHeight;

    
    int deltaY = move.dir1Height-move.dir2Height;
    double pushScore = 0
        // just under 1 floor up for me so I prefer climbing, but 2 stairs fall is better
        + 15.0 * deltaY
        // If opp is on lvl 3 and we push it <= than lvl 1, it's a very good move
        + (move.dir1Height == 3 ? 1000*Math.max(deltaY-1,0) : 0);
    
    return moveScore + pushScore;
  }
}
