import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class Player {
    private static final char UNKNOWN_CELL = '*';

    private static final char WALL = '#';
    
    private static char[] letter = { 'A', 'B', 'C', 'D', 'E'};
    private static char[] predefined = { 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', ' ' };

    private static char[][] board = null;
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
            
            int rand = ThreadLocalRandom.current().nextInt(availableDirections.size());
            Direction direction = availableDirections.get(rand);
            
            
            if (pos[2].y > myPos.y && availableDirections.contains(Direction.UP)) {
              direction = Direction.UP;
            }
            if (pos[2].y < myPos.y && availableDirections.contains(Direction.DOWN)) {
              direction = Direction.DOWN;
            }
            if (pos[2].x < myPos.x && availableDirections.contains(Direction.LEFT)) {
              direction = Direction.LEFT;
            }
            if (pos[2].x > myPos.x && availableDirections.contains(Direction.RIGHT)) {
              direction = Direction.RIGHT;
            }
            scoreType1++;
            
            System.err.println(direction.toString());
            System.err.println(scoreType1+" / "+scoreType2);
            System.out.println(direction.dir);
        }
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