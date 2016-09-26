import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Player {
  private static Scanner in;
  private static int myIndex;

  private static final int ENTITY_PLAYER = 0;
  private static final int ENTITY_BOMB = 1;
  private static final int ENTITY_ITEM = 2;

  static int[] rotx = { 1, 0, -1, 0 };
  static int[] roty = { 0, 1, 0, -1 };
  private static Game game;

  enum EntityType {
    PLAYER, BOMB
  }
  enum CellType {
    WALL, BOX, FLOOR
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

    int manhattanDistance(P p) {
      return Math.abs(x - p.x) + Math.abs(y - p.y);
    }

    @Override
    public String toString() {
      return "(" + x + "," + y + ")";
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
  }

  static class Entity  {
    public Entity(int type, int owner, int x, int y) {
      this.type = type;
      this.owner = owner;
      p = new P(x,y);
    }
    int type;
    int owner;
    P p;
    public void update(GameState state) {
    }
  }
  static class APlayer extends Entity {
    public APlayer(int owner, int x, int y, int param1, int param2) {
      super(ENTITY_PLAYER, owner, x, y);
      bombsLeft = param1;
      bombRange = param2;
    }
    int bombRange = 3;
    int bombsLeft = 1;
    boolean isDead = false;
  }
  
  static class Bomb extends Entity {
    public Bomb(int owner, int x, int y, int param1, int param2) {
      super(ENTITY_BOMB, owner, x, y);
      ticksLeft=param1;
      range=param2;
    }
    int ticksLeft;
    int range;
    
    public void update(GameState state) {
      ticksLeft--;
      if (ticksLeft <= 0) {
        explode(state);
      } else {
        affectBoxInfluenza(state);
      }
    }
    private void affectBoxInfluenza(GameState state) {
      for (int rot=0;rot<4;rot++) {
        Cell currentCell = state.getCellAt(p.x, p.y);
        for (int d=1;d<range;d++) {
          currentCell = currentCell.neighbors[rot];
          if (currentCell.hardBlock()) {
            break;
          }
          currentCell.willBeHitLater = this.ticksLeft;
          if (currentCell.type != CellType.FLOOR) {
            break;
          }
        }
      }
    }

    public void explode(GameState state) {
      state.getCellAt(p.x, p.y).addExplodedBomb(this, state);
      
      for (int rot=0;rot<4;rot++) {
        Cell currentCell = state.getCellAt(p.x, p.y);
        for (int d=1;d<range;d++) {
          currentCell = currentCell.neighbors[rot];
          if (currentCell.hardBlock()) {
            break;
          }
          currentCell.addExplodedBomb(this, state);
          if (currentCell.softBlock()) {
            break;
          }
        }
      }
    }
  }
  static class Item extends Entity {
    final static int RANGE_UP = 1;
    final static int BOMB_UP = 2;
    
    public Item(int owner, int x, int y, int param1, int param2) {
      super(ENTITY_ITEM, owner, x, y);
      option = param1;
      // param2 not used
    }

    int option;
  }  
  
  static class Cell {
    public static final int RIGHT = 0;
    public static final int UP = 1;
    public static final int LEFT = 2;
    public static final int DOWN = 3;
    public static final Cell WALL = new Cell(-1,-1);
    static {
      WALL.type = CellType.WALL;
      WALL.p = new P(-1,-1);
    }
    
    public Cell[] neighbors = new Cell[4];
    P p;
    
    CellType type;
    int option;
    List<Entity> entities = new ArrayList<>();
    List<Bomb> explodedBombs = new ArrayList<>();
    int boxInfluenza;
    int willBeHitLater;
    
    public Cell(int x, int y) {
      this.p = new P(x,y);
    }

    public void reset() {
      boxInfluenza = 0;
      willBeHitLater = 0;
      explodedBombs.clear();
      entities.clear();
    }

    public void addExplodedBomb(Bomb bomb, GameState state) {
      explodedBombs.add(bomb);
      for (Entity entity : entities) {
        if (entity.type == ENTITY_BOMB && !explodedBombs.contains(bomb)) {
          bomb.explode(state);
        }
      }
    }

    public boolean softBlock() {
      if (type == CellType.BOX) return true;
      if (entities.isEmpty()) {
        return false;
      } else {
        for (Entity entity :entities) {
          if (entity.type != ENTITY_PLAYER) { // bombs go through players
            return true;
          }
        }
        return false;
      }
    }

    public boolean hardBlock() {
      return type == CellType.WALL;
    }

    public void updateBoxInfluenza(int range) {
      if (type == CellType.BOX && willBeHitLater == 0) {
        Cell currentCell = this;
        for (int rot=0;rot<4;rot++) {
          currentCell = this;
          for (int d=1;d<range;d++) {
            currentCell = currentCell.neighbors[rot];
            if (currentCell.hardBlock()) {
              break;
            }
            currentCell.boxInfluenza+=1;
            if (currentCell.softBlock()) {
              break;
            }
          }
        }
      }
    }

    public boolean hasOption(int option) {
      if (this.type != CellType.FLOOR) {
        return false;
      }
      for (Entity entity : entities) {
        if (entity.type == ENTITY_ITEM) {
          Item item = (Item)entity;
          if (item.option == option) {
            return true;
          }
        }
      }
      return false;
    }

    public boolean hasOptionBombUp() {
      return hasOption(Item.BOMB_UP);
    }

    public boolean hasOptionRangeUp() {
      return hasOption(Item.RANGE_UP);
    }

    public void clone(Cell fromCell) {
      reset();
      this.type = fromCell.type;
      this.option = fromCell.option;
      for (Entity entity : fromCell.entities) {
        this.entities.add(entity);
      }
    }

    // will die if go here
    public boolean deadly() {
      if (explodedBombs.isEmpty()) {
        return false;
      } else {
        for (Bomb bomb : explodedBombs) {
          if (bomb.owner != game.myIndex) {
            return true;
          }
        }
      }
      return false;
    }
  }
  
  static class Action {
    P pos;
    boolean dropBomb;
    String message;
    
    String get() {
      return (dropBomb ? "BOMB" : "MOVE") + " "+ pos.x +" "+ pos.y +" "+ message; 
    }
  }
  
  static class Game {
    int width, height;
    GameState currentState;
    GameState[] states = new GameState[1];
    
    public int myIndex;
    
    Game(int width, int height) {
      this.width = width;
      this.height = height;
      
      for (int i=0;i<states.length;i++) {
        states[i] = new GameState(width, height);
      }
      currentState = states[0];
    }
    
    private void play() {
      while (true) {
        prepareGameState();
        currentState.computeRound();

        updateNextStates();
        
//        System.err.println("Current grid:");
//        System.err.println("-------------");
//        currentState.debugBombs();
//        System.err.println("Next grid:");
//        System.err.println("----------");
//        states[1].debugBombs();
        
        Action action = computeBestMove_v1();
        System.out.println(action.get());
      }
    }

    void updateNextStates() {
      for (int i=1;i<states.length;i++) {
        states[i].clone(states[i-1]);
        states[i].computeRound();
      }
    }

    private Action computeBestMove_v1() {
      Action action = new Action();
      action.pos = currentState.players[0].p;
      action.dropBomb = true;
      
      currentState.debugBoxInfluenza();
      
      double maxScore = -1;
      Path bestPath = null;
      
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          Cell c = currentState.getCellAt(x, y);
          int distance = c.p.manhattanDistance(currentState.players[0].p);
          double score = c.boxInfluenza
              + (c.hasOptionBombUp() ? 1 : 0)
              + (c.hasOptionRangeUp() ? 1 : 0)
                - 0.5*distance
              + 0;
          if (score > maxScore) {
            maxScore = score;
            Path path = new Path(states, currentState.players[0].p, c.p);
            Path.PathItem item = path.find();
            if (!path.path.isEmpty()) {
              bestPath = path;
              System.err.println("new best path with score : "+score);
              bestPath.debug();
            }
          }
        }
      }
      if (bestPath != null) {
        if (bestPath.path.size() < 2) {
          action.dropBomb = true;
          action.pos = currentState.players[0].p;
        } else {
          Path.PathItem i = bestPath.path.get(1);
          action.dropBomb = false;
          action.pos = i.cell.p;
        }
      }
      return action;
    }

    private void prepareGameState() {
      currentState.reset();
      for (int y = 0; y < height; y++) {
        String row = in.nextLine();
        currentState.addRow(y, row);
      }
      
      int entitiesCount = in.nextInt();
      currentState.entities.clear();
      for (int i = 0; i < entitiesCount; i++) {
        int entityType = in.nextInt();
        int owner = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        int param1 = in.nextInt();
        int param2 = in.nextInt();
        Entity entity;
        Cell cell = currentState.getCellAt(x, y);
        if (entityType == ENTITY_BOMB) {
          entity = new Bomb(owner, x,y, param1, param2);
        } else if (entityType == ENTITY_PLAYER) {
          entity = new APlayer(owner, x,y, param1, param2);
        } else if (entityType == ENTITY_ITEM) {
          entity = new Item(owner, x,y, param1, param2);
        } else {
          System.err.println("Hmmm entitytype not found");
          entity = new Item(owner, x,y, param1, param2);
        }
        currentState.addEntity(entity);
        cell.entities.add(entity);
      }
      in.nextLine();
    }
  }
  static class GameState {
    int width, height;
    List<Entity> entities = new ArrayList<>();
    Cell[][] grid;
    APlayer players[] = new APlayer[2];
    
    GameState(int width, int height) {
      this.width = width;
      this.height = height;
      grid = new Cell[width][height];
      initGrid();
    }

    public void clone(GameState fromState) {
      this.entities.addAll(fromState.entities);
      this.players[0] = fromState.players[0];
      this.players[1] = fromState.players[1];
      
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          Cell fromCell = fromState.getCellAt(x, y);
          Cell toCell = this.getCellAt(x, y);
          toCell.clone(fromCell);
        }
      }
    }

    public void addEntity(Entity entity) {
      entities.add(entity);
      if (entity.type == ENTITY_PLAYER) {
        if (entity.owner == game.myIndex) {
          players[0] = (APlayer)entity;
        } else {
          players[1] = (APlayer)entity;
        }
      }
    }

    private void initGrid() {
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

    private Cell getCellAt(int x, int y) {
      if (x < 0 || x >= width || y < 0 || y >= height) {
        return Cell.WALL;
      }
      return grid[x][y];
    }

    public void computeRound() {
      for (Entity entity : entities) {
        entity.update(this);
      }
      removeHittedBoxes();
      updateBoxInfluenza();
      // debugBombs();
      // debugBoxInfluenza();
      // debugPlayerAccessibleCells();
    }

    private void debugPlayerAccessibleCellsWithAStar() {
      // TODO don't use A* to check this !
      System.err.println("Accessible cells from "+players[1].p);
      GameState[] states = new GameState[1];
      states[0] = this;

      for (int y = 0; y < height; y++) {
        String result="";
        for (int x = 0; x < width; x++) {
          Cell cell = grid[x][y];
          Path path = new Path(states, players[1].p, cell.p);
          path.find();
          if (path.path.size() > 0) {
            result+="A";
          } else {
            result+=" ";
          }
        }
        System.err.println(result);
      }
    }

    private void removeHittedBoxes() {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          Cell cell = grid[x][y];
          if (cell.type == CellType.BOX && !cell.explodedBombs.isEmpty()) {
            cell.type = CellType.FLOOR;
          }
        }
      }
    }

    private void updateBoxInfluenza() {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          Cell cell = grid[x][y];
          cell.updateBoxInfluenza(players[0].bombRange);
        }
      }
    }

    public void addRow(int y, String row) {
      for (int x = 0; x < row.length(); x++) {
        char c = row.charAt(x);
        Cell cell = grid[x][y];
        if (c == '.') {
          cell.type = CellType.FLOOR;
        } else if (c == 'X') {
          cell.type = CellType.WALL;
        } else if (c >= '0') {
          cell.type = CellType.BOX;
          cell.option = c - '0';
        }
      }
    }

    public void reset() {
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          grid[x][y].reset();
        }
      }      
    }
    
    void debugBoxInfluenza() {
      System.err.println("Bow influenza for range "+players[0].bombRange);
      for (int y = 0; y < height; y++) {
        String result="";
        for (int x = 0; x < width; x++) {
          Cell cell = grid[x][y];
          if (cell.type == CellType.WALL) {
            result+="X";
          } else if (cell.type == CellType.BOX) {
            result+="b";
          } else if (cell.boxInfluenza > 0) {
            result+=""+cell.boxInfluenza;
          } else {
            result+=" ";
          }
        }
        System.err.println(result);
      }
    }
    
    void debugBombs() {
      for (int y = 0; y < height; y++) {
        String result="";
        for (int x = 0; x < width; x++) {
          Cell cell = grid[x][y];
          if (!cell.explodedBombs.isEmpty()) {
            result+="@";
          } else if (cell.type == CellType.BOX) {
            result+="b";
          } else if (cell.type == CellType.WALL) {
            result+="X";
          } else if (!cell.entities.isEmpty()) {
            result+="s";
          } else {
            result+=" ";
          }
        }
        System.err.println(result);
      }
    }
  }
  
  public static void main(String[] args) {
    in = new Scanner(System.in);
    int width = in.nextInt();
    int height = in.nextInt();
    myIndex = in.nextInt();
    in.nextLine();

    game = new Game(width, height);
    game.myIndex = myIndex;

    game.play();
  }
  
  /**
   * PATH : A*
   *
   */
  public static class Path {
    Map<Cell, PathItem> closedList = new HashMap<>();
    List<PathItem> openList = new ArrayList<>();
    
    List<PathItem> path = new ArrayList<>();
    
    private GameState[] states;
    P from;
    P target;
    
    Path(GameState[] states, P from, P target) {
      this.states = states;
      this.from = from;
      this.target = target;
    }

    public void debug() {
      System.err.println("found a path: "+target);
      System.err.println("path ("+path.size()+ ") :  ");
      for (Path.PathItem i : path) {
        System.err.print(i.cell.p+" --> ");
      }
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
      Cell origin = states[0].grid[from.x][from.y];
      PathItem root = new PathItem();
      root.cell = origin;

      openList.add(root);

      while (openList.size() > 0) {
        PathItem visiting = openList.remove(0); // imagine it's the best
        Cell cell = visiting.cell;
        if (cell.p.equals(target)) {
          return visiting;
        }

        closedList.put(cell, visiting);
        int step = Math.min(states.length-1, visiting.length());
        if (cell.p.y > 0) {
          Cell cellUp = states[step].grid[cell.p.x][cell.p.y - 1];
          addToOpenList(visiting, cell, cellUp);
        }
        if (cell.p.y < states[step].height - 1) {
          Cell cellDown = states[step].grid[cell.p.x][cell.p.y + 1];
          addToOpenList(visiting, cell, cellDown);
        }
        if (cell.p.x > 0) {
          Cell cellLeft = states[step].grid[cell.p.x - 1][cell.p.y];
          addToOpenList(visiting, cell, cellLeft);
        }
        if (cell.p.x < states[step].width - 1) {
          Cell cellRight = states[step].grid[cell.p.x + 1][cell.p.y];
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
      if (!toCell.hardBlock() && toCell.type != CellType.BOX && !toCell.deadly()) {
        PathItem pi = new PathItem();
        pi.cell = toCell;
        pi.cumulativeLength = visiting.cumulativeLength + 1;
        pi.totalPrevisionalLength = pi.cumulativeLength + fromCell.p.manhattanDistance(target);
        pi.precedent = visiting;
        openList.add(pi);
      }
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
  /** End of PATH */
}
