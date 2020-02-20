package coderoyale2.units;

import coderoyale2.Point;

public class Unit {
  public double mass;
  public int owner;
  public Point location = new Point();
  public int radius;
  public int health;

  public Point _pos = new Point();
  private int _radius;
  private int _health;

  public Unit(int owner) {
    super();
    this.owner = owner;
  }
  
  public void backup() {
    _pos.x = location.x;
    _pos.y = location.y;
    _radius = radius;
    _health = health;
  }
  public void restore() {
    location.x = _pos.x;
    location.y = _pos.y;
    radius = _radius;
    health = _health;
  }

  @Override
  public String toString() {
    return "pos:"+location;
  }
  
  public void damage(int damage) {
    this.health-= damage;
  }

}
