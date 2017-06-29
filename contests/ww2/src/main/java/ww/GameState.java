package ww;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ww.sim.Move;
import ww.think.Node;

public class GameState {
  public static final long MAX_TIME = 45;
  public static int size;
  public static int unitsPerPlayer;
  
  public int legalActions;
  public Grid grid;
  public Agent agents[];
  public static long startTime;

  public List<Move> legalActionsDepth0Cache = new ArrayList<>();
  public List<Node> legalActionDepth0NodeCache = new ArrayList<>(); // yeah, it's ugly
  
  public GameState() {
    agents = new Agent[2*2];
    for (int i=0;i<2*2;i++) {
      agents[i] = new Agent(i);
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
    legalActionsDepth0Cache.clear();
    legalActionDepth0NodeCache.clear();
    for (int i = 0; i < legalActions; i++) {
      String action = in.next(); // 
      int agentId = in.nextInt();
      String dir1 = in.next();
      String dir2 = in.next();

      Move move = new Move(agents[agentId]);
      move.isPush = "PUSH&BUILD".equals(action);
      move.dir1 = Dir.valueOf(dir1);
      move.dir2 = Dir.valueOf(dir2);
      
//      System.err.println("reading from CG : "+action+" "+agentId+" "+dir1+" "+dir2);
//      System.err.println("reading from CG : "+move);
      legalActionsDepth0Cache.add(move);
      
      Node node = new Node(1);
      node.move = move;
      legalActionDepth0NodeCache.add(node);
    }
    // System.err.println("Read "+legalActionsDepth0Cache.size()+" valid actions");
   
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

  public boolean isModified() {
    for (int i=0;i<4;i++) {
      if (agents[i].isModified()) return true;
    }
    for (int y=0;y<size;y++) {
      for (int x=0;x<size;x++) {
        Cell cell = grid.get(x, y);
        if (cell.isModified()) return true;
      }
    }
    return false;
  }

}
