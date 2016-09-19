import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

  public static class SimulationStep {
    Grid grid;
    int x;
    int y;
    int step;
    double score = 0;
    
    public SimulationStep(Grid grid, int step, int x, int y) {
      this.grid = new Grid(grid);
      this.step = step;
      this.x = x;
      this.y = y;
    }

    void simulate() {
      if (grid.valueOf(x, y) != Grid.EMPTY) {
        score = -10000;
        return;
      }
      
      int sentinels = 0;
      for (int rot=0;rot<4;rot++) {
        for (int d=1;d<4;d++) {
          int ppx = x + d*(int)Math.cos(rot * Math.PI/2);
          int ppy = y + d*(int)Math.sin(rot * Math.PI/2);
          if (grid.valueOf(ppx, ppy) == grid.SURVEILLANCE_NODE) {
            sentinels++;
          }
        }
      }
      score = sentinels;
    }
  }
  public static class Simulation {
    // where to place bomb
    Grid grid;
    
    SimulationStep simulate(Player player) {
      grid = player.grid;
      
      double bestScore = -10000;
      SimulationStep bestSS = null;
      
      for (int x=0;x<grid.width;x++) {
        for(int y=0;y<grid.height;y++) {
          if (grid.board[x][y] == Grid.EMPTY) {
            SimulationStep ss = new SimulationStep(grid, 0, x, y);
            ss.simulate();
            if (ss.score > bestScore) {
              bestScore = ss.score;
              bestSS = ss;
            }
          }
        }
      }
      return bestSS;
    }
  }
  
  static class P {
    public P(int x, int y) {
      this.x = x;
      this.y = y;
    }

    int x, y;
  }
  static class Grid {
    public final static char EMPTY = '.';
    public final static char SURVEILLANCE_NODE = '@';
    public final static char EXPLODED_SURVEILLANCE_NODE = 'e';
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
    public Grid(Grid grid) {
      this.width = grid.width;
      this.height = grid.height;
      this.board = grid.board.clone();
    }
    void init(List<String> rows) {
      board = new int[width][height];
      int y=0;
      for (String row : rows) {
        for (int x=0;x<row.length();x++) {
          if (row.charAt(x) == '.') {
            board[x][y] = EMPTY;
          }  else if (row.charAt(x) == SURVEILLANCE_NODE) {
            board[x][y] = row.charAt(x);
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
      setValue(EMPTY, x,y);
      return true;
    }
    void explode(int x, int y) {
      for (int rot=0;rot<4;rot++) {
        for (int d=1;d<4;d++) {
          int ppx = x + d*(int)Math.cos(rot * Math.PI/2);
          int ppy = y + d*(int)Math.sin(rot * Math.PI/2);
          explodeOne(ppx, ppy);
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
      
      if (bombsLeft > 0) {
        udpate();
        SimulationStep ss = s.simulate(this);
        grid.explode(ss.x, ss.y);
        System.out.println(""+ss.x+" "+ss.y);
      } else {
        System.out.println("WAIT");
      }
    }
  }

}