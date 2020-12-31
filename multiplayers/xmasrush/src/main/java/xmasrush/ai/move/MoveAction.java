package xmasrush.ai.move;

public class MoveAction {
  public static final MoveAction PASS = new MoveAction(-1);

  
  
  
  int move;
  private MoveAction(int move) {
    this.move = move;
  }
  
  
  @Override
  public String toString() {
    if (move == -1) {
      return "PASS";
    } else if (move == 0) {
      return "UP";
    } else if (move == 1) {
      return "RIGHT";
    } else if (move == 2) {
      return "DOWN";
    } else if (move == 3) {
      return "LEFT";
    } else {
      return "UNKNOWN";
    }
  }
  
}
