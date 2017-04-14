package cotc.entities;

public class Barrel extends Entity{
  public int health;
  
  int b_health;
  
  public Barrel(int entityId, int x, int y, int rum) {
    super(EntityType.BARREL, entityId, x, y);
    this.health = rum;
  }
  public void backup() {
    super.backup();
    b_health = health;
  }
  
  public void restore() {
    super.restore();
    health = b_health;
  }
  
  public String toPlayerString(int playerIdx) {
    return toPlayerString(health, 0, 0, 0);
  }

}
