package ww;

/**
 * Responsible of magical finding where the opponents are (hope so)
 * @author nmahoude
 *
 */
public class Divination {
  GameState initial;
  GameState expected;
  public Point guessedPosition[] = new Point[2];
  public boolean guessedPositionLocked[] = new boolean[2];
  private int stillToFind;
  private boolean debugMode;
  
  Divination(GameState model) {
    expected = new GameState();
    expected.grid = new Grid(model.size);

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
    if (debugMode) System.err.println("Divination : ");

    getCorrectInformations(state);
    if (stillToFind == 0) {
      // no guess this time, TODO still need to prepare something ?
      return;
    }
    
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
    wasThereMask &= ~constructionCell.position.mask;
    
    long potential = gridMask & willBeThereMask;
    if (debugMode) {
      System.err.println("guessFromConstruction");
      state.grid.debugLayer(potential);
    }
    if (Long.bitCount(potential) == 1 && stillToFind >= 1) {
      int y = (int)(Math.log(potential) / Math.log(256));
      int x = (int)(Math.log(potential) / Math.log(2)) - 8*y;
      if (guessedPosition[0] == Point.unknown) {
        guessedPosition[0] = Point.get(x, y);
      } else {
        guessedPosition[1] = Point.get(x, y);
      }
      stillToFind--;
      return; 
    }
  }

  Cell locateConstruction(GameState state) {
    for (int y=0;y<GameState.size;y++) {
      for (int x=0;x<GameState.size;x++) {
        Cell expectedCell = expected.grid.get(x, y);
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
      } else {
        guessedPosition[1] = Point.get(x, y);
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
      }
    }
  }
  
  public void debug() {
    for (int i=0;i<2;i++) {
      if (guessedPosition[i] != Point.unknown) {
        if (debugMode) System.err.println("Found agent "+i+" at "+guessedPosition[i]);
      }
    }
  }
  
  private void getCorrectInformations(GameState state) {
    for (int i=0;i<2;i++) {
      if (state.agents[2+i].position != Point.unknown) {
        guessedPosition[i] = state.agents[2+i].position; // save the point but we know it
        guessedPositionLocked[i] = isLocked(state.agents[2+i]);
        stillToFind--;
      } else {
        if (guessedPositionLocked[i] == true) {
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
  public void updatePrediction(GameState state) {
    state.copyTo(expected);
  }

  
}
