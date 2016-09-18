import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
  static Scanner in = new Scanner(System.in);

  Grid grid;
  int leftRounds;
  int bombsLeft;

  public Player(int width, int height, List<String> rows) {
    grid = new Grid(width, height, rows);
  }

  void udpate() {
    
  }

  public static class Simulation {
    // where to place bomb
    int x;
    int y;
    Grid grid;
    
    void simulate(Player player) {
      grid = player.grid;
      if (grid.surveillanceNodes.size() == 0) {
        return;
      }
      // find surveillance node
      P p = grid.surveillanceNodes.remove(0);
      findAFreePlaceFor(p.x, p.y);
    }

    boolean findAFreePlaceFor(int px, int py) {
      for (int x=1;x<4;x++) {
        if (grid.valueOf(px+x,py) == '.') {
          this.x = px+x;
          this.y = py;
          return true;
        }
      }
      for (int x=-4;x<-1;x++) {
        if (grid.valueOf(px+x,py) == '.') {
          this.x = px+x;
          this.y = py;
          return true;
        }
      }
      for (int y=1;y<4;y++) {
        if (grid.valueOf(px,py+y) == '.') {
          this.x = px;
          this.y = py+y;
          return true;
        }
      }
      for (int y=-4;y<-1;y++) {
        if (grid.valueOf(px,py+y) == '.') {
          this.x = px;
          this.y = py+y;
          return true;
        }
      }
      return false;
    }
  }
  
  class P {
    public P(int x, int y) {
      this.x = x;
      this.y = y;
    }

    int x, y;
  }
  class Grid {
    public final static char SURVEILLANCE_NODE = '@';
    public final static char STATIC_NODE = '#';
    
    private int width;
    private int height;
    int[][] board;
    List<P> surveillanceNodes = new ArrayList<>();
    
    public Grid(int width, int height, List<String> rows) {
      this.width = width;
      this.height = height;
      init(rows);
    }
    void init(List<String> rows) {
      board = new int[width][height];
      int y=0;
      for (String row : rows) {
        for (int x=0;x<row.length();x++) {
          if (row.charAt(x) == '.') {
            board[x][y] = '.';
          }  else if (row.charAt(x) == SURVEILLANCE_NODE) {
            surveillanceNodes.add(new P(x,y));
          } else {
            board[x][y] = row.charAt(x);
          }
        }
        y++;
      }
    }
    void placeBomb(int x, int y) {
      setValue(3, x, y);
    }
    
    void udpate() {
      for (int y=0;y<height;y++) {
        for (int x=0;x<width;x++) {
          if (board[x][y] > 0 && board[x][y] < 4) {
            board[x][y] = board[x][y] -1;
            if (board[x][y] == 0) {
              explode(x,y);
            }
          }
        }
      }
    }
    
    int valueOf(int x, int y) {
      if (x<0 || x>width-1 || y<0 || y>height-1) {
        return STATIC_NODE;
      }
      return board[x][y];
    }
    void setValue(int value, int x, int y) {
      if (x<0 || x>width-1 || y<0 || y>height-1) {
        return;
      }
      int oldValue = board[x][y];
      board[x][y] = value;
      
      if (oldValue == SURVEILLANCE_NODE) {
      }
      if (oldValue > 0 && oldValue < 4) {
        explode(x, y);
      }
    }
    boolean explodeOne(int x, int y) {
      if (valueOf(x,y) == STATIC_NODE) {
        return false;
      }
      setValue(0, x,y);
      return true;
    }
    void explode(int px, int py) {
      for (int x=1;x<4;x++) {
        if (!explodeOne(px+x,py)) {
          break;
        }
      }
      for (int x=-4;x<-1;x++) {
        if (!explodeOne(px+x,py)) {
          break;
        }
      }
      for (int y=1;y<4;y++) {
        if (!explodeOne(px,py+y)) {
          break;
        }
      }
      for (int y=-4;y<-1;y++) {
        if (!explodeOne(px,py+y)) {
          break;
        }
      }
    }
    
    public String getRow(int row) {
      String result = "";
      for (int x=0;x<width;x++) {
        if (board[x][row] >= 0 && board[x][row] <=4) {
          result+=board[x][row];
        } else {
          result+=(char)(board[x][row]);
        }
      }
      return result;
    }
  }

  public static void main(String args[]) {
    int width = in.nextInt();
    int height = in.nextInt();
    in.nextLine();
    List<String> rows = new ArrayList<>();
    for (int i = 0; i < height; i++) {
      String mapRow = in.nextLine();
      rows.add(mapRow);
    }
    Player player = new Player(width, height, rows);

    player.play();
  }

  private void play() {
    Simulation s = new Simulation();
    while (true) {
      leftRounds = in.nextInt();
      bombsLeft = in.nextInt();
      
      udpate();
      s.simulate(this);
      System.out.println(""+s.x+" "+s.y);
    }
  }

}