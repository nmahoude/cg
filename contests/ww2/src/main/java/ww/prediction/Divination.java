package ww.prediction;

import ww.Agent;
import ww.Cell;
import ww.Dir;
import ww.GameState;
import ww.Grid;
import ww.Point;
import ww.sim.Move;

/**
 * Responsible of magical finding where the opponents are (hope so)
 * @author nmahoude
 *
 */
public class Divination {
  GameState initial;

  Move simulatedMove = new Move(null);
  GameState simulated;
  
  public Point guessedPosition[] = new Point[2];
  private Point backups[] = new Point[2];
  public boolean guessedPositionLocked[] = new boolean[2];
  private int stillToFind;
  private boolean debugMode;
  
  public Divination(GameState model) {
    simulated = new GameState();
    simulated.grid = new Grid(model.size);

    initial = new GameState();
    initial.grid = new Grid(model.size);
    
    guessedPosition[0] = Point.unknown;
    guessedPosition[1] = Point.unknown;
  }
  
  public void setDebug(boolean value) {
    debugMode = value;
  }
  /**
   * Entry : the new state
   * Divination keep the old state (after our move)
   * 
   * Warning : our move may be invaidated by the referee
   */
  
  public void guessFrom(GameState state) {
    stillToFind = 2;
    if (debugMode) {
      System.err.println("Divination : ");
      System.err.println("known agents positions");
      System.err.println("   0 "+guessedPosition[0]);
      System.err.println("   1 "+guessedPosition[1]);
    }

    backups[0] = guessedPosition[0];
    backups[1] = guessedPosition[1];
    
    getCorrectInformationsFromCurrentStateOrLockedAgents(state);
    if (stillToFind == 0) {
      // no guess this time, TODO still need to prepare something ?
      return;
    }
    
    // check if we have move in respect of the simulation
    checkPushes(state);
    
    long potential = getPotentialPositionsFromStaticGrid(state);
    if (debugMode) {
      System.err.println("static potential");
      state.grid.debugLayer(potential);
    }
    
    long temp = potential;
    temp = removeMyVision(state, potential);
    temp = removeOpponents(state, temp);
    guessFromPotentialOnly(temp);
    if (stillToFind == 0) return;
    
    temp = potential;
    temp = removeMyVision(state, potential);
    temp = readdOpponents(state, temp);
    guessFromConstruction(state, temp); 
    if (stillToFind == 0) return;
  }

  private void checkPushes(GameState state) {
    if (simulatedMove.agent != null) {
      // check if we have been pushed
      boolean haveBeenPushed = false;
      for (int i=0;i<2;i++) {
        if (state.agents[i].position != simulated.agents[i].position) {
          haveBeenPushed = true;
          if (debugMode) System.err.println("Agent "+i+" has been pushed");
          for (int other=0;other<2;other++) {
            if (state.agents[2+other].position == Point.unknown && simulated.agents[2+other].position != Point.unknown) {
              guessedPosition[other] = simulated.agents[2+other].position;
              stillToFind--;
            }
          }
        }
      }
    }
    
  }

  private void guessFromConstruction(GameState state, long gridMask) {
    Cell constructionCell = locateConstruction(state);
    if (constructionCell == Cell.InvalidCell) return;
    
    long wasThereMask = 0;
    long willBeThereMask = 0;
    
    wasThereMask|= constructionCell.position.mask;
    for (int xi=-2;xi<3;xi++) {
      for (int yi=-2;yi<3;yi++) {
        int cx = constructionCell.position.x+xi; 
        int cy=constructionCell.position.y+yi;
        if (cx >=0 && cx < state.grid.size && cy >=0 && cy<state.grid.size) {
          wasThereMask|= Point.get(cx,  cy).mask;
          if (xi!= -2 && xi != 2 && yi!=-2 && yi!=2) {
            willBeThereMask|= Point.get(cx,  cy).mask;
          }
        }
      }
    }
    willBeThereMask &= ~constructionCell.position.mask;
    
    // check if we know an agent that was not in the zone !
    for (int i=0;i<2;i++) {
      if (guessedPosition[i] != Point.unknown) continue; // already guessed
      if (backups[i] == Point.unknown) continue; // not known before
      if ((backups[i].mask & wasThereMask) == 0) {
        if (debugMode) {
          System.err.println("Agent "+(i+2) +" is not in the construction 'wasThere' zone, so he didn't move");
        }
        guessedPosition[i] = backups[i];
        stillToFind--;
      }
    }
    
    long potential = gridMask & willBeThereMask;
    if (debugMode) {
      System.err.println("guessFromConstruction");
      state.grid.debugLayer(potential);
    }
    if (Long.bitCount(potential) == 1 && stillToFind >= 1) {
      int y = (int)(Math.log(potential) / Math.log(256));
      int x = (int)(Math.log(potential) / Math.log(2)) - 8*y;
      // check that we don't already know the culprit
      if (guessedPosition[0] != Point.get(x, y) && guessedPosition[1] != Point.get(x, y)) {
        if (guessedPosition[0] == Point.unknown) {
          guessedPosition[0] = Point.get(x, y);
          if (debugMode) System.err.println("guessFromConstruction:: potentialCtor - Found 0 @ "+guessedPosition[0]);
          stillToFind--;
        } else {
          guessedPosition[1] = Point.get(x, y);
          if (debugMode) System.err.println("guessFromConstruction:: potentialCtor -Found 1 @ "+guessedPosition[1]);
          stillToFind--;
        }
      }
    }
    
  }

  Cell locateConstruction(GameState state) {
    for (int y=0;y<GameState.size;y++) {
      for (int x=0;x<GameState.size;x++) {
        Cell expectedCell = simulated.grid.get(x, y);
        Cell currentCell = state.grid.get(x,y);
        if (expectedCell.height != currentCell.height) {
          return currentCell;
        }
      }
    }
    return Cell.InvalidCell;
  }
  
  private void guessFromPotentialOnly(long potential) {
    if (Long.bitCount(potential) == 1 && stillToFind == 1) {
      int y = (int)(Math.log(potential) / Math.log(256));
      int x = (int)(Math.log(potential) / Math.log(2)) - 8*y;
      if (guessedPosition[0] == Point.unknown) {
        guessedPosition[0] = Point.get(x, y);
        if (debugMode) System.err.println("potential only found 0 @ "+guessedPosition[0]);
      } else {
        guessedPosition[1] = Point.get(x, y);
        if (debugMode) System.err.println("potential only found 1 @ "+guessedPosition[1]);
      }
      stillToFind = 0;
      return; 
    } else if (Long.bitCount(potential) == 2 && stillToFind == 2) {
      long first = Long.highestOneBit(potential);
      int y = (int)(Math.log(potential) / Math.log(256));
      int x = (int)(Math.log(potential) / Math.log(2)) - 8*y;
      guessedPosition[0] = Point.get(x, y);
      
      long second = potential & ~first;
      y = (int)(Math.log(second) / Math.log(256));
      x = (int)(Math.log(second) / Math.log(2)) - 8*y;
      guessedPosition[1] = Point.get(x, y);

      stillToFind = 0;
    }
  }

  /**
   * Apply the divination
   * @param state
   */
  public void apply(GameState state) {
    for (int i=0;i<2;i++) {
      if (guessedPosition[i] != Point.unknown) {
        if (debugMode) System.err.println("Applying guessed agent "+i+" at "+guessedPosition[i]);
        state.positionAgent(state.agents[2+i], guessedPosition[i]);
        guessedPositionLocked[i] = isLocked(state.agents[2+i]);
      }
    }
  }
  
  public void debug(GameState state) {
    for (int i=0;i<2;i++) {
      if (guessedPosition[i] != Point.unknown ) {
        if (state.agents[2+i].position == Point.unknown) {
          System.err.println("*GUESS* agent "+i+" at "+guessedPosition[i] + (guessedPositionLocked[i] ? "[Locked]" : ""));
        } else {
          System.err.println("known agent "+i+" at "+guessedPosition[i] + (guessedPositionLocked[i] ? "[Locked]" : ""));
        }
      }
    }
  }
  
  private void getCorrectInformationsFromCurrentStateOrLockedAgents(GameState state) {
    for (int i=0;i<2;i++) {
      if (state.agents[2+i].position != Point.unknown) {
        if (guessedPosition[1-i] == state.agents[2+i].position) {
          // we had inversed the agents, correct error now
          guessedPosition[1-i] = guessedPosition[i];
          guessedPositionLocked[1-i] = guessedPositionLocked[i];
        }
        guessedPosition[i] = state.agents[2+i].position; // save the point but we know it
        guessedPositionLocked[i] = isLocked(state.agents[2+i]);
        if (debugMode) System.err.println("CG Info : set "+i+" @ "+guessedPosition[i]);
        stillToFind--;
      } else {
        if (guessedPositionLocked[i] == true) {
          if (debugMode) System.err.println("CG Info & locked "+i+" @ "+guessedPosition[i]);
          stillToFind--;
        } else {
          guessedPosition[i] = Point.unknown;
        }
      }
    }
  }

  private boolean isLocked(Agent agent) {
    int height = agent.cell.height;
    for (int i=0;i<Dir.LENGTH;i++) {
      Cell cell = agent.cell.neighbors[i];
      if (cell.isValid() && cell.height <= height+1) return false; 
    }
    return true;
  }

  /**
   * Build a map of potential positions 
   * @param state
   * @return
   */
  private long getPotentialPositionsFromStaticGrid(GameState state) {
    long potential = state.grid.toBitMask();
    
    // remove holes & height = 4
    for (int y=0;y<GameState.size;y++) {
      for (int x=0;x<GameState.size;x++) {
        if (!state.grid.get(x, y).isValid()) {
          potential &= ~Point.get(x, y).mask; // remove the point
        }
      }
    }
    return potential;
  }

  private long removeMyVision(GameState state, long potential) {
    // remove my vision
    for (int id=0;id<2;id++) {
      Agent agent = state.agents[id];
      potential &= ~agent.position.mask; // remove the point

      for (int i=0;i<Dir.LENGTH;i++) {
        Cell around = agent.cell.neighbors[i];
        if (around != Cell.InvalidCell) {
          potential &= ~around.position.mask; // remove the point
        }
      }
    }
    return potential;
  }

  private long removeOpponents(GameState state, long potential) {
    for (int id=0;id<2;id++) {
      if (guessedPosition[id] != Point.unknown) {
        potential &= ~guessedPosition[id].mask; // reput the point
      }
    }
    return potential;
  }

  private long readdOpponents(GameState state, long potential) {
    // if we know one of the opponent, re-add him
    for (int id=0;id<2;id++) {
      if (guessedPosition[id] != Point.unknown) {
        potential |= guessedPosition[id].mask; // reput the point
      }
    }
    return potential;
  }
  
  /** update initial state as the one we got from CG*/
  public void updateInitialState(GameState state) {
    state.copyTo(initial);
  }

  /* update after our move */
  public void updateSimulated(GameState state, Move move) {
    move.copyTo(simulatedMove);
    state.copyTo(simulated);
    for (int i=0;i<2;i++) {
      guessedPosition[i] = state.agents[2+i].position;
      if (debugMode) System.err.println("Setting guessedPos for "+i+" at "+guessedPosition[i]+" in the simulated state");
    }
  }

  
}
