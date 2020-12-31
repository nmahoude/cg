package xmasrush.ai.push;

public class PushAction {
  public static final PushAction[] actions = new PushAction[28];
  
  
  static {
    for (Direction dir : Direction.values()) {
      for (int row=0;row<7;row++) {
        actions[row + dir.offset] = new PushAction(dir, row);
      }
    }
    for (Direction dir : Direction.values()) {
      for (int row=0;row<7;row++) {
        actions[row + dir.offset].counter = actions[row + ((dir.offset + 2*7)%28)];
      }
    }
  }
  
  public final Direction dir;
  public final int offset;
  public PushAction counter;
  
  private PushAction(Direction dir, int offset) {
    this.dir = dir;
    this.offset = offset;
  }
  
  @Override
  public String toString() {
    return "PUSH "+offset+" "+dir;
  }

  public static PushAction actions(int row, Direction dir) {
    return actions[row + dir.offset];
  }
}
