import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class PlayerV2 {
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
      System.err.println("updating bomb at "+cell.x+","+cell.y);
      System.err.println("timer: "+timer);

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
      if (bomb != null) {
        if (this.bomb.timer > fromBomb.timer) {
          this.bomb.timer = fromBomb.timer;
        }
      }
    }

    public Cell copy() {
      Cell newCell = new Cell(x,y);
      if (bomb != null && !bomb.hasExploded) {
        newCell.bomb = bomb;
      }
      return newCell;
    }
  }

  static class GameState {
    Cell[][] grid;
    private int width;
    private int height;

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
          grid[x][y] = gameState.grid[x][y].copy();
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

    GameState[] state = new GameState[8];
    private List<Bomb> bombs = new ArrayList<>();

    public Game(int width, int height) {
      this.width = width;
      this.height = height;
      initBoard();
    }

    private void initBoard() {
      for (int i=0;i<8;i++) {
        state[i] = new GameState(width, height);
        state[i].init();
      }
    }

    public void addRow(int rowIndex, String row) {
      // System.err.println("row: "+row);
      for (int i = 0; i < row.length(); i++) {
        char c = row.charAt(i);
        Cell cell = state[0].grid[i][rowIndex];
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
          Cell c = state[0].getCellAt(x + d * rotx[rot], y + d * roty[rot]);
          if (c.type == Cell.Type.WALL || c.type == Cell.Type.BOX) {
            break;
          }
          if (c.willExplodeIn > 0 && c.willExplodeIn < Integer.MAX_VALUE - 2) {
            continue;
          } else {
            c.boxCount++;
          }
        }
      }
    }

    void debug(String message, int step) {
      System.err.println("*** "+message+" ***");
      step = Math.max(step, 7);
      for (int y=0;y<11;y++) {
        String result = "";
        for (int x=0;x<13;x++) {
          if (state[step].grid[x][y].type == Cell.Type.WALL) {
            result+="X";
          } else {
            if (state[step].grid[x][y].exploding) {
              result+="@";
            } else {
              result+=" ";
            }
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
          state[0].grid[x][y].reset();
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
      bombs.clear();
      for (EntityInfo info : entities) {
        updateOneEntity(info);
      }
    }

    void updateOneEntity(EntityInfo info) {
      Cell cell = state[0].grid[info.x][info.y];

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
      bombs.add(bomb);
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
          Cell c = state[0].getCellAt(bomb.cell.x + d * rotx[rot], bomb.cell.y + d * roty[rot]);
          if (c.isBlocked()) {
            break;
          }
          if (c.isSoftBlocked()) {
            // there is a bomb
            if (c.bomb.timer > bomb.timer) {
              // this bomb will trigger another one, need to update its effect
              c.bomb.timer = bomb.timer;
              updateBombInfluence(c.bomb);
            } 
            break;
          }
          c.willExplodeIn = Math.min(c.willExplodeIn, bomb.timer);
          if (bomb.player != me) {
            c.threat = Math.max(8 - bomb.timer, c.threat);
          }
        }
      }
    }

    private void play() {
      Path.PathItem currentPath = null;
      Cell nextCell = null;

      while (true) {
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
        // update board now !
        
        updateEntities(entities);
        for (Cell cell : boxes) {
          updateBoxInfluence(cell);
        }
        for (int i=0;i<8;i++) {
          simulateOneTurn(i);
        }
        
        if (me.cell.exploding && !me.cell.isSafe(me)) {
          System.err.println("je vais mourrir");
        }
        debug("now", 0);
        debug("time+1",1);
        
        double maxScore = -1;
        Cell maxCell = me.cell;
        
        Path.PathItem maxItem = null;
        for (int y=0;y<11;y++) {
          for (int x=0;x<13;x++) {
            Cell c = state[0].getCellAt(x, y);
            if (c.isBlocked()) {
              continue;
            }
            int distance = Math.abs(c.x-me.x) + Math.abs(c.y-me.y);
            double score = c.boxCount
                + c.hasOption_bombUp
                + c.hasOption_rangeUp
                  - 0.2*distance;
            if (score > maxScore) {
              Path path = new Path(state[0].grid, me.x, me.y, c.x, c.y);
              Path.PathItem item = path.find();
              if (item != null) {
                System.err.println("found : "+c.x+","+c.y+" with score: "+score);
                System.err.println("path : "+item.length()+ "--> ");
                Path.PathItem i = item;
                while(i != null) {
                  System.err.print(i.cell.x+","+i.cell.y+" <<-");
                  i = i.precedent;
                }
                System.err.println("");
                maxScore = score;
                maxCell = c;
                maxItem = item;
              }
            }
          }
        }
        in.nextLine();

        if (maxItem == null) {
          System.err.println("can't find path to best bomb");
          System.out.println("MOVE "+me.x+" "+me.y);
        } else {
          String command ="MOVE ";
          if (me.x == maxCell.x && me.y == maxCell.y) {
            command = "BOMB ";
          }
          Path.PathItem i = maxItem;
          Path.PathItem firstStep = maxItem;
          while(i.precedent != null) {
            firstStep = i;
            i=i.precedent;
          }
          System.out.println(command+firstStep.cell.x+" "+firstStep.cell.y);
        }
      }
    }

    public void simulateOneTurn(int step) {
      if (step > 0) {
        state[step].clone(state[step-1]);
      }
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          state[0].grid[x][y].resetBombsArtefacts();
        }
      }
      for (Bomb b : bombs ) {
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

  public static class Path {
    Map<Cell, PathItem> closedList = new HashMap<>();
    List<PathItem> openList = new ArrayList<>();
    private Cell[][] grid;
    private int ix;
    private int iy;
    private int tx;
    private int ty;

    Path(Cell[][] grid, int ix, int iy, int tx, int ty) {
      this.grid = grid;
      this.ix = ix;
      this.iy = iy;
      this.tx = tx;
      this.ty = ty;

    }

    PathItem find() {
      Cell origin = grid[ix][iy];
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
        if (cell.y > 0) {
          Cell cellUp = grid[cell.x][cell.y - 1];
          addToOpenList(visiting, cell, cellUp);
        }
        if (cell.y < grid[0].length - 1) {
          Cell cellDown = grid[cell.x][cell.y + 1];
          addToOpenList(visiting, cell, cellDown);
        }
        if (cell.x > 0) {
          Cell cellLeft = grid[cell.x - 1][cell.y];
          addToOpenList(visiting, cell, cellLeft);
        }
        if (cell.x < grid.length - 1) {
          Cell cellRight = grid[cell.x + 1][cell.y];
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
