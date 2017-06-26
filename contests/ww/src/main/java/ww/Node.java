package ww;

import ww.sim.Move;
import ww.sim.Simulation;

public class Node {
  private static final int MAX_DEPTH = 1;
  
  static private Simulation simulation = new Simulation();
  static private Eval eval = new Eval();
  
  // GameState Backup :
  long layer1, layer2, ceiling;
  int x[] = new int[4];
  int y[] = new int[4];

  public Move bestAction;

  GameState state;
  private double bestScore;
  
  void save(GameState state) {
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
  
  public Move createMove(int id) {
    Move move = new Move();
    move.id = id;
    move.currentHeight = state.getHeight(state.agents[id].x, state.agents[id].y);
    return move;
  }
  
  public void calculateChilds(int depth, GameState state) {
    this.state = state;
    save(state);
    
    bestScore = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < GameState.unitsPerPlayer; i++) {
      Move move = createMove(i);
      
      for (Dir dir1 : Dir.values()) {
        for (Dir dir2 : Dir.values()) {
          if (depth == 0 && System.currentTimeMillis() - GameState.startTime > 40 ) {
            return;
          }
          move.dir1 = dir1;
          move.dir2 = dir2;
          simulation.simulate(move, state);
          if (!move.isDir1Valid()) break; 
          if (!move.isDir2Valid()) continue;
          
          // move append
          if (depth < MAX_DEPTH) {
            double score = eval.calculateScore(state, move);
            
            Node child = new Node();
            child.calculateChilds(depth+1, state);

            double totalScore;
            if (Double.isFinite(child.bestScore)) {
              totalScore = score + 0.9*child.bestScore;
            } else {
              totalScore = score ;
            }
            
            if (totalScore > bestScore) {
              bestScore = totalScore;
              bestAction = move;
              move = createMove(i);
            }
          } else {
            double score = eval.calculateScore(state, move);
            if (score > bestScore) {
              bestScore = score;
              bestAction = move;
              move = createMove(i);
            }
          }
          reload(state);
        }
      }
    }
  }
}
