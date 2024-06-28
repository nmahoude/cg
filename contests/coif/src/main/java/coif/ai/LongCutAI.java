package coif.ai;

import java.util.ArrayList;
import java.util.List;

import coif.Board;
import coif.Cell;
import coif.State;
import coif.units.UnitType;

public class LongCutAI implements AI {

  private Simulation sim;
  private State state;
  private Cell bestCutCell;
  private int bestCutReward;
  private int bestDir;

  public LongCutAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;

  }
  
  @Override
  public void think() {
    int completeValue = findValueFromCut();
    int done[] = new int[144];
    bestCutCell = null;
    bestCutReward = 0;
    bestDir = 0;
    
    for (int i=0;i<144;i++) {
      Cell current = state.board.cells[i];
      if (current == Cell.VOID) continue;
      if (current.getStatut() != Board.P0_ACTIVE) continue;
      if (done[current.pos.index] != 0) continue;

      for (int dir=0;dir<4;dir++) {
        cutInDir(current, dir, completeValue);
      }
    }
    
    if (bestCutCell != null) {
      System.err.println("**** APPLYING CUT from "+bestCutCell);
      Cell next = bestCutCell;
      for (int decal = 0;true;decal++) {
        next = next.neighbors[bestDir];
        if (next == Cell.VOID || next.getStatut() != Board.P1_ACTIVE) {
          break;
        }
        sim.trainUnit(state.getUnitToConquer(next), next.pos);
      }
    }
  }

  private void cutInDir(Cell current, int dir, int completeValue) {
    List<Cell> cutted = new ArrayList<>();

    int cost = 0;
    Cell next = current;
    for (int decal = 0;true;decal++) {
      next = next.neighbors[dir];
      if (next == Cell.VOID || next.getStatut() != Board.P1_ACTIVE) {
        break;
      }
      next.cut = true;
      cost += state.getCostToConquerCell(next);
      cutted.add(next);
    }
    if (cutted.isEmpty()) return; // no opp territory

    // calc the reward
    int reward = completeValue - findValueFromCut();
    if (reward > 0 ) {
      System.err.println("CUT AT "+current.pos+" in dir "+dir+" for "+cutted.size()+" cells");
      System.err.println("cutted are "+cutted);
      System.err.println(" cut value is "+reward+" for a cost of "+cost+ "my gold : "+state.me.gold);
      if (reward > cost && state.me.gold >= cost) {
        System.err.println("COST IS BETTER THAN REWARD : "+cost +" vs reward "+reward);
        int cutReward = reward-cost;
        next.cutValue = cutReward;
        System.err.println(""+cutReward);
        if (cutReward > bestCutReward) {
          System.err.println("NEw best cut is at "+current);
          bestCutCell = current;
          bestCutReward = cutReward;
          bestDir = dir;
        }
      }
    }
    
    // uncut
    cutted.forEach(c -> c.cut = false);
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
