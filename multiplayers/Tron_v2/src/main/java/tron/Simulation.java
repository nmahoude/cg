package tron;

import tron.common.Cell;

public class Simulation {

  public static boolean play(Action action) {
    Cell current = action.agent.currentCell;
    Cell next = current.neighbors[action.moveIndex];
    if (next.owner == -1) {
      next.owner = action.agent.id;
      action.agent.currentCell = next;
      return true;
    }
    return false;
  }
  
  public static void unplay(Action action) {
    int oppMoveIndex = getOppositeMoveIndex(action);
    Cell current = action.agent.currentCell;
    Cell previous = current.neighbors[oppMoveIndex];
    current.owner = -1;
    action.agent.currentCell = previous;
  }

  private static int getOppositeMoveIndex(Action action) {
    int moveIndex = action.moveIndex+2;
    if (moveIndex > 3) {
      moveIndex-=4;
    }
    return moveIndex;
  }
}
