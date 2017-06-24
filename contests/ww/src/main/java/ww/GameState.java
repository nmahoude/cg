package ww;

import java.util.Scanner;

public class GameState {
  public  int size;
  public int unitsPerPlayer;
  
  public int unitX[];
  public int unitY[];

  long players[];
  long holes =  0; // 1 in x*y is a hole
  long layer1, layer2;
  long ceiling = 0;
  
  GameState() {
  }

  public void readInit(Scanner in) {
    size = in.nextInt();
    unitsPerPlayer = in.nextInt();

    unitX = new int[2*unitsPerPlayer];
    unitY = new int[2*unitsPerPlayer];
    players = new long[2*unitsPerPlayer];
  }

  public static final long toBitMask(int x, int y) {
    return 0b1L << (x+8L*y);
  }
  
  public void readRound(Scanner in) {
    holes = 0xFFFFFFFFFFFFFFFFL;
    layer1 = layer2 = 0L;
    
    for (int y = 0; y < size; y++) {
      String row = in.next();
      for (int x=0;x<size;x++) {
        char c = row.charAt(x);
        if (c == '.') {
          holes |= toBitMask(x, y);
        } else {
          long bitToSet = toBitMask(x, y);
          holes &= ~bitToSet;
          int layer = c-'0';
          if (layer < 4) {
            if (layer == 1 || layer == 3) {
              layer1 |= bitToSet;
            }
            if (layer == 2 || layer == 3) {
              layer2 |= bitToSet;
            }
          } else {
            // ceiling
            ceiling |= bitToSet;
          }
        }
      }
    }
    holes |=0b1000000000000000000000000000000000000000000000000000000000000000L;
    
    for (int i = 0; i < unitsPerPlayer; i++) {
      unitX[i] = in.nextInt();
      unitY[i] = in.nextInt();
      players[i] = toBitMask(unitX[i] , unitY[i]);
    }
    for (int i = 0; i < unitsPerPlayer; i++) {
      unitX[unitsPerPlayer+i] = in.nextInt();
      unitY[unitsPerPlayer+i] = in.nextInt();
      players[unitsPerPlayer+i] = toBitMask(unitX[unitsPerPlayer+i],unitY[unitsPerPlayer+i]);
    }
    
    // don't need the legal actions
    int legalActions = in.nextInt();
    for (int i = 0; i < legalActions; i++) {
      in.next();
      in.nextInt();
      in.next();
      in.next();
    }
    // debugHoles();
    // debugLayers();
  }

  public void debugHoles() {
    for (int i=0;i<8;i++) {
      System.err.println(
          new StringBuilder(
              Long.toBinaryString(holes).substring(8*(7-i), 8*(7-i)+8)
          ).reverse().toString());
    }
  }

  public void debugLayers() {
      System.err.println("layer "+1+" :");
      long layer = layer1;
      layer |=0b1000000000000000000000000000000000000000000000000000000000000000L;
      for (int i=0;i<8;i++) {
        System.err.println(
            new StringBuilder(
                Long.toBinaryString(layer).substring(8*(7-i), 8*(7-i)+8)
            ).reverse().toString());
      }
      
      System.err.println("layer "+2+" :");
      layer = layer2;
      layer |=0b1000000000000000000000000000000000000000000000000000000000000000L;
      for (int i=0;i<8;i++) {
        System.err.println(
            new StringBuilder(
                Long.toBinaryString(layer).substring(8*(7-i), 8*(7-i)+8)
            ).reverse().toString());
      }
  }

  int getHeight(int x, int y) {
    long bitToTest = toBitMask(x, y);
    
    if ((holes & bitToTest) != 0L) return -1;
    if ((ceiling & bitToTest) != 0L) return 4;
    
    int height = 0;
    if ((layer1 & bitToTest) != 0L)  height += 1;
    if ((layer2 & bitToTest) != 0L)  height += 2;
    
    return height;
  }
  
  boolean isOccupied(int id, int x, int y) {
    long bitToTest = toBitMask(x, y);
    
    for (int i=0;i<2*unitsPerPlayer;i++) {
      if (i == id ) continue; // Id won't be there after moving
      if ((players[i] & bitToTest) != 0) return true;
    }
    return false;
  }

  public boolean isFriendly(int id, int x, int y) {
    long bitToTest = toBitMask(x, y);
    
    for (int i=0;i<2*unitsPerPlayer;i++) {
      if (i == id ) continue; // Id won't be there after moving
      if ((players[i] & bitToTest) != 0 && ( (id <= 1 && i <=1) || (id>1 && i>1))) return true;
    }
    return false;
  }

  
  boolean isValid(int x, int y) {
    boolean isValid = true;
    isValid = isValid && x >= 0 && x<size && y>=0 && y<size;
    long bitToTest = toBitMask(x, y);
    
    isValid = isValid 
        && ((holes & bitToTest) == 0) 
        && ((ceiling & bitToTest) == 0);
    return isValid;
  }

}
