package ww.sim;

import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing.Validation;

import ww.Agent;
import ww.Dir;
import ww.GameState;

public class Simulation {

  private static final int FINAL_HEIGHT = 4;
  private GameState state;
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
  public void simulate(Move move, GameState state) {
    this.move = move;
    this.state = state;
    this.agent = state.agents[move.id];

    int targetX = agent.x + move.dir1.dx;
    int targetY = agent.y + move.dir1.dy;

    if (!state.isValid(targetX, targetY)) {
      move.dir1Invalid();
      return;
    }
    
    int occupiedBy = state.occupiedBy(targetX, targetY);
    if (occupiedBy == -1) {
      move.isPush = false;
      computeMove();
      return;
    } else {
      // potential push
      if (state.isFriendly(move.id, targetX, targetY)) {
        move.dir1Invalid();
        return;
      }
      move.isPush = true;
      computePush();
      return;
    }
  }

  private void computeMove() {

    int targetX = agent.x + move.dir1.dx;
    int targetY = agent.y + move.dir1.dy;
    int targetHeight = state.getHeight(targetX, targetY);

    int currentHeight = state.getHeight(agent.x, agent.y);
    if ((targetHeight > currentHeight + 1)
       || (targetHeight >= FINAL_HEIGHT)
       || (state.isOccupied(agent.id, targetX, targetY))) {
      
      move.dir1Invalid();
      return;
    }

    int placeTargetX = targetX + move.dir2.dx;
    int placeTargetY = targetY + move.dir2.dy;
    
    if ((!state.isValid(placeTargetX, placeTargetY))
        || (state.isOccupied(agent.id, placeTargetX, placeTargetY))) {
      move.dir2Invalid();
      return;
    }
    
    int placeTargetHeight = state.getHeight(placeTargetX, placeTargetY);
    if (placeTargetHeight >= FINAL_HEIGHT) {
      move.dir2Invalid();
      return;
    }

    // all is ok, do the actual action
    agent.x = targetX;
    agent.y = targetY;

    state.setHeight(placeTargetX, placeTargetY, placeTargetHeight + 1);

    move.dir1X = targetX;
    move.dir1Y = targetY;
    move.dir1Height = targetHeight;
    
    move.dir2X = placeTargetX;
    move.dir2Y = placeTargetY;
    move.dir2Height = placeTargetHeight;
    
    if (targetHeight == FINAL_HEIGHT - 1) {
      agent.score++;
    }
    
    move.allValid();
    return;
  }

  private void computePush() {
    Dir[] validDirs = move.dir1.pushDirections();
    boolean validPushDirection = 
        (move.dir2 == validDirs[0])
        || (move.dir2 == validDirs[1])
        || (move.dir2 == validDirs[2]);

    if (!validPushDirection) {
      move.dir2Invalid(); // dir2 is invalid
      return;
    }
    
    int targetX = agent.x + move.dir1.dx;
    int targetY = agent.y + move.dir1.dy;
    Agent pushed = state.agents[state.occupiedBy(targetX, targetY)];

    int pushToX = targetX+move.dir2.dx;
    int pushToY = targetY+move.dir2.dy;
    
    if ((!state.isValid(pushToX, pushToY)) 
       || (state.isOccupied(move.id, pushToX, pushToY))) {
      move.dir2Invalid();
      return;
    }
    
    int pushToHeight = state.getHeight(pushToX, pushToY);
    int pushFromHeight = state.getHeight(targetX, targetY);

    if (pushToHeight >= FINAL_HEIGHT || pushToHeight > pushFromHeight + 1) {
      move.dir2Invalid();
      return;
    }

    // TODO check if we know a agent is at pushTo and would block the action ?
    
    pushed.x = pushToX;
    pushed.y = pushToY;

    move.dir1X = targetX;
    move.dir1Y = targetY;
    move.dir1Height = pushFromHeight;
    
    move.dir2X = pushToX;
    move.dir2Y = pushToY;
    move.dir2Height = pushToHeight;
    
    move.allValid();
    return;
  }

}
