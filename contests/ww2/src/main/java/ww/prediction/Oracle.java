package ww.prediction;

import ww.Agent;
import ww.Cell;
import ww.Dir;
import ww.GameState;
import ww.Grid;
import ww.Point;
import ww.sim.Move;

public class Oracle {
  GameState initial;

  Move simulatedMove = new Move(null);
  GameState simulated;
  
  public Point guessedPosition[] = new Point[2];
  private Point backupPosition[] = new Point[2];
  public boolean guessedPositionLocked[] = new boolean[2];
  private boolean debugMode;
  
  public Oracle(GameState model) {
    simulated = new GameState();
    simulated.grid = new Grid(model.size);

    initial = new GameState();
    initial.grid = new Grid(model.size);
    
    guessedPosition[0] = Point.unknown;
    guessedPosition[1] = Point.unknown;
  }
  
  public void guessFrom(GameState currentState) {
    for (int i=0;i<2;i++) {
      backupPosition[i] = guessedPosition[i];
      if (guessedPosition[i] != Point.unknown && guessedPositionLocked[i]) {
      } else {
        guessedPosition[i] = Point.unknown;
      }
    }

    for (int i=0;i<2;i++) {
      if (currentState.agents[2+i].position != Point.unknown) {
          guessedPosition[i] = currentState.agents[2+i].position;
          guessedPositionLocked[i] = isLocked(currentState.agents[2+i]);
      }
    }
    if (stillToFind() == 0) {
      return; // nothing more to guess
    }

    checkForPushed(currentState);
    
    // check if we see an agent and we can tell HE has move, so the other one didn't move
    for (int i=0;i<2;i++) {
      if (simulated.agents[2+i].position != Point.unknown && 
          guessedPosition[i] != Point.unknown 
          && (simulated.agents[2+i].position != guessedPosition[i])) {
        // A2 has move but we know him, but A3 has not move
        guessedPosition[1-i] = backupPosition[1-1];
      }
    }

    if (stillToFind() == 0) {
      return; // nothing more to guess
    }
    
    
    
  }

  void checkForPushed(GameState currentState) {
    // 1. check for pushed
    for (int myAgentId=0;myAgentId<2;myAgentId++) {
      if (simulated.agents[myAgentId].position != currentState.agents[myAgentId].position) {
        // pushed ! by who ?
        Cell in = currentState.agents[myAgentId].cell;
        Cell from = currentState.grid.get(simulated.agents[myAgentId].position);
        Dir dir = in.dirTo(from);
        Cell onlyOne = null;
        int potentialCount = 0;
        for (Dir potentialDir : dir.pushDirections()) {
          Cell potential = from.get(potentialDir);
          if (!potential.isValid()) continue;
          if (potential.agent == null && canSeeCell(currentState.agents[theOtherId(myAgentId)], potential)) continue;
          
          potentialCount++;
          onlyOne = potential;
          Cell simulatedPotential = simulated.grid.get(potential.position);
          if (potential.isThreat(currentState.agents[myAgentId]) 
              || simulatedPotential.isThreat(currentState.agents[myAgentId])) {
            noMoveFromEnemy(currentState, 0);
            noMoveFromEnemy(currentState, 1);
            return;
          }
        }
        if (potentialCount == 1) {
          foundPosition(onlyOne.position);
          return;
        }
      }
    }
  }
  
  private boolean canSeeCell(Agent agent, Cell potential) {
    return agent.cell.dirTo(potential) != null;
  }

  private void foundPosition(Point position) {
    if (guessedPosition[0] == position || guessedPosition[1] == position) {
      return;
    } else {
      if (guessedPosition[0] == Point.unknown)  {
        guessedPosition[0] = position;
      } else {
        guessedPosition[1] = position;
      }
    }
  }

  private void noMoveFromEnemy(GameState currentState, int id) {
    if (guessedPosition[id] != Point.unknown) return;
    
    if (currentState.agents[2+id].position != Point.unknown) {
      guessedPosition[id] = currentState.agents[2+id].position;
    } else if (backupPosition[id] != Point.unknown) {
      guessedPosition[id] = backupPosition[id];
    }
    
  }

  private int theOtherId(int id) {
    switch(id) {
    case 0 : return 1;
    case 1 : return 0;
    case 2 : return 3;
    case 3 : return 2;
    }
    return -1;
  }

  private Point theBestOf(Agent simulatedAgent, Agent currentAgent) {
    if (currentAgent.position != Point.unknown) {
      return currentAgent.position;
    }
    return simulatedAgent.position;
  }

  private int stillToFind() {
    return 
          (guessedPosition[0] == Point.unknown ? 1 : 0)
        + (guessedPosition[1] == Point.unknown ? 1 : 0);
  }

  /**
   * Apply the Oracle
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
  
  private boolean isLocked(Agent agent) {
    int height = agent.cell.height;
    for (int i=0;i<Dir.LENGTH;i++) {
      Cell cell = agent.cell.neighbors[i];
      if (cell.isValid() && cell.height <= height+1) return false; 
    }
    return true;
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
