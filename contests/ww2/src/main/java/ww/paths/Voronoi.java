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
  boolean stop[] = new boolean[4];
  int cellsCount[] = new int[4];
  
  static VoronoiInfo infos[] = new VoronoiInfo[4];
  static {
    for (int i=0;i<4;i++) {
      infos[i] = new VoronoiInfo();
    }
  }
  public int[] voronoi(GameState state, Agent[] agents) {
    visited = 0L;

    for (int i=0;i<agents.length;i++) {
      stop[i] = agents[i].inFogOfWar();
      infos[i].cellsList.clear();
      infos[i].cellsList.add(agents[i].cell);
      visited|= agents[i].position.mask;
    }
    for (int i=agents.length;i<4;i++) {
      stop[i] = true;
    }
    
    while(dontStop(stop)) {
      for (int i=0;i<agents.length;i++) {
        if (stop[i] == true) continue;
        
        visitCells(infos[i]);
        if (infos[i].cellsList.length == 0) {
          stop[i] = true;
        } else {
          cellsCount[i]+= infos[i].cellsList.length;
        }
      }
    }
    
    return cellsCount;
  }
  
  private boolean dontStop(boolean[] stop) {
    return ! (stop[0] && stop[1] && stop[2] && stop[3]);
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
      Cell cell = info.cellsList.elements[c];
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
