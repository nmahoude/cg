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

  public static class SimulationStep {
    int turnsLeft;

    enum Command {
      WAIT, BOMB
    }

    // initial grid
    Grid grid;

    // command
    Command command;
    int x;
    int y;

    // result of simulation
    int bombsLeft;
    double score = 0;
    SimulationStep next;

    private int iteration;

    public SimulationStep(Grid grid, boolean canWait, int iteration, int turnsLeft, int bombsCount, Command command, int x, int y) {
      this.iteration = iteration;
      this.bombsLeft = bombsCount;
      this.command = command;
      this.grid = new Grid(grid);
      this.turnsLeft = turnsLeft;
      this.x = x;
      this.y = y;
    }

    void simulate() {
      if (!isFirstStep()) {
        grid.udpate(); // tick bombs (and explode)

        int aliveBefore = grid.countAliveSentinels();
        int aliveAfter = aliveBefore;
        if (command != Command.WAIT ) {
          if (bombsLeft < 0 ) {
            score = -1000;
            return;
          } else {
            grid.placeBomb(x, y);
            bombsLeft--;
            aliveAfter = grid.countAliveSentinels(); 
          }
          if (aliveAfter == aliveBefore) {
            score = -1000;
            return;
          }
        }
        if (aliveAfter > 0 && bombsLeft == 0) {
          score = -1000;
        } else {
          score = aliveBefore - aliveAfter;
        }
      }
      if (shouldStopSimulation()) {
        return;
      } else {
        // one step further
        next = getBestNextSimulationStep();
        if (next != null) {
          score = score + 0.999*next.score;
        }
      }
    }

    private SimulationStep getBestNextSimulationStep() {
      double bestScore = -10000;
      SimulationStep bestSS = null;

      if (canPlaceABomb()) {
        for (int newX = 0; newX < grid.width; newX++) {
          for (int newY = 0; newY < grid.height; newY++) {
            if (grid.board[newX+grid.width*newY] == Grid.EMPTY && grid.sentinelsTouchedByBomb(newX, newY) > 0) {
              SimulationStep ss = new SimulationStep(grid, true, iteration-1, turnsLeft - 1, bombsLeft, Command.BOMB, newX, newY);
              ss.simulate();
              if (ss.score > bestScore) {
                bestScore = ss.score;
                bestSS = ss;
                this.command = Command.BOMB;
                this.x = newX;
                this.y = newY;
              }
            }
          }
        }
      }
      if (true) {
        // don't forget to try WAIT !
        SimulationStep ss = new SimulationStep(grid, true, iteration-1, turnsLeft - 1, bombsLeft, Command.WAIT, 0,0);
        ss.simulate();
        if (ss.score > bestScore) {
          bestScore = ss.score;
          bestSS = ss;
          this.command = Command.WAIT;
          this.x = 0;
          this.y = 0;
        }
      }
      return bestSS;
    }

    private boolean canPlaceABomb() {
      return bombsLeft > 0;
    }

    private boolean isFirstStep() {
      return x == -1;
    }

    private boolean shouldStopSimulation() {
      return turnsLeft < 0 || iteration == 0;
    }
  }

  public static class SimulationRoot {
    // where to place bomb
    Grid grid;
    int bombsLeft;

    SimulationStep simulate(Player player) {
      grid = player.grid;
      
      int steps = 3;
      if (grid.width*grid.height > 25) {
        steps = 2;
      }
      SimulationStep base = new SimulationStep(grid, true, steps, player.leftRounds, player.bombsLeft, SimulationStep.Command.WAIT, -1, -1);
      base.simulate();
      return base;
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
    int[] board;
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

    public int countSentinelsToBeDestroyed() {
      return countCellOfType(Grid.EXPLODED_SURVEILLANCE_NODE);
    }

    public int countAliveSentinels() {
      return countCellOfType(Grid.SURVEILLANCE_NODE);
    }

    private int countCellOfType(int type) {
      int sentinels = 0;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (board[x+width*y] == type) {
            sentinels++;
          }
        }
      }
      return sentinels;
    }


    void init(List<String> rows) {
      board = new int[width*height];
      int y = 0;
      for (String row : rows) {
        for (int x = 0; x < row.length(); x++) {
          if (row.charAt(x) == '.') {
            board[x+width*y] = EMPTY;
          } else if (row.charAt(x) == SURVEILLANCE_NODE) {
            board[x+width*y] = row.charAt(x);
            surveillanceNodes.add(new P(x, y));
          } else if (row.charAt(x) == STATIC_NODE) {
            board[x+width*y] = row.charAt(x);
          } else {
            board[x+width*y] = row.charAt(x)-'0';
          }
        }
        y++;
      }
    }

    int sentinelsTouchedByBomb(int x,int y) {
      int sentinels = 0;
      for (int rot = 0; rot < 4; rot++) {
        for (int d = 1; d < 4; d++) {
          int ppx = x + d * (int) Math.cos(rot * Math.PI / 2);
          int ppy = y + d * (int) Math.sin(rot * Math.PI / 2);
          if (valueOf(ppx, ppy) == STATIC_NODE) {
            break;
          }
          if (valueOf(ppx, ppy) == SURVEILLANCE_NODE) {
            sentinels++;
          }
        }
      }
      return sentinels;
    }
    void placeBomb(int x, int y) {
      setValue(3, x, y);
      for (int rot = 0; rot < 4; rot++) {
        for (int d = 1; d < 4; d++) {
          int ppx = x + d * (int) Math.cos(rot * Math.PI / 2);
          int ppy = y + d * (int) Math.sin(rot * Math.PI / 2);
          if (valueOf(ppx, ppy) == SURVEILLANCE_NODE) {
            board[ppx+width*ppy] = EXPLODED_SURVEILLANCE_NODE;
          }
        }
      }
    }

    void udpate() {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int cellValue = board[x+width*y];
          if (cellValue > 0 && cellValue < 4) {
            board[x+width*y] = cellValue - 1;
            if (cellValue-1 == 0) {
              explode(x, y);
            }
          }
        }
      }
    }

    int valueOf(int x, int y) {
      if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
        return STATIC_NODE;
      }
      return board[x+width*y];
    }

    void setValue(int value, int x, int y) {
      if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
        return;
      }
      int oldValue = board[x+width*y];
      board[x+width*y] = value;

      if (oldValue == SURVEILLANCE_NODE) {
      }
      if (oldValue > 0 && oldValue < 4) {
        explode(x, y);
      }
    }

    boolean explodeOne(int x, int y) {
      if (valueOf(x, y) == STATIC_NODE) {
        return false;
      }
      setValue(EMPTY, x, y);
      return true;
    }

    void explode(int x, int y) {
      for (int rot = 0; rot < 4; rot++) {
        for (int d = 1; d < 4; d++) {
          int ppx = x + d * (int) Math.cos(rot * Math.PI / 2);
          int ppy = y + d * (int) Math.sin(rot * Math.PI / 2);
          explodeOne(ppx, ppy);
        }
      }
    }

    public String getRow(int row) {
      String result = "";
      for (int x = 0; x < width; x++) {
        if (board[x+width*row] >= 0 && board[x+width*row] <= 4) {
          result += board[x+width*row];
        } else {
          result += (char) (board[x+width*row]);
        }
      }
      return result;
    }

    public void debug() {
      for (int y=0;y<height;y++) {
        System.err.println(getRow(y));
      }
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
    SimulationRoot s = new SimulationRoot();
    SimulationStep currentSS = null;
    
    while (true) {
      leftRounds = in.nextInt();
      bombsLeft = in.nextInt();
      
      currentSS = s.simulate(this);
      
      if (currentSS.command == SimulationStep.Command.WAIT) {
        System.out.println("WAIT");
      } else {
        System.out.println("" + currentSS.x + " " + currentSS.y);
        grid.placeBomb(currentSS.x, currentSS.y);
        System.err.println("will destroy "+currentSS.score+" bombs");
      }
      grid.udpate();
    }
  }

}