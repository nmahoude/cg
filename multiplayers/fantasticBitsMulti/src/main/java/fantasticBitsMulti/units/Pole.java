package fantasticBitsMulti.units;

import fantasticBitsMulti.simulation.Collision;
import trigonometry.Point;

public class Pole extends Unit {

  public Pole(int id, int x, int y) {
    super(EntityType.POLE, 300, Integer.MAX_VALUE, 0.0);
    position = new Point(x, y);
    this.id = id;
    vx = 0;
    vy = 0;
    dead = false;
  }

  @Override
  public void move() {}
  
  @Override
  public void save() { }
  
  public void reset() {};
  
  @Override
  public Collision collision(double from) {
    return null;
  }
}
