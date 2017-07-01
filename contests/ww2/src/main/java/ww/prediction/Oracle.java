package ww.prediction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ww.Agent;
import ww.Cell;
import ww.Dir;
import ww.GameState;
import ww.Grid;
import ww.Point;
import ww.sim.Move;
import ww.sim.Simulation;

public class Oracle {
  Simulation simulatedSimulation;
  GameState initialPreviousState;

  Move formerSimulatedMove = new Move(null);
  GameState formerSimulatedState;

  HashSet<Point> formerPossiblePositions[] = (HashSet<Point>[])new HashSet[4];
  HashSet<Point> possiblePositions[] = (HashSet<Point>[])new HashSet[4];
  
  private boolean debugMode;

  public Oracle(GameState model) {
    
    formerSimulatedState = new GameState();
    formerSimulatedState.grid = new Grid(model.size);

    initialPreviousState = new GameState();
    initialPreviousState.grid = new Grid(model.size);

    for (int i=2;i<4;i++) {
      possiblePositions[i] = new HashSet<>();
      formerPossiblePositions[i] = new HashSet<>();
    }
    
    if (model.grid != null) {
      for (int x=0;x<model.size;x++) {
        for (int y=0;y<model.size;y++) {
          Cell cell = model.grid.get(x,y);
          if (!cell.isValid()) continue;
          if (canSeeCell(model.agents[0], cell)) continue;
          if (canSeeCell(model.agents[1], cell)) continue;
          possiblePositions[2].add(cell.position);
          possiblePositions[3].add(cell.position);
        }
      }
      for (int i=2;i<4;i++) {
        if (!model.agents[i].inFogOfWar()) {
          possiblePositions[i].clear();
          possiblePositions[i].add(model.agents[i].position);
        }
      }
    }
  }
  
  Cell locateConstruction(GameState state) {
    for (int y=0;y<GameState.size;y++) {
      for (int x=0;x<GameState.size;x++) {
        Cell expectedCell = formerSimulatedState.grid.get(x, y);
        Cell currentCell = state.grid.get(x,y);
        if (expectedCell.height != currentCell.height) {
          return currentCell;
        }
      }
    }
    return Cell.InvalidCell;
  }
  
  public void guessFrom(GameState currentState) {
    if (debugMode) {
      System.err.println("Former possible positions for 2: ");
      System.err.println(formerPossiblePositions[2]);
      System.err.println("Former possible positions for 3: ");
      System.err.println(formerPossiblePositions[3]);
    }
    possiblePositions[2].clear();
    possiblePositions[3].clear();

    if (formerSimulatedMove.isPush && checkForCancellPush(currentState)) {
      revertOwnPush();
    }
    
    int id = whoHasBeenPushed(currentState);
    if (id != -1) {
      if (debugMode) System.err.println("Opp action : push");
      checkForPushed(id, currentState);
    } else {
      // here, we have not been pushed, check for move
      Cell locateConstruction = locateConstruction(currentState);
      if (locateConstruction != Cell.InvalidCell) {
        if (debugMode) System.err.println("Opp action : move");
        checkForMove(currentState, locateConstruction);
      } else {
        // invalidated Move, try to know who move ..
        if (hasMoved(currentState, 2) || hasBeenStill(currentState, 3)) {
          possiblePositions[2].addAll(getOneMoveAnyDirectionList(currentState, formerPossiblePositions[2]));
          possiblePositions[3].addAll(formerPossiblePositions[3]);
        } else if (hasMoved(currentState, 3) || hasBeenStill(currentState, 2)) {
          possiblePositions[2].addAll(formerPossiblePositions[2]);
          possiblePositions[3].addAll(getOneMoveAnyDirectionList(currentState, formerPossiblePositions[3]));
        } else {
          // we don't know
          possiblePositions[2].addAll(getOneMoveAnyDirectionList(currentState, formerPossiblePositions[2]));
          possiblePositions[3].addAll(getOneMoveAnyDirectionList(currentState, formerPossiblePositions[3]));
        }
      }
    }
    Set<Point> temp;
    for (int i=2;i<4;i++) {
      temp = filterImpossiblePositions(i, currentState, possiblePositions[i]);
      possiblePositions[i].clear();
      possiblePositions[i].addAll(temp);
    }
  }

  private Set<Point> getOneMoveAnyDirectionList(GameState currentState, HashSet<Point> formerPoints) {
    Set<Point> points = new HashSet<>();
    for (Point p : formerPoints) {
      Cell cell = currentState.grid.get(p);
      for (int i=0;i<8;i++) {
        Cell newCell = cell.neighbors[i];
        if (cell.isValid()) {
          points.add(newCell.position);
        }
      }
    }
    return points;
  }

  private void checkForMove(GameState currentState, Cell locatedConstruction) {
    // TODO be more precise, bruteforce possible moves !
    HashSet<Point> possibilitiesByAgent[] = (HashSet<Point>[])new HashSet[4];
    possibilitiesByAgent[2] = inRadius(currentState, 2, formerPossiblePositions[2], locatedConstruction.position);
    possibilitiesByAgent[3] = inRadius(currentState, 2, formerPossiblePositions[3], locatedConstruction.position);

    // if agents was seen and is still seen and didn't move, it's cant be him
    // TODO handle the case of cancelled move
    for (int id= 2;id<4;id++) {
      if (hasBeenStill(currentState, id)) {
        possibilitiesByAgent[id].clear();
      }
      if (hasMoved(currentState, id)) {
        possibilitiesByAgent[theOtherId(id)].clear();
      }
    }

    int count = (possibilitiesByAgent[2].size() != 0 ? 1 : 0)
              + (possibilitiesByAgent[3].size() != 0 ? 1 : 0);
  
    if (count == 0) {
      throw new RuntimeException("Nobody in the range to make the move !");
    } else if (count == 1) {
      int id = possibilitiesByAgent[2].size() > 0 ? 2 : 3;
      if (!currentState.agents[id].inFogOfWar()) {
        possiblePositions[id].add(currentState.agents[id].position);
      } else {
        // now we know who, we need to infer it's new position
        // TODO brute force the possibilities will be better than checking distance !
        for (int i=0;i<8;i++) {
          Cell cellToCheck = locatedConstruction.neighbors[i];
          if (!cellToCheck.isValid()) continue;
          if (canSeeCell(currentState.agents[0], cellToCheck)) continue;
          if (canSeeCell(currentState.agents[1], cellToCheck)) continue;
          
          boolean foundOneInRange = false;
          for (Point p : possibilitiesByAgent[id]) {
            if (p.inRange(1, cellToCheck.position)) {
              foundOneInRange = true;
              break;
            }
          }
          if (foundOneInRange) {
            possiblePositions[id].add(cellToCheck.position);
          }
        }
      }
      possiblePositions[theOtherId(id)].addAll(formerPossiblePositions[theOtherId(id)]);
    } else {
      // on ne sait pas qui a fait quoi, donc on doit ajouter toutes les cellules possibles aux 2 agents :(
      possiblePositions[2].addAll(formerPossiblePositions[2]);
      possiblePositions[3].addAll(formerPossiblePositions[3]);
      for (int i=0;i<8;i++) {
        Cell cellToCheck = locatedConstruction.neighbors[i];
        if (!cellToCheck.isValid()) continue;
        if (canSeeCell(currentState.agents[0], cellToCheck)) continue;
        if (canSeeCell(currentState.agents[1], cellToCheck)) continue;
        possiblePositions[2].add(cellToCheck.position);
        possiblePositions[3].add(cellToCheck.position);
      }
    }
  }

  private boolean hasMoved(GameState currentState, int id) {
    return formerSimulatedState.agents[id].position != Point.unknown && currentState.agents[id].position != Point.unknown && formerSimulatedState.agents[id].position != currentState.agents[id].position;
  }

  private boolean hasBeenStill(GameState currentState, int id) {
    return formerSimulatedState.agents[id].position != Point.unknown && formerSimulatedState.agents[id].position == currentState.agents[id].position;
  }

  private HashSet<Point> inRadius(GameState currentState, int radius, HashSet<Point> positions, Point target) {
    HashSet<Point> points = new HashSet<>();
    for (Point p : positions) {
      if (p.inRange(radius, target)) {
        points.add(p);
      }
    }
    return points;
  }

  private void revertOwnPush() {
    // revert our push
    Agent pushedAgent = formerSimulatedMove.agent.cell.get(formerSimulatedMove.dir1).get(formerSimulatedMove.dir2).agent;
    simulatedSimulation.undo(formerSimulatedMove); // our move has been invalidated
    formerPossiblePositions[pushedAgent.id].clear();
    formerPossiblePositions[pushedAgent.id].add(pushedAgent.cell.position);
  }
  
  /** find who has been pushed (if true) 
   * 
   * Note : if the move was cancelled, it is already undone
   * 
   */
  private int whoHasBeenPushed(GameState currentState) {
    for (int myAgentId=0;myAgentId<2;myAgentId++) {
      if (formerSimulatedState.agents[myAgentId].position != currentState.agents[myAgentId].position) {
        return myAgentId;
      }
    }
    return -1; // nobody pushed
  }
  
    private boolean checkForCancellPush(GameState currentState) {
    int id = formerSimulatedMove.agent.id;
    Cell simulatedPushedFrom = formerSimulatedMove.agent.cell.get(formerSimulatedMove.dir1);
    Cell currentPushedFrom = currentState.grid.get(currentState.agents[id].cell.get(formerSimulatedMove.dir1).position);
    if (simulatedPushedFrom.height > currentPushedFrom.height) {
      return true;
    }
    return false;
  }

  /**
   *  as there was a push, there is no move, all opp agents are at the same place
   */
  void checkForPushed(int myAgentId, GameState currentState) {
    // pushed ! by who ?
    Cell in = currentState.agents[myAgentId].cell;
    Cell from = currentState.grid.get(formerSimulatedState.agents[myAgentId].position);
    Dir dir = in.dirTo(from);
    
    Set<Point> pushFilter = new HashSet<>();
    
    for (Dir pushedDir: dir.pushDirections()) {
      Cell origin = from.get(pushedDir);
      if (!formerSimulatedState.grid.get(origin.position).isValid()) continue;
      // watch for this condition : the inverse is not true : if we saw an entity, it doesn't mean it did it
      if (canSeeCell(formerSimulatedState.agents[theOtherId(myAgentId)], origin) && formerSimulatedState.grid.get(origin.position).agent == null) continue;
      
      pushFilter.add(origin.position);
    }
    
    // here pushFilter contains only cell where the push can originate
    if (pushFilter.size() == 0) {
      throw new RuntimeException("Cant find the origin of the push ? ");
    }
    boolean agent2InFilter = isAgentInFilter(currentState, pushFilter, 2);
    boolean agent3InFilter = isAgentInFilter(currentState, pushFilter, 3);
    int count = (agent2InFilter ? 1: 0) +( agent3InFilter ? 1:0);
    if (count ==0) {
      throw new RuntimeException("Nobody can be in the origin of the push ? ");
    } else if (count == 1) {
      // only on agent can be here !
      int id = agent2InFilter ? 2 : 3;
      possiblePositions[id].addAll(getFilteredPositions(currentState, formerPossiblePositions[id], pushFilter));
      possiblePositions[theOtherId(id)].addAll(formerPossiblePositions[theOtherId(id)]);
    } else if (count == 2) {
      // we don't know :(
      // one of them was in the pushFilter, but which one has move ?
      // if there is only one place, check if an agent has only one move possible ! 
      // if not, choose the one with the least moves
      if (pushFilter.size() == 1) {
        // ok, we d'ont know which one, but we are sure one was here ! 
        //TODO infer further ?
        int id;
        if (formerPossiblePositions[2].size() == 1) {
          id =2;
        } else if (formerPossiblePositions[3].size() == 1) {
          id = 3;
        } else {
          id = formerPossiblePositions[3].size() >= formerPossiblePositions[2].size() ? 2 : 3;
        }
        
        possiblePositions[id].addAll(pushFilter);
        possiblePositions[theOtherId(id)].addAll(formerPossiblePositions[theOtherId(id)]);
      } else {
        possiblePositions[2].addAll(formerPossiblePositions[2]);
        possiblePositions[3].addAll(formerPossiblePositions[3]);
      }
    }
  }

  private Set<Point> filterImpossiblePositions(int id, GameState currentState, HashSet<Point> allPoints) {
    Set<Point> filteredPoints = new HashSet<>();
    if (!currentState.agents[id].inFogOfWar()) {
      filteredPoints.add(currentState.agents[id].position);
      return filteredPoints;
    }
    
    for (Point p : allPoints) {
      Cell cell = currentState.grid.get(p);
      if (!cell.isValid()) continue; // cell is not valid anymore, remove it !
      if (canSeeCell(currentState.agents[0], cell)) continue;
      if (canSeeCell(currentState.agents[1], cell)) continue;
      filteredPoints.add(p);
    }
    return filteredPoints;
  }

  private Set<Point> getFilteredPositions(GameState state, HashSet<Point> oldPos, Set<Point> filter) {
    Set<Point> points = new HashSet<>();
    for (Point p : oldPos) {
      if (!filter.contains(p)) continue;
      points.add(p);
    }
    return points;
  }

  boolean isInFogOfWar(GameState state, Point p) {
    Cell cell = state.grid.get(p);
    return isInFogOfWar(state, cell);
  }
  
  boolean isInFogOfWar(GameState state, Cell cell) {
    if (!cell.isValid()) return false;
    if (canSeeCell(state.agents[0], cell)) return false;
    if (canSeeCell(state.agents[1], cell)) return false;
    return false;
  }
  
  private boolean isAgentInFilter(GameState state, Set<Point> pushFilter, int id) {
    for (Point p : pushFilter) {
      if (formerPossiblePositions[id].contains(p)) {
        return true;
      }
    }
    return false;
  }

  private boolean canSeeCell(Agent agent, Cell potential) {
    return agent.cell.position.inRange(1, potential.position);
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

  // ----- old
  /**
   * Apply the Oracle
   * @param state
   */
  public void apply(GameState state) {
    for (int id=2;id<4;id++) {
      if (!state.agents[id].inFogOfWar()) {
        possiblePositions[id].clear();
        possiblePositions[id].add(state.agents[id].position);
      } else if (possiblePositions[id].size() == 1) {
        Point p = possiblePositions[id].iterator().next();
        state.positionAgent(state.agents[id], p);
      } else {
        if (debugMode) System.err.println("We don't know where the agent "+id+" is but he is in "+possiblePositions[id]);
      }
    }
  }
  
  public void debug(GameState state) {
    for (int id=2;id<4;id++) {
      if (!state.agents[id].inFogOfWar()) {
        System.err.println("*FACT* agent "+id+" at "+state.agents[id].position);
      } else if (possiblePositions[id].size() == 1) {
        Point p = possiblePositions[id].iterator().next();
        System.err.println("*GUESS* agent "+id+" at "+p);
      } else if (possiblePositions[id].size() > 1) {
        System.err.print("*Potential agent "+id+" positions could be : ");
        for (Point p : possiblePositions[id]) {
          System.err.print(p+" , ");
        }
        System.err.println();
      } else {
        System.err.println("Can't find any position for agent "+id);
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
  
  
  /** update initial state as the one we got from CG*/
  public void updatePreviousState(GameState state) {
    state.copyTo(initialPreviousState);
  }

  /* update after our move */
  public void updateSimulated(GameState state, Move move) {
    simulatedSimulation = new Simulation(formerSimulatedState);
    move.copyTo(formerSimulatedMove);
    state.copyTo(formerSimulatedState);
    
    for (int id=2;id<4;id++) {
      formerPossiblePositions[id].clear();
      if (state.agents[id].inFogOfWar()) {
        formerPossiblePositions[id].addAll(possiblePositions[id]);
      } else {
        formerPossiblePositions[id].add(state.agents[id].position);
      }
    }
  }

  public void setDebug(boolean b) {
    debugMode = b;
  }
}
