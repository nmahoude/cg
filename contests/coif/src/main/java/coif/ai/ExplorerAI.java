package coif.ai;

import java.util.ArrayList;
import java.util.List;

import coif.Board;
import coif.Cell;
import coif.Pos;
import coif.State;
import coif.units.Unit;

public class ExplorerAI implements AI {

  private Simulation sim;
  private State state;

  public ExplorerAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }
  
  @Override
  public void think() {
    List<Unit> toMove = new ArrayList<>();
    for (Unit unit : state.units) {
      if (unit.done 
          || unit.owner == 1 
          || unit.dead 
          || unit.isStatic()) continue;
      updateDegreeOfFreedom(unit);
      toMove.add(unit);
    }
    state.units.sort((u1, u2) -> Integer.compare(u1.degreeOfFreedom, u2.degreeOfFreedom));

    for (Unit unit : toMove) {
      moveForExploration(unit);
    }
  }

  
  private void moveForExploration(Unit unit) {
    Cell current = state.getCell(unit);

    int bestScore = 100-state.board.distances[current.pos.index];
    Pos bestPos = unit.pos;
    
    for (Cell cell : current.neighbors) {
      if (cell == Cell.VOID) continue;
      if (!unit.canWalkOn(cell)) continue;
      
      int score = 0;
      //TODO redo  ? score += cell.cutValue;
      
      if (cell.getStatut() == Board.EMPTY) {
        score = 100;
      } else if (cell.getStatut() == Board.P1_INACTIVE) {
        score = 200;
      } else if (cell.getStatut() == Board.P1_ACTIVE) {
        score = 300;
      }
      score = 100 - state.board.distances[cell.pos.index];
      if (cell.unit != null) {
        if( cell.unit.owner == 1 
            && unit.canKill(cell.unit)) {
            score += 10000;
        }
      }
      
      if (score > bestScore) {
        bestScore = score;
        bestPos = cell.pos;
      } 
    }
    if (bestScore > 100-state.board.distances[current.pos.index]) {
      sim.moveUnit(unit, bestPos);
    } else {
    }
  }

  private void updateDegreeOfFreedom(Unit unit) {
    if (unit.isStatic()) {
      return;
    }
    int dof = 0;
    Cell current = state.getCell(unit);
    for (Cell neighbor : current.neighbors) {
      if (neighbor == Cell.VOID) continue;
      
      if (neighbor.getStatut() == Board.EMPTY 
          || neighbor.getStatut() == Board.P1_INACTIVE) {
        dof++;
      } else if (neighbor.getStatut() == Board.P1_ACTIVE) {
        if (neighbor.unit == null || neighbor.unit.canBeKilledBy(unit)) {
          dof++;
        }
      }
    }
    unit.degreeOfFreedom = dof;
  }
}
