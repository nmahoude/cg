package ww;

import ww.sim.Move;
import ww.sim.Simulation;

public class NodeV2 {
  private static final boolean DO_MINIMAX = true;
  private static final boolean CHECK_BLOCKED = false;
  
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
          double actionScore = eval.calculateScore(state, move);
          double minimaxScore = actionScore;
          Move minimaxAction = move;

          if (CHECK_BLOCKED) {
            int count = checkAgentPotentiallyBlocked();
            if (count > 0) {
              minimaxScore -= count * 10_000;
            }
          }
          
          if (DO_MINIMAX) {
            for (int id=0;id<2;id++) {
              Agent toCheck = state.agents[id];
              // move
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
          
          if (actionScore + 0.1*minimaxScore > bestScore) {
            bestScore = actionScore + 0.1*minimaxScore;
            bestAction = minimaxAction;
            move = createMove(i);
          }
          
          reload(state);
        }
      }
    }
  }

  int checkAgentPotentiallyBlocked() {
    int count = 0;
    for (int id=0;id<2;id++) {
      Agent toCheck = state.agents[id];
      if (canBeBlocked(toCheck)) {
        count++;
      }
    }
    return count;
  }

  boolean canBeBlocked(Agent toCheck) {
    for (Dir oppWall : Dir.values()) {
      int tx = toCheck.x + oppWall.dx;
      int ty = toCheck.y + oppWall.dy;
      if (!state.isValid(tx, ty)) continue;
      int currentHeight = state.getHeight(tx, ty);

      state.setHeight(tx, ty, currentHeight+1);
      if (toCheck.getPossibleActions(state) == 0) {
        state.setHeight(tx, ty, currentHeight);
        return true;
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

      if (toCheck.getPossibleActions(state) == 0) {
        toCheck.x = x;
        toCheck.y = y;
        state.setHeight(x, y, currentHeight);
        return true;
      }
      toCheck.x = x;
      toCheck.y = y;
      state.setHeight(x, y, currentHeight);
    }
    return false;
  }
}
