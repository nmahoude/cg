package ww.sim;

import ww.Agent;
import ww.Cell;
import ww.Dir;
import ww.GameState;

public class Simulation {

  private Move move;
  private Agent agent;
  private GameState state;

  
  public Simulation(GameState state) {
    this.state = state;
  }
  /**
   * simulate the move of any player (dir1, dir2 not null)
   * 
   * Pre-requisite : the move is valid only one player will move
   * 
   * GameState is backedup
   * 
   * @return true if the move is valid
   */
  public void simulate(Move move, long[] transposition) {
    this.move = move;
    this.agent = move.agent;

    Cell target = agent.cell.get(move.dir1);
    if (!target.isValid()) {
      move.dir1Invalid();
      return;
    }
    
    if (!target.isOccupied()) {
      computeMove(state, transposition);
    } else {
      computePush(state, transposition);
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
      if (move.agent.cell.height == Cell.FINAL_HEIGHT - 1) {
        move.agent.score--;
      }
      Cell comingFrom = move.agent.cell.get(move.dir1.inverse());
      Cell builtOn = move.agent.cell.get(move.dir2);
      builtOn.decrease();
      move.agent.moveTo(comingFrom);
    }
  }
  
  private void computeMove(GameState state, long[] transposition) {
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
    agent.moveTo(target);
    
    if (target.height == Cell.FINAL_HEIGHT - 1) {
      agent.score++;
    }
    placeTarget.elevate();

    updateTransposition(state, transposition, placeTarget.height, placeTarget.position.mask);
    
    move.allValid();
    return;
  }


  private void updateTransposition(GameState state, long[] transposition, int newHeight, long mask) {
    if ((newHeight & 0b100) != 0) {
      transposition[2] |= mask;
    } else {
      transposition[2] &= ~mask;
    }
    if ((newHeight & 0b010) != 0) {
      transposition[1] |= mask;
    } else {
      transposition[1] &= ~mask;
    }
    if ((newHeight & 0b001) != 0) {
      transposition[0] |= mask;
    } else {
      transposition[0] &= ~mask;
    }
    transposition[3] = 0; // pos
    transposition[3] |= state.agents[0].position.mask;
    transposition[3] |= state.agents[1].position.mask;
    if (!state.agents[2].inFogOfWar()) { transposition[4] |= state.agents[2].position.mask; }
    if (!state.agents[3].inFogOfWar()) { transposition[4] |= state.agents[3].position.mask; }
  }

  private void computePush(GameState state, long[] transposition) {
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
    pushed.pushTo(pushTo);
    pushFrom.elevate();
    
    updateTransposition(state, transposition, pushFrom.height, pushFrom.position.mask);

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


  public void simulate(Move move) {
    long transpo[] = new long[6];
    simulate(move, transpo);
  }

}
