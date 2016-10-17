import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class Player {
    private static final char UNKNOWN_CELL = '*';

    private static final char WALL = '#';
    
    private static char[] letter = { 'A', 'B', 'C', 'D', 'E'};
    private static char[] predefined = { 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', ' ' };

    
    private static char[][] board = null;
    
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
    }
    static P[] pos = null;
    static P myPos = null;

    private static int height;
    private static int width;

    private static int nbPos;
    
    static int scoreType1 = 0;
    static int scoreType2 = 0;
    
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
        pos = new P[nbPos];
        for (int i=0;i<nbPos;i++) {
          pos[i] = new P();
        }
        
        myPos = pos[nbPos-1];
        
        System.err.println(""+width+" / "+height+ " / "+nbPos);
        
        int loop=0;
        // game loop
        //for (loop=0;loop<predefined.length;) {
        while(true) {
          loop++;
            String cellUp    = in.next();
            String cellRight = in.next();
            String cellDown  = in.next();
            String cellLeft  = in.next();
            
            System.err.println(  " "   + cellUp  +    " ");
            System.err.println(cellLeft+  "@"    + cellRight);
            System.err.println(  " "   + cellDown+    " ");
            
            System.err.print("Pos : ");
            for (int i = 0; i < nbPos; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                pos[i].x = x;
                pos[i].y = y;
                System.err.print("("+x+","+y+")");
            }
            System.err.println("");
            
            
            updateBoard(cellUp, cellRight, cellDown, cellLeft);
            debugBoard();
            List<Direction> availableDirections = new ArrayList<>();
            fillAvailableDirection(availableDirections);
            
            List<P> bestPath = findClosestUnexploredCell(myPos, Collections.emptyList());
            System.err.println("Best path : ");
            for (P p : bestPath) {
              System.err.print(p.toString()+"->");
            }
            System.err.println("");
            Direction direction = calculateDirectionFromPath(myPos, bestPath);
            scoreType1++;
            
            System.err.println(direction.toString());
            System.err.println(scoreType1+" / "+scoreType2);
            System.out.println(direction.dir);
        }
    }

    private static Direction calculateDirectionFromPath(P myPos, List<P> bestPath) {
      P p1 = bestPath.get(1);
      if (p1.x == myPos.x) {
        return p1.y > myPos.y ? Direction.DOWN : Direction.UP;
      } else {
        return p1.x > myPos.x ? Direction.RIGHT : Direction.LEFT;
      }
    }

    static List<P> findClosestUnexploredCell(P originalPos, List<P> callerPositions) {
      List<P> visitedPositions = new ArrayList<>(callerPositions);
      visitedPositions.add(originalPos);
      if (board[originalPos.x][originalPos.y] == UNKNOWN_CELL) {
        return visitedPositions;
      }

      int bestPathLength = Integer.MAX_VALUE;
      List<P> bestPath = null;
      for (int d=0;d<4;d++) {
        P testedPos = new P(originalPos.x+directions[d][0], originalPos.y+directions[d][1]);
        if (board[testedPos.x][testedPos.y] == WALL) {
          continue;
        }
        if (visitedPositions.contains(testedPos)) {
          continue;
        }
        for (int enemy=0;enemy<nbPos-1;enemy++) {
          if (testedPos.equals(pos[enemy])) {
            continue;
          }
        }
        List<P> path = findClosestUnexploredCell(testedPos, visitedPositions);
        if (path != null &&  path.size() < bestPathLength) {
          bestPathLength = path.size();
          bestPath = path;
        }
      }
      return bestPath;
    }

    
    private static void debugBoard() {
      System.err.println("Board");
      for (int y=0;y<height;y++) {
        String row = "";
        for (int x=0;x<width;x++) {
          boolean player = false;
          for (int posl= 0;posl<nbPos-1;posl++) {
            if (x == pos[posl].x && y == pos[posl].y) {
              row+=""+(posl+1);
              player = true;
              break;
            }
          }
          if (x == myPos.x && y == myPos.y) {
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
      
      if (WALL != board[myPos.x][myPos.y-1]) {
        availableDirections.add(Direction.UP);
      }
      if (WALL != board[myPos.x][myPos.y+1]) {
        availableDirections.add(Direction.DOWN);
      }
      if (WALL != board[myPos.x+1][myPos.y]) {
        availableDirections.add(Direction.RIGHT);
      }
      if (WALL != board[myPos.x-1][myPos.y]) {
        availableDirections.add(Direction.LEFT);
      }
    }

    private static void updateBoard(String cellUp, String cellRight, String cellDown, String cellLeft) {
      if (board[myPos.x][myPos.y] == UNKNOWN_CELL) {
        scoreType2++;
      }
      for (int posl = 0;posl<nbPos;posl++) {
        board[pos[posl].x][pos[posl].y] = ' ';
      }
      board[myPos.x][myPos.y] = ' ';
      board[myPos.x][myPos.y-1] = cellUp.charAt(0) == WALL? WALL : ' ';
      board[myPos.x][myPos.y+1] = cellDown.charAt(0) == WALL? WALL : ' ';
      board[myPos.x+1][myPos.y] = cellRight.charAt(0) == WALL? WALL : ' ';
      board[myPos.x-1][myPos.y] = cellLeft.charAt(0) == WALL? WALL : ' ';
    }
}