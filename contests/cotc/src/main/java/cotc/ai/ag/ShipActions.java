package cotc.ai.ag;

public class ShipActions {
  public AGAction actions[] = new AGAction[3];
  
  /** one ship */
  public int hash1() {
    int hash = actions[0].action.ordinal() * 1024 + actions[0].target.x*32 + actions[0].target.y;
    return hash;
  }
  /** 2 ships */
  public int hash2() {
    return hash1() * 2048 + actions[1].action.ordinal() * 1024 + actions[1].target.x*32 + actions[1].target.y;
  }
  
  /** 3 ships */
  public int hash3() {
    return hash2() * 2048 + actions[2].action.ordinal() * 1024 + actions[2].target.x*32 + actions[2].target.y;
  }
}
