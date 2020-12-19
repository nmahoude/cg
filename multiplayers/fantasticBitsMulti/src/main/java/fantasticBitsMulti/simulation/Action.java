package fantasticBitsMulti.simulation;

public class Action {
  private static final int TYPE_WAIT = -1;
  public static final int TYPE_MOVE = 0;
  public static final int TYPE_SPELL = 1;
  public static final int TYPE_THROW = 2;

  public static final Action WAIT = new Action();
  static {
    WAIT.type = TYPE_WAIT;
  };
  
  
  public int type;
  
  // move info
  public double cosAngle;
  public double sinAngle;
  public int    thrust;
  
  // spell info
  public int spellId;
  public int targetId;
}
