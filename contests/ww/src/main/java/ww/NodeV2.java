package ww;

import ww.sim.Move;
import ww.sim.Simulation;

public class NodeV2 {
  private static final boolean DO_MINIMAX = false;
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
      Agent moving = state.agents[i];
      Move move = createMove(i);
      
      for (Dir dir1 : Dir.values()) {
        for (Dir dir2 : Dir.values()) {
          move.dir1 = dir1;
          move.dir2 = dir2;
          simulation.simulate(move, state);
          if (!move.isDir1Valid()) break; 
          if (!move.isDir2Valid()) continue;
          
          // Minimax style : do all possible move by opp and get the lowest score
          // moves
          double minimaxScore = eval.calculateScore(state, move);
          Move minimaxAction = move;
          if (DO_MINIMAX) {
            for (int id=0;id<2;id++) {
              Agent toCheck = state.agents[id];
              for (Dir oppWall : Dir.values()) {
                int tx = toCheck.x + oppWall.dx;
                int ty = toCheck.y + oppWall.dy;
                if (!state.isValid(tx, ty)) continue;
                int currentHeight = state.getHeight(tx, ty);
    
                state.setHeight(tx, ty, currentHeight+1);
                double score = eval.calculateScore(state, move);
                if (score < minimaxScore) {
                  minimaxScore = score;
                  minimaxAction = move;
                }
                state.setHeight(tx, ty, currentHeight);
              }
    
              // pushes
              for (Dir oppWall : Dir.values()) {
                int tx = toCheck.x + oppWall.dx;
                int ty = toCheck.y + oppWall.dy;
                if (!state.isValid(tx, ty)) continue;
                
                int x = toCheck.x;
                int y = toCheck.y;
                int currentHeight = state.getHeight(x, y);
    
                toCheck.x = tx;
                toCheck.y = ty;
                state.setHeight(x, y, currentHeight+1);
                double score = eval.calculateScore(state, move);
                if (score < minimaxScore) {
                  minimaxScore = score;
                  minimaxAction = move;
                }
                toCheck.x = x;
                toCheck.y = y;
                state.setHeight(x, y, currentHeight);
              }
            }
          }
          if (minimaxScore > bestScore) {
            bestScore = minimaxScore;
            bestAction = minimaxAction;
            move = createMove(i);
          }
          
          reload(state);
        }
      }
    }
  }
}
