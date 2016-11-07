package stc2;

public class BitLayer {
  private static final int FULL = 0b111111111111;
  static final int yMask[] = new int[] {
      1<<0, 1<<1, 1<<2,1<<3,1<<4,1<<5,
      1<<6, 1<<7,1<<8, 1<<9,1<<10,1<<11
  };
  static final int yFullMask[] = new int[] {
      0b0, 0b1, 0b11, 0b111, 0b1111,0b11111,
      0b111111,0b1111111, 0b11111111, 0b111111111, 0b1111111111, 0b11111111111, 0b111111111111 
  };
  private static final int COLUMNS = 6;

  int cols[] = new int[COLUMNS];
  
  public void reset() {
    for (int x=0;x<6;x++) {
      cols[x] = 0;
    }
  }

  public void unsetCell(int x, int y) {
    cols[x] &= ~yMask[y]; 
  }

  public void setCell(int x, int y) {
    cols[x] |= yMask[y]; 
  }

  public void slideRight(int x, int factor) {
    cols[x] = cols[x] >>> factor;
  }

  public void slideRightFromTo(int x, int from, int to) {
    int keepStartMask = cols[x] & ((int)Math.pow(2, to)-1);
    int rest = cols[x] >>> from << to ;
    cols[x] = rest | keepStartMask;
  }

  
  public String getDebugString() {
    char[] output = new char[7*12];
    for (int y=0;y<12;y++) {
      output[6+7*y] = '\n';
    }
    for (int x=0;x<6;x++) {
      int value = cols[x];
      for (int y=0;y<12;y++) {
        if ((value & yMask[y]) != 0) {
          output[x+7*(11-y)] = '1';
        } else {
          output[x+7*(11-y)] = '0';
        }
      }
    }
    return new String(output);
  }

  public static int getNextSettedBit(int value, int target) {
    if (target >= 11) {
      return 12;
    }
    int mask;
    value = value & ~yFullMask[target+1];
    if (value == 0) {
      return 12;
    }
    do {
      mask = value & yMask[++target];
    } while (mask == 0 && target < 11);
    return target;
  }

  public static int getNeighborBits(int value, int pos) {
    int invMask = ~value & FULL;
    int lastPos = -1;
    int nextPos = getNextSettedBit(invMask, 0);
    while (nextPos < pos) {
      invMask = invMask & ~(FULL & yFullMask[nextPos]);
      lastPos = nextPos;
      nextPos = getNextSettedBit(invMask, nextPos);
    }
    return getMaskFromTo(lastPos+1, nextPos);
  }

  public static int getSettedBits(int mask) {
    return Integer.bitCount(mask);
  }
  public static int getMaskFromTo(int from, int to) {
    return yFullMask[to] & ~yFullMask[from];
  }
  
  public int countNeighbors(int x, int y) {
    int tempCols[] = { cols[0], cols[1], cols[2], cols[3], cols[4],cols[5] };
    int count = 0;
    int value = tempCols[x];
    int mask = getNeighborBits(value, y);
    if (mask == 0) {
      return 0;
    }
    tempCols[x] &= ~mask; // remove counted cells
    
    count = Integer.bitCount(mask);
    if (x>0) {
      count += countNeighbors(x-1, mask, tempCols);
    }
    if (x<5) {
      count += countNeighbors(x+1, mask, tempCols);
    }
    return count;
  }
  
  private int countNeighbors(int x, int previousMask, int[] tempCols) {
    int value = tempCols[x] & previousMask;
    if (value == 0) {
      return 0;
    }
    int pos = Integer.numberOfTrailingZeros(value)+1;
    int mask = getNeighborBits(value, pos);
    int count = Integer.bitCount(mask);
    tempCols[x] &= ~mask;
    if (x>0) {
      count += countNeighbors(x-1, mask, tempCols);
    }
    if (x<5) {
      count += countNeighbors(x+1, mask, tempCols);
    }
    return count;
  }

  public String getColAsString(int x) {
    int value = cols[x];
    return toString(value);
  }

  public static String toString(int value) {
    String binaryString = Integer.toBinaryString(value);
    while (binaryString.length() < 12) {
      binaryString = "0"+binaryString;
    }
    return binaryString;
  }
}
