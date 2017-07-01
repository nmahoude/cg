package ww.paths;

import java.util.ArrayList;
import java.util.List;

import cgcollections.arrays.FastArray;
import ww.Agent;
import ww.Cell;
import ww.Dir;
import ww.GameState;

public class Voronoi {
  long visited;
  
  VoronoiInfo infos[] = new VoronoiInfo[4];
  
  public int[] voronoi(GameState state, Agent[] agents) {

    
    boolean stop[] = new boolean[agents.length];
    int cellsCount[] = new int[agents.length];
    for (int i=0;i<agents.length;i++) {
      infos[i] = new VoronoiInfo();
    }
    
    visited = 0L;

    for (int i=0;i<agents.length;i++) {
      stop[i] = agents[i].inFogOfWar();
      infos[i].cellsList.add(agents[i].cell);
      visited|= agents[i].position.mask;
    }
    
    while(dontStop(stop)) {
      for (int i=0;i<agents.length;i++) {
        if (stop[i] == true) continue;
        
        visitCells(infos[i]);
        if (infos[i].cellsList.size() == 0) {
          stop[i] = true;
        }
        cellsCount[i]+= infos[i].cellsList.size();
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

  private void visitCells(VoronoiInfo info) {
    info.nextCellsList.clear();
    
    for (int c=0;c<info.cellsList.size();c++) {
      Cell cell = info.cellsList.get(c);
      for (int i=0;i<Dir.LENGTH;i++) {
        Cell nextCell = cell.neighbors[i];
        if ((nextCell.position.mask & visited) == 0 && nextCell.height != 4 && (nextCell.height <= cell.height+1)) {
          visited|= nextCell.position.mask;
          info.nextCellsList.add(nextCell);
        }
      }
    }
    FastArray<Cell> temp = info.cellsList;
    info.cellsList = info.nextCellsList;
    info.nextCellsList = temp;
  }
}
