package ww;

import java.util.Scanner;

import javax.xml.bind.ValidationException;

public class GameState {
  public static int size;
  public static int unitsPerPlayer;
  
  public int legalActions;
  public Grid grid = new Grid();
  public Agent agents[];
  public static long startTime;
  
  public GameState() {
    agents = new Agent[2*2];
    for (int i=0;i<2*2;i++) {
      agents[i] = new Agent(i);
    } 
  }

  public void backup() {
    grid.backup();
    for (int i=0;i<agents.length;i++) {
      agents[i].backup();
    }
  }
  
  public void restore() {
    grid.restore();
    for (int i=0;i<agents.length;i++) {
      agents[i].restore();
    }
  }

  public void readInit(Scanner in) {
    size = in.nextInt();
    unitsPerPlayer = in.nextInt();

    
  }

  public void readRound(Scanner in) {
    grid.reset();
    
    for (int y = 0; y < size; y++) {
      String row = in.next();
      for (int x=0;x<size;x++) {
        char c = row.charAt(x);
        if (c == '.') {
          grid.setHole(x, y);
        } else {
          grid.setHeight(x, y, c-'0');
        }
      }
    }
    startTime = System.currentTimeMillis();
    
    for (int i = 0; i < unitsPerPlayer; i++) {
      Agent agent = agents[i];
      agent.x = in.nextInt();
      agent.y = in.nextInt();
    }
    for (int i = 0; i < unitsPerPlayer; i++) {
      Agent agent = agents[unitsPerPlayer+i];
      agent.x = in.nextInt();
      agent.y = in.nextInt();
    }
    
    legalActions = in.nextInt();
    for (int i = 0; i < legalActions; i++) {
      in.next();
      in.nextInt();
      in.next();
      in.next();
    }
  }

  public void debugHoles() {
    grid.debugHoles();
  }

  public void debugLayers() {
    grid.debugLayers();
  }

  public int getHeight(int x, int y) {
    return grid.getHeight(x, y);
  }
  
  public boolean isOccupied(int id, int x, int y) {
    int occupiedBy = occupiedBy(x, y);
    return (occupiedBy != -1 && occupiedBy != id);
  }

  public boolean isFriendly(int id, int x, int y) {
    int occupiedBy = occupiedBy(x, y);
    if (occupiedBy == -1) return false;
    
    if (id <=1 && occupiedBy <=1) return true;
    if (id >1 && occupiedBy >1) return true;
    return false;
  }

  public int occupiedBy(int x, int y) {
    for (int i=0;i<agents.length;i++) {
      if ( agents[i].x == x && agents[i].y == y) return i;
    }
    return -1;
  }
  
  public boolean isValid(int x, int y) {
    boolean isValid = true;
    isValid = isValid && x >= 0 && x<size && y>=0 && y<size;
    isValid = isValid && grid.isValid(x, y);
    return isValid;
  }

  public void setHeight(int x, int y, int height) {
    grid.setHeight(x, y, height);
  }

  public void toTDD() {
    System.err.println("@Test");
    System.err.println("public void test() {");
    System.err.println("  state.size = "+size+";");
    for (Agent agent : agents) {
      System.err.println("  TU.setAgent(state, "+agent.id+","+agent.x+","+agent.y+");");
    }
    
    System.err.println("  TU.setHeights(state, ");
    for (int y=0;y<size;y++) {
      System.err.print("    \"");
      for (int x=0;x<size;x++) {
        int height = grid.getHeight(x, y);
        if (height == -1) { 
          System.err.print(".");
        } else {
          System.err.print(""+height);
        }
      }
      System.err.print("\"");
      if (y != size-1) {
        System.err.println(",");
      } else {
        System.err.println(");");
      }
    }
    
    System.err.println("}");
  }

  public void getFloodFillInfo() {
    System.err.println("Flood fill: ");
    for (int i=0;i<4;i++) {
      int movable = getReachableCells(i);
      System.err.println(" for "+i+" => "+ movable);
    }
  }
  
  long visited;
  long validPosMask;
  public int getReachableCells(int id) {
    if (agents[id].inFogOfWar()) return 0;
    
    visited = 0L;
    validPosMask = (0xFFFFFFFFFFFFFFFFL & ~grid.holes) & ~grid.ceiling;

    int x = agents[id].x;
    int y = agents[id].y;
    
    return getReachableCells(Grid.toBitMask(x, y), getHeight(x, y)) -1 ;// -1 to remove the cell we are one
  }

  private int getReachableCells(long positionMask, int fromHeight) {

    if ((positionMask & visited) != 0) return 0; // already visited

    int currentHeight = grid.getHeightFromMask(positionMask);
    if (currentHeight > fromHeight+1) return 0; // step too high

    visited |= positionMask;
    int count = 1;
    
    long newPosition;
    
    newPosition = (positionMask << 1) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask >>> 1) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask << 8) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask >>> 8) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask << 7) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask << 9) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask >>> 7) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }
    newPosition = (positionMask >>> 9) & validPosMask;
    if (newPosition != 0) { count+=getReachableCells(newPosition, currentHeight); }

    return count;
  }

  public int getPossibleActions(int id) {
   if (agents[id].inFogOfWar()) return 0;
    
    validPosMask = (0xFFFFFFFFFFFFFFFFL & ~grid.holes) & ~grid.ceiling;
        
    
    int x = agents[id].x;
    int y = agents[id].y;
    
    long positionMask = Grid.toBitMask(x, y);
    int currentHeight = getHeight(x, y);
    
    int count = 0;
    
    // move positions
    long newPosition;
    newPosition = (positionMask << 1) & validPosMask;
    if (newPosition != 0) { count+=getPossibleActions(newPosition, currentHeight); }
    newPosition = (positionMask >>> 1) & validPosMask;
    if (newPosition != 0) { count+=getPossibleActions(newPosition, currentHeight); }
    newPosition = (positionMask << 8) & validPosMask;
    if (newPosition != 0) { count+=getPossibleActions(newPosition, currentHeight); }
    newPosition = (positionMask >>> 8) & validPosMask;
    if (newPosition != 0) { count+=getPossibleActions(newPosition, currentHeight); }
    newPosition = (positionMask << 7) & validPosMask;
    if (newPosition != 0) { count+=getPossibleActions(newPosition, currentHeight); }
    newPosition = (positionMask << 9) & validPosMask;
    if (newPosition != 0) { count+=getPossibleActions(newPosition, currentHeight); }
    newPosition = (positionMask >>> 7) & validPosMask;
    if (newPosition != 0) { count+=getPossibleActions(newPosition, currentHeight); }
    newPosition = (positionMask >>> 9) & validPosMask;
    if (newPosition != 0) { count+=getPossibleActions(newPosition, currentHeight); }

    // push
    if (id == 0 || id == 1) {
      validPosMask = 0x0L | Grid.toBitMask(agents[2].y, agents[2].y) | Grid.toBitMask(agents[3].y, agents[3].y);
    } else {
      validPosMask = 0x0L | Grid.toBitMask(agents[0].y, agents[0].y) | Grid.toBitMask(agents[1].y, agents[1].y);
    }
    newPosition = (positionMask << 1) & validPosMask;
    if (newPosition != 0) { count+=1; }
    newPosition = (positionMask >>> 1) & validPosMask;
    if (newPosition != 0) { count+=1; }
    newPosition = (positionMask << 8) & validPosMask;
    if (newPosition != 0) { count+=1; }
    newPosition = (positionMask >>> 8) & validPosMask;
    if (newPosition != 0) { count+=1; }
    newPosition = (positionMask << 7) & validPosMask;
    if (newPosition != 0) { count+=1; }
    newPosition = (positionMask << 9) & validPosMask;
    if (newPosition != 0) { count+=1; }
    newPosition = (positionMask >>> 7) & validPosMask;
    if (newPosition != 0) { count+=1; }
    newPosition = (positionMask >>> 9) & validPosMask;
    if (newPosition != 0) { count+=1; }

    return count;
  }

  private int getPossibleActions(long positionMask, int fromHeight) {
    int currentHeight = grid.getHeightFromMask(positionMask);

    if (currentHeight > fromHeight+1) return 0; // stop too high

    return 1;
  }
}
