package coif_old.units;

import coif_old.Dir;

public enum UnitOrder {
  FREE,
  STAY,
  MOVE_UP,
  MOVE_DOWN,
  MOVE_RIGHT,
  MOVE_LEFT;
  
  
  public static UnitOrder moveOpposite(Dir dir) {
    switch(dir) {
    case DOWN:
      return UnitOrder.MOVE_UP;
    case LEFT:
      return UnitOrder.MOVE_RIGHT;
    case RIGHT:
      return UnitOrder.MOVE_LEFT;
    case UP:
      return UnitOrder.MOVE_DOWN;
    }
    throw new RuntimeException("Unknown dir "+dir);
  }

  
  public static UnitOrder move(Dir dir) {
    switch(dir) {
    case DOWN:
      return UnitOrder.MOVE_DOWN;
    case LEFT:
      return UnitOrder.MOVE_LEFT;
    case RIGHT:
      return UnitOrder.MOVE_RIGHT;
    case UP:
      return UnitOrder.MOVE_UP;
    }
    throw new RuntimeException("Unknown dir "+dir);
  }

}
