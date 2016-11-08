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

  public void destroyBlocks(List<P> ps) {
    boolean destruction;
    do {
      destruction = false;
      if (ps != null) {
        for (P p : ps) {
          destruction |= destroyFrom(p.x, p.y);
        }
        ps = null;
      } else {
        // TODO be smarter 
        // need to try all, we are on the second wave
        // be smart and don't do 0 bits
        // be smarted and don't retest groups with less than 4 neighbors
        for (int x=0;x<6;x++) {
          for (int y=0;y<12;y++) {
            destruction |= destroyFrom(x, y);
          }
        }
      }
      if (destruction) {
        updateScores();
        updateBoard();
      }
    } while(destruction);
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

  public boolean destroyFrom(int x, int y) {
    int colorLayer = getColorFromLayers(x, y);
    if (colorLayer == 0) {
      return false;
    }

    workDestroyLayer.copyFrom(board.layers[colorLayer]);
    NeighborInfo neighborInfo = workDestroyLayer.getNeighbors(x, y);
    int count = neighborInfo.count;
    if (count >= 4) {
      colorDestroyed[colorLayer-1] = true;
      clearedBlocks+=count;
      updateGroupBonus(count);

      board.layers[colorLayer].unset(neighborInfo.neighborsMask);
      board.layers[BitBoard.SKULL_LAYER].removeSkullsFrom(neighborInfo.neighborsMask);
      return true;
    } else {
      return false;
    }
  }

  private void updateGroupBonus(int count) {
    groupBonus += count >= 11 ? 8 : count - 4;
    groupBonus = Math.min(8, groupBonus);
  }
  
  private int getColorFromLayers(int x, int y) {
    for (int i=1;i<6;i++) {
      if (board.layers[i].isCellSetAt(x, y)) {
        return i;
      }
    }
    return 0;
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
