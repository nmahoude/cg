package ww2;

import ww.Grid;

public class ReachableCellCalculator {
  GameState state;
  long visited;
  long validPosMask;
  
  public int getReachableCells(GameState state, int id) {
    if (state.agents[id].inFogOfWar()) return 0;
    
    this.state = state;
    visited = 0L;
    validPosMask = state.grid.allValidPos();

    Point pos =  state.agents[id].position;
    
    return getReachableCells(pos.mask, state.grid.getHeight(pos)) -1 ;// -1 to remove the cell we are one
  }

  private int getReachableCells(long positionMask, int fromHeight) {

    if ((positionMask & visited) != 0) return 0; // already visited

    int currentHeight = state.grid.getHeightFromBitMask(positionMask);
    if (currentHeight > fromHeight+1) return 0; // step too high

    visited |= positionMask;
    int count = 1;
    
    long newPosition;
    
    newPosition = (positionMask << 1) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask >>> 1) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask << 8) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask >>> 8) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask << 7) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask << 9) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask >>> 7) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask >>> 9) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }

    return count;
  }

}
