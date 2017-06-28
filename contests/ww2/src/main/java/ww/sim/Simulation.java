package ww.sim;

import ww.Agent;
import ww.Cell;
import ww.Dir;
import ww.GameState;

public class Simulation {

  private Move move;
  private Agent agent;

  
  /**
   * simulate the move of any player (dir1, dir2 not null)
   * 
   * Pre-requisite : the move is valid only one player will move
   * 
   * GameState is backedup
   * 
   * @return true if the move is valid
   */
  public void simulate(Move move, boolean doAction) {
    this.move = move;
    this.agent = move.agent;

    Cell target = agent.cell.get(move.dir1);
    if (!target.isValid()) {
      move.dir1Invalid();
      return;
    }
    
    if (!target.isOccupied()) {
      computeMove(doAction);
    } else {
      computePush(doAction);
    }
  }

  
  /** undo a move, move should be valid ! */
  public void undo(Move move) {
    if (move.isPush) {
      Cell pushFrom = move.agent.cell.get(move.dir1);
      Cell pushTo = pushFrom.get(move.dir2);
      pushFrom.decrease();
      pushTo.agent.moveTo(pushFrom);
    } else {
      if (move.agent.cell.height == Cell.FINAL_HEIGHT - 1) move.agent.score--;
      Cell comingFrom = move.agent.cell.get(move.dir1.inverse());
      Cell builtOn = move.agent.cell.get(move.dir2);
      builtOn.decrease();
      move.agent.moveTo(comingFrom);
    }
  }
  
  private void computeMove(boolean doAction) {
    Cell target = agent.cell.get(move.dir1);

    int currentHeight = agent.cell.height;
    if ((target.height > currentHeight + 1)
       || (target.height >= Cell.FINAL_HEIGHT)) {
      move.dir1Invalid();
      return;
    }

    Cell placeTarget = target.get(move.dir2);
    
    if ((!placeTarget.isValid()) || placeTarget.isOccupiedButNotBy(agent)) {
      move.dir2Invalid();
      return;
    }
    
    if (placeTarget.height >= Cell.FINAL_HEIGHT) {
      move.dir2Invalid();
      return;
    }

    // move ok, update agent position & grid
    move.isPush = false;
    if (doAction) {
      agent.moveTo(target);
      if (target.height == Cell.FINAL_HEIGHT - 1) {
        agent.score++;
      }
      placeTarget.elevate();
    }
    move.allValid();
    return;
  }

  private void computePush(boolean doAction) {
    Dir[] validDirs = move.dir1.pushDirections();
    boolean validPushDirection = 
        (move.dir2 == validDirs[0])
        || (move.dir2 == validDirs[1])
        || (move.dir2 == validDirs[2]);

    if (!validPushDirection) {
      move.dir2Invalid(); // dir2 is invalid
      return;
    }
    
    Cell pushFrom = agent.cell.get(move.dir1);
    Agent pushed = pushFrom.agent;
    if (pushed.isFriendly(agent)) {
      move.dir1Invalid();
      return;
    }

    Cell pushTo = pushFrom.get(move.dir2);
    
    

    if ((!pushTo.isValid()) || (pushTo.isOccupied())) {
      move.dir2Invalid();
      return;
    }
    
    if (pushTo.height >= Cell.FINAL_HEIGHT || pushTo.height > pushFrom.height + 1) {
      move.dir2Invalid();
      return;
    }

    // move ok, update agent position & grid
    move.isPush = true;
    if (doAction) {
      pushed.pushTo(pushTo);
      pushFrom.elevate();
    }
    
    move.allValid();
    return;
  }

  public static int getPossibleActionsCount(GameState state, Agent agent) {
    int count = 0;
    
    for (Dir dir1 : Dir.getValues()) {
      Cell dir1Cell = agent.cell.get(dir1);
      if (!dir1Cell.isValid()) continue;
      if (dir1Cell.isFriendly(agent)) continue;
      if (dir1Cell.agent != null) {
        // push
        for (Dir dir2 : dir1.pushDirections()) {
          Cell pushCell = dir1Cell.get(dir2);
          if (pushCell.isValid() && !pushCell.isOccupied()) {
            count++;
          }
        }
      } else {
        // move
        for (Dir dir2 : Dir.getValues()) {
          Cell buildCell = dir1Cell.get(dir2);
          if (!buildCell.isValid()) continue;
          if (buildCell.isOccupiedButNotBy(agent)) continue;
          count++;
        }
      }
    }
    return count;
  }

}
