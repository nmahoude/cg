package hypersonic.entities;

import hypersonic.Board;
import hypersonic.utils.P;

public class Entity {
  public final Board board;
  public final int owner;
  public final EntityType entityType;
  public P position;
  
  public Entity(Board board, int owner, EntityType type, P position) {
    this.board = board;
    this.owner = owner;
    this.entityType = type;
    this.position = position;
  }
}
