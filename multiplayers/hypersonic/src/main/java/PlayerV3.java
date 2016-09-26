import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class PlayerV3 {
  private static Scanner in;
  private static int myIndex;

  private static final int ENTITY_PLAYER = 0;
  private static final int ENTITY_BOMB = 1;
  private static final int ENTITY_ITEM = 2;

  static int[] rotx = { 1, 0, -1, 0 };
  static int[] roty = { 0, 1, 0, -1 };

  enum EntityType {
    PLAYER, BOMB
  }

  static class P {
    final int x;
    final int y;

    public P(int x, int y) {
      super();
      this.x = x;
      this.y = y;
    }

    int distance(P p) {
      return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
    }

    @Override
    public String toString() {
      return "(" + x + "," + y + ")";
    }
  }

  static class Entity {
  }

  static class Item extends Entity {
    int type;
  }
  
  static class Bomb extends Entity {
    APlayer player; // who drops the bomb
    boolean hasExploded = false;
    int timer;
    int range;
    Cell cell;
    
    public Bomb(APlayer player, Cell cell, int timer, int range) {
      super();
      this.player = player;
      this.timer = timer;
      this.range = range;
      this.cell = cell;
    }

    public void update() {
      timer--;
      if (timer == 0) {
        explode();
      }
    }

    private void explode() {
      if (hasExploded) {
        return;
      }
      hasExploded = true;
      this.cell.explode(this); // explode center
      
      for (int rot =0;rot<4;rot++) {
        Cell currentCell = this.cell;
        for (int d = 1; d <= range; d++) {
          currentCell = currentCell.neighbors[rot];
          if (currentCell.isExplosionHardBlocked()) {
            break;
          } else if (currentCell.isExplosionSoftBlocked()) {
            currentCell.explode(this);
            break;
          } else {
            currentCell.explode(this);
          }
        }
      }
    }
  }

  static class APlayer extends Entity {
    int index;
    int x, y; // his position
    int bombsLeft = 1;
    public int bombRange = 3;
    public Cell cell;
  }

  static class Cell {
    static final int UP = 0;
    static final int DOWN = 1;
    static final int RIGHT = 2;
    static final int LEFT = 3;

    static final int OPTION_EXTRARANGE = 1;
    static final int OPTION_EXTRABOMB = 2;
    public static final int MIN_WEIGHT = -2000000;

    enum Type {
      BOX, FLOOR, WALL
    }

    int x;
    int y;
    Type type;
    List<Entity> entities; // present on cell (type floor)
    Item item;
    
    public int weight = 0;
    public int option;
    private int threat;
    public int boxCount;
    public int hasOption_bombUp;
    public int hasOption_rangeUp;

    Cell neighbors[] = new Cell[4];
    private boolean exploding;
    
    public Cell(int x, int y) {
      this.x = x;
      this.y = y;
      reset();
    }

    public void explode(Bomb originBomb) {
      exploding = true;
      
      if (type == Type.BOX) {
        willExplodeIn = 0;
        type = Type.FLOOR;
      } else {
        item = null;
      }
      explodingBombs.add(originBomb);
      if (bomb != null && bomb != originBomb) {
        bomb.explode();
      }
    }

    public int hvDistanceTo(APlayer me) {
      if (me.x == x) {
        return Math.abs(me.y - y);
      } else if (me.y == y) {
        return Math.abs(me.x - x);
      } else {
        return Integer.MAX_VALUE;
      }
    }


    public boolean isExplosionHardBlocked() {
      return type == Type.WALL;
    }
    public boolean isExplosionSoftBlocked() {
      return type == Type.BOX || bomb != null || hasOption_bombUp > 0 || hasOption_rangeUp > 0;
    }

    public boolean isBlocked() {
      return type == Type.WALL || type == Type.BOX || exploding;
    }
    public boolean isSoftBlocked() {
      return bomb != null;
    }
    public void reset() {
      weight = 0;
      threat = 0;
      boxCount = 0;
      hasOption_bombUp = 0;
      hasOption_rangeUp = 0;
      bomb = null;
      
      willExplodeIn = Integer.MAX_VALUE;
      resetBombsArtefacts();
    }

    public void resetBombsArtefacts() {
      explodingBombs.clear();
      exploding = false;
    }
    
    int willExplodeIn;
    Bomb bomb;
    List<Bomb> explodingBombs = new ArrayList<>();
    
    public boolean isSafe(APlayer player) {
      if (!exploding) {
        return true;
      }
      for (Bomb b : explodingBombs) {
        if (b.player == player) {
          continue; // no threat
        } else {
          return false;
        }
      }
      return true;
    }

    public void placeBomb(Bomb bomb) {
      this.bomb = bomb;
      updateBombInfluence();
    }
    void updateBombInfluence() {
      if (bomb == null) {
        return;
      }
      
      for (int rot =0;rot<4;rot++) {
        updateBombInfluenceForOneLine(rot);
      }
    }

    private void updateBombInfluenceForOneLine(int rot) {
      Cell currentCell = this;  
      for (int d = 1; d <= bomb.range; d++) {
        currentCell = currentCell.neighbors[rot];
        if (currentCell.isExplosionHardBlocked()) {
          break;
        } else if (currentCell.isExplosionSoftBlocked()) {
          currentCell.addBombInfluence(bomb);
          break;
        } else {
          currentCell.addBombInfluence(bomb);
        }
      }
    }
    private void addBombInfluence(Bomb fromBomb) {
      explodingBombs.add(fromBomb);
      willExplodeIn = Math.min(willExplodeIn, fromBomb.timer);
      if (bomb != null) {
        if (this.bomb.timer > fromBomb.timer) {
          this.bomb.timer = fromBomb.timer;
        }
      }
    }

    public void copyFrom(Cell cell) {
      reset();
      this.type = cell.type;
      if (cell.willExplodeIn != Integer.MAX_VALUE) {
        this.willExplodeIn = cell.willExplodeIn-1;
        if (willExplodeIn == 0) {
          this.type = Type.FLOOR;
        }
      }
      this.hasOption_bombUp = cell.hasOption_bombUp;
      this.hasOption_rangeUp = cell.hasOption_rangeUp;
      if (cell.bomb != null && !cell.bomb.hasExploded) {
        bomb = cell.bomb;
        bomb.cell = this;
      }
    }
  }

  static class GameState {
    private int width;
    private int height;
    Cell[][] grid;
    List<Bomb> bombs = new ArrayList<>();
    APlayer[] players = new APlayer[2];
    
    GameState(int width, int height) {
      this.width = width;
      this.height = height;
      
      grid = new Cell[width][height];
    }
    
    private Cell getCellAt(int x, int y) {
      if (x < 0 || x >= width || y < 0 || y >= height) {
        return Game.NO_CELL;
      }
      return grid[x][y];
    }

    public void clone(GameState gameState) {
      for (int x = 0; x < grid.length; x++) {
        for (int y = 0; y < grid[0].length; y++) {
          grid[x][y].copyFrom(gameState.grid[x][y]);
          if (grid[x][y].bomb != null) {
            bombs.add(grid[x][y].bomb);
          }
        }
      }
    }

    public void init() {
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          Cell c = new Cell(x, y);
          grid[x][y] = c;
        }
      }
      // update neighbors
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          grid[x][y].neighbors[Cell.UP] = getCellAt(x, y-1);
          grid[x][y].neighbors[Cell.DOWN] = getCellAt(x, y+1);
          grid[x][y].neighbors[Cell.LEFT] = getCellAt(x-1, y);
          grid[x][y].neighbors[Cell.RIGHT] = getCellAt(x+1, y);
        }
      }
    }

    public boolean deadly() {
      return false;
    }
    
  }
  static class Game {
    private static final Cell NO_CELL = new Cell(-1, -1);
    static {
      NO_CELL.type = Cell.Type.WALL;
      NO_CELL.weight = Cell.MIN_WEIGHT;
    }
    private int width;
    private int height;

    List<Cell> boxes = new ArrayList<>();

    Map<Integer, APlayer> players = new HashMap<>();
    APlayer me = null;

    GameState[] states = new GameState[8];

    public Game(int width, int height) {
      this.width = width;
      this.height = height;
      initBoard();
    }

    private void initBoard() {
      for (int i=0;i<8;i++) {
        states[i] = new GameState(width, height);
        states[i].init();
      }
    }

    public void addRow(int rowIndex, String row) {
      // System.err.println("row: "+row);
      for (int i = 0; i < row.length(); i++) {
        char c = row.charAt(i);
        Cell cell = states[0].grid[i][rowIndex];
        if (c == '.') {
          cell.type = Cell.Type.FLOOR;
        } else if (c == 'X') {
          cell.type = Cell.Type.WALL;
          updateWallInfluence(cell);
        } else if (c >= '0') {
          cell.type = Cell.Type.BOX;
          cell.option = c - '0';
          boxes.add(cell);
        }
      }
    }

    private void updateWallInfluence(Cell cell) {
      cell.weight = Cell.MIN_WEIGHT;
    }

    private void updateBoxInfluence(Cell originCell) {
      int x = originCell.x;
      int y = originCell.y;
      for (int rot = 0; rot < 4; rot++) {
        for (int d = 1; d < me.bombRange; d++) {
          Cell c = states[0].getCellAt(x + d * rotx[rot], y + d * roty[rot]);
          if (c.type == Cell.Type.WALL || c.type == Cell.Type.BOX) {
            break;
          }
          if (c.willExplodeIn > 0 
              && c.willExplodeIn < Integer.MAX_VALUE - 2) {
            continue;
          } else {
            c.boxCount++;
          }
        }
      }
    }

    void debugBombExplosions(String message, int step) {
      System.err.println("*** "+message+" ***");
      step = Math.min(step, 7);
      for (int y=0;y<height;y++) {
        String result = "";
        for (int x=0;x<width;x++) {
          if (states[step].grid[x][y].type == Cell.Type.WALL) {
            result+="X";
          } else {
            if (states[step].grid[x][y].exploding) {
              result+="@";
            } else {
              result+=" ";
            }
          }
        }
        System.err.println(result);
      }
    }
    
    void debugBox(String message, int step) {
      System.err.println("*** "+message+" ***");
      step = Math.min(step, 7);
      for (int y=0;y<height;y++) {
        String result = "";
        for (int x=0;x<width;x++) {
          if (states[step].grid[x][y].type == Cell.Type.BOX) {
            if (states[step].grid[x][y].willExplodeIn != Integer.MAX_VALUE) {
              result+=states[step].grid[x][y].willExplodeIn;
            } else {
              result+="B";
            }
          } else {
            result+=" ";
          }
        }
        System.err.println(result);
      }
    }
    
    private void updateOptionInfluence(Cell cell) {
      cell.weight += 2;
    }

    public void resetGame() {
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          states[0].grid[x][y].reset();
        }
      }
      boxes.clear();
    }

    static class EntityInfo {
      int entityType, owner, x, y;
      private int param1;
      private int param2;

      public EntityInfo(int entityType, int owner, int x, int y, int param1, int param2) {
        super();
        this.entityType = entityType;
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.param1 = param1;
        this.param2 = param2;
      }
    }

    void updateEntities(List<EntityInfo> entities) {
      states[0].bombs.clear();
      for (EntityInfo info : entities) {
        updateOneEntity(info);
      }
    }

    void updateOneEntity(EntityInfo info) {
      Cell cell = states[0].grid[info.x][info.y];

      APlayer player = players.get(info.owner);
      if (info.entityType == ENTITY_PLAYER) {
        if (player == null) {
          player = new APlayer();
          players.put(info.owner, player);
        }
        player.x = info.x;
        player.y = info.y;
        player.cell = cell;
        if (info.owner == myIndex) {
          me = player;
        }
      } else {
        if (info.entityType == ENTITY_ITEM) {
          if (info.param1 == 1) {
            cell.hasOption_rangeUp = 1;
          } else if (info.param1 == 2) {
            cell.hasOption_bombUp = 1;
          }
          updateOptionInfluence(cell);
        } else if (info.entityType == ENTITY_BOMB) {
          Bomb bomb = new Bomb(player, cell, info.param1 /* tickLeft */, info.param2 /* range */);
          updateOneBomb(bomb);
        }
      }
    }

    public void updateOneBomb(Bomb bomb) {
      states[0].bombs.add(bomb);
      bomb.cell.placeBomb(bomb);
    }


    
    void updateBombInfluence(Bomb bomb) {
      //System.err.println("update bomb at ("+bombCell.x+","+bombCell.y+") with "+range+" range and "+tickLeft+" tickLeft");
      
      if (bomb.cell.willExplodeIn < bomb.timer) {
        // the cell will explode sooner that bomb.timer, so the 'bomb' will be triggered sooner
        bomb.timer = bomb.cell.willExplodeIn;
      }
      
      bomb.cell.bomb = bomb;
      bomb.cell.willExplodeIn = Math.min(bomb.cell.willExplodeIn, bomb.timer);

      for (int rot = 0; rot < 4; rot++) {
        for (int d = 1; d <= bomb.range; d++) {
          //System.err.println("check ("+(bombCell.x + d * rotx[rot])+","+ (bombCell.y + d * roty[rot])+")");
          Cell c = states[0].getCellAt(bomb.cell.x + d * rotx[rot], bomb.cell.y + d * roty[rot]);
          if (c.isBlocked()) {
            break;
          }
          c.willExplodeIn = Math.min(c.willExplodeIn, bomb.timer);
          if (c.isSoftBlocked()) {
            // there is a bomb
            if (c.bomb.timer > bomb.timer) {
              // this bomb will trigger another one, need to update its effect
              c.bomb.timer = bomb.timer;
              updateBombInfluence(c.bomb);
            } 
            break;
          }
          if (bomb.player != me) {
            c.threat = Math.max(8 - bomb.timer, c.threat);
          }
        }
      }
    }

    private void play() {
      Path currentPath = null;
      Path nextPath = null;
      List<Action> actions = new ArrayList<>();
      while (true) {
        prepareGameState();
        
        if (!actions.isEmpty() ) {
          int stepToSimulate = Math.min(8, actions.size());
          for (int i =0;i<stepToSimulate;i++) {
            simulateOneTurn(i, actions.get(i), null);
            if (states[i].deadly()) {
              actions.clear();
              break;
            }
          }
        }
        // print some debug information
        //debugBombExplosions("now", 0);
        //debugBombExplosions("time+1",1);
        //debugBox("now", 0);
        //debugBox("t+1", 1);


      }
    }

    private Path findBestPath() {
      // find a better way
      double maxScore = -1;
      Cell maxCell = me.cell;
      Path.PathItem maxItem = null;
      Path bestPath = null;

      for (int y=0;y<height;y++) {
        for (int x=0;x<width;x++) {
          Cell c = states[0].getCellAt(x, y);
          if (c.isBlocked()) {
            continue;
          }
          int distance = Math.abs(c.x-me.x) + Math.abs(c.y-me.y);
          double score = c.boxCount
              + c.hasOption_bombUp
              + c.hasOption_rangeUp
                - 0.2*distance;
          if (score > maxScore) {
            Path path = new Path(states, me.x, me.y, c.x, c.y);
            Path.PathItem item = path.find();
            if (item != null) {
              System.err.println("found a path: "+c.x+","+c.y+" with score: "+score);
              System.err.println("path ("+path.path.size()+ ") :  ");
              for (Path.PathItem i : path.path) {
                System.err.print(i.cell.x+","+i.cell.y+" --> ");
              }
              System.err.println("");
              maxScore = score;
              maxCell = c;
              maxItem = item;
              bestPath = path;
            }
          }
        }
      }
      return bestPath;
    }

    private boolean isNotSafe(Path currentPath) {
      for (int i=0;i<currentPath.path.size();i++) {
        Path.PathItem item = currentPath.path.get(i);
        if (!isSafe(i, item.cell.x, item.cell.y)) {
          return false;
        }
      }
      return true;
    }

    private void prepareGameState() {
      resetGame();
      for (int y = 0; y < height; y++) {
        String row = in.nextLine();
        addRow(y, row);
      }
      
      int entitiesCount = in.nextInt();
      List<EntityInfo> entities = new ArrayList<>();
      for (int i = 0; i < entitiesCount; i++) {
        int entityType = in.nextInt();
        int owner = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        int param1 = in.nextInt();
        int param2 = in.nextInt();
        entities.add(new EntityInfo(entityType, owner, x, y, param1, param2));
      }
      in.nextLine();

      // update board now !
      updateEntities(entities);
      for (Cell cell : boxes) {
        updateBoxInfluence(cell);
      }
    }

    private boolean isSafe(int i, int x, int y) {
      if (i>=8) {
        return true; // no simulation
      } else {
        return !states[i].grid[x][y].exploding || states[i].grid[x][y].isSafe(me);
      }
    }

    private boolean isDyingNextStep(Path.PathItem firstStep, int moveX, int moveY) {
      return states[1].grid[moveX][moveY].exploding && !states[1].grid[moveX][moveY].isSafe(me);
    }

    public void simulateOneTurn(int step, Action myAction, Action opponentAction) {
      if (step > 0) {
        states[step].clone(states[step-1]);
      }
      if (myAction != null) {
        if (myAction.dropBomb == true) {
          Cell mySimulatedCell = states[step].grid[myAction.x][myAction.y];
          Bomb bomb = new Bomb(me, mySimulatedCell, 8, me.bombRange);
          states[step].bombs.add(bomb);
          mySimulatedCell.placeBomb(bomb);
        }
      }
      for (Bomb b : states[step].bombs ) {
        if (!b.hasExploded) {
          b.update();
        }
      }
    }
  }

  public static void main(String args[]) {
    in = new Scanner(System.in);
    int width = in.nextInt();
    int height = in.nextInt();
    myIndex = in.nextInt();
    in.nextLine();

    Game game = new Game(width, height);
    game.me = new APlayer();
    game.me.index = myIndex;

    game.play();
  }

  public static class ActionList {
    List<Action> actions;
  }
  
  public static class Action {
    int x,y;
    boolean dropBomb;
    String message;
    
    String get() {
      return (dropBomb ? "BOMB" : "MOVE") + " "+ x +" "+ y +" "+ message; 
    }
  }
  
  public static class Path {
    Map<Cell, PathItem> closedList = new HashMap<>();
    List<PathItem> openList = new ArrayList<>();
    
    List<PathItem> path = new ArrayList<>();
    
    private GameState[] states;
    private int ix;
    private int iy;
    private int tx;
    private int ty;

    Path(GameState[] states, int ix, int iy, int tx, int ty) {
      this.states = states;
      this.ix = ix;
      this.iy = iy;
      this.tx = tx;
      this.ty = ty;

    }

    PathItem find() {
      PathItem item = calculus();
      path.clear();
      if (item != null) {
        calculatePath(item);
      }
      return item;
    }

    private void calculatePath(PathItem item) {
      PathItem i = item;
      while (i != null) {
        path.add(0, i);
        i = i.precedent;
      }
    }
    PathItem calculus() {
      Cell origin = states[0].grid[ix][iy];
      PathItem root = new PathItem();
      root.cell = origin;

      openList.add(root);

      while (openList.size() > 0) {
        PathItem visiting = openList.remove(0); // imagine it's the best
        Cell cell = visiting.cell;
        if (cell.x == tx && cell.y == ty) {
          return visiting;
        }

        closedList.put(cell, visiting);
        int step = Math.min(7, visiting.length());
        if (cell.y > 0) {
          Cell cellUp = states[step].grid[cell.x][cell.y - 1];
          addToOpenList(visiting, cell, cellUp);
        }
        if (cell.y < states[step].height - 1) {
          Cell cellDown = states[step].grid[cell.x][cell.y + 1];
          addToOpenList(visiting, cell, cellDown);
        }
        if (cell.x > 0) {
          Cell cellLeft = states[step].grid[cell.x - 1][cell.y];
          addToOpenList(visiting, cell, cellLeft);
        }
        if (cell.x < states[step].width - 1) {
          Cell cellRight = states[step].grid[cell.x + 1][cell.y];
          addToOpenList(visiting, cell, cellRight);
        }
        // sort with distances
        Collections.sort(openList, new Comparator<PathItem>() {
          @Override
          public int compare(PathItem o1, PathItem o2) {
            return Integer.compare(o1.totalPrevisionalLength, o2.totalPrevisionalLength);
          }
        });
      }
      return null; // not found !
    }

    private void addToOpenList(PathItem visiting, Cell fromCell, Cell toCell) {
      if (closedList.containsKey(toCell)) {
        return;
      }
      if (!toCell.isBlocked() && toCell.willExplodeIn != visiting.cumulativeLength+1) {
        PathItem pi = new PathItem();
        pi.cell = toCell;
        pi.cumulativeLength = visiting.cumulativeLength + 1;
        pi.totalPrevisionalLength = pi.cumulativeLength + manhattanDistance(fromCell);
        pi.precedent = visiting;
        openList.add(pi);
      }
    }

    private int manhattanDistance(Cell cell) {
      return Math.abs(cell.x - tx) + Math.abs(cell.y - ty);
    }

    public static class PathItem {
      int cumulativeLength = 0;
      int totalPrevisionalLength = 0;
      PathItem precedent = null;
      Cell cell;

      public int length() {
        PathItem i = this;
        int count = 0;
        while (i != null) {
          count++;
          i = i.precedent;
        }
        return count;
      }
    }
  }
}
