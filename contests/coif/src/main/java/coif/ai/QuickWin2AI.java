package coif.ai;

import java.util.ArrayList;
import java.util.List;

import coif.Board;
import coif.Cell;
import coif.Pos;
import coif.State;
import coif.units.UnitType;

/**
 * Second iteration of the quickWin AI
 * 
 * should be able to calculate fast, from 2 sides
 * 
 * @author nmahoude
 *
 */
public class QuickWin2AI implements AI {

  private Simulation sim;
  private State state;

  List<Pos> positions =new ArrayList<>();
  public boolean wannaPlay;

  public QuickWin2AI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }

  @Override
  public void think() {
    findFor(state.me.HQ, Board.P1_ACTIVE);
    System.err.println("FOR HIM : Found best cell at "+bestCell.pos+ " with cost : "+bestCost);
    System.err.println(String.format("%f%%", 100.0 * (state.opp.gold + state.opp.income) / bestCost));

    findFor(state.opp.HQ, Board.P0_ACTIVE);
    System.err.println("FOR ME : Found best cell at "+bestCell.pos+ " with cost : "+bestCost);
    System.err.println(String.format("%f%%", 100.0 * state.me.gold / bestCost));
    wannaPlay = simulate();
  }

  private boolean simulate() {
    if (positions.isEmpty() || bestCost > state.me.gold) {
      System.err.println("We CANT pay");
      return false;
    }
    System.err.println("We can pay for the quickwin !");
    System.err.println("Positions : "+positions);
    Pos current = positions.remove(0); // our own cell
    while (!positions.isEmpty()) {
      current = positions.remove(0);
      Cell currentCell = state.getCell(current);
      
      boolean shouldTrain = true;
      for (Cell neighbor2 : currentCell.neighbors) {
        if (neighbor2 != Cell.VOID && neighbor2.getStatut() == Board.P0_ACTIVE && neighbor2.unit != null) {
          if (neighbor2.unit.done != true && neighbor2.unit.canKill(currentCell.unit)) {
            sim.moveUnit(neighbor2.unit, currentCell.pos);
            shouldTrain = false;
            break;
          }
        }
      }
      if (shouldTrain) {
        int costToConquerCell = state.getCostToConquerCell(state.getCell(current));
        
        UnitType type;
        if (costToConquerCell == 10) {
          type = UnitType.SOLDIER_1;
        } else if (costToConquerCell == 20) {
          type = UnitType.SOLDIER_2;
        } else {
          type = UnitType.SOLDIER_3;
        }
  
        sim.trainUnit(type, current);
      }
    }
    return true;
  }

  void findFor(Pos init, int cellStatut) {
    pseudoCosts = new int[144];
    Cell start = state.getCell(init);
    pseudoCosts[start.pos.index] = 10;
    calculateCosts(start, cellStatut);
    
    bestCost = Integer.MAX_VALUE;
    bestCell = null;
    for (int i=0;i<144;i++) {
      Cell cell = state.board.cells[i];
      if (cell.getStatut() == cellStatut) {
        int cost = pseudoCosts[cell.pos.index];
        if (cost < bestCost) {
          bestCost = cost; 
          bestCell = cell;
        }
      }
    }
    // now we have the best cell, follow the gradient
    Cell current = bestCell;
    positions.clear();
    int realCost = 0;
    while (current != start) {
      positions.add(current.pos);
      Cell next = null;
      int bestScore = Integer.MAX_VALUE;
      for (Cell neighbors : current.neighbors) {
        if (neighbors == Cell.VOID) continue;
        if (pseudoCosts[neighbors.pos.index] < bestScore) {
          bestScore = pseudoCosts[neighbors.pos.index];
          next = neighbors;
        }
      }
      int costToConquerCell = state.getCostToConquerCell(next);
      realCost+=costToConquerCell;
      current = next;
    }
    positions.add(start.pos);
    bestCost = realCost;
    System.err.println("Real cost : " + realCost);
    System.err.println("With path : " + positions);
  }

  int pseudoCosts[] = new int[144];
  private Cell bestCell;
  private int bestCost;
  private void calculateCosts(Cell cell, int cellStatut) {
    int currentCost = pseudoCosts[cell.pos.index];
    for (Cell neighbor : cell.neighbors) {
      if (neighbor == Cell.VOID) continue;
      int costForCell = state.getCostToConquerCell(neighbor);
      
      for (Cell neighbor2 : neighbor.neighbors) {
        if (neighbor2 != Cell.VOID && neighbor2.getStatut() == Board.P0_ACTIVE && neighbor2.unit != null) {
          if (neighbor2.unit.canKill(neighbor.unit)) {
            costForCell = 0; // will move unit
          }
        }
      }

      if (pseudoCosts[neighbor.pos.index] == 0 
          || pseudoCosts[neighbor.pos.index] > currentCost + costForCell) {
        pseudoCosts[neighbor.pos.index] = currentCost + costForCell;
        calculateCosts(neighbor, cellStatut);
      }
    }
  }
}
