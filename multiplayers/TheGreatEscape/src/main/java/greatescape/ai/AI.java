package greatescape.ai;

import java.util.List;

import greatescape.AStar;
import greatescape.Agent;
import greatescape.Cell;
import greatescape.GameState;
import greatescape.PathItem;
import greatescape.Player;
import greatescape.WallOrientation;

/** Responsible to study all actions and find the best one */
public class AI {
  /*
   * Minimize our length while maximizing at least the nextOpponent length
   */
  public void evaluate(GameState state) {
    Cell currentCell = state.getMyCell();

    // move with an adverserial Wall
    int shortestPathWithAdverserialWall = Integer.MAX_VALUE;
    Cell directionWithAdverserialWall = null;
    for (int i=0;i<4;i++) {
      if (currentCell.canGoDir(i)) {
        int length = evaluateMoveWithAdverserialWall(state, currentCell.goDir(i));
        if (length < shortestPathWithAdverserialWall) {
          shortestPathWithAdverserialWall = length;
          directionWithAdverserialWall = currentCell.goDir(i);
        }
      }
    }
    
    // WE put a wall
    MinimalPath info;
    if (state.me.wallsLeft > 0) {
      info = putAWall(state);
    } else {
      info = new MinimalPath();
    }
    
    if (info.minimalPath > shortestPathWithAdverserialWall) {
      System.out.println(""+info.x +" "+info.y+" "+info.wo.toString());
    } else {
      System.out.println(""+Player.getMoveFromCells(currentCell, directionWithAdverserialWall));
    }
  }

  /**
   * Put a wall any where and check everybody length
   * @param state
   */
  private MinimalPath putAWall(GameState state) {
    MinimalPath info = new MinimalPath();
    
    for (int x=0;x<Player.W-1;x++) {
      for (int y=0;y<Player.H-1;y++) {
        for (WallOrientation wo : WallOrientation.values()) {
          if (x == 0 && wo == WallOrientation.V) continue;
          if (y == 0 && wo == WallOrientation.H) continue;
          
          if (!state.board.addWall(state.wallCount+1,x, y, wo)) {
            continue;
          }
          Agent opponent = state.agents[state.directOpponentId];
          List<PathItem> trajectory = new AStar(state.board, state.board.getCell(opponent.position), opponent.goal).find();
          if (!trajectory.isEmpty() && trajectory.size() < info.minimalPath) {
            info.minimalPath = trajectory.size();
            info.x = x;
            info.y = y;
            info.wo = wo;
            info.path = trajectory;
          }

          state.board.removeWall(x, y, wo);
        }
      }
    }
    return info;
  }

  // TODO optimize: all walls are clearly too much work
  private int evaluateMoveWithAdverserialWall(GameState state, Cell currentCell) {
    int longestPath = 0;
    if (state.agents[state.directOpponentId].wallsLeft == 0)  {
      // TODO only calculate the direct path (no wall) . Or consider another opponent ?
      List<PathItem> trajectory = new AStar(state.board, currentCell, state.me.goal).find();
      return trajectory.size();
    }
    for (int x=0;x<Player.W-1;x++) {
      for (int y=0;y<Player.H-1;y++) {
        for (WallOrientation wo : WallOrientation.values()) {
          if (x == 0 && wo == WallOrientation.V) continue;
          if (y == 0 && wo == WallOrientation.H) continue;
          
          if (!state.board.addWall(state.wallCount+1,x, y, wo)) {
            continue;
          }
          List<PathItem> trajectory = new AStar(state.board, currentCell, state.me.goal).find();
          if (!trajectory.isEmpty() && trajectory.size() > longestPath) {
            longestPath = trajectory.size();
          }
          state.board.removeWall(x, y, wo);
        }
      }
    }
    return longestPath;
  }
}
