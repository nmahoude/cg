package cotc.entities;

import cotc.utils.Coord;
import cotc.utils.Util;

public class Entity {
  public final EntityType type;
  public final int id;
  public Coord position;
  
  Coord b_position;

  
  public Entity(EntityType type, int entityId, int x, int y) {
    this.type = type;
    this.id = entityId;
    this.position = new Coord(x,y);
  }
  public void backup() {
    b_position = position;
  }
  public void restore() {
    position = b_position;
  }
  public void update(int x, int y) {
    this.position = new Coord(x,y);
  }
  
  public String toViewString() {
    return Util.join(id, position.y, position.x);
}

protected String toPlayerString(int arg1, int arg2, int arg3, int arg4) {
    return Util.join(id, type.name(), position.x, position.y, arg1, arg2, arg3, arg4);
}
}
