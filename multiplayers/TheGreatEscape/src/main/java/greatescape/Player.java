package greatescape;

import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
public class Player {
  static Board board = new Board();
  static Point myPos;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int w = in.nextInt(); // width of the board
    int h = in.nextInt(); // height of the board
    int playerCount = in.nextInt(); // number of players (2 or 3)
    int myId = in.nextInt(); // id of my player (0 = 1st player, 1 = 2nd player,
                             // ...)

    int turn = 0;
    Point coords[] = new Point[3];
    for (int i = 0; i < 3; i++) {
      coords[i] = new Point();
    }
    myPos = coords[myId];

    // game loop
    while (true) {
      turn++;
      for (int i = 0; i < playerCount; i++) {
        int x = in.nextInt(); // x-coordinate of the player
        int y = in.nextInt(); // y-coordinate of the player
        int wallsLeft = in.nextInt(); // number of walls available for the
                                      // player
        coords[i].x = x;
        coords[i].y = y;
      }
      int wallCount = in.nextInt(); // number of walls on the board
      for (int i = 0; i < wallCount; i++) {
        int wallX = in.nextInt(); // x-coordinate of the wall
        int wallY = in.nextInt(); // y-coordinate of the wall
        String wallOrientation = in.next(); // wall orientation ('H' or 'V')
        board.addWall(wallX, wallY, wallOrientation);
      }
      board.backupCells();

      // calculate all players best trajectory
      int lengths[] = new int[3];
      int bestLength = Integer.MAX_VALUE;
      int bestId = 0;
      List<PathItem> myTrajectory = null;
      Cell myCurrentCell = null;
      for (int i=0;i<playerCount;i++) {
        Cell currentCell = board.cells[coords[i].x][coords[i].y];
        List<PathItem> trajectory = new AStar(board, currentCell, getTargetFromId(i)).find();
        lengths[i] = trajectory.size();
        System.err.println("Player "+i+" -> length="+lengths[i]);
        if (lengths[i]<bestLength) {
          bestLength = lengths[i];
          bestId = i;
        }
        if (i == myId) {
          myCurrentCell = currentCell;
          myTrajectory = trajectory;
        }
      }
      // debugTrajectory(trajectory);
      if (myId != bestId) {
        System.err.println("I will loose to "+bestId+"!");
        System.err.println("his length is "+(bestLength-1)+" , mine is "+(myTrajectory.size()-1));
      }
      
      String move = getMoveFromCells(myCurrentCell, myTrajectory.get(1).pos);
      switch (myId) {
        case 0:
          System.out.println(move);
          break;
        case 1:
          if (turn == 1) {
            System.out.println("2 " + Math.min(7, coords[0].y) + " V");
          } else {
            System.out.println(move);
          }
          break;
        case 2:
          System.out.println(move);
          break;
      }
    }
  }

  private static Target getTargetFromId(int id) {
    Target target = Target.RIGHT;
    switch (id) {
      case 0:
        target = Target.RIGHT;
        break;
      case 1:
        target = Target.LEFT;
        break;
      case 2:
        target = Target.DOWN;
        break;
    }
    return target;
  }

  private static void debugTrajectory(List<PathItem> trajectory) {
    for (PathItem item : trajectory) {
      System.err.print("(" + item.pos.x + "," + item.pos.y + ") -> ");
    }
    System.err.println("");
  }

  private static String getMoveFromCells(Cell origin, Cell destination) {
    if (origin.up == destination) {
      return "UP";
    }
    if (origin.right == destination) {
      return "RIGHT";
    }
    if (origin.down == destination) {
      return "DOWN";
    }
    if (origin.left == destination) {
      return "LEFT";
    }
    return "RIGHT";
  }
}