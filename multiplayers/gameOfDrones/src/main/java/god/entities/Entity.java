package god.entities;

import god.utils.Point;

public class Entity {
  public Entity(int x, int y) {
    position = new Point(x,y);
  }

  public Point position;

}
