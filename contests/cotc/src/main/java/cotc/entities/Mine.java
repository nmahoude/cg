package cotc.entities;

public class Mine extends Entity {
  public Mine(int entityId, int x, int y, int arg1, int arg2, int arg3, int arg4) {
    super(EntityType.MINE, entityId, x, y);
    
    // no args
  }
  
  public String toPlayerString(int playerIdx) {
    return toPlayerString(0, 0, 0, 0);
}

}
