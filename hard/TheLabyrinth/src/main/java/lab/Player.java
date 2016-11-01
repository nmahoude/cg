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
      int kirkY = in.nextInt(); // row where Kirk is located.
      int kirkX = in.nextInt(); // column where Kirk is located.
      kirk = new P(kirkX, kirkY);
      
      for (int y = 0; y < height; y++) {
        String row = in.next(); // C of the characters in '#.TC?' (i.e. one line
                                // of the ASCII maze).
        for (int x = 0; x < row.length(); x++) {
          char value = row.charAt(x);
          cells[x][y] = value;
          if (value == 'T') {
            exit = new P(x,y);
          } else if (value == 'C') {
            //controlRoom = new P(x,y);
          }
        }
      }

      //debugCells();

      String action = "PERDU";
      System.err.println("Kick is at " + kirk);
      if (controlRoom != null && mode == Mode.EXPLORE) {
        mode = Mode.TO_CONTROLROOM;
        System.err.println("Found control room at"+controlRoom+", go to it !");
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
        Explore e = new Explore(width, height, cells);
        P target = e.findClosedReachableCell(kirk);
        if (target != null) {
          Path astar = new Path(width, height, cells, kirk, target);
          List<PathItem> path = astar.find();
          if (!path.isEmpty()) {
             PathItem first = path.get(1);
             P pos = first.getPosition();
             action = pToAction(kirk, pos);
          } else {
            System.err.println("Path is empty");
          }
        } else {
          System.err.println("Plus de '?' à trouver");
        }
      } else {
      }

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
      for (int x = 0; x < width; x++) {
        System.err.print(cells[x][y]);
      }
      System.err.println("");
    }
  }
}