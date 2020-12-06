import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Solution {
  static int[][] Directions = {
      {0, 1},  // s
      {1, 0}, // e
      {0, -1}, // n
      {-1, 0}   // w
  };
  static final int DIR_SOUTH = 0;
  static final int DIR_EAST  = 1;
  static final int DIR_NORTH = 2;
  static final int DIR_WEST  = 3;
  
  static final char WALL = '#';
  static final char START_PLACE = '@';
  static final char SUICIDE_BOOTH = '$';
  static final char OBSTACLE = 'X';
  static final char MODIFIER_SOUTH = 'S';
  static final char MODIFIER_EAST = 'E';
  static final char MODIFIER_NORTH = 'N';
  static final char MODIFIER_WEST = 'W';
  static final char CIRCUIT_INVERTER = 'I';
  static final char BEER = 'B';
  static final char TELEPORT = 'T';
  static final char SPACE = ' ';
  
  static char board[][];
  static int  loopDetector[][][/*state*/];
  static int loopDetectorIndex = 1;
  
  static int benderX,benderY; // bender position
  static int directionIndex = 0;
  static int priorityCheck = 1;
  static boolean breakerMode;
  
  static int teleporter1X=-1,teleporter1Y=-1;
  static int teleporter2X=-1,teleporter2Y=-1;
  
  static List<String> path = new ArrayList<>();
  private static int startX;
  private static int  startY;
  
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int height = in.nextInt();
        int width = in.nextInt();
        in.nextLine();
        readBoard(in, height, width);

        while (board[benderX][benderY] != SUICIDE_BOOTH || (benderX == startX && benderY == startY)) {
          int expectedPosX;
          int expectedPosY;
          expectedPosX = benderX + Directions[directionIndex][0];
          expectedPosY = benderY + Directions[directionIndex][1];
          if (board[expectedPosX][expectedPosY] == WALL ||
              (breakerMode == false && board[expectedPosX][expectedPosY] == OBSTACLE)) {
            if (priorityCheck == 1) {
              directionIndex= 0;
            } else {
              directionIndex = 3;
            }
            while(true) {
              expectedPosX = benderX + Directions[directionIndex][0];
              expectedPosY = benderY + Directions[directionIndex][1];
              if (board[expectedPosX][expectedPosY] != WALL 
                  && board[expectedPosX][expectedPosY] != OBSTACLE) {
                break;
              } else {
                directionIndex+=priorityCheck;
              }
            }
          }
          if (breakerMode && board[expectedPosX][expectedPosY] == OBSTACLE) {
            board[expectedPosX][expectedPosY] = SPACE;
            loopDetectorIndex++;
          }
          
          benderX = expectedPosX;
          benderY = expectedPosY;
          if (loopDetector[benderX][benderY][getCurrentState()] == loopDetectorIndex) {
            // TODO already passing by is not garantued to form a loop
            break;
          } 
          loopDetector[benderX][benderY][getCurrentState()] = loopDetectorIndex;
          
          char currentCell = board[benderX][benderY];
          outputDir(directionIndex);

          // check modifiers
          checkBreakerMode(currentCell);
          checkInverter(currentCell);
          checkDirectionModifier(currentCell);
          checkTeleport(currentCell);
        }
        if (board[benderX][benderY] == SUICIDE_BOOTH) {
          for (String p : path) {
            System.out.println(p);
          }
        } else {
          System.err.println("Path would have been");
          for (String p : path) {
            System.err.println(p);
          }
          System.out.println("LOOP");
        }
        
    }

    private static int getCurrentState() {
      return directionIndex+4*(breakerMode?0:1);
    }

    private static void checkTeleport(char currentCell) {
      if (currentCell == TELEPORT) {
        if (benderX == teleporter1X && benderY == teleporter1Y) {
          benderX = teleporter2X;
          benderY = teleporter2Y;
        } else {
          benderX = teleporter1X;
          benderY = teleporter1Y;
        }
      }
    }

    private static void checkInverter(char currentCell) {
      if (currentCell == CIRCUIT_INVERTER) {
        priorityCheck = -priorityCheck;
      }
    }

    private static void checkBreakerMode(char currentCell) {
      if (currentCell == BEER) {
        breakerMode = !breakerMode;
      }
    }

    private static void checkDirectionModifier(char currentCell) {
      if (currentCell == MODIFIER_SOUTH) {
        directionIndex = DIR_SOUTH;
      }
      if (currentCell == MODIFIER_EAST) {
        directionIndex = DIR_EAST;
      }
      if (currentCell == MODIFIER_NORTH) {
        directionIndex = DIR_NORTH;
      }
      if (currentCell == MODIFIER_WEST) {
        directionIndex = DIR_WEST;
      }
    }

    private static void readBoard(Scanner in, int height, int width) {
      board = new char[width][height];
      loopDetector = new int[width][height][8];
      
      for (int y = 0; y < height; y++) {
          String row = in.nextLine();
          for (int x=0;x<width;x++) {
            board[x][y] = row.charAt(x);
            if (board[x][y] == START_PLACE) {
              startX = x;
              startY = y;
              setBenderStartPosition(x, y);
            }
            if (board[x][y] == TELEPORT) {
              setTeleporter(y, x);
            }
          }
      }
    }

    private static void outputDir(int directionIndex) {
      path.add(getDirectionFromIndex(directionIndex));
    }

    static String getDirectionFromIndex(int directionIndex) {
      switch(directionIndex) {
        case 0: return "SOUTH";
        case 1: return "EAST";
        case 2: return "NORTH";
        case 3: return "WEST";
        default: return "Hmmmm";
      }
    }
    private static void setBenderStartPosition(int x, int y) {
      benderX = x;
       benderY = y;
    }


    private static void setTeleporter(int y, int x) {
      if (teleporter1X == -1) {
        teleporter1X = x;
        teleporter1Y = y;
      } else {
        teleporter2X = x; 
        teleporter2Y = y;
      }
    }
}