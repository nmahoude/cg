package tge.simulation;

import tge.Agent;
import tge.Point;
import tge.WallOrientation;

public class Action {
  
  public Agent agent; // the agent who makes the move
  public ActionType type = ActionType.MOVE;
  
  public int moveIndex; // 0 (right) to 3 (up)
  public Point position = Point.unknown;
  public WallOrientation orientation = WallOrientation.HORIZONTAL; // wall
  
  public void copyTo(Action toCopyTo) {
    toCopyTo.agent = agent;
    toCopyTo.type = type;
    toCopyTo.moveIndex = moveIndex;
    toCopyTo.position = position;
    toCopyTo.orientation = orientation;
  }

  public String toOutput() {
    if (type == ActionType.MOVE) {
      switch (moveIndex) {
        case 0 : return "RIGHT";
        case 1 : return "DOWN";
        case 2 : return "LEFT";
        case 3 : return "UP";
      }
    } else {
      return ""+position.x+" "+position.y+" "+(orientation == WallOrientation.HORIZONTAL ? "H" :"V");
    }
    return null;
  }
}
