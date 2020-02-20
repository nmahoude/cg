package hypersonic.entities;

import hypersonic.State;
import hypersonic.utils.P;

public class Entity {
  public State board;
  public int owner;
  public final EntityType entityType;
  public P position;
  
  public Entity(State board, int owner, EntityType type, P position) {
    this.board = board;
    this.owner = owner;
    this.entityType = type;
    this.position = position;
  }
}
