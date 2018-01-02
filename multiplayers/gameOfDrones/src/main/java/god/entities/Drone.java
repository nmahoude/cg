package god.entities;

import god.utils.Point;

public class Drone extends Entity {
  public final static int speed = 100;
  
  public Point lastPos = null;
  public Drone(int id, int x, int y) {
    super(x, y);
    this.id = id;
    lastPos = position;
  }
  public final int id;
  public int owner;

  // work data
  public Zone inZone;
  public Point target;


  public void update(int x, int y) {
    lastPos = position;
    this.position = new Point(x,y);
  }
}
