package stc2;

public class BitLayer {
  private static final int FULL = 0b111111111111;
  
  private static final long FULL1 = 0b1111111111111111L;
  private static final long FULL2 = 0b11111111111111110000000000000000L;
  private static final long FULL3= 0b1111111111111111111100000000000000000000000000000000L;

  static final int yMask[] = new int[] {
      1<<0, 1<<1, 1<<2,1<<3,1<<4,1<<5,
      1<<6, 1<<7,1<<8, 1<<9,1<<10,1<<11
  };
  static final int yFullMask[] = new int[] {
      0b0, 0b1, 0b11, 0b111, 0b1111,0b11111,
      0b111111,0b1111111, 0b11111111, 0b111111111, 0b1111111111, 0b11111111111, 0b111111111111 
  };

  long col1 = 0; // 0->2
  long col2 = 0; // 3->5
  
  public BitLayer() {
  }
  
  public BitLayer(BitLayer layer) {
    this.col1 = layer.col1;
    this.col2 = layer.col2;
  }
  
  public void clear() {
    col1 = 0;
    col2 = 0;
  }

  int getCol(int x) {
    switch(x) {
      case 0:
        return (int)(col1 & FULL1);
      case 1:
        return (int)(col1 >>> 16 & FULL1);
      case 2:
        return (int)(col1 >>> 32 & FULL1);
      case 3:
        return (int)(col2 & FULL1);
      case 4:
        return (int)(col2 >>> 16 & FULL1);
      case 5:
        return (int)(col2 >>> 32 & FULL1);
    }
    throw new UnsupportedOperationException(" can't acces col "+x);
  }
  
  void setCol(int x, long value) {
    switch(x) {
      case 0:
        col1 = (col1 & ~FULL1) | value;
        break;
      case 1:
        col1 = (col1 & ~FULL2) | (value << 16);
        break;
      case 2:
        col1 = (col1 & ~FULL3) | (value << 32);
        break;
      case 3:
        col2 = (col2 & ~FULL1) | value;
        break;
      case 4:
        col2 = (col2 & ~FULL2) | (value << 16);
        break;
      case 5:
        col2 = (col2 & ~FULL3) | (value << 32);
        break;
      default:
        throw new UnsupportedOperationException(" can't acces col "+x);
    }
  }
  
  public void unsetCell(int x, int y) {
    setCol(x, getCol(x) & ~yMask[y]);
  }

  public void setCell(int x, int y) {
    setCol(x, getCol(x) | yMask[y]);
  }

  public void slideRight(int x, int factor) {
    setCol(x, getCol(x) >>> factor);
  }

  public void slideRightFromTo(int x, int from, int to) {
    int value = getCol(x);
    int keepStartMask = value & yFullMask[to];
    int rest = value >>> from << to ;
    
    setCol(x, rest | keepStartMask);
  }
  
  public String getDebugString() {
    char[] output = new char[7*12];
    for (int y=0;y<12;y++) {
      output[6+7*y] = '\n';
    }
    for (int x=0;x<6;x++) {
      int value = getCol(x);
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

  public static int getNeighborBitsOnOneRow(int value, int pos) {
    int invMask = ~value & FULL;
    int firstPos = pos - Integer.numberOfLeadingZeros(invMask << (32-pos));
    if (firstPos <= 0) {
      firstPos = 0;
    }
    int secondPos = pos+1 + Integer.numberOfTrailingZeros(invMask >>> (pos+1));
    return getMaskFromTo(Math.min(firstPos, 11), Math.min(secondPos, 12));
  }

  public static int getSettedBits(int mask) {
    return Integer.bitCount(mask);
  }
  public static int getMaskFromTo(int from, int to) {
    return yFullMask[to] & ~yFullMask[from];
  }
  
  public NeighborInfo getNeighbors(int x, int y) {
    NeighborInfo neighborsInfo = new NeighborInfo();
    neighborsInfo.x = x;
    neighborsInfo.y = y;
    
    int value = getCol(x);
    int mask = getNeighborBitsOnOneRow(value, y);
    if (mask == 0) {
      return neighborsInfo;
    }
    neighborsInfo.neighborsMask.setCol(x, mask);
    setCol(x, value & ~mask); // remove counted cells
    neighborsInfo.count = Integer.bitCount(mask);
    if (x>0) {
      countNeighborsRecursive(x-1, mask, neighborsInfo);
    }
    if (x<5) {
      countNeighborsRecursive(x+1, mask, neighborsInfo);
    }
    return neighborsInfo;
  }
  
  private void countNeighborsRecursive(int x, int previousMask, NeighborInfo info) {
    int value = getCol(x);
    int intersection = value & previousMask;
    if (intersection == 0) {
      return;
    }
    int pos = Integer.numberOfTrailingZeros(intersection);
    int mask = getNeighborBitsOnOneRow(value, pos);
    info.count += Integer.bitCount(mask);
    info.neighborsMask.setCol(x, mask);
    setCol(x, value & ~mask);
    if (x>0) {
      countNeighborsRecursive(x-1, mask, info);
    }
    if (x<5) {
      countNeighborsRecursive(x+1, mask, info);
    }
    return;
  }

  public String getColAsString(int x) {
    int value = getCol(x);
    return toString(value);
  }

  public static String toString(int value) {
    String binaryString = Integer.toBinaryString(value);
    while (binaryString.length() < 12) {
      binaryString = "0"+binaryString;
    }
    return binaryString;
  }

  
  public void shiftDown() {
    col1 = col1 >>> 1;
    col2 = col2 >>> 1;
  }

  public void shiftUp() {
    col1 = col1 << 1;
    col2 = col2 << 1;
  }

  public void shiftRight() {
    col2 = (col2 << 16) | (col1 & FULL3) >>> 32;
    col1 = col1 << 16;
  }

  public void shiftLeft() {
    col1 = (col1 >>> 16) | ((col2 & FULL1) << 32);
    col2 = col2 >>> 16;
  }

  public void removeSkullsFrom(BitLayer color) {
    long shiftedCol1 = (color.col1 >>> 1) 
        | (color.col1 << 1)
        | (color.col1 << 16)
        | ((color.col1 >>> 16) | ((color.col2 & FULL1) << 32));
    long shiftedCol2 = (color.col2 >>> 1)
        | (color.col2 << 1)
        | ((color.col2 << 16) | (color.col1 & FULL3) >>> 32)
        | (color.col2 >>> 16);

    col1 = col1 & ~shiftedCol1;
    col2 = col2 & ~shiftedCol2;
  }

  public void merge(BitLayer bitLayer) {
    col1 = col1 | bitLayer.col1;
    col2 = col2 | bitLayer.col2;
  }

  public boolean isEmpty() {
    return col1 == 0 && col2 == 0;
  }

  public int bitCount() {
    return Long.bitCount(col1)+Long.bitCount(col2);
  }

  public void copyFrom(BitLayer bitLayer) {
    col1 = bitLayer.col1;
    col2 = bitLayer.col2;
  }

  public boolean isCellSetAt(int x, int y) {
    return (getCol(x) & yMask[y]) != 0;
  }

  public int pushFromTopOfColumn(int column) {
    int value = getCol(column);
    int y = 32-Integer.numberOfLeadingZeros(value);
    setCol(column, value | yMask[y]);
    return y;
  }

  public void unset(BitLayer neighborsMask) {
    col1 = col1 & ~neighborsMask.col1;
    col2 = col2 & ~neighborsMask.col2;
  }

  public static void generateMvs(int mask, int[] mvs) {
    int mp, mv;
    int mk = ~mask << 1; // We will count 0's to right.
    for (int i = 0; i < 5; i++) {
      mp = mk ^ (mk << 1);
      mp = mp ^ (mp << 2);
      mp = mp ^ (mp << 4);
      mp = mp ^ (mp << 8);
      mp = mp ^ (mp << 16);
      mv = mp & mask; // Bits to move.
      mvs[i] = mv;
      mask = mask ^ mv | (mv >> (1 << i)); // Compress m.
      mk = mk & ~mp;
    }
  }

  public static int[] generateMvs(int mask) {
    int mvs[] = new int[5];
    generateMvs(mask, mvs);
    return mvs;
  }
  
  public static int compress(int value, int mask, int mvs[]) {
    int t;
    value = value & mask;
    t = value & mvs[0]; value = value ^ t | (t >> 1);
    t = value & mvs[1]; value = value ^ t | (t >> 2);
    t = value & mvs[2]; value = value ^ t | (t >> 4);
    t = value & mvs[3]; value = value ^ t | (t >> 8);
    t = value & mvs[4]; value = value ^ t | (t >> 16);
    return value;
  }
  
  public static int compress(int value, int mask) {
    int mk, mp, mv, t;
    int i;
    value = value & mask; // Clear irrelevant bits.
    mk = ~mask << 1; // We will count 0's to right.
    for (i = 0; i < 5; i++) {
      mp = mk ^ (mk << 1);
      mp = mp ^ (mp << 2);
      mp = mp ^ (mp << 4);
      mp = mp ^ (mp << 8);
      mp = mp ^ (mp << 16);
      mv = mp & mask; // Bits to move.
      mask = mask ^ mv | (mv >> (1 << i)); // Compress m.
      t = value & mv;
      value = value ^ t | (t >> (1 << i)); // Compress x.
      mk = mk & ~mp;
    }
    return value;
  }

  public void fill() {
    col1 = 0b111111111111111111111111111111111111111111111111L;
    col2 = 0b111111111111111111111111111111111111111111111111L;
  }
  
  @Override
  public String toString() {
    return getDebugString();
  }

  public boolean isMaskSetted(long mask0, long mask1) {
    return (col1 & mask0 | col2 & mask1) != 0;
  }
}
