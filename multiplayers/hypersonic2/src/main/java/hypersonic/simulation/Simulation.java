package hypersonic.simulation;

import hypersonic.State;
import hypersonic.Cache;
import hypersonic.Move;
import hypersonic.entities.Bomb;
import hypersonic.utils.P;

public class Simulation {
  public State state;
  
  public Simulation(State board) {
    this.state = board;
  }

  public final boolean isFinished() {
    return false;
  }
  
  public final void simulate(final Move move) {
    state.updateBombs();
    simulateMove(move);
  }
  
  private void simulateMove(final Move move) {
    int newX = state.me.position.x;
    int newY = state.me.position.y;
    boolean dropBomb = false;
    switch(move) {
      case DOWN_BOMB:
        dropBomb = true;
      case DOWN:
        newY+=1;
        break;
      case LEFT_BOMB:
        dropBomb = true;
      case LEFT:
        newX-=1;
        break;
      case RIGHT_BOMB:
        dropBomb = true;
      case RIGHT:
        newX+=1;
        break;
      case STAY_BOMB:
        dropBomb = true;
      case STAY:
        break;
      case UP_BOMB:
        dropBomb = true;
      case UP:
        newY-=1;
    }
    
    if (dropBomb) {
      Bomb newBomb = Cache.popBomb(
          state.me.owner, 
          state.me.position, 
          state.turn + Bomb.DEFAULT_TIMER, 
          state.me.currentRange);
      state.addBomb(newBomb);

      state.me.bombsLeft-=1;
    }
    if (state.canWalkOn(P.get(newX, newY)) && (newX != state.me.position.x || newY != state.me.position.y)) {
      state.walkOn(state.me, P.get(newX, newY));
    }
  }
}
