package fantasticBitsMulti.units;

public class Wall extends Unit {
  private int dir;

  public Wall(int dir) {
    super(EntityType.WALL, 0, 0, 0);
    this.id = 100;
    this.dir = dir;
  }

  @Override
  public void bounce(Unit u) {
    if (dir == HORIZONTAL) {
      u.vx = -u.vx;
    } else {
      u.vy = -u.vy;
    }
  }
}
