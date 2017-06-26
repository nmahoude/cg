package ww;

public class Agent {
  public final int id;
  
  public int x, y;
  public int score;
  
  public Agent(int id) {
    this.id = id;
  }
  
  private int _x, _y;
  private int _score;
  public void backup() {
    _x = x;
    _y = y;
    _score = score;
  }
  
  public void restore() {
    x = _x;
    y = _y;
    score = _score;
  }
  
  public int getPossibleMoves(GameState state) {
    return getPossibleActions(state, true);
  }

  public int getPossibleActions(GameState state) {
    return getPossibleActions(state, false);
  }
  
  private int getPossibleActions(GameState state, boolean restrictToMove) {
    int actionsLeft = 0; // count possible actions for the agent, if too few, we force this agent to move (boost his score)
    for (Dir dir : Dir.values()) {
      int dirX = x+dir.dx;
      int dirY = y+dir.dy;
      if (!state.isValid(dirX, dirY)) continue;
    
      if (state.isOccupied(id, dirX, dirY)) {
        // PUSH action
        if (state.isFriendly(id, dirX, dirY)) continue;
        
        for (Dir push : dir.pushDirections()) {
          int pushedX = dirX+push.dx;
          int pushedY = dirY+push.dy;
          if (!state.isValid(pushedX, pushedY)) continue;
          if (state.isOccupied(id, pushedX, pushedY)) continue;
          
          int deltaY = state.getHeight(dirX, dirY)-state.getHeight(pushedX, pushedY);
          if (deltaY < -1) continue;
          actionsLeft++;
          if (restrictToMove) break;
        }
      } else {
        // MOVE ACTION
        int deltaHeight = state.getHeight(dirX,dirY) - state.getHeight(x,y);
        if (deltaHeight > 1 ) continue;

        if (restrictToMove) {
          // we always can put a block on the incomming cell, so it's always true
          actionsLeft++;
          break;
        }
        
        for (Dir dirBlock : Dir.values()) {
          int blockX = dirX+dirBlock.dx;
          int blockY = dirY+dirBlock.dy;
          if (!state.isValid(blockX, blockY)) continue;
          if (state.isOccupied(id, blockX, blockY)) continue;
          
          actionsLeft++;
        }
      }
    }
    return actionsLeft;
  }

  public boolean inFogOfWar() {
    return x == -1;
  }
}
