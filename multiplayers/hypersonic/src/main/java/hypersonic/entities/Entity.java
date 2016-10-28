package hypersonic.entities;

import hypersonic.utils.P;

public class Entity {
  public final EntityType type;
  public P position;
  
  public Entity(EntityType type, P position) {
    this.type = type;
    this.position = position;
  }
}
