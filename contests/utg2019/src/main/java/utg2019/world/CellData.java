package utg2019.world;

import utg2019.Player;

public class CellData {
  public static final int ORE_MASK   = 0b0000111;
  public static final int HOLE_MASK  = 0b0001000;
  public static final int KNOWN_MASK = 0b0010000;
  public static final int RADAR_MASK = 0b0100000;
  public static final int TRAP_MASK  = 0b1000000;

 
  public final MapCell mapCell;
  public int currentlyKnown = 0;
  
  // correct values
  public int ore;
  public boolean hole;
  public boolean radar; // myself
  public boolean trap; // myself

  

  public static long setOre(long mask, int ore) {
    return (mask & ~ORE_MASK) | (ore+1);
  }
  
  public static int getOre(long mask) {
    return (int)(mask & ORE_MASK)-1;
  }
  public static boolean hasRadar(long mask) {
    return (mask & RADAR_MASK) != 0;
  }
  public static boolean hasTrap(long mask) {
    return (mask & TRAP_MASK) != 0;
  }
  public static boolean hasHole(long mask) {
    return (mask & HOLE_MASK) != 0;
  }
  
  // potential unknwon values
  double hisRadar; // % of chance
  double hisMine; //% of chance

  public CellData(MapCell mapCell) {
    this.mapCell = mapCell;
  }

  public boolean currentlyKnown() {
    return currentlyKnown > 0;
  }
  
  public void read(String oreStr, String holeStr) {
    this.radar = false;
    this.trap = false;
    
    if (Player.DEBUG_INPUT) {
      System.err.print(oreStr+" ");
    }
    if (oreStr.charAt(0) == '?') {
      currentlyKnown = 0;
      ore = 0;
    } else {
      ore = oreStr.charAt(0) - '0';
    }
    if (Player.DEBUG_INPUT) {
      System.err.print(holeStr+" ");
    }
    hole = (holeStr.charAt(0) != '0');
  }

  public void copyFrom(CellData model) {
    this.ore = model.ore;
    this.hole = model.hole;
    this.radar = model.radar;
    this.trap = model.trap;
    this.hisRadar = model.hisRadar;
    this.hisMine = model.hisMine;
    this.currentlyKnown = model.currentlyKnown;
  }

  public void updateCurrentlyKnown(int dr) {
    currentlyKnown+= dr;
  }

  public static long setHole(long mask, boolean hole) {
    if (hole) {
      return mask | HOLE_MASK;
    } else {
      return mask & ~HOLE_MASK;
    }
  }

  public static long setRadar(long mask, boolean radar) {
    if (radar) {
      return mask | RADAR_MASK;
    } else {
      return mask & ~RADAR_MASK;
    }    
  }

  public static long setTrap(long mask, boolean trap) {
    if (trap) {
      return mask | TRAP_MASK;
    } else {
      return mask & ~TRAP_MASK;
    }    
  }
  
  public static boolean isCurrentlyKnown(long mask) {
    return (mask & KNOWN_MASK) != 0;
  }

  public static long setCurrentlyKnown(long mask) {
    return mask | KNOWN_MASK;
  }

  public static long setCurrentlyKnown(long mask, boolean value) {
    if (value) {
      return mask | KNOWN_MASK;
    } else {
      return mask & ~KNOWN_MASK;
    }
  }

  public static long reset(long mask) {
    // TODO history bits to keep ?
    return 0;
  }
}
