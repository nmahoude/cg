package fantasticBitsMulti.units;

public class Wall extends Unit {
  private int dir;

  public Wall(int dir) {
    super(EntityType.WALL, 0, 0, 0);
    this.id = 100;
    this.dir = dir;
  }

}
