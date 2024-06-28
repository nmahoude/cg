package coif.ai;

import java.util.ArrayList;
import java.util.List;

import coif.Board;
import coif.Cell;
import coif.State;
import coif.units.UnitType;

public class CutAI implements AI {

  private Simulation sim;
  private State state;

  public CutAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;

  }
  
  @Override
  public void think() {
    int completeReward = findValueFromCut();
    int done[] = new int[144];
    Cell bestCutCell = null;
    int bestCutReward = 0;
    
    for (int i=0;i<144;i++) {
      Cell current = state.board.cells[i];
      if (current == Cell.VOID) continue;
      if (current.getStatut() != Board.P0_ACTIVE) continue;
      if (done[current.pos.index] != 0) continue;
      
      for (int dir=0;dir<4;dir++) {
        Cell next = current.neighbors[dir];
        next.cutValue = 0;
        
        if (next.getStatut() == Board.P1_ACTIVE) {
          next.cut = true;
          int reward = completeReward - findValueFromCut();
          done[i] = 1;
          if (reward > 0) {
            System.err.println("CUT AT "+next.pos);
            System.err.println(" complete is "+completeReward+" cut value is "+reward);
            int cost = state.getCostToConquerCell(next);
            if (reward > cost) {
              System.err.println("COST IS BETTER THAN REWARD : "+cost +" vs reward "+reward);
              int cutReward = reward-cost;
              next.cutValue = cutReward;
              System.err.println(""+cutReward);
              if (cutReward > bestCutReward) {
                System.err.println("NEw best cut is at "+next);
                bestCutCell = next;
                bestCutReward = cutReward;
              }
            }
          }
          next.cut = false;
        }
      }
    }
    if (bestCutCell != null) {
      System.err.println("best cut cell is "+bestCutCell);
      sim.trainUnit(state.getUnitToConquer(bestCutCell), bestCutCell.pos);
    }
  }

  private int findValueFromCut() {
    int visited[] = new int[144];
    List<Cell> toVisit = new ArrayList<>();
    toVisit.add(state.getCell(state.opp.HQ));
    int value = 0;
    while (!toVisit.isEmpty()) {
      Cell current = toVisit.remove(0);
      if (visited[current.pos.index] != 0) continue;
      visited[current.pos.index] = 1;

      value+=1;
      if (current.unit != null) {
        value+= (current.unit.type == UnitType.SOLDIER_1) ? 10 : 0;
        value+= (current.unit.type == UnitType.SOLDIER_2) ? 20 : 0;
        value+= (current.unit.type == UnitType.SOLDIER_3) ? 30 : 0;
      }
      
      for (Cell neighbor : current.neighbors) {
        if (neighbor.getStatut() == Board.P1_ACTIVE
            && neighbor.cut == false
            && visited[neighbor.pos.index] == 0
            ) {
          toVisit.add(neighbor);
        }
      }
    }
    return value;
  }

}
