import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class Player {
    private static final char UNKNOWN_CELL = '*';

    private static final char WALL = '#';
    
    static char[][] board = null;
    
    static int directions[][] = {
        { 1,0},
        { 0, -1},
        { -1, 0},
        { 0, 1}
    };
    enum Direction {
      RIGHT("A"),
      UP("C"),
      LEFT("E"),
      DOWN("D"),
      STAY("B");
      
      Direction(String dir) {
        this.dir = dir;
      }
      String dir;
    }
    
    static class P {
      int x, y;
      public P() {
      }
      public P (int x, int y) {
        this.x = x;
        this.y = y;
      }
      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
      }
      @Override
      public boolean equals(Object obj) {
        if (this == obj)
          return true;
        if (obj == null)
          return false;
        if (getClass() != obj.getClass())
          return false;
        P other = (P) obj;
        if (x != other.x)
          return false;
        if (y != other.y)
          return false;
        return true;
      }
      @Override
      public String toString() {
        return "("+x+","+y+")";
      }
      public boolean isInSquare(int dist, P pos) {
        if (this.x == pos.x) {
          if (this.y <= pos.y+dist 
              || this.y <= pos.y-dist) {
            return true;
          }
        }  else if (this.y == pos.y) {
          if (this.x <= pos.x+dist
              || this.x <= pos.x-dist) {
            return true;
          }
        }
        return false;
      }
      
      int manhattanDist(P pos) {
        return Math.abs(pos.x-x)+Math.abs(pos.y-y);
      }
      //TODO find a better name!
      public P clampRotate() {
        int x = this.x;
        int y = this.y;
        if (x > width-1) { x = 0; }
        if (x < 0) { x = width-1; }
        if (y > height-1) { y = 0; }
        if (y <0) { y = height-1;}

        return new P(x,y);
      }
    }
    
    static class Ghost {
      boolean hasMoved = false;
      P pos = new P();
      List<P> oldPos = new ArrayList<>();
      public int id;
      
      void updatePos(int x, int y) {
        if (oldPos.size()>0 && pos.x != x && pos.y != y) {
          hasMoved = true;
        }
        oldPos.add(0, pos);
        pos.x = x;
        pos.y = y;
      }
    }
    static class Pacman extends Ghost {
      private int score = 0;

      @Override
      void updatePos(int x, int y) {
        P p = new P(x,y);
        if (!oldPos.contains(p)) {
          score += 2;
        } else {
          score -=2;
        }
        super.updatePos(x, y);
        
      }
    }
    
    
    static Ghost[] ghosts = null;
    static Pacman pacman = new Pacman(); // Pacman is a ghost ?

    private static int height;
    private static int width;
    private static int nbPos;
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        height = in.nextInt();
        width = in.nextInt();
        nbPos = in.nextInt();
        board = new char[width][height];
        for (int y=0;y<height;y++) {
          for (int x=0;x<width;x++) {
            board[x][y] = UNKNOWN_CELL;
          }
        }
        ghosts = new Ghost[nbPos-1]; // 'Pacman' is a ghost too :)
        for (int i=0;i<nbPos-1;i++) {
          ghosts[i] = new Ghost();
          ghosts[i].id = i+1;
        }
        
        //System.err.println(""+width+" / "+height+ " / "+nbPos);
        
        int loop=0;
        // game loop
        while(true) {
          detectPotentialGhostSwitch();
          
          loop++;
            String cellUp    = in.next();
            String cellRight = in.next();
            String cellDown  = in.next();
            String cellLeft  = in.next();
            
//            System.err.println(  " "   + cellUp  +    " ");
//            System.err.println(cellLeft+  "@"    + cellRight);
//            System.err.println(  " "   + cellDown+    " ");
            
//            System.err.print("Pos : ");
            for (Ghost ghost : ghosts) {
                int x = in.nextInt();
                int y = in.nextInt();
                ghost.updatePos(x, y);
//                System.err.print("("+x+","+y+")");
            }
            pacman.updatePos(in.nextInt(), in.nextInt());
//            System.err.println("");
            
            long time1 = System.currentTimeMillis();
            
            updateBoard(cellUp, cellRight, cellDown, cellLeft);
            debugBoard();
            List<Direction> availableDirections = new ArrayList<>();
            fillAvailableDirection(availableDirections);
            
            List<P> bestPath = findClosestUnexploredCell(pacman.pos);
            Direction direction;
            if (bestPath != null) {
              if (false) {
                debugPath(bestPath);
              }
              direction = calculateDirectionFromPath(pacman.pos, bestPath);
            } else {
             System.err.println("No path");
             direction = Direction.STAY;
            }
            long time2 = System.currentTimeMillis();

            System.err.println("Time : "+(time2-time1));
            System.err.println(direction.toString());
            System.err.println(pacman.score);
            System.out.println(direction.dir);
        }
    }

    private static void debugPath(List<P> bestPath) {
      System.err.println("Best path : ");
      int pathShown = 0;
      for (P p : bestPath) {
        System.err.print(p.toString()+"->");
        pathShown++;
        if (pathShown >= 5) {
          System.err.println("");
          pathShown = 0;
        }
      }
      System.err.println("");
    }

    private static void detectPotentialGhostSwitch() {
      for(Ghost ghost : ghosts) {
        if (ghost.hasMoved && ghost.oldPos.size() >= 2) {
          P oldPos = ghost.oldPos.get(1);
          if (oldPos.equals(ghost.pos)) {
            System.out.println("GHOST IS SCARED!");
          }
        }
      }
    }

    private static Direction calculateDirectionFromPath(P pos, List<P> bestPath) {
      P p1 = bestPath.get(1);
      if (p1.x == pos.x) {
        if (p1.y == 0 && pos.y == height-1) {
          return Direction.DOWN;
        } else if (pos.y == 0 && p1.y == height-1) {
          return Direction.UP;
        } else if (p1.y > pos.y) {
          return Direction.DOWN;
        } else {
          return Direction.UP;
        }
      } else {
        if (p1.x == 0 && pos.x == width-1) {
          return Direction.RIGHT;
        } else if (pos.x == 0 && p1.x == width-1) {
          return Direction.LEFT;
        } else if (p1.x > pos.x) {
          return Direction.RIGHT;
        } else {
          return Direction.LEFT;
        }
      }
    }

    static int minBestDist = Integer.MAX_VALUE;
    
    static List<P> findClosestUnexploredCell(P originalPos) {
      minBestDist = Integer.MAX_VALUE;
      return findClosestUnexploredCell(originalPos, Collections.emptyList());
    }
    static List<P> findClosestUnexploredCell(P originalPos, List<P> callerPositions) {
      if (callerPositions.size() > minBestDist) {
        return null;
      }
      List<P> visitedPositions = new ArrayList<>(callerPositions);
      visitedPositions.add(originalPos);
      if (board[originalPos.x][originalPos.y] == UNKNOWN_CELL) {
        return visitedPositions;
      }
      if (callerPositions.size() > 1) {
        for (Ghost g : ghosts) {
          if (originalPos.equals(g.pos)) {
            return null;
          } else if (originalPos.manhattanDist(g.pos)== 0) {
            return null;
          }
        }
      }
      int bestPathLength = Integer.MAX_VALUE;
      List<P> bestPath = null;
      for (int d=4;--d>=0;) {
        P testedPos = new P(originalPos.x+directions[d][0], originalPos.y+directions[d][1]).clampRotate();
        
        if (board[testedPos.x][testedPos.y] == WALL) {
          continue;
        }
        if (visitedPositions.contains(testedPos)) {
          continue;
        }
        
        List<P> path = findClosestUnexploredCell(testedPos, visitedPositions);
        if (path != null &&  path.size() < bestPathLength ) {
          if (checkPathEndNotNearFromGhosts(path)) {
            bestPathLength = path.size();
            minBestDist = Math.min(minBestDist, bestPathLength);
            bestPath = path;
          }
        }
      }
      return bestPath;
    }

    
    private static boolean checkPathEndNotNearFromGhosts(List<P> path) {
      P p = path.get(path.size()-1);
      for (Ghost ghost : ghosts) {
        if (p.manhattanDist(ghost.pos) <= 1) {
          return false;
        }
      }
      return true;
    }

    private static void debugBoard() {
      System.err.println("Board");
      for (int y=0;y<height;y++) {
        String row = "";
        for (int x=0;x<width;x++) {
          boolean player = false;
          for (Ghost g : ghosts) {
            if (g.pos.equals(new P(x,y))) {
              row+=""+(g.id+1);
              player = true;
              break;
            }
          }
          if (x == pacman.pos.x && y == pacman.pos.y) {
            row+="@";
          } else if (!player){
            row+=board[x][y] == WALL ? "#": board[x][y];
          }
        }
        System.err.println(row);
      }
    }

    private static void fillAvailableDirection(List<Direction> availableDirections) {
      availableDirections.add(Direction.STAY);
      
      if (pacman.pos.y == 0 || WALL != board[pacman.pos.x][pacman.pos.y-1]) {
        availableDirections.add(Direction.UP);
      }
      if (pacman.pos.y == height-1 || WALL != board[pacman.pos.x][pacman.pos.y+1]) {
        availableDirections.add(Direction.DOWN);
      }
      if (pacman.pos.x == width-1 || WALL != board[pacman.pos.x+1][pacman.pos.y]) {
        availableDirections.add(Direction.RIGHT);
      }
      if (pacman.pos.x == 0 || WALL != board[pacman.pos.x-1][pacman.pos.y]) {
        availableDirections.add(Direction.LEFT);
      }
    }

    private static void updateBoard(String cellUp, String cellRight, String cellDown, String cellLeft) {
      board[pacman.pos.x][pacman.pos.y] = ' ';
      
      if (pacman.pos.y > 0) {
        if (cellUp.charAt(0) == WALL) board[pacman.pos.x][pacman.pos.y-1] = WALL;
      }
      if (pacman.pos.y < height-1) {
        if (cellDown.charAt(0) == WALL) board[pacman.pos.x][pacman.pos.y+1] = WALL;
      }
      if (pacman.pos.x < width-1) {
        if (cellRight.charAt(0) == WALL) board[pacman.pos.x+1][pacman.pos.y] = WALL;
      }
      if (pacman.pos.x > 0) {
        if (cellLeft.charAt(0) == WALL) board[pacman.pos.x-1][pacman.pos.y] = WALL;
      }
    }
}