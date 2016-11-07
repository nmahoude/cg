package stc2;

public class BitLayer {
  static final int yMask[] = new int[] {
      1<<0, 1<<1, 1<<2,1<<3,1<<4,1<<5,1<<6,
      1<<7,1<<8, 1<<9,1<<10,1<<11
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

  public int getSurroundingSetBitsMask(int value, int y) {
    return 0;
  }
  
  public int countNeighbors(int x, int y) {
    int mask = yMask[y];
    int count = 0;
    int value = cols[x];
    while (x < 6 && (value & mask) != 0) {
      count++;
      value = cols[x];
      x++;
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
