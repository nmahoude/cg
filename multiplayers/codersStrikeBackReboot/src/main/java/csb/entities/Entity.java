package csb.entities;

import trigonometry.Point;

public class Entity {
  int id;
  public final double radius;

  public Point position;
  
  private Point b_position;
  
  public Entity(int radius) {
    this.radius = radius;
  }
  public void backup() {
    b_position = position;
  }
  public void restore() {
    position = b_position;
  }
}
