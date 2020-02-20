package coderoyale2.units;

public class Queen extends Unit {

  public int touchedSite;

  public int gold;

  private int _gold;

  public Queen(int owner) {
    super(owner);
    mass = 100;
  }

  public void backup() {
    super.backup();
    _gold = gold;
  }
  public void restore() {
    super.restore();
    gold = _gold;
  }

  boolean isDead() {
    return health <= 0;
  }
}
