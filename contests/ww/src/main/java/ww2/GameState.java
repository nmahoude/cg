package ww2;

import java.util.Scanner;

public class GameState {
  public Grid grid;
  public Agent agents[];
  private int size;
  private int unitsPerPlayer;
  public long startTime;
  private int legalActions;
  
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
    
    grid = new Grid(size);
  }

  public void readRound(Scanner in) {
    grid.reset();
    
    for (int y = 0; y < size; y++) {
      String row = in.next();
      for (int x=0;x<size;x++) {
        char c = row.charAt(x);
        if (c == '.') {
          // holes by default !
        } else {
          grid.setHeight(Point.get(x, y), c-'0');
        }
      }
    }
    startTime = System.currentTimeMillis();
    
    for (int i = 0; i < unitsPerPlayer; i++) {
      Agent agent = agents[i];
      agent.position = Point.get(in.nextInt(), in.nextInt());
    }
    for (int i = 0; i < unitsPerPlayer; i++) {
      Agent agent = agents[unitsPerPlayer+i];
      agent.position = Point.get(in.nextInt(), in.nextInt());
    }
    
    legalActions = in.nextInt();
    for (int i = 0; i < legalActions; i++) {
      in.next();
      in.nextInt();
      in.next();
      in.next();
    }
  }

  public boolean isOccupied(int id, Point point) {
    int occupiedBy = occupiedBy(point);
    return (occupiedBy != -1 && occupiedBy != id);
  }

  public boolean isFriendly(int id, Point p) {
    int occupiedBy = occupiedBy(p.mask);
    if (occupiedBy == -1) return false;
    
    if (id <=1 && occupiedBy <=1) return true;
    if (id >1 && occupiedBy >1) return true;
    return false;
  }

  public int occupiedBy(Point p) { 
    return occupiedBy(p.mask);
  }
  public int occupiedBy(long posMask) {
    for (int i=0;i<agents.length;i++) {
      if (agents[i].position.mask == posMask) return i;
    }
    return -1;
  }
  
  public boolean isValid(Point p) {
    boolean isValid = (p.mask != 0L);
    isValid = isValid && grid.isValid(p);
    return isValid;
  }

  public void toTDD() {
    System.err.println("@Test");
    System.err.println("public void test() {");
    System.err.println("  state.size = "+size+";");
    for (Agent agent : agents) {
      System.err.println("  TU.setAgent(state, "+agent.id+","+agent.position.x+","+agent.position.y+");");
    }
    
    System.err.println("  TU.setHeights(state, ");
    for (int y=0;y<size;y++) {
      System.err.print("    \"");
      for (int x=0;x<size;x++) {
        int height = grid.getHeight(Point.get(x, y));
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
}
