package ww;

import java.util.Scanner;

import javax.xml.bind.ValidationException;

public class GameState {
  public static int size;
  public static int unitsPerPlayer;
  
  public int legalActions;
  public Grid grid;
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

    grid = new Grid(size);
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
    
    for (int i = 0; i < 2*unitsPerPlayer; i++) {
      Agent agent = agents[i];
      Point position = Point.get(in.nextInt(), in.nextInt());
      positionAgent(agent, position);
    }
    
    legalActions = in.nextInt();
    for (int i = 0; i < legalActions; i++) {
      in.next();
      in.nextInt();
      in.next();
      in.next();
    }
    
    backup();
  }
  
  public void positionAgent(Agent agent, Point position) {
    agent.position = position;
    
    if (agent.position != Point.unknown) {
      if (agent.cell != null) {
        agent.cell.agent = null; // detach agent
      }
      agent.cell = grid.get(agent.position.x, agent.position.y);
      agent.cell.agent = agent;
    } else {
      if (agent.cell != null) {
        agent.cell.agent = null;
      }
      agent.cell = Cell.InvalidCell;
    }
  }
  
  public void toTDD() {
    System.err.println("@Test");
    System.err.println("public void test() {");
    System.err.println("  state.size = "+size+";");
    System.err.println("  state.readInit(new Scanner(\"\" + state.size + \" 2\"));");
    System.err.println("  TU.setHeights(state, ");
    for (int y=0;y<size;y++) {
      System.err.print("    \"");
      for (int x=0;x<size;x++) {
        Cell cell = grid.get(x, y);
        if (cell.isHole) { 
          System.err.print(".");
        } else {
          System.err.print(""+cell.height);
        }
      }
      System.err.print("\"");
      if (y != size-1) {
        System.err.println(",");
      } else {
        System.err.println(");");
      }
    }
    for (Agent agent : agents) {
      System.err.println("  TU.setAgent(state, "+agent.id+","+agent.position.x+","+agent.position.y+");");
    }
    System.err.println("  state.backup();");
    System.err.println();
    System.err.println();
    System.err.println();
    System.err.println("}");
  }

  public void copyTo(GameState expected) {
    grid.copyTo(expected);
    for (int i=0;i<4;i++) {
      agents[i].copyTo(expected, expected.agents[i]);
    }
  }

}
