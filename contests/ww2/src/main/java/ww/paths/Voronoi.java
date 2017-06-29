package ww.paths;

import java.util.ArrayList;
import java.util.List;

import ww.Agent;
import ww.Cell;
import ww.Dir;
import ww.GameState;

public class Voronoi {
  long visited;
  
  public int[] voronoi(GameState state, Agent[] agents) {

    
    boolean stop[] = new boolean[agents.length];
    int cellsCount[] = new int[agents.length];
    ArrayList<Cell>[] cellsList = (ArrayList<Cell>[])new ArrayList[agents.length];
    for (int i=0;i<agents.length;i++) {
      cellsList[i] = new ArrayList<Cell>();
    }
    
    visited = 0L;

    for (int i=0;i<agents.length;i++) {
      stop[i] = agents[i].inFogOfWar();
      cellsList[i].add(agents[i].cell);
      visited|= agents[i].position.mask;
    }
    
    while(dontStop(stop)) {
      for (int i=0;i<agents.length;i++) {
        if (stop[i] == true) continue;
        
        cellsList[i] = visitCells(cellsList[i]);
        if (cellsList[i].size() == 0) {
          stop[i] = true;
        }
        cellsCount[i]+= cellsList[i].size();
      }
    }
    
    return cellsCount;
  }
  
  private boolean dontStop(boolean[] stop) {
    for (boolean b : stop) {
      if (!b) return true;
    }
    return false;
  }

  public int[] voronoi2(GameState state, Agent agent0, Agent agent1) {
    Agent agents[] = new Agent[2];
    agents[0] = agent0;
    agents[1] = agent1;
    return voronoi(state, agents);
  }

  public int[] voronoi4(GameState state) {
    return voronoi(state, state.agents);
  }

  private ArrayList<Cell> visitCells(List<Cell> cells) {
    ArrayList<Cell> newCells = new ArrayList<>();
    
    for (Cell cell : cells) {
      for (int i=0;i<Dir.LENGTH;i++) {
        Cell nextCell = cell.neighbors[i];
        if (nextCell.isValid() && (nextCell.position.mask & visited) == 0 && (nextCell.height <= cell.height+1)) {
          visited|= nextCell.position.mask;
          newCells.add(nextCell);
        }
      }
    }
    return newCells;
  }
}
