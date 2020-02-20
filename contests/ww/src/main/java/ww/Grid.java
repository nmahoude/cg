package ww;

public class Grid {
  public static long holes = 0; // 1 in x*y is a hole, static because it is immutable
  
  public long layer1, layer2;
  public long ceiling = 0;

  long _layer1, _layer2, _ceiling;

  public void backup() {
    _layer1 = layer1;
    _layer2 = layer2;
    _ceiling = ceiling;
  }
  
  public void restore() {
    layer1 = _layer1;
    layer2 = _layer2;
    ceiling = _ceiling;
  }

  public void reset() {
    holes = 0xFFFFFFFFFFFFFFFFL;
    layer1 = layer2 = 0L;
    holes |= 0b1000000000000000000000000000000000000000000000000000000000000000L; // TODO NEEDED ?
  }

  public void setHole(int x, int y) {
    holes |= toBitMask(x, y);
  }

  public void setHeight(int x, int y, int height) {
    long bitToSet = toBitMask(x, y);
    holes &= ~bitToSet;
    if (height < 4) {
      setHeight(bitToSet, height);
    } else {
      // ceiling
      ceiling |= bitToSet;
    }
  }

  public void setHeight(long bitToSet, int height) {
    if ((height & 0b1L) != 0) {
      layer1 |= bitToSet;
      ceiling &= ~bitToSet; // unset ceiling bit
    } else {
      layer1 &= ~bitToSet; 
    }
    if ((height & 0b10L) != 0) {
      layer2 |= bitToSet;
      ceiling &= ~bitToSet; // unset ceiling bit
    } else {
      layer2 &= ~bitToSet;
    }
  }

  public static final long toBitMask(int x, int y) {
    return 0b1L << (x + 8L * y);
  }

  public void debugHoles() {
    for (int i = 0; i < 8; i++) {
      System.err.println(
          new StringBuilder(
              Long.toBinaryString(holes).substring(8 * (7 - i), 8 * (7 - i) + 8)).reverse().toString());
    }
  }

  public void debugLayers() {
    System.err.println("layer " + 1 + " :");
    debugLayer(layer1);
    
    System.err.println("layer " + 2 + " :");
    debugLayer(layer2);
  }

  public static void debugLayer(long layer) {
    layer |= 0b1000000000000000000000000000000000000000000000000000000000000000L;
    for (int i = 0; i < 8; i++) {
      System.err.println(
          new StringBuilder(
              Long.toBinaryString(layer).substring(8 * (7 - i), 8 * (7 - i) + 8)).reverse().toString());
    }
  }
  public int getHeight(int x, int y) {
    long bitToTest = toBitMask(x, y);

    return getHeightFromMask(bitToTest);
  }

  public int getHeightFromMask(long bitToTest) {
    if (((holes|ceiling) & bitToTest ) != 0L) return 4;

    return (((layer1 & bitToTest) != 0L) ? 1:0) 
        + (((layer2 & bitToTest) != 0L) ? 2:0);
  }

  public boolean isValid(int x, int y) {
    long bitToTest = toBitMask(x, y);
    return ((holes & bitToTest) == 0) 
        && ((ceiling & bitToTest) == 0);
  }

  public long getFloodFillMask(long initialPositionMask, long toFill) {
    long currentMask = initialPositionMask;
    long nextMask = initialPositionMask;
    do {
      currentMask = nextMask;

      nextMask = (currentMask | (currentMask << 8L) | (currentMask >>> 8L) | (currentMask << 1L) | (currentMask >>> 1L));
      nextMask = (nextMask | (currentMask << 7L) | (currentMask >>> 7L) | (currentMask << 9L) | (currentMask >>> 9L));
      nextMask &= toFill;
    } while (nextMask != currentMask);
    return currentMask;
  }
}
