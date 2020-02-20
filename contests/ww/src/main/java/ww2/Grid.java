package ww2;

public class Grid {
  static long gridMask;
  static int size;

  long layer0, layer1, layer2, layer3; // par hauteur
  
  long _layer0, _layer1, _layer2, _layer3;
  public void backup() {
    _layer0 = layer0;
    _layer1 = layer1;
    _layer2 = layer2;
    _layer3 = layer3;
  }

  public void restore() {
    layer0 = _layer0;
    layer1 = _layer1;
    layer2 = _layer2;
    layer3 = _layer3;
  }
  
  public Grid(int size) {
    Grid.size = size;
    buildGridMask();
  }
  
  static void buildGridMask() {
    long line = 0L;
    if (size == 5) line = 0b00011111L;
    if (size == 6) line = 0b00111111L;
    if (size == 7) line = 0b01111111L;
    gridMask = 0L;
    for (int i=0;i<size;i++) {
      gridMask|= line;
      line = line << 8L;
    }
  }

  public int getHeight(Point position) {
    return getHeightFromBitMask(position.mask);
  }
  public void setHeight(Point p, int height) {
    setHeightFromMask(p.mask, height);
  }
  
  public boolean isValid(Point p) {
    long mask = p.mask & gridMask;
    if (mask == 0) return false;
    return (mask & (allValidPos())) != 0;
  }
  
  
  public static void debugMask(long layer) {
    layer |= 0b1000000000000000000000000000000000000000000000000000000000000000L;
    String binaryString = Long.toBinaryString(layer);
    binaryString = "0"+binaryString.substring(1, 64); // replace the 1 in trail
    for (int i = 0; i < 8; i++) {
      System.err.println(
          new StringBuilder(
              binaryString.substring(8 * (7 - i), 8 * (7 - i) + 8)).reverse().toString());
    }
  }
  
  public int getHeightFromBitMask(long bitmask) {
    if ((layer3 & bitmask) != 0) return 3;
    if ((layer2 & bitmask) != 0) return 2;
    if ((layer1 & bitmask) != 0) return 1;
    if ((layer0 & bitmask) != 0) return 0;
    return 4;
  }
  
  private void setHeightFromMask(long mask, int height) {
    if (height == 3) { layer3 |= mask; } else { layer3 &= mask;}
    if (height == 2) { layer2 |= mask; } else { layer2 &= mask;}
    if (height == 1) { layer1 |= mask; } else { layer1 &= mask;}
    if (height == 0) { layer0 |= mask; } else { layer0 &= mask;}
  }

  public void reset() {
    layer0 = layer1 = layer2 = layer3 = 0L;
  }

  public long allValidPos() {
    return layer0 | layer1 | layer2 | layer3; // TODO Cache it ?
  }
  
}
