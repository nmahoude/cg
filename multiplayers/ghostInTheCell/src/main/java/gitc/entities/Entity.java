package gitc.entities;

import gitc.GameState;

public class Entity {
  public int id;
  protected int playerId;
  public Owner owner;

  // backup
  public Owner b_owner;
  private int b_playerId;
  
  public void backup() {
    b_playerId = playerId;
    b_owner = owner;
  }
  public void restore() {
    owner = b_owner;
    playerId = b_playerId;
  }
  
  public Entity(int id, Owner owner) {
    this.id = id;
    this.owner = owner;
  }
  
  public void readPlayer (int playerId) {
    this.playerId = playerId;
    
    if (playerId == 0) {
      owner = null;
    } else if (playerId == 1) {
      owner = GameState.me;
    } else {
      owner = GameState.opp;
    }
  }
  public final boolean isOpponent() {
    return owner == GameState.opp;
  }
  public boolean isMe() {
    return owner == GameState.me;
  }
  public boolean isNeutral() {
    return owner == null;
  }


}
