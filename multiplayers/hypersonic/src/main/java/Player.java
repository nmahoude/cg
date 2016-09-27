import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

class Player {
  private static Scanner in;
  
  private static final int ENTITY_PLAYER = 0;
  private static final int ENTITY_BOMB = 1;
  private static final int ENTITY_ITEM = 2;
  
  static int[] rotx = { 1, 0, -1, 0, 0 };
  static int[] roty = { 0, 1, 0, -1, 0 };

  static class Entity  {
    GameState state;
    public Entity(GameState state, int type, int owner, int x, int y) {
      this.state = state; // the state we deal with
      this.type = type;
      this.owner = owner;
      p = new P(x,y);
    }
    int type;
    int owner;
    P p;
    public void update(GameState state) {
    }
    public Entity duplicate(GameState newState) {
      return null;
    }
  }
  static class APlayer extends Entity {
    public APlayer(GameState state, int owner, int x, int y, int param1, int param2) {
      super(state, ENTITY_PLAYER, owner, x, y);
      bombsLeft = param1;
      bombRange = param2;
    }
    int bombRange = 3;
    int bombsLeft = 1;
    public int points = 0;
    public boolean isDead = false;
    
    public Entity duplicate(GameState newState) {
      APlayer aPlayer = new APlayer(newState, owner, p.x, p.y, bombsLeft, bombRange);
      aPlayer.points = this.points;
      aPlayer.isDead = this.isDead;
      return aPlayer;
    }
  }
  
   static class Bomb extends Entity {
    public Bomb(GameState state, int owner, int x, int y, int ticksLeft, int range) {
      super(state, ENTITY_BOMB, owner, x, y);
      this.ticksLeft=ticksLeft;
      this.range=range;
      
      // update state grid
      updateStateGrid();
    }
    public Entity duplicate(GameState newState) {
      return new Bomb(newState, owner, p.x, p.y, ticksLeft, range);
    }

    int ticksLeft;
    int range;
    
    public void update(GameState state) {
      if (ticksLeft == 0){
        return; // already triggered
      }
      ticksLeft--;
      updateStateGrid();

      if (ticksLeft <= 0) {
        explode(state);
      } else {
        affectBoxInfluenza(state);
      }
    }
    private void updateStateGrid() {
      if (ticksLeft == 0) {
        state.grid[p.x][p.y] = GameState.CELL_FIRE;
      } else {
        state.grid[p.x][p.y] = GameState.CELL_BOMB_0+ticksLeft;
      }
    }

    private void affectBoxInfluenza(GameState state) {
      for (int rot=0;rot<4;rot++) {
        for (int d=1;d<range;d++) {
        }
      }
    }

    void updatePlayerDeath(P position) {
      for (int p=0;p<4;p++) {
        if (state.players[p] != null && state.players[p].p.equals(position)) {
          state.players[p].isDead = true;
        }
      }
    }
    public void explode(GameState state) {
      if (state.players[owner] != null) {
        state.players[owner].bombsLeft++;
      }
      updatePlayerDeath(this.p);
      
      for (int rot=0;rot<4;rot++) {
        for (int d=1;d<range;d++) {
          int testedX = p.x+d*rotx[rot];
          int testedY = p.y+d*roty[rot];
          
          int value = state.getCellAt(testedX, testedY);
          if (GameState.explosionBlocked(value)) {
            break;
          }

          P testedP = P.get(testedX, testedY);
          updatePlayerDeath(testedP);

          state.explodedBombs.add(this);
          if (GameState.explosionSoftBlocked(value)) {
            if (GameState.isABomb(value)) {
              // TODO should trigger this bomb to
              state.triggerBomb(testedP);
            } else if (GameState.isABox(value)) {
              state.hittedBoxes.add(testedP);
              if (state.players[owner] != null) {
                state.players[owner].points ++;
              }
            }
            break;
          }
          state.grid[testedX][testedY] = GameState.CELL_FIRE;
        }
      }
    }
  }
  static class Item extends Entity {
    final static int RANGE_UP = 1;
    final static int BOMB_UP = 2;
    private int param2;
    
    public Item(GameState state, int owner, int x, int y, int param1, int param2) {
      super(state, ENTITY_ITEM, owner, x, y);
      option = param1;
      this.param2 = param2;// param2 not used
    }
    public Entity duplicate(GameState newState) {
      return new Item(newState, owner, p.x, p.y, option, param2);
    }

    int option;
  }  
  
  static class Action {
    P pos;
    boolean dropBomb;
    String message;
    
    String get() {
      return (dropBomb ? "BOMB" : "MOVE") + " "+ pos.x +" "+ pos.y +" "+ message; 
    }
  }
  
  static abstract class AI {
    Game game;
    enum Mode {
      CRUISING,
      DEFENSIVE,
      OFFENSIVE
    }
    Mode mode = Mode.CRUISING;
    List<APlayer> offensive = new ArrayList<>();
  
    List<Action> actions = new ArrayList<>();
    public int myIndex;
    
    abstract void compute();
  }
  static class AI1 extends AI {
    void compute() {
//      GameState cState = game.currentState;
//      int index = 0;
//      while (cState != null && index < 10) {
//        System.err.println("Step : "+index);
//        cState.debugBombs();
//        cState = cState.childs.get(Game.MOVE_STAY_NOBOMB);
//        index++;
//      }
      
      
      Action action = new Action();
      action.pos = game.currentState.players[myIndex].p;
      action.dropBomb = false;
      action.message = "FOUND NOTHING TODO :(";
      
      GameState state0 = game.currentState;
      P playerPos = state0.players[0].p;

      //1. find best influencedCell
      double maxScore = -100000;
      Path bestPath = null;

      for (int x=0;x<state0.width;x++) {
        for (int y=0;y<state0.height;y++) {
          P target = P.get(x, y);
          int value = state0.getCellAt(x, y);
          if (GameState.canWalkThrough(value)) {
            // score heuristic
            int distance = playerPos.manhattanDistance(target);
            double score = state0.boxInfluence[target.x][target.y]
                + (GameState.isAnItem(value) ? 1 : 0)
                  - 0.2*distance
                + 0;
            if (score > maxScore) { // only check path if score is better
              // start A* to find a path
              Path path = new Path(game.currentState, playerPos, target);
              Path.PathItem lastPathPos = path.find();
              if (!path.path.isEmpty()) {
                // ok we have a path
                // check if it is safe
                int tStep = 0;
                boolean isSafe= true;
                for (Path.PathItem pi : path.path) {
                  if (game.currentState.isThreat(pi.pos) == tStep++) {
                    isSafe = false;
                  }                  
                }
                for (;tStep<8;tStep++) {
                  if (game.currentState.isThreat(lastPathPos.pos) == tStep++) {
                    isSafe = false;
                  }                  
                }
                if(isSafe) {
                  maxScore = score;
                  bestPath = path;
                }
              }
            }
          }
        }
      }
      
      if (bestPath != null) {
        if (bestPath.path.size() == 1) {
          action.dropBomb = true; 
          action.message = "At dest,drop a bomb";
        } else {
          Path.PathItem firstStep = bestPath.path.get(1);
          action.pos = firstStep.pos;
          action.message = "Cruising";
        }
      }
      
      actions.clear();
      actions.add(action);
    }
  }
  
  static class MCTS {
    static Game game; // static reference to the game (only one). Better way to do it ?
    
    int[] dir = new int[5];
    boolean[] bomb = new boolean[5];
    
    int simulatedCount=0; // how many branches (total > childs.size())
    int win=0;
    
    MCTS parent;
    Map<String, MCTS> childs = new HashMap<>();


    static int [] possibilities = new int[5]; // dont' parralelize !
    static {
      possibilities[0] = 4; // don't move !
    }
    int findARandomMove(APlayer player, GameState fromState) {
      int moveLeft = 1; // don't move is always a possible move ! 
      for (int i=0;i<4;i++) {
        int px = player.p.x + rotx[i];
        int py = player.p.y + roty[i];
        if (GameState.canWalkThrough(fromState.getCellAt(px, py))) {
          possibilities[moveLeft++] = i;
        }
      }
      
      if (moveLeft == 1) {
        return possibilities[0];
      } else {
        // need to randomize here
        return possibilities[getRandom(0,moveLeft-1)];
      }
    }
    static int getRandom(int i, int j) {
      return (int)(i+j * Math.random());
    }
    void randomActions(GameState fromState) {
      for (int p=0;p<game.playersCount;p++) {
        APlayer player = fromState.players[p];
        dir[p] = findARandomMove(player, fromState);
        bomb[p] = fromState.players[p].bombsLeft > 0 ? Math.random() > 0.5 : false;
      }
    }
    String getKeyFromActions() {
      String key = "";
      for (int i=0;i<game.playersCount;i++) {
        key+= dir[i]+(bomb[i] ? "B" : "M");
      }
      return key;
    }

    boolean simulate(GameState fromState, int depth) {
      this.simulatedCount++;
      
      //do simulation here
      fromState.computeRound_MCTS();
      if (   (fromState.players[1] == null || fromState.players[1].isDead)
          && (fromState.players[2] == null || fromState.players[2].isDead)
          && (fromState.players[3] == null || fromState.players[3].isDead)) {
        if (fromState.players[0].isDead) {
          return victoryFromPoints(fromState);
        } else {
          return true; // all other dead or missing, we won
        }
      } else if (fromState.players[0].isDead) {
        return false; // loss
      }
      // choose some new random moves
      randomActions(fromState);
      for (int p=0;p<game.playersCount-1;p++) {
        int i = dir[p];
        APlayer player = fromState.players[p];
        
        // drop bomb if needed
        if (bomb[i]) {
          Bomb bomb = new Bomb(fromState, player.owner, player.p.x,player.p.y, 8, player.bombRange);
          fromState.addEntity(bomb);
          player.bombsLeft--;
        }
        // then move
        player.p = P.get(player.p.x + rotx[i], player.p.y + roty[i]);
      }
      
      if (depth > 0) {
        // prepare child
        String key = getKeyFromActions();
        MCTS chosenChild = childs.get(key);
        if (chosenChild != null) {
          // we already go there !
        } else {
          // create a new one
          chosenChild = new MCTS();
          childs.put(key, chosenChild);
        }
        boolean hasWon = chosenChild.simulate(fromState, depth-1);
        if (hasWon) {
          win++;
        }
        return hasWon;
      } else {
        return victoryFromPoints(fromState);
      }
    }
    private boolean victoryFromPoints(GameState fromState) {
      APlayer me = fromState.players[0];
      for (int i=1;i<4;i++) {
        if (fromState.players[i] != null && me.points < fromState.players[i].points) {
          return false;
        }
      }
      win++; // don't forget to count our victory
      return true; // we won !
    }
  }

  static class MCTSAI extends AI {
    MCTS root = new MCTS();
    @Override
    void compute() {
      GameState copyOfRoot = new GameState(game.currentState.width, game.currentState.height, 0);
      for (int i=0;i<100;i++) {
        copyOfRoot.duplicateFrom(game.currentState);
        root.simulate(copyOfRoot, 200);
      }
      
      MCTS chosen = null;
      double bestRatio = -1.0;
      for (Entry<String, MCTS> m : root.childs.entrySet()) {
        MCTS tested = m.getValue();
        double ratio1 = tested.win / tested.simulatedCount;
        if (ratio1 > bestRatio) {
          chosen = tested;
          bestRatio = ratio1;
        }
      }
      System.err.println("Best chosen child has a ratio of "+bestRatio + " ("+chosen.win+" / "+chosen.simulatedCount+")");
      System.err.println("best key is "+chosen.getKeyFromActions());
      
      Action action = new Action();
      action.message = chosen.getKeyFromActions()+", simulations: "+root.simulatedCount;

      APlayer player = game.currentState.players[myIndex];
      action.dropBomb = chosen.bomb[0];

      int newPosX = player.p.x+rotx[chosen.dir[0]];
      int newPosY = player.p.y+roty[chosen.dir[0]];
      action.pos = P.get(newPosX, newPosY);

      actions.clear();
      actions.add(action);
    }
  }
  
  static class Game {
    public int playersCount=2;
    private static final String MOVE_STAY_NOBOMB = "  ";
    private static final int MAX_STEPS = 20;
    int width, height;
    GameState currentState;
    int depth = 0;
    
    public int myIndex;
    
    Game(int width, int height) {
      this.width = width;
      this.height = height;
      
      currentState = new GameState(width, height, 0);
    }
    
    private void play() {
      MCTS.game = this; // OUTCH, it's ugly
      
      AI ai = new MCTSAI();
      ai.game = this;
      ai.myIndex = myIndex;
      
      while (true) {
        long long1 = System.currentTimeMillis();
        prepareGameState();
        long long2 = System.currentTimeMillis();
        //currentState.computeRound();
        long long3 = System.currentTimeMillis();
        updateNextStates();
        long long4 = System.currentTimeMillis();

        
/** debug informations */
        System.err.println("Current grid:");
        System.err.println("-------------");
//        debugThreats();
//        currentState.debugPlayerAccessibleCellsWithAStar();
//        currentState.debugBoxInfluenza();
//        currentState.debugBombs();
//        System.err.println("Next grid:");
//        System.err.println("----------");
//        states[1].debugBombs();
//        System.err.println("8th grid:");
//        System.err.println("----------");
//        states[7].debugBombs();
        long long5 = System.currentTimeMillis();

        
        /*ai */long aiBefore = System.currentTimeMillis();
        ai.compute();
        Action action = ai.actions.get(0);
        /*ai */long aiAfter= System.currentTimeMillis();
        
        System.err.println("prepareGame : "+(long2-long1));
        System.err.println("computeRound: "+(long3-long2));
        System.err.println("updateStates: "+(long4-long3));
        System.err.println("debug       : "+(long5-long4));
        System.err.println("AI          : "+(aiAfter-aiBefore));
        
        
        System.out.println(action.get());
      }
    }

    void debugThreats() {
      for (int y = 0; y < height; y++) {
        String result="";
        for (int x = 0; x < width; x++) {
          char c= ' ';
          if (currentState.isThreat(P.get(x, y)) >= 0) {
            c='!';
          }
          result+=c;
        }
        System.err.println(result);
      }
    }



    void updateNextStates() {
      currentState.simulate(MOVE_STAY_NOBOMB);
    }

    private void prepareGameState() {
      currentState.reset();

      for (int y = 0; y < height; y++) {
        String row = in.nextLine();
        currentState.addRow(y, row);
      }
      int entitiesCount = in.nextInt();
      int playersCount = 0;
      for (int i = 0; i < entitiesCount; i++) {
        int entityType = in.nextInt();
        int owner = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        int param1 = in.nextInt();
        int param2 = in.nextInt();
        Entity entity = null;
        if (entityType == ENTITY_BOMB) {
          entity = new Bomb(currentState, owner, x,y, param1, param2);
        } else if (entityType == ENTITY_PLAYER) {
          APlayer player = new APlayer(currentState, owner, x,y, param1, param2);
          currentState.players[owner] = player;
          entity = player;
          playersCount++;
        } else if (entityType == ENTITY_ITEM) {
          entity = new Item(currentState, owner, x,y, param1, param2);
        } else {
          System.err.println("Hmmm entitytype not found");
        }
        if (entity != null) {
          currentState.addEntity(entity);
        }
      }
      in.nextLine();
      
      this.playersCount = playersCount;
    }
  }
  static class GameState {
    static final int CELL_FLOOR = 0;
    static final int CELL_WALL = Integer.MAX_VALUE;
    static final int CELL_EMPTY_BOX = 10;
    static final int CELL_BOMBUP_BOX = 11;
    static final int CELL_RANGEUP_BOX = 12;
    static final int CELL_BOMB_0 = 90;
    static final int CELL_BOMB_9 = 99;
    static final int CELL_FIRE = 200;
    static final int CELL_ITEM_BOMBUP = 31;
    static final int CELL_ITEM_RANGEUP = 32;

    public void explodeBox(P p) {
      int value = getCellAt(p.x, p.y);
      if (value == CELL_EMPTY_BOX) {
        grid[p.x][p.y] = CELL_FLOOR;
      }else if (value == CELL_BOMBUP_BOX) {
        grid[p.x][p.y] = CELL_ITEM_BOMBUP;
      }else if (value == CELL_RANGEUP_BOX) {
        grid[p.x][p.y] = CELL_ITEM_RANGEUP;
      }
    }
    public void simulate(String theMove) {
      if (this.depth < Game.MAX_STEPS) {
        GameState nextState = childs.get(Game.MOVE_STAY_NOBOMB);
        if (nextState != null) {
          nextState.clone(this);
          nextState.computeRound();
          nextState.simulate(theMove);
        }
      }
    }
    public void triggerBomb(P pointToCheck) {
      for (Entity e: entities) {
        if (e.type == ENTITY_BOMB && e.p.equals(pointToCheck)) {
          Bomb b = (Bomb)e;
          if (b.ticksLeft > 0) { // 0 == already triggered
            b.ticksLeft = 1; // TODO ouch. besoin de ça pour qu'elle explose
            b.update(this);
          }
        }
      }
    }
    
    static boolean canWalkThrough(int value) {
      return !isFire(value) && !isABomb(value) && !isWall(value) && !isABox(value);
    }
    
    static boolean isFire(int value) {
      return value == CELL_FIRE;
    }
    static public boolean explosionBlocked(int value) {
      return isWall(value);
    }
    static boolean isWall(int value) {
      return value == CELL_WALL;
    }
    static public boolean explosionSoftBlocked(int value) {
      return isABox(value) || isAnItem(value) || isABomb(value);
    }

    static private boolean isAnItem(int value) {
      return false;
    }
    static private boolean isABox(int value) {
      return value == CELL_EMPTY_BOX || value == CELL_BOMBUP_BOX || value == CELL_RANGEUP_BOX;
    }
    boolean isHardBlocked(P pos) {
      return isHardBlocked(grid[pos.x][pos.y]);
    }
    static boolean isHardBlocked(int value) {
      return value == CELL_WALL || isABomb(value);
    }
    static boolean isSoftBlock(int value) {
      return value == CELL_BOMBUP_BOX || value == CELL_EMPTY_BOX || value == CELL_RANGEUP_BOX;
    }
    static boolean isABomb(int value) {
      return value >= CELL_BOMB_0 && value <= CELL_BOMB_9;
    }
    
    int width, height;
    Map<String, GameState> childs = new HashMap<>();

    int[][] grid;
    APlayer players[] = new APlayer[4];

    int[][] boxInfluence;
    List<Entity> entities = new ArrayList<>();
    private List<P> boxes = new ArrayList<>();
    private List<P> hittedBoxes = new ArrayList<>();
    public List<Bomb> explodedBombs = new ArrayList<>();
    int depth;
    
    GameState(int width, int height, int depth) {
      this.width = width;
      this.height = height;
      this.depth = depth;
      grid = new int[width][height];
      boxInfluence = new int[width][height];
      
      if( depth < Game.MAX_STEPS) {
        childs.put("  ", new GameState(width,height, depth+1));
      }
    }

    // clean for next simulation on same spot
    void softReset() {
      explodedBombs.clear();
      hittedBoxes.clear();
      
    }
    // clean cumulative states
    void reset() {
      boxes.clear();
      hittedBoxes.clear();
      entities.clear();
      explodedBombs.clear();
      
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          boxInfluence[x][y] = 0;
        }
      }
    }

    public void duplicateFrom(GameState fromState) {
      reset();
      for (Entity e : fromState.entities) {
        Entity duplicate = e.duplicate(this);
        if (duplicate.type == ENTITY_PLAYER) {
          this.players[duplicate.owner] = (APlayer)duplicate;
        }
        this.entities.add(duplicate);
      }

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          grid[x][y] = fromState.grid[x][y];
        }
      }
    }
    
    public void clone(GameState fromState) {
      reset();
      for (Entity e : fromState.entities) {
        this.entities.add(e.duplicate(this));
      }

      for (int p=0;p<4;p++) {
        this.players[p] = fromState.players[p];
      } 
      
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int value = fromState.grid[x][y];
          if (isABomb(value)) {
            value --;
            if (value == CELL_BOMB_0) {
            }
          } else if (isFire(value)) {
            value = CELL_FLOOR; // fire from bomb return to floor
          }
          grid[x][y] = value;
        }
      }
    }

    public void addEntity(Entity entity) {
      entities.add(entity);
      if (entity.type == ENTITY_PLAYER) {
        if (entity.owner == players[0].owner) {
          players[0] = (APlayer)entity;
        } else {
          players[1] = (APlayer)entity;
        }
      }
    }

    private int getCellAt(int x, int y) {
      if (x < 0 || x >= width || y < 0 || y >= height) {
        return CELL_WALL;
      }
      return grid[x][y];
    }

    public void computeRound() {
      for (Entity entity : entities) {
        entity.update(this);
      }
      removeHittedBoxes();
      updateBoxInfluenza(players[0].bombRange);
    }

    public void computeRound_MCTS() {
      for (Entity entity : entities) {
        entity.update(this);
      }
      removeHittedBoxes();
    }

    private void debugPlayerAccessibleCellsWithAStar(APlayer player) {
      // TODO don't use A* to check this !
      System.err.println("Accessible cells from "+player);

      for (int y = 0; y < height; y++) {
        String result="";
        for (int x = 0; x < width; x++) {
          Path path = new Path(this, player.p, P.get(x, y));
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
      for (P p : hittedBoxes ) {
        int boxValue = grid[p.x][p.y];
        if (boxValue == CELL_EMPTY_BOX || boxValue == CELL_BOMBUP_BOX || boxValue == CELL_RANGEUP_BOX) {
          grid[p.x][p.y] = CELL_FLOOR;
        }
      }
    }

    private void updateBoxInfluenza(int bombRange) {
      for (P p : boxes) {
        for (int rot = 0;rot<4;rot++) {
          for (int range = 1;range<bombRange;range++) {
            int testedX = p.x+range*rotx[rot];
            int testedY = p.y+range*roty[rot];
            
            int value = getCellAt(testedX, testedY);
            if (explosionBlocked(value)) {
              break;
            }
            boxInfluence[testedX][testedY] ++;
          }
        }
      }
    }

    public void addRow(int y, String row) {
      for (int x = 0; x < row.length(); x++) {
        char c = row.charAt(x);
        if (c == '.') {
          grid[x][y] = CELL_FLOOR;
        } else if (c == 'X') {
          grid[x][y] = CELL_WALL;
        } else if (c == '0') {
          grid[x][y] = CELL_EMPTY_BOX;
          boxes.add(P.get(x, y));
        } else if (c == '1') {
          grid[x][y] = CELL_RANGEUP_BOX;
          boxes.add(P.get(x, y));
        } else if (c == '2') {
          grid[x][y] = CELL_BOMBUP_BOX;
          boxes.add(P.get(x, y));
        }
        // update influence map
        boxInfluence[x][y] = 0;
      }
    }

    void debugBoxInfluenza() {
      System.err.println("Box influenza for range "+players[0].bombRange);
      for (int y = 0; y < height; y++) {
        String result="";
        for (int x = 0; x < width; x++) {
          result+=(char)('0'+boxInfluence[x][y]);
        }
        System.err.println(result);
      }
    }
    
    void debugBombs() {
      for (int y = 0; y < height; y++) {
        String result="";
        for (int x = 0; x < width; x++) {
          int value = grid[x][y];
          char c= '?';
          if (value == CELL_FLOOR) { c = ' '; }
          else if (value == CELL_WALL) { c = 'X'; }
          else if (value == CELL_EMPTY_BOX
            || value == CELL_BOMBUP_BOX
            || value == CELL_RANGEUP_BOX) {
              c = 'b';
          } else if (value >= CELL_BOMB_0 && value <= CELL_BOMB_9) {
              c = (char)('0' + (value - CELL_BOMB_0));
          }
          result+=c;
        }
        System.err.println(result);
      }
    }
    int isThreat(P p) {
      GameState theState = this;
      for (int layer = 0;layer<Game.MAX_STEPS;layer++) {
        if (theState == null) {
          return -1;
        }
        int value = theState.grid[p.x][p.y];
        if (GameState.isFire(value)) {
          return layer;
        }
        theState = theState.childs.get(Game.MOVE_STAY_NOBOMB);
      }
      return -1;
    }
  }
  
  public static void main(String[] args) {
    in = new Scanner(System.in);
    int width = in.nextInt();
    int height = in.nextInt();
    int myIndex = in.nextInt();
    in.nextLine();

    Game game = new Game(width, height);
    game.myIndex = myIndex;

    game.play();
  }
  
  /**
   * PATH : A*
   *
   */
  public static class Path {
    Map<P, PathItem> closedList = new HashMap<>();
    List<PathItem> openList = new ArrayList<>();
    
    List<PathItem> path = new ArrayList<>();
    
    private GameState rootState;
    P from;
    P target;
    
    Path(GameState root, P from, P target) {
      this.rootState = root;
      this.from = from;
      this.target = target;
    }

    public void debug() {
      System.err.println("found a path: "+target);
      System.err.println("path ("+path.size()+ ") :  ");
      for (Path.PathItem i : path) {
        System.err.print(i.pos+" --> ");
      }
      System.err.println("");
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
      PathItem root = new PathItem();
      root.pos = this.from;
      root.state = this.rootState;
      openList.add(root);

      while (openList.size() > 0) {
        PathItem visiting = openList.remove(0); // imagine it's the best
        GameState theState = visiting.state;
        P pos = visiting.pos;
        if (pos.equals(target)) {
          return visiting;
        }

        closedList.put(pos, visiting);
        if (pos.y > 0) {
          addToOpenList(visiting, pos , P.get(pos.x, pos.y-1));
        }
        if (pos.y < theState.height - 1) {
          addToOpenList(visiting, pos , P.get(pos.x, pos.y+1));
        }
        if (pos.x > 0) {
          addToOpenList(visiting, pos , P.get(pos.x-1, pos.y));
        }
        if (pos.x < theState.width - 1) {
          addToOpenList(visiting, pos , P.get(pos.x+1, pos.y));
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

    private void addToOpenList(PathItem visiting, P fromCell, P toCell) {
      if (closedList.containsKey(toCell)) {
        return;
      }
      int value = visiting.state.getCellAt(toCell.x, toCell.y);
      if (GameState.canWalkThrough(value)) {
        PathItem pi = new PathItem();
        pi.pos = toCell;
        pi.cumulativeLength = visiting.cumulativeLength + 1;
        pi.totalPrevisionalLength = pi.cumulativeLength + fromCell.manhattanDistance(target);
        pi.precedent = visiting;
        pi.state = visiting.state.childs.get(Game.MOVE_STAY_NOBOMB);
        if (pi.state == null) {
          pi.state = visiting.state;
        }
        openList.add(pi);
      }
    }


    public static class PathItem {
      public GameState state;
      int cumulativeLength = 0;
      int totalPrevisionalLength = 0;
      PathItem precedent = null;
      P pos;

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
  
  static class P {
    static P[][] ps = new P[20][20]; // maximum board
    static {
      for (int x=0;x<20;x++) {
        for (int y=0;y<20;y++) {
          ps[x][y] = new P(x,y);
        }
      }
    }
    static P get(int x,int y) {
      return ps[x][y];
    }
    
    
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
      return x == other.x && y == other.y;
    }
  }

}
