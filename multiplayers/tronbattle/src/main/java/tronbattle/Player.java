package tronbattle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Player {
  private static final int TEMP_FLOODFILL = 10;
  private static final int ANTICIPATE_MOVE_ID= 11;
  
  static int[][] board = new int[30][20];
  
  static int floodFillRecursive(int[][] board, int x, int y) {
    board[x][y] = TEMP_FLOODFILL;
    int freeSquare = 1;

    if (x < 29 && board[x + 1][y] == 0) {
      freeSquare += floodFillRecursive(board, x + 1, y);
    }
    if (x > 0 && board[x - 1][y] == 0) {
      freeSquare += floodFillRecursive(board, x - 1, y);
    }
    if (y < 19 && board[x][y + 1] == 0) {
      freeSquare += floodFillRecursive(board, x, y + 1);
    }
    if (y > 0 && board[x][y - 1] == 0) {
      freeSquare += floodFillRecursive(board, x, y - 1);
    }
    return freeSquare;
  }

  public static int floodFill(int x, int y) {
    int free = floodFillRecursive(board, x, y);
    cleanBoardforId(TEMP_FLOODFILL);
    return free;
  }

  private static void cleanBoardforId(int id) {
    for (int i = 0; i < 30; i++) {
      for (int j = 0; j < 20; j++) {
        if (board[i][j] == id) {
          board[i][j] = 0;
        }
      }
    }
  }

  private static void fillOne(int x, int y, int id) {
    if (x < 29 && board[x + 1][y] == 0) {
      board[x + 1][y] = id;
    }
    if (x > 0 && board[x - 1][y] == 0) {
      board[x - 1][y] = id;
    }
    if (y < 19 && board[x][y + 1] == 0) {
      board[x ][y+1] = id;
    }
    if (y > 0 && board[x][y - 1] == 0) {
      board[x ][y-1] = id;
    }
  }

  static class P {
    public P() {
      x = y = 0;
    }

    public P(int x, int y) {
      this.x = x;
      this.y = y;
    }

    int x;
    int y;
    public int manhattanDistance(P newPos) {
      return Math.abs(x-newPos.x) + Math.abs(y-newPos.y);
    }
  }

  static class Cycle {
    int id;
    P lastPos;
    P newPos;
    public int areaPoints;

    public Cycle(int i) {
      id = i;
    }

    boolean alreadyPlay() {
      return lastPos.x == newPos.x && lastPos.y == newPos.y;
    }
  }

  static List<Cycle> cycles = new ArrayList<>();
  private static int n;
  private static int myId;
  private static int[][][] distanceBoards;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    // game loop
    while (true) {
      distanceBoards = new int[4][30][20];

      n = in.nextInt();
      myId = in.nextInt();

      for (int i = 0; i < n; i++) {
        int X0 = in.nextInt(); // starting X coordinate of lightcycle (or -1)
        int Y0 = in.nextInt(); // starting Y coordinate of lightcycle (or -1)
        int X1 = in.nextInt(); // starting X coordinate of lightcycle (can be
                               // the same as X0 if you play before this player)
        int Y1 = in.nextInt(); // starting Y coordinate of lightcycle (can be
                               // the same as Y0 if you play before this player)

        Cycle cycle = findCycleWithId(i);
        if (cycle == null) {
          cycle = new Cycle(i);
          cycles.add(cycle);
        }
        if (X0 == -1) {
          // cycle is dead
          System.err.println("Cycle is dead, cleaning");
          cleanCycleRibbon(cycle);
          cycles.remove(cycle);
          continue;
        } else {
          cycle.lastPos = new P(X0, Y0);
          cycle.newPos = new P(X1, Y1);
          board[X0][Y0] = i + 1;
          board[X1][Y1] = i + 1;
        }

      }
      // debugBoard();
      String output = think();
      System.out.println(output); // A single line with UP, DOWN, LEFT or RIGHT
    }
  }


  private static void debugBoard() {
    for (int j = 0; j < 20; j++) {
      for (int i = 0; i < 30; i++) {
        System.err.print(board[i][j]);
      }
      System.err.println("");
    }
  }

  private static boolean isLegit(P point) {
    return point.x >=0 && point.x <=29 && point.y >=0 && point.y <=19;
  }
  
  private static String think() {
    Cycle myCycle = findCycleWithId(myId);
    
    // anticipate move from other lightcycle with id > myId
//    for (Cycle cycle : cycles) {
//      if (cycle.id > myId && cycle.newPos.manhattanDistance(myCycle.newPos) > 2) {
//        fillOne(cycle.newPos.x, cycle.newPos.y, ANTICIPATE_MOVE_ID);
//      }
//    }
    
    
    int X1 = myCycle.newPos.x;
    int Y1 = myCycle.newPos.y;
    
    String output = "";
    
    P right = new P(X1+1, Y1);
    P left = new P(X1-1, Y1);
    P up = new P(X1, Y1-1);
    P down = new P(X1, Y1+1);
    List<P> pointsToTest = Arrays.asList( right, left, down, up);
    
    
    int bestScore = 0;
    P bestP = null;
    for (P p : pointsToTest) {
      if (!isLegit(p) || !isEmpty(p)) continue;
      int score = getControlledAreaSize(p);
      //int score = floodFill(p.x, p.y);
      if (score > bestScore) {
        bestScore = score;
        bestP = p;
      }
    }
    if (bestP == up) {
      output = "UP";
    } else if (bestP == down) {
      output = "DOWN";
    } else if (bestP == right) {
      output = "RIGHT";
    } else {
      output = "LEFT";
    }
    
    cleanBoardforId(ANTICIPATE_MOVE_ID);
    return output;
  }

  private static int getControlledAreaSize(P point) {
    Map<Cycle, List<P>> cycleExtensions = new HashMap<>();
    Cycle myCycle = null;
    
    int bonus = calculateBonus(point);
        
    
    for (Cycle cycle : cycles) {
      ArrayList<P> extensionPoints = new ArrayList<>();
      if (cycle.id != myId) {
        extensionPoints.add(cycle.newPos);
      } else {
        myCycle = cycle;
        extensionPoints.add(point);
      }
      cycleExtensions.put(cycle, extensionPoints);
      cycle.areaPoints = 0;
    }

    boolean movement = true;
    boolean firstTurn = true;
    while (movement) {
      movement = false;
      // TODO loop through cycle in the same order (me -> end, 0->me (exclusive)
      for (Cycle cycle : cycles) {
        List<P> extensionPoints = cycleExtensions.get(cycle);
        List<P> newExtensionPoints = new ArrayList<>();

        for (P p : extensionPoints) {
          if (firstTurn || (isLegit(p) && isEmpty(p))) {
            if (!firstTurn) {
              board[p.x][p.y] = TEMP_FLOODFILL;
              cycle.areaPoints++;
            }
            movement = true;
            add4directions(newExtensionPoints, p);
          }
        }
        cycleExtensions.put(cycle, newExtensionPoints);
      }
      firstTurn = false;
    }
    for (Cycle cycle : cycles) {
      System.err.println("cycle "+cycle.id+" area : "+cycle.areaPoints);
    }
    cleanBoardforId(TEMP_FLOODFILL);
    return myCycle.areaPoints + 10*bonus;
  }

  private static int calculateBonus(P point) {
    List<P> testDirections = new ArrayList<>();
    add4directions(testDirections, point);
    int neighborsCount = 0;
    for (P p : testDirections) {
      if (isLegit(p)) {
        if (!isEmpty(p)) {
          neighborsCount++;
        }
      }
    }
    int bonus = neighborsCount>=2 ? 1 : 0;
    return bonus;
  }

  private static void add4directions(List<P> newExtensionPoints, P p) {
    newExtensionPoints.add(new P(p.x+1, p.y));
    newExtensionPoints.add(new P(p.x-1, p.y));
    newExtensionPoints.add(new P(p.x, p.y+1));
    newExtensionPoints.add(new P(p.x, p.y-1));
  }

  private static boolean isEmpty(P p) {
    return board[p.x][p.y] == 0;
  }

  private static Cycle findCycleWithId(int i) {
    for (Cycle cycle : cycles) {
      if (cycle.id == i) {
        return cycle;
      }
    }
    return null;
  }

  private static void cleanCycleRibbon(Cycle deadCycle) {
    cleanBoardforId(deadCycle.id+1);
  }
}
