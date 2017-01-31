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
  private static int playerCount;
  private static int alivePlayer;
  
  private static int w;
  private static int h;
  private static int myId;
  private static Point coords[];
  private static int turn;
  private static int[] wallsLeft;
  private static int wallCount;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    w = in.nextInt();
    h = in.nextInt();
    playerCount = in.nextInt();
    myId = in.nextInt();

    turn = 0;
    coords = new Point[3];
    wallsLeft = new int[3];
    
    for (int i = 0; i < 3; i++) {
      coords[i] = new Point();
    }
    myPos = coords[myId];

    // game loop
    while (true) {
      turn++;
      alivePlayer = 0;
      for (int i = 0; i < playerCount; i++) {
        int x = in.nextInt(); // x-coordinate of the player
        int y = in.nextInt(); // y-coordinate of the player
        int wl = in.nextInt(); // number of walls available for the // player
        alivePlayer += x != -1 ? 1 : 0;
        
        coords[i].x = x;
        coords[i].y = y;
        wallsLeft[i] = wl;
        System.err.println("Player"+i+" ("+x+","+y+","+wl+")");
      }
      wallCount = in.nextInt();
      System.err.println("Board board = new Board();");
      board.resetWalls();
      for (int i = 0; i < wallCount; i++) {
        int wallX = in.nextInt(); // x-coordinate of the wall
        int wallY = in.nextInt(); // y-coordinate of the wall
        String wallOrientation = in.next(); // wall orientation ('H' or 'V')
        board.addWall(i+1, wallX, wallY, WallOrientation.valueOf(wallOrientation));
        System.err.println("board.addWall("+(i+1)+","+wallX+", "+wallY+", WallOrientation."+wallOrientation+");");
      }

      String finalMove = calculateMove();
     
      System.out.println(finalMove);
    }
  }

  private static String calculateMove() {
    System.err.println("My id  : "+myId);
    int bestScore = evaluateMove()-myId;// substract myId to counter turn disadvantage
    System.err.println("Score for move is "+bestScore);
    String finalMove = null;
    
    if (bestScore < 0 && wallsLeft[myId] > 0) {
//      System.err.println("I'm loosing if just moving, need to put a  wall");
    
      board.backupCells();
      for (int x=1;x<w-1;x++) {
        for (int y=1;y<h-1;y++) {
          for (WallOrientation wo : WallOrientation.values()) {
            board.restore();
            if (!board.addWall(wallCount+1,x, y, wo)) {
              continue;
            }
            int score = evaluateMove()-myId; 
            if (score < -1000) {
              System.err.println("Doing "+x+" "+y+" "+wo+" would block a player");
            }
            if (score > bestScore) {
              bestScore = score;
              finalMove = ""+x+" "+y+" "+wo.toString();
              System.err.println("New best score: "+bestScore+" for move "+finalMove);
              System.err.println("My trajectory");
              List<PathItem> trajectory = new AStar(board, board.cells[coords[myId].x][coords[myId].y], getTargetFromId(myId)).find();
              debugTrajectory(trajectory);
              System.err.println("others trajectorys");
              for (int i=0;i<playerCount;i++) {
                if (i == myId) continue;
                if (coords[i].x == -1) continue;
                System.err.println("player "+i);
                List<PathItem> trajectory2 = new AStar(board, board.cells[coords[i].x][coords[i].y], getTargetFromId(i)).find();
                debugTrajectory(trajectory2);
              }
            }
          }
        }
      }
    } 
    if (finalMove == null) {
      // need to get the trajectory back
      Cell currentCell = board.cells[myPos.x][myPos.y];
      List<PathItem> trajectory = new AStar(board, currentCell, getTargetFromId(myId)).find();
      debugTrajectory(trajectory);
      if (trajectory.size() > 1) {
        finalMove = getMoveFromCells(currentCell, trajectory.get(1).pos);
      } else {
        finalMove = "RIGHT ERROR";
      }
    }
    return finalMove;
  }

  private static int evaluateMove() {
    int lengths[] = new int[3];
    int bestLength = Integer.MAX_VALUE;
    int bestId = 0;
    List<PathItem> myTrajectory = null;
    Cell myCurrentCell = null;
    
    for (int i=0;i<playerCount;i++) {
      if ( coords[i].x == -1) {
        continue; // dead player
      }
      
      Cell currentCell = board.cells[coords[i].x][coords[i].y];
      List<PathItem> trajectory = new AStar(board, currentCell, getTargetFromId(i)).find();
      lengths[i] = trajectory.size();
      if (lengths[i]<bestLength) {
        bestLength = lengths[i];
        bestId = i;
      }
      if (i == myId) {
        myCurrentCell = currentCell;
        myTrajectory = trajectory;
      }
    }

    int score = 0;
    for (int i=0;i<playerCount;i++) {
      if (lengths[i] <= 1) {
        if ( coords[i].x == -1) {
          continue; // dead player
        }
        return -5000;
      }
      if (i!= myId) {
        score +=lengths[i];
      } else {
        score -=(alivePlayer-1)*lengths[i];
      }
    }
    return score;
  }

  static Target getTargetFromId(int id) {
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

  static void debugTrajectory(List<PathItem> trajectory) {
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