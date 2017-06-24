package ww.sim;

import ww.GameState;

public class Simulation {

  private GameState state;
  private Move move;

  /**
   * simulate the move of any player (dir1, dir2 not null)
   * 
   * Pre-requisite : 
   *  the move is valid
   *  only one player will move
   *  
   *  GameState is backedup
   * @param state
   */
  public void simulate(Move move, GameState state) {
    this.move = move;
    this.state = state;
    
    if (moveIsPush()) {
      // TODO
    } else {
      
    }
  }

  private boolean moveIsPush() {
    return false;
  }
}
