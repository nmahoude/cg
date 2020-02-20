import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Player {
  static Scanner in = new Scanner(System.in);
  static Player player;
  
  static int rots[][] = { {1, 0}, {0, 1}, {-1, 0}, {0 ,-1}};
  Grid grid;
  int leftRounds;
  int bombsLeft;

  public Player(int width, int height, List<String> rows) {
    grid = new Grid(width, height, rows);
  }


  static class P {
    public P(int x, int y) {
      this.x = x;
      this.y = y;
    }

    int x, y;
  }

  static class SurveillanceNode {
    public SurveillanceNode(P p) {
      position = p;
    }
    P position;
    enum Status {
      ALIVE,
      WILL_BE_DEAD,
      DEAD
    }
    Status status = Status.ALIVE;
  }
  static class Bomb {
    P position;
    int tickLeft;
    
    Bomb(P p) {
      position = p;
      tickLeft = 3;
    }
  }
  
  static class Grid {
    public final static char EMPTY = '.';
    public final static char SURVEILLANCE_NODE = '@';
    public final static char EXPLODED_SURVEILLANCE_NODE = 'e';
    public final static char STATIC_NODE = '#';
    public static final int UNPLACEABLE_BOMB = '-';

    private int width;
    private int height;
    int[] board;
    List<SurveillanceNode> surveillanceNodes = new ArrayList<>();
    List<Bomb> bombs = new ArrayList<>();
  
    public Grid(int width, int height, List<String> rows) {
      this.width = width;
      this.height = height;
      board = new int[width*height];
      init(rows);
    }

    public Grid(Grid grid) {
      this.width = grid.width;
      this.height = grid.height;
      board = new int[width*height];
      System.arraycopy(grid.board, 0, board, 0, grid.board.length);
      for (SurveillanceNode sn: grid.surveillanceNodes) {
        this.surveillanceNodes.add(new SurveillanceNode(sn.position));
      }
    }

    public int countSentinelsToBeDestroyed() {
      return countCellOfType(Grid.EXPLODED_SURVEILLANCE_NODE);
    }

    public int countAliveSentinels() {
      int count = 0;
      for (SurveillanceNode sn : surveillanceNodes) {
        if (sn.status == SurveillanceNode.Status.ALIVE) {
          count++;
        }
      }
      return count;
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
            surveillanceNodes.add(new SurveillanceNode(new P(x, y)));
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
          int ppx = x + d * rots[rot][0];
          int ppy = y + d * rots[rot][1];
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
      Bomb bomb = new Bomb(new P(x,y));
      bombs.add(bomb);
      board[x+width*y] = bomb.tickLeft;
      transformSurveillanceNodeIntoFutureDeadSurveillanceNode(bomb);
    }

    private void transformSurveillanceNodeIntoFutureDeadSurveillanceNode(Bomb bomb) {
      for (int rot = 0; rot < 4; rot++) {
        for (int d = 1; d < 4; d++) {
          int ppx = bomb.position.x + d * rots[rot][0];
          int ppy = bomb.position.y + d * rots[rot][1];
          if (ppx <0 || ppx>=width) break;
          if (ppy <0 || ppy>=height) break;
          
          int value = board[ppx+width*ppy];
          if (value == STATIC_NODE) break;
          if (value == SURVEILLANCE_NODE) {
            board[ppx+width*ppy] = EXPLODED_SURVEILLANCE_NODE;
            getSurveillanceNodeAt(ppx, ppy).status = SurveillanceNode.Status.WILL_BE_DEAD; 
          }
        }
      }
    }

    SurveillanceNode getSurveillanceNodeAt(int x, int y) {
      for (SurveillanceNode sn : surveillanceNodes) {
        if (sn.position.x == x && sn.position.y == y) {
          return sn;
        }
      }
      return null;
    }
    void udpate() {
      for (Bomb bomb : bombs) {
        bomb.tickLeft--;
        board[bomb.position.x+width*bomb.position.y] = bomb.tickLeft;
      }
      if (!bombs.isEmpty() && bombs.get(0).tickLeft == 0) {
        Bomb bomb = bombs.remove(0);
        boolean potentiallySomething = false;
        for (SurveillanceNode sn : surveillanceNodes) {
          if (isIn4x4(bomb.position, sn.position)) {
            potentiallySomething = true;
          }
        }
        for (Bomb checkedBomb : bombs) {
          if (isIn4x4(checkedBomb.position, bomb.position)) {
            potentiallySomething = true;
          }
        }
        if (potentiallySomething) {
          explode(bomb.position.x, bomb.position.y);
        }
      }
    }

    boolean isIn4x4(P p, P target) {
      if ((Math.abs(p.x-target.x) < 4 && p.y == target.y)
        || (Math.abs(p.y-target.y) < 4 && p.x == target.x)) {
        return true;
      }
      return false;
    }
    
    int valueOf(int x, int y) {
      if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
        return STATIC_NODE;
      }
      return board[x+width*y];
    }

    void setValue(int value, int x, int y) {
      int oldValue = board[x+width*y];
      board[x+width*y] = value;

      if (oldValue == EXPLODED_SURVEILLANCE_NODE) {
        surveillanceNodes.remove(getSurveillanceNodeAt(x, y));
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
          int ppx = x + d * rots[rot][0];
          if (ppx <0 || ppx>=width) break;
          int ppy = y + d * rots[rot][1];
          if (ppy <0 || ppy>=height) break;

          if (board[x+width*y] == STATIC_NODE) break;
          setValue(EMPTY, ppx, ppy);
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
    player = new Player(width, height, rows);

    player.play();
  }

  static class BombExpectation {
    public BombExpectation(P p, int count) {
      pos = p;
      potentialCount = count;
    }
    P pos;
    int potentialCount;
    int real;
  }
  
  
  static class Simulation {
    int bombsLeft;
    int turnsLeft;
    List<String> actions;
    
    public Simulation(int leftRounds, int bombsLeft) {
      this.turnsLeft = leftRounds;
      this.bombsLeft = bombsLeft;
    }


    public List<String> simulate(Grid grid) {
      return doOneSimulation(grid);
    }
    public List<String> doOneSimulation(Grid gridInitial) {
      List<String> actions = new ArrayList<>();
      int initialBombsLeft = bombsLeft;
      int initialTurnsLeft = turnsLeft;
      
      Grid grid = null;
      do {
        grid = new Grid(gridInitial);
        bombsLeft = initialBombsLeft;
        turnsLeft = initialTurnsLeft;
        while (turnsLeft > 0) {
          String action = getAction(grid);
          actions.add(action);
          grid.udpate();
  
          if (!action.equals("WAIT")) {
            bombsLeft--;
          }
          turnsLeft--;
        }
        
        if (!grid.surveillanceNodes.isEmpty()) {
          // block first bomb first Action
          String[] poss = actions.get(0).split(" ");
          int posx = Integer.parseInt(poss[0]);
          int posy = Integer.parseInt(poss[1]);
          gridInitial.board[posx+posy*gridInitial.width] = Grid.UNPLACEABLE_BOMB; 
          actions.clear();
        }
      } while (!grid.surveillanceNodes.isEmpty());
      return actions;
    }
      
    public String getAction(Grid grid) {  
      List<BombExpectation> bombExpectation = new ArrayList<>();
      for (int x=0;x<grid.width;x++) {
        for (int y=0;y<grid.height;y++) {
          if (grid.board[x+y*grid.width] != Grid.EMPTY) {
            continue;
          }
          P p = new P(x,y);
          int count = 0;
          for (SurveillanceNode target : grid.surveillanceNodes) {
            if (target.status == SurveillanceNode.Status.ALIVE && grid.isIn4x4(p, target.position)) {
              count++;
            }
          }
          bombExpectation.add(new BombExpectation(p, count));
        }
      }
      sortExpectations(bombExpectation);
      BombExpectation be = getBestFromExpectations(grid, bombExpectation);
      
      if (be != null) {
        if (bombsLeft == 1 && grid.countAliveSentinels() - be.real > 0) {
          return "WAIT";
        }
        grid.placeBomb(be.pos.x, be.pos.y);
        return ""+be.pos.x+" "+be.pos.y;
      } else {
        return "WAIT";
      }
    }

      private BombExpectation getBestFromExpectations(Grid grid, List<BombExpectation> bombExpectation) {
        int maxReal = -2;
        BombExpectation maxBE = null;
        for (BombExpectation be : bombExpectation) {
          int realCount = grid.sentinelsTouchedByBomb(be.pos.x, be.pos.y);
          be.real = realCount;
          if (maxReal > be.potentialCount) {
            return maxBE;
          }
          if (realCount == be.potentialCount) {
            return be;
          } else {
            if (maxReal < realCount) {
              maxReal = realCount;
              maxBE = be;
            }
          }
        }
        return null;
      }

      private void sortExpectations(List<BombExpectation> bombExpectation) {
        bombExpectation.sort(new Comparator<BombExpectation>() {
          @Override
          public int compare(BombExpectation b1, BombExpectation b2) {
            return Integer.compare(b2.potentialCount, b1.potentialCount);
          }
        });
      }
    }

  
  static class Ai {
    private int leftRounds;
    private int bombsLeft;
    List<String> actions = null;
    
    public void update(int leftRounds, int bombsLeft) {
      this.leftRounds = leftRounds;
      this.bombsLeft = bombsLeft;
    }

    public String getCommand() {
      if (actions == null) {
        Simulation s = new Simulation(leftRounds, bombsLeft);
        actions = s.simulate(player.grid);
      }
      String result = actions.remove(0);
      return result;
    }
    
  }
  private void play() {
    Ai ai = new Ai();
    
    while (true) {
      leftRounds = in.nextInt();
      bombsLeft = in.nextInt();
      ai.update(leftRounds, bombsLeft);
      
      System.out.println(ai.getCommand());
      
      grid.udpate();
    }
  }

}