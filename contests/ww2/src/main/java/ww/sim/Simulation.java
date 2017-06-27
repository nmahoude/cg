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
      move.isPush = false;
      computeMove(doAction);
      return;
    } else {
      // potential push
      if (target.isFriendly(agent)) {
        move.dir1Invalid();
        return;
      }
      move.isPush = true;
      computePush(doAction);
      return;
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
    if (doAction) {
      agent.moveTo(target);
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
    if (doAction) {
      pushed.pushTo(pushTo);
      pushFrom.elevate();
    }
    
    move.allValid();
    return;
  }

  public static int getPossibleActionsCount(GameState state, Agent agent) {
    Simulation sim = new Simulation();
    int count = 0;
    
    Move move = new Move(agent);
    for (Dir dir1 : Dir.values()) {
      for (Dir dir2 : Dir.values()) {
        move.dir1 = dir1;
        move.dir2 = dir2;
        sim.simulate(move, false);
        if (move.isValid()) {
          count++;
        }
      }
    }
    return count;
  }

}
