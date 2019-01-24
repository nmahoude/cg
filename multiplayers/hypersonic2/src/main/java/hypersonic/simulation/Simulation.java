package hypersonic.simulation;

import hypersonic.Cache;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.utils.P;

public class Simulation {
  public State state;
  
  public static int deltaRange = 0;
  public static int deltaBomb = 0;
  
  public Simulation(State board) {
    this.state = board;
  }

  public final boolean isFinished() {
    return false;
  }
  
  public final void simulate(final Move move) {
    deltaRange = 0;
    deltaBomb = 0;
    
    state.updateBombs();
    simulateMove(move);
  }
  
  private void simulateMove(final Move move) {
    Bomberman player = state.players[Player.myId];

    int newX = player.position.x + move.dx;
    int newY = player.position.y + move.dy;
    if (move.dropBomb) {
      Bomb newBomb = Cache.popBomb(
          player.owner, 
          player.position, 
          state.turn + Bomb.DEFAULT_TIMER, 
          player.currentRange);
      state.addBomb(newBomb);

      player.bombsLeft-=1;
    }
    if ((newX !=player.position.x || newY != player.position.y) /** && state.canWalkOn(P.get(newX, newY)) dont check move, it has been done before...*/) {
      state.walkOn(player, P.get(newX, newY));
    }
  }
}
