package h.simulation;

import h.GameState;

public class Simulation {
  GameState state;
  
  public Simulation(GameState state) {
    this.state = state;
  }
  
  public void simulate() {
    state.base++;
    explodeBombs();
    applyPlayerMove();
  }

  private void explodeBombs() {
    state.explodeBombs();
  }
  private void applyPlayerMove() {
    // TODO Auto-generated method stub
    
  }


}
