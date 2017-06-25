package ww;

import ww.sim.Move;
import ww.sim.Simulation;

public class Node {
  static private Simulation simulation = new Simulation();
  
  // GameState Backup :
  long layer1, layer2, ceiling;
  int x[] = new int[4];
  int y[] = new int[4];

  public String bestAction;

  private GameState state;
  
  private void save(GameState state) {
    layer1 = state.grid.layer1;
    layer2 = state.grid.layer2;
    ceiling = state.grid.ceiling;
    for (int i=0;i<4;i++) {
      x[i] = state.agents[i].x;
      y[i] = state.agents[i].y;
    }
  }
  
  private void reload(GameState state) {
    state.grid.layer1 = layer1;
    state.grid.layer2 = layer2;
    state.grid.ceiling = ceiling;
    for (int i=0;i<4;i++) {
      state.agents[i].x = x[i];
      state.agents[i].y = y[i];
    }
  }
  
  public void calculateChilds(GameState state) {
    this.state = state;
    save(state);
    
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < state.unitsPerPlayer; i++) {
      Move move = new Move();
      move.id = i;
      move.currentHeight = state.getHeight(state.agents[i].x, state.agents[i].y);
      for (Dir dir1 : Dir.values()) {
        for (Dir dir2 : Dir.values()) {
          move.dir1 = dir1;
          move.dir2 = dir2;
          simulation.simulate(move, state);
          if (!move.isDir1Valid()) break; 
          if (!move.isDir2Valid()) continue;
          
          double score = calculateScore(move);
          if (score > bestScore) {
            bestScore = score;
            if (move.isPush) {
              bestAction = "PUSH&BUILD "+i+" "+dir1.toString()+" "+dir2.toString();
            } else {
              bestAction = "MOVE&BUILD "+i+" "+dir1.toString()+" "+dir2.toString();
            }
          }
          reload(state);
        }
      }
    }

  }
  public double calculateScore(Move move) {
    if (move.isPush) {
      return positioningScore() + calculateMovabilityBonus() + calculatePushScore(move);
    } else {
      return positioningScore() + calculateMovabilityBonus() + calculateMoveScore(move);
    }
  }

  private double positioningScore() {
    // 1 try : manhattan distance between my agents
    double score = 0.0;
    for (int i=0;i<2;i++) {
      int manhattanDistance = Math.abs(state.agents[i].x - state.size/2)
          +Math.abs(state.agents[i].y - state.size/2);
      score += 10.0*(state.size - manhattanDistance);
    }
    return score;
  }

  /**
   * Bonus if we have a lot of move possible, malus for the opp
   * @return
   */
  private double calculateMovabilityBonus() {
    double movability = 0;
    for (int i=0;i<state.unitsPerPlayer;i++) {
      int possibleMoves = state.agents[i].getPossibleMoves(state);
      if (possibleMoves == 0) {
        movability -= 10_000; // big malus if we can't move
      }
      movability += 5 * possibleMoves;
    }
    for (int i=0;i<state.unitsPerPlayer;i++) {
      if (state.agents[state.unitsPerPlayer+i].inFogOfWar()) continue;
      
      int possibleMoves = state.agents[state.unitsPerPlayer+i].getPossibleMoves(state);
      if (possibleMoves == 0) {
        movability += 10_000; // high bonus if he can't move
      }
      movability -= 1 * possibleMoves;
    }
    return movability;
  }
  
  public double calculateMoveScore(Move move) {
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
  
  public double calculatePushScore(Move move) {
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
