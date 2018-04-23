package coderoyale.units;

public class Unit extends Disk {
  private static final int KNIGHT = 0; 
  private static final int ARCHER = 1;
  private static final int GIANT = 2; 

  public int owner;
  public int type;
  public int health;
  

  public Unit(int x, int y, int unitType) {
    super(x, y, getRadius(unitType));
    this.type = unitType;
  }

  private static int getRadius(int unitType) {
    int radius;
    if (unitType == KNIGHT) {
      radius = 20;
    } else if (unitType == ARCHER) {
      radius = 25;
    } else if (unitType == GIANT) {
      radius = 40;
    } else {
      radius = 30;
    }
    return radius;
  }

  public boolean isKnight() {
    return type == Unit.KNIGHT;
  }
  public boolean isGiant() {
    return type == Unit.GIANT;
  }
  
  
  public void updatePos(int x, int y) {
    pos.x = x;
    pos.y = y;
  }
}
