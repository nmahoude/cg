package fantasticBits.fb.ai;

import fantasticBits.fb.Player;
import fantasticBits.fb.state.GameState;

public abstract class AI {
  public boolean isAlive = true;
  public Player player;
  public GameState gameState;
  
  public AI() {
  }
  
  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public void think() {
  }

  public abstract Move getNextMove();

  public boolean isAlive() {
    return isAlive;
  }

  public void control(Player player) {
    this.player = player;
  }
}
