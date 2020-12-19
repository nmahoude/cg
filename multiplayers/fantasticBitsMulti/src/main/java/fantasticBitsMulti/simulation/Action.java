package fantasticBitsMulti.simulation;

import fantasticBitsMulti.units.Unit;

public class Action {
  public static final int TYPE_WAIT = -1;
  public static final int TYPE_MOVE = 0;
  public static final int TYPE_CAST = 1;
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
  public Unit target;
  
  @Override
  public String toString() {
    switch(type) {
    case TYPE_WAIT: return "WAIT";
    case TYPE_MOVE: return "MOVE ("+cosAngle+","+sinAngle+") thrust="+thrust;
    case TYPE_THROW : return "THROW ("+cosAngle+","+sinAngle+") thrust="+thrust;
    case TYPE_CAST : return "CAST "+spellId+" @ "+target;
    }
    return "";
  }
}
