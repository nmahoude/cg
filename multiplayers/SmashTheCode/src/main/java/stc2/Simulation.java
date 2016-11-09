package stc2;

import java.util.Arrays;
import java.util.List;

import stc.P;

public class Simulation {
  BitBoard board;
  BitLayer workDestroyLayer = new BitLayer();
  BitLayer workTestLayer = new BitLayer();
  
  int points;
  int chainPower;
  int clearedBlocks;
  int colorBonus;
  int groupBonus;
  boolean colorDestroyed[] = new boolean[6];

  int groupsCount[] = new int[4];
  
  public void init() {
    points = 0;
    chainPower = 0;
    clearedBlocks = 0;
    colorBonus = 0;
    groupBonus = 0;
    for (int i=0;i<6;i++) {
      colorDestroyed[i] = false;
    }
    for (int i=0;i<4;i++) {
      groupsCount[i] = 0;
    }
    
  }
  
  public void destroyBlocks(List<P> ps) {
    BitLayer toCheckLayer = new BitLayer();
    
    boolean destruction;
    do {
      toCheckLayer.merge(board.layers[BitBoard.COMPLETE_LAYER_MASK]);
      destruction = false;
      if (ps != null) {
        for (P p : ps) {
          destruction |= destroyFrom(p.x, p.y, toCheckLayer);
        }
        ps = null;
      } else {
        destruction = doFullDestruct(toCheckLayer, destruction);
      }
      if (destruction) {
        updateScores();
        updateBoard();
      }
    } while(destruction);
  }

  private boolean doFullDestruct(BitLayer toCheckLayer, boolean destruction) {
    for (int y=0;y<12 && !toCheckLayer.isEmpty();y++) {
      for (int x=0;x<6 && !toCheckLayer.isEmpty();x++) {
        destruction |= destroyFrom(x, y, toCheckLayer);
      }
    }
    return destruction;
  }

  private void updateScores() {
    colorBonus = getColorBonus();
    points += getPoints();
    clearedBlocks = 0;
    groupBonus = 0;
    colorBonus = 0;
    chainPower = (chainPower == 0) ? 8 : 2*chainPower;
  }
  
  void updateBoard() {
    board.update();
  }

  int getPoints() {
    return (10 * clearedBlocks) * Math.min(999, Math.max(1, chainPower + colorBonus + groupBonus));
  }

  private int getColorBonus() {
    int CB = 1;
    for (int i=0;i<6;i++) {
      CB*= colorDestroyed[i] ? 2 : 1;
      colorDestroyed[i] = false; // reset color destroyed
    }
    if (CB <= 2) {
      CB = 0;
    } else {
      CB/=2;
    }
    return CB;
  }

  public boolean destroyFrom(int x, int y, BitLayer toCheckedLayer) {
    long mask[] = new long[2];
    getMaskFor(mask, x,y);
    
    if (!toCheckedLayer.isMaskSetted(mask[0], mask[1])) {
      return false;
    }
    int colorLayer = board.getColorFromLayers(mask);
    if (colorLayer == 0) {
      return false;
    }

    workDestroyLayer.copyFrom(board.layers[colorLayer]);
    NeighborInfo neighborInfo = workDestroyLayer.getNeighbors(x, y);
    
    // unset bits on the toCheckLayer
    toCheckedLayer.unset(neighborInfo.neighborsMask);
    int count = neighborInfo.count;
    if (count >= 4) {
      colorDestroyed[colorLayer-1] = true;
      clearedBlocks+=count;
      updateGroupBonus(count);

      board.layers[colorLayer].unset(neighborInfo.neighborsMask);
      board.layers[BitBoard.SKULL_LAYER].removeSkullsFrom(neighborInfo.neighborsMask);
      return true;
    } else {
      groupsCount[count]++;
      return false;
    }
  }
  
  private void getMaskFor(long masks[], int x, int y) {
    long mask = BitLayer.yMask[y];
    switch(x) {
      case 0:
        masks[0] = mask;
        masks[1] = 0;
        break;
      case 1:
        masks[0] = mask << 16;
        masks[1] = 0;
        break;
      case 2:
        masks[0] = mask << 32;
        masks[1] = 0;
        break;
      case 3:
        masks[0] = 0;
        masks[1] = mask;
        break;
      case 4:
        masks[0] = 0;
        masks[1] = mask << 16;
        break;
      case 5:
        masks[0] = 0;
        masks[1] = mask << 32;
        break;
    }
  }



  private void updateGroupBonus(int count) {
    groupBonus += count >= 11 ? 8 : count - 4;
    groupBonus = Math.min(8, groupBonus);
  }
  
  public boolean putBalls(int color1, int color2, int rotation, int baseColumn) {
    if (!board.canPutBalls(rotation, baseColumn)) {
      return false;
    }
    P posToCheck1 = null;
    P posToCheck2 = null;
    switch (rotation) {
      case 0:
        posToCheck1 = board.pushBall(color1, baseColumn);
        posToCheck2 = board.pushBall(color2, baseColumn+1);
        break;
      case 1:
        posToCheck1 = board.pushBall(color1, baseColumn);
        posToCheck2 = board.pushBall(color2, baseColumn);
        break;
      case 3:
        posToCheck1 = board.pushBall(color2, baseColumn);
        posToCheck2 = board.pushBall(color1, baseColumn);
        break;
      case 2:
        posToCheck1 = board.pushBall(color1, baseColumn);
        posToCheck2 = board.pushBall(color2, baseColumn-1);
        break;
    }
    
    destroyBlocks(Arrays.asList(posToCheck1, posToCheck2));
    return true;
  }
}
