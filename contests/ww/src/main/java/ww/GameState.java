package ww;

import java.util.Scanner;

public class GameState {
  public  int size;
  public int unitsPerPlayer;
  
  public Grid grid = new Grid();
  public Agent agents[];

  
  public GameState() {
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

    agents = new Agent[2*unitsPerPlayer];
    for (int i=0;i<2*unitsPerPlayer;i++) {
      agents[i] = new Agent(i);
    }
  }

  public void readRound(Scanner in) {
    grid.initRound();
    
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
    
    // don't need the legal actions
    int legalActions = in.nextInt();
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

}
