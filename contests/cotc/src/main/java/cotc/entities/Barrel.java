package cotc.entities;

public class Barrel extends Entity{
  int rum;
  
  int b_rum;
  
  public Barrel(int entityId, int x, int y, int arg1, int arg2, int arg3, int arg4) {
    super(EntityType.BARREL, entityId, x, y);
    rum = arg1;
  }
  public void backup() {
    super.backup();
    b_rum = rum;
  }
  
  public void restore() {
    super.restore();
    rum = b_rum;
  }
}
