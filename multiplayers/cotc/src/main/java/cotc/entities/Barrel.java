package cotc.entities;

public class Barrel extends Entity{
  
  public Barrel(int entityId, int x, int y, int rum) {
    super(EntityType.BARREL, entityId, x, y);
    this.health = rum;
  }
  public void backup() {
    super.backup();
  }
  
  public void restore() {
    super.restore();
  }
  
  public String toPlayerString(int playerIdx) {
    return toPlayerString(health, 0, 0, 0);
  }

}
