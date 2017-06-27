package ww;

import com.sun.org.apache.bcel.internal.util.SyntheticRepository;

/**
 * Responsible of magical finding where the opponents are (hope so)
 * @author nmahoude
 *
 */
public class Divination {
  GameState initial;
  GameState expected;
  
  Point knownAgentPos[] = new Point[2];
  Point expectedAgent[] = new Point[2];
  {
    expectedAgent[0] = Point.unknown;
    expectedAgent[1] = Point.unknown;
  }
  
  Divination(GameState model) {
    expected = new GameState();
    expected.grid = new Grid(model.size);

    initial = new GameState();
    initial.grid = new Grid(model.size);
    
  }
  
  /**
   * Entry : the new state
   * Divination keep the old state (after our move)
   * 
   * Warning : our move may be invaidated by the referee
   */
  
  public void guessFrom(GameState state) {
    System.err.println("Divination : ");

    boolean allFound = getCorrectInformations(state);
    if (allFound == true) {
      // no guess this time, TODO still need to prepare something ?
      return;
    }
    
    // do the divination here
    long potential = getPotentialPositionsFromStaticGrid(state);
    Grid.debugLayer(potential);
    
    long wasThereMask = 0;
    long willBeThereMask = 0;
    // 1. find the constructed cell
    for (int y=0;y<GameState.size;y++) {
      for (int x=0;x<GameState.size;x++) {
        Cell expectedCell = expected.grid.get(x, y);
        Cell currentCell = state.grid.get(x,y);
        if (expectedCell.height != currentCell.height) {
          System.err.println("Construction on "+Point.get(x, y));
          
          wasThereMask|= currentCell.position.mask;
          for (int xi=-2;xi<3;xi++) {
            for (int yi=-2;yi<3;yi++) {
              int cx = x+xi; int cy=y+yi;
              if (cx >=0 && cx < Grid.size && cy >=0 && cy<Grid.size) {
                wasThereMask|= Point.get(cx,  cy).mask;
                if (xi!= -2 && xi != 2 && yi!=-2 && yi!=2) {
                  willBeThereMask|= Point.get(cx,  cy).mask;
                }
              }
            }
          }
        }
      }
    }
    
    Grid.debugLayer(wasThereMask);
    // check agent 0 :
    if (knownAgentPos[0] == Point.unknown) {
      if (expectedAgent[0] != Point.unknown) {
        if ((expectedAgent[0].mask & wasThereMask) != 0)  {
          // ok were there so there is a chance it's us
          if (knownAgentPos[1] != Point.unknown && (knownAgentPos[1].mask & wasThereMask) == 0) {
            // the other one could not have been here, so it's us :)
          }
        }
      }
    }
    
  }

  private boolean getCorrectInformations(GameState state) {
    boolean allFound = true;
    for (int i=0;i<2;i++) {
      if (state.agents[2+i].position != Point.unknown) {
        // found it :)
        System.err.println("   oppAgent "+i+" is at "+state.agents[2+i].position);
        knownAgentPos[i] = state.agents[2+i].position; // save the point but we know it
      } else {
        knownAgentPos[i] = Point.unknown;
        allFound = false;
      }
    }
    return allFound;
  }

  /**
   * Build a map of potential positions 
   * @param state
   * @return
   */
  private long getPotentialPositionsFromStaticGrid(GameState state) {
    long potential = Grid.toBitMask();
    
    // remove holes & height = 4
    for (int y=0;y<GameState.size;y++) {
      for (int x=0;x<GameState.size;x++) {
        if (!state.grid.get(x, y).isValid()) {
          potential &= ~Point.get(x, y).mask; // remove the point
        }
      }
    }
    // remove my vision
    for (int id=0;id<2;id++) {
      Agent agent = state.agents[id];
      potential &= ~agent.position.mask; // remove the point

      for (Dir dir : Dir.values()) {
        Cell around = agent.cell.get(dir);
        if (around != Cell.InvalidCell) {
          potential &= ~around.position.mask; // remove the point
        }
      }
    }
    // if we know one of the opponent, remove him too
    for (int id=2;id<4;id++) {
      Agent agent = state.agents[id];
      if (!agent.inFogOfWar()) {
        potential &= ~agent.position.mask; // remove the point
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
