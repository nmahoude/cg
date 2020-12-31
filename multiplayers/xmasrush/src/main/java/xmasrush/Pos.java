package xmasrush;

import xmasrush.ai.push.Direction;
import xmasrush.ai.push.PushAction;

public class Pos {
  public static final Pos unknown = new Pos(-1,-1);
  
  static Pos[] allPos = new Pos[49]; 
  static {
    for (int y=0;y<7;y++) {
      for (int x=0;x<7;x++) {
        allPos[x+7*y] = new Pos(x,y);
      }
    }
  }
  
  
  public final int x,y, offset;
  
  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
    this.offset = x + 7*y;
  }
  
  @Override
  public String toString() {
    return String.format("(%d, %d)", x, y);
  }
  public static Pos get(int x, int y) {
    return allPos[x+7*y];
  }
  
  public static Pos getCircular(Pos pos, Direction dir) {
    int x = pos.x + dir.dx;
    int y = pos.y + dir.dy;
    if (x == -1) x +=7;
    if (y == -1) y +=7;
    if (x == 7) x -=7;
    if (y == 7) y -=7;
    
    return allPos[x+7*y];
  }

  public int manhattan(Pos pos) {
    return Math.abs(x-pos.x) + Math.abs(y-pos.y);
  }

  public Pos moveIfNecessary(PushAction action) {
    switch(action.dir ) {
    case UP:
      if (action.offset == x) {
        return getCircular(this, action.dir);
      }
      break;
    case RIGHT:
      if (action.offset == y) {
        return getCircular(this, action.dir);
      }
      break;
    case DOWN:
      if (action.offset == x) {
        return getCircular(this, action.dir);
      }

      break;
    case LEFT:
      if (action.offset == y) {
        return getCircular(this, action.dir);
      }

      break;
    default:
      break;
    }
    return this;
  }
}
 