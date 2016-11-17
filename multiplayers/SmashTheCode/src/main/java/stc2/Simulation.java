package stc2;

import java.util.Arrays;
import java.util.List;

import stc.P;

public class Simulation {
  public BitBoard board;
  public BitLayer workDestroyLayer = new BitLayer();
  public BitLayer workTestLayer = new BitLayer();
  public NeighborInfo neighborsInfo = new NeighborInfo();

  public int points;
  public int groupsCount[] = new int[4];
  
  public int chainPower;
  public int clearedBlocks;
  public int colorBonus;
  public int groupBonus;
  public boolean colorDestroyed[] = new boolean[6];
  
  public void clear() {
    points = 0;
    chainPower = 0;
    clearedBlocks = 0;
    colorBonus = 0;
    groupBonus = 0;
    colorDestroyed[0] = false;
    colorDestroyed[1] = false;
    colorDestroyed[2] = false;
    colorDestroyed[3] = false;
    colorDestroyed[4] = false;
    colorDestroyed[5] = false;
    groupsCount[0] = 0;
    groupsCount[1] = 0;
    groupsCount[2] = 0;
    groupsCount[3] = 0;
  }
  
  BitLayer toCheckLayer = new BitLayer();
  public void destroyBlocks(List<P> ps) {
    
    boolean destruction;
    do {
      toCheckLayer.set(board.layers[BitBoard.COMPLETE_LAYER_MASK]);
      toCheckLayer.unset(board.layers[BitBoard.SKULL_LAYER]);
      destruction = false;
      if (ps != null) {
        for (P p : ps) {
          destruction |= destroyFrom(p.x, p.y);
        }
        ps = null;
      } else {
        destruction = doFullDestruct();
      }
      if (destruction) {
        updateScores();
        updateBoard();
      }
    } while(destruction);
  }

  public boolean doFullDestruct() {
    boolean destruction = false;
    for (int y=0;y<12 && !toCheckLayer.isEmpty();y++) {
      for (int x=0;x<6 && !toCheckLayer.isEmpty();x++) {
        destruction |= destroyFrom(x, y);
      }
    }
    return destruction;
  }

  public void updateScores() {
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

  public int getColorBonus() {
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

  long mask[] = new long[2];
  public boolean destroyFrom(int x, int y) {
    getMaskFor(mask, x,y);
    
    if (!toCheckLayer.isMaskSetted(mask[0], mask[1])) {
      return false;
    }
    int colorLayer = board.getColorFromLayers(mask);
    if (colorLayer == 0) {
      return false;
    }

    workDestroyLayer.copyFrom(board.layers[colorLayer]);
    neighborsInfo.neighborsMask.clear();
    workDestroyLayer.getNeighbors(neighborsInfo, x, y);
    
    // unset bits on the toCheckLayer
    toCheckLayer.unset(neighborsInfo.neighborsMask);
    int count = neighborsInfo.count;
    if (count >= 4) {
      colorDestroyed[colorLayer-1] = true;
      clearedBlocks+=count;
      updateGroupBonus(count);

      board.layers[colorLayer].unset(neighborsInfo.neighborsMask);
      board.layers[BitBoard.SKULL_LAYER].removeSkullsFrom(neighborsInfo.neighborsMask);
      return true;
    } else {
      groupsCount[count]++;
      return false;
    }
  }
  
  public void getMaskFor(long masks[], int x, int y) {
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



  public void updateGroupBonus(int count) {
    groupBonus += count >= 11 ? 8 : count - 4;
    groupBonus = Math.min(8, groupBonus);
  }
  
  public boolean putBalls(int color1, int color2, int rotation, int baseColumn) {
    if (!board.canPutBalls(rotation, baseColumn)) {
      return false;
    }
    return putBallsNoCheck(color1, color2, rotation, baseColumn);
  }

  public boolean putBallsNoCheck(int color1, int color2, int rotation, int baseColumn) {
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
