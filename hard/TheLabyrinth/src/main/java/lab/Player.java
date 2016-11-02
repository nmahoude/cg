package lab;

import java.util.List;
import java.util.Scanner;

import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;

import oracle.jrockit.jfr.tools.ConCatRepository;
import utils.Explore;
import utils.P;
import utils.Path;
import utils.PathItem;

public class Player {
  static char cells[][];
  static P kirk;
  static P exit;
  static P controlRoom;

  static Mode mode = Mode.EXPLORE;
  static int height;
  static int width;

  static List<PathItem> returnPath = null;
  private static P controlRoomToBe;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    height = in.nextInt();
    width = in.nextInt();
    int A = in.nextInt(); // number of rounds between the time the alarm
                          // countdown is activated and the time the alarm goes
                          // off.

    cells = new char[width][height];

    // game loop
    while (true) {
      long tt0 = System.currentTimeMillis();
      int kirkY = in.nextInt(); // row where Kirk is located.
      int kirkX = in.nextInt(); // column where Kirk is located.
      kirk = P.get(kirkX, kirkY);

      for (int y = 0; y < height; y++) {
        String row = in.next(); // C of the characters in '#.TC?' (i.e. one line
                                // of the ASCII maze).
        for (int x = 0; x < row.length(); x++) {
          char value = row.charAt(x);
          cells[x][y] = value;
          if (value == '?') {
          }
          if (value == 'T') {
            exit = P.get(x, y);
          } else if (value == 'C') {
            controlRoomToBe = P.get(x, y);
          }
        }
      }

      debugCells();

      String action = "PERDU";
      System.err.println("Kick is at " + kirk + "in mode " + mode);

      if (controlRoom != null && mode == Mode.EXPLORE) {
        mode = Mode.TO_CONTROLROOM;
        System.err.println("Found control room at" + controlRoom + ", go to it !");
      }

      if (mode == Mode.TO_EXIT) {
        if (returnPath == null) {
          Path astar = new Path(width, height, cells, kirk, exit);
          returnPath = astar.find();
          returnPath.remove(0); // 1st is the currentPosition
        }
        if (!returnPath.isEmpty()) {
          PathItem first = returnPath.remove(0);
          P pos = first.getPosition();
          action = pToAction(kirk, pos);
        }
      }
      if (mode == Mode.TO_CONTROLROOM) {
        Path astar = new Path(width, height, cells, kirk, controlRoom);
        List<PathItem> path = astar.find();
        if (astar.controlRoom != null) {
          controlRoom = astar.controlRoom;
        }
        if (!path.isEmpty()) {
          PathItem first = path.get(1);
          P pos = first.getPosition();
          if (pos.equals(controlRoom)) {
            mode = Mode.TO_EXIT;
            System.err.println("Kirk found controlRoom, next rush to exit");
          }
          action = pToAction(kirk, pos);
        }
      }
      if (mode == Mode.EXPLORE) {
        System.err.println("Exploring");
        Explore e = new Explore(width, height, cells);
        long t1 = System.currentTimeMillis();
        P target = e.findClosedReachableCell(kirk);
        long t2 = System.currentTimeMillis();
        System.err.println("Explore took : "+(t2-t1)+"");
        if (target != null) {
          Path astar = new Path(width, height, cells, kirk, target);
          List<PathItem> path = astar.find();
          long t3= System.currentTimeMillis();
          System.err.println("AStar took : "+(t3-t2)+"");
          if (!path.isEmpty()) {
            PathItem first = path.get(1);
            P pos = first.getPosition();
            action = pToAction(kirk, pos);
          } else {
            System.err.println("Path is empty");
          }
        } else {
          System.err.println("Plus de '?' à trouver");
          controlRoom = controlRoomToBe;
          mode = Mode.TO_CONTROLROOM;
          System.err.println("Switching to mode " + mode + " no more '?' to find");
          System.out.println("RIGHT");
          continue;
          
        }
      } else {
      }

      long tt1 = System.currentTimeMillis();
      System.err.println("All took : "+(tt1-tt0)+"");
      System.out.println(action); // Kirk's next move (UP DOWN LEFT or RIGHT).
    }
  }

  private static String pToAction(P kirk, P pos) {
    System.err.println("Find move from " + kirk + " to " + pos);

    String action = "";
    if (pos.x == kirk.x + 1) {
      action = "RIGHT";
    } else if (pos.x == kirk.x - 1) {
      action = "LEFT";
    } else if (pos.y == kirk.y + 1) {
      action = "DOWN";
    } else if (pos.y == kirk.y - 1) {
      action = "UP";
    } else {
      System.err.println("Can't find move from " + kirk + " to " + pos);
    }
    return action;
  }

  private static void debugCells() {
    System.err.println("Labyrinth : ");
    for (int y = 0; y < height; y++) {
      System.err.print("\"");
      for (int x = 0; x < width; x++) {
        System.err.print(cells[x][y]);
      }
      System.err.print("\"");
      if (y != height-1) {
        System.err.print(",");
      }
      System.err.println("");
    }
  }
}