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
  
  public String output;
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
        case 0 : return "RIGHT "+output;
        case 1 : return "DOWN "+output;
        case 2 : return "LEFT "+output;
        case 3 : return "UP "+output;
      }
    } else {
      return ""+position.x+" "+position.y+" "+(orientation == WallOrientation.HORIZONTAL ? "H" :"V") + " "+output;
    }
    return null;
  }
}
