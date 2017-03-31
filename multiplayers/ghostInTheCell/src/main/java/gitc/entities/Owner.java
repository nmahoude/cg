package gitc.entities;

import gitc.GameState;

public class Owner {
  public Owner(int id) {
    this.id = id;
  }

  public int id;
  public int bombsLeft = 2;
  public int unitCount=0;
  public int totalDisposable;

  public Owner getEnemy() {
    return id == 0 ? GameState.opp : GameState.me;
  }
}
