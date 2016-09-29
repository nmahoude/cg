
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class Player {
  private static Scanner in;
  
  private static final int ENTITY_PLAYER = 0;
  private static final int ENTITY_BOMB = 1;
  private static final int ENTITY_ITEM = 2;

  static int MOVE_RIGHT = 0;
  static int MOVE_DOWN = 1;
  static int MOVE_LEFT = 2;
  static int MOVE_UP = 3;
  static int MOVE_STAY = 4;
  
  static int[] rotx = { 1, 0, -1, 0, 0 };
  static int[] roty = { 0, 1, 0, -1, 0 };
  static String[] rotString = { "RIGHT", "DOWN ", "LEFT ", "UP   ", "STAY " };

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
      isDead = false;
    }
    int bombRange = 3;
    int bombsLeft = 1;
    public int points = 0;
    private boolean isDead = false;
    public int droppedBombs = 0;
    
    int getTotalBombs() {
      return bombsLeft + droppedBombs;
    }
    @Override
    public void update(GameState state) {
      pickupBonuses(state);
      
      super.update(state);
    }
    private void pickupBonuses(GameState state) {
      int value = state.grid[p.x+13*p.y];
      if (GameState.isRangeUpItem(value)) {
        bombRange++;
      } else if (GameState.isBombUpItem(value)) {
        bombsLeft++;
      }
    }
    
    public Entity duplicate(GameState newState) {
      APlayer aPlayer = new APlayer(newState, owner, p.x, p.y, bombsLeft, bombRange);
      aPlayer.points = this.points;
      
      aPlayer.droppedBombs = this.droppedBombs;
      aPlayer.isDead = this.isDead;
      
      return aPlayer;
    }
    boolean isDead() {
      return isDead;
    }
    void setDead() {
      isDead = true;
    }
    public void dropBomb() {
      Bomb droppedBomb = new Bomb(state, owner, p.x,p.y, 8, bombRange);
      state.addEntity(droppedBomb);
      this.bombsLeft-=1;
      this.droppedBombs+=1;
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
        state.grid[p.x+13*p.y] = GameState.CELL_FIRE;
        state.fireCells.add(p);
      } else {
        state.grid[p.x+13*p.y] = GameState.CELL_BOMB_0+ticksLeft;
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
          state.players[p].setDead();
        }
      }
    }
    public void explode(GameState state) {
      if (state.players[owner] != null) {
        state.players[owner].bombsLeft++;
        state.players[owner].droppedBombs--;
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

          if (GameState.explosionSoftBlocked(value)) {
            if (GameState.isABomb(value)) {
              state.triggerBomb(testedP);
            } else if (GameState.isABox(value)) {
              state.hittedBoxes.add(testedP);
              if (state.players[owner] != null) {
                state.players[owner].points ++;
              }
            } else if (GameState.isAnItem(value)) {
              state.removeItem(testedP);
            }
            break;
          }
          state.grid[testedX+13*testedY] = GameState.CELL_FIRE;
          state.fireCells.add(p);
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
      action.pos = game.currentState.players[Game.myIndex].p;
      action.dropBomb = false;
      action.message = "FOUND NOTHING TODO :(";
      
      GameState state0 = game.currentState;
      P playerPos = state0.players[Game.myIndex].p;

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
            double score = 1
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
  
  static abstract class MovementAlgorithm {
    enum AlgoType {
      FAIR,
      BOX,
      AGGRESSIVE, EARLY
    }
    AlgoType type;
    public MovementAlgorithm(AlgoType type) {
      this.type = type;
    }
    abstract Integer getBestChild(MCTS root);
    abstract int compute(APlayer player, GameState fromState, int[] possibilities);
    
    void computeBombs(APlayer player, GameState fromState, int p, boolean[] bombs) {
      boolean canDropBombOnBoard = !GameState.isABomb(fromState.grid[player.p.x+13*player.p.y]);
      if (!canDropBombOnBoard) {
        bombs[p] = false;
      } else {
        int THRESHOLD = 500;
        if (fromState.boxes.size() == 0) {
          if (player.p.x % 2 == 0 && player.p.y % 2 == 0) {
            THRESHOLD = 200;
          }
        }
        bombs[p] = ThreadLocalRandom.current().nextInt(1000) > THRESHOLD;
      }
    }
  }
  
  static class AggressiveMovementAlgorithm extends MovementAlgorithm {
    private APlayer bestOpponent;
    private APlayer player;

    public AggressiveMovementAlgorithm() {
      super(AlgoType.AGGRESSIVE);
    }

    @Override
    void computeBombs(APlayer player, GameState fromState, int p, boolean[] bombs) {
      // TODO
    }
    private APlayer getBestOpponent(APlayer player, GameState fromState) {
      APlayer bestOpponent=null;
      int minDist = 1024;
      for (int p=0;p<4;p++) {
        APlayer opponent = fromState.players[p];
        if (opponent == null || opponent == player) {
          continue;
        }
        if (opponent.points > player.points) {
          int dist = opponent.p.manhattanDistance(player.p);
          if (dist < minDist) {
            minDist = dist;
            bestOpponent = opponent;
          }
        }
      }
      return bestOpponent;
    }
    
    @Override
    int compute(APlayer player, GameState fromState, int[] possibilities) {
      possibilities[4] = 1; // stay
      int total=1;

      this.player = player;
      bestOpponent = getBestOpponent(player, fromState);
      int minDist = 0;
      if (bestOpponent == null) {
        // avoid fights
      } else {
        // look for fight
        minDist = bestOpponent.p.manhattanDistance(player.p);
      }
      
      for (int i=0;i<4;i++) {
        int px = player.p.x + rotx[i];
        int py = player.p.y + roty[i];
        int valueAtCell = fromState.getCellAt(px, py);
        if (GameState.canWalkThrough(valueAtCell)) {
          if (bestOpponent != null 
              && bestOpponent.p.manhattanDistance(P.get(px, py)) < minDist) {
            possibilities[i] = 5;
          } else {
           possibilities[i] = 1;
          }
          total+=possibilities[i];
        } else {
          possibilities[i] = 0;
        }
      }
      return total;
    }
    
    
    Integer getBestChild(MCTS root) {
      String chosenKey = null;
      double bestPointsRatio = -100.0;
      double bestWinRatio = -100.0;
      
      int moreWin = 0;
      Entry<String, MCTS> bestEntry = null;
      
      int nominalDistance = 0;
      if (bestOpponent != null) {
        nominalDistance = player.p.manhattanDistance(bestOpponent.p);
      }
      
      if (bestOpponent != null) {
        for (Entry<Integer, MCTS> m : root.childs.entrySet()) {
          MCTS tested = m.getValue();
          if (tested.win == 0) {
            continue; // avoid Divide/0
          }
          
          int op = m.getKey() / 2;
          int nearest = nominalDistance - P.get(player.p.x+rotx[op], player.p.y+roty[op]).manhattanDistance(bestOpponent.p);
          double testedWinRatio = (1.0*m.getValue().win / m.getValue().simulatedCount);
          if (nearest < 0 && testedWinRatio > 0.10) {
            System.err.println("AGGRO : "+m.getKey());

            return m.getKey();
          }
        }
      }
      System.err.println("AGGRO : No best way");
      return null; // FIXME really ? the null ?
    }
  }
  
  static class BoxedOrientedPossibilitiesAlgorithm extends MovementAlgorithm {
    public BoxedOrientedPossibilitiesAlgorithm() {
      super(AlgoType.BOX);
    }
    
    @Override
    void computeBombs(APlayer player, GameState fromState, int p, boolean[] bombs) {
      // TODO Auto-generated method stub
      
    }
    Integer getBestChild(MCTS root) {
      Integer chosenKey = null;
      double bestPointsRatio = -100.0;
      double bestWinRatio = -100.0;
      
      int moreWin = 0;
      Entry<Integer, MCTS> bestEntry = null;
      
      for (Entry<Integer, MCTS> m : root.childs.entrySet()) {
        MCTS tested = m.getValue();
        if (tested.win == 0) {
          continue; // avoid Divide/0
        }
        double testedPointsRatio = (1.0*m.getValue().totalPoints / tested.win);
        double testedWinRatio = (1.0*m.getValue().win / m.getValue().simulatedCount);
        
        //System.err.println("Tested child: "+MCTSAI.keyToString(m.getKey(), Game.myIndex)+" -> rPts:"+String.format("%.2f",testedPointsRatio)+" rWin:"+String.format("%.2f", testedWinRatio));
        if ((testedPointsRatio > 0.01 && testedPointsRatio > bestPointsRatio) 
            || (bestPointsRatio < 0.01 && testedWinRatio > bestWinRatio )) {
          bestEntry = m;
          bestPointsRatio = testedPointsRatio;
          bestWinRatio = testedWinRatio;
          moreWin = tested.win;
          chosenKey = m.getKey();
        }
      }
//      if (bestEntry != null) {
//        System.err.println("Best child: "+chosenKey + " ("+bestPointsRatio+" / "+bestWinRatio+")");
//      } else {
//        System.err.println("No best move");
//      }
      return chosenKey;
    }

    @Override
    int compute(APlayer player, GameState fromState, int[] possibilities) {
      possibilities[4] = 1; // stay
      int total = 1;

      for (int i = 0; i < 4; i++) {
        int px = player.p.x + rotx[i];
        int py = player.p.y + roty[i];
        int valueAtCell = fromState.getCellAt(px, py);
        if (GameState.canWalkThrough(valueAtCell)) {
          P newP = P.get(px, py);
          if (fromState.depth == 1 ) {
            if (Game.nearestBox != null && Game.nearestBox.manhattanDistance(player.p) < 6 && Game.nearestBox.manhattanDistance(player.p) - Game.nearestBox.manhattanDistance(newP) >= 0) {
              possibilities[i] = 3;
            } else if (Game.nearestOption != null && Game.nearestOption.manhattanDistance(player.p) < 6 && Game.nearestOption.manhattanDistance(player.p) - Game.nearestOption.manhattanDistance(newP) >= 0) { 
              possibilities[i] = 3;
            } else {
              possibilities[i] = 1;
            }
          } else {
            possibilities[i] = 1;
          }
          total+=possibilities[i];
        } else {
          possibilities[i] = 0;
        }
      }
      return total;
    }
  }
  
  static class EarlyGameAlgorithm extends MovementAlgorithm {
    private static final double SCORE_MINUS_INFINITY = -1_000_000;

    enum GameType {
      EARLY,
      MIDDLE,
      LATE
    }
    GameType gametype = GameType.MIDDLE;
    
    public EarlyGameAlgorithm() {
      super(AlgoType.EARLY);
    }
    
    double getScore(MCTS node) {
      if (node.childs.isEmpty()) {
        // score
        if (node.playerIsDead ) {
          return SCORE_MINUS_INFINITY;
        } else {
          if (gametype == GameType.EARLY) {
            return 10*node.totalPoints + node.totalBombs + node.bombRange - node.depth;
          } else if (gametype == GameType.MIDDLE) {
            return 2*node.totalPoints + node.totalBombs + node.bombRange;
          } else {
            return 1;
          }
        }
      } else {
        double score = SCORE_MINUS_INFINITY;
        for (Entry<Integer, MCTS> m : node.childs.entrySet()) {
          //TODO bien y penser, on bypasse tous les infinity max !
          // il faut peut-etre prendre en compt le % de win dans l'heuristique?
          score = Math.max(score, getScore(m.getValue())); 
        }
        return score;
      }
    }
    
    Integer getBestChild(MCTS root) {
      Entry<Integer, MCTS> bestEntry = null;
      double bestScore = SCORE_MINUS_INFINITY;
      
      for (Entry<Integer, MCTS> m : root.childs.entrySet()) {
        MCTS mcts = m.getValue();
        double ratio = 1.0*mcts.win / mcts.simulatedCount;
        double score = getScore(mcts);
        double testedScore = ratio * score;
        if (testedScore > bestScore) {
          bestScore = testedScore;
          bestEntry = m;
        }
      }
      return bestEntry != null ? bestEntry.getKey() : null;
    }

    @Override
    int compute(APlayer player, GameState fromState, int[] possibilities) {
      possibilities[4] = 1; // stay
      int total = 1;
      for (int i = 0; i < 4; i++) {
        int px = player.p.x + rotx[i];
        int py = player.p.y + roty[i];
        int valueAtCell = fromState.getCellAt(px, py);
        if (GameState.canWalkThrough(valueAtCell)) {
          possibilities[i] = 1;
          total+=possibilities[i];
        } else {
          possibilities[i] = 0;
        }
      }
      return total;
    }
    
    @Override
    void computeBombs(APlayer player, GameState fromState, int p, boolean[] bombs) {
      bombs[p] = ThreadLocalRandom.current().nextInt(1000) > 500;
    }
  }
  
  static class MCTS {
    //static MovementAlgorithm biasedMovementAlgorithm = new BoxedOrientedPossibilitiesAlgorithm();
    static MovementAlgorithm biasedMovementAlgorithm = new EarlyGameAlgorithm();
    // debug : start with the aggressive algorithm
    //static MovementAlgorithm biasedMovementAlgorithm = new AggressiveMovementAlgorithm();
    
    static Game game; // static reference to the game (only one). Better way to do it ?
    Map<Integer, MCTS> childs = new HashMap<>();
    
    public int depth;
    int simulatedCount=0; // how many branches (total > childs.size())
    int win=0;
    int points;
    int totalPoints;
    public boolean playerIsDead = false;
    public int bombRange;
    public int totalBombs;

    static int [] possibilities = new int[5]; // dont' parralelize !
    static {
      possibilities[4] = 1; // don't move is the 1st choice!
    }
    int findARandomMove() {
      int sum = 0;
      for (int i=0;i<5;i++) {
        sum+=possibilities[i];
      }
      if (sum == 1) {
        return 4; // STAY
      } else {
        int rand = getRandom(sum);
        int i;
        for(i = 0;i<5;i++) {
          rand=rand-possibilities[i];
          if (rand < 0) {
            break;
          }
        }
        return i;
      }
    }
    
    int getRandom(int range) {
      return ThreadLocalRandom.current().nextInt(range);
    }
    void calculateActions(GameState fromState, int[] dir, boolean[] bomb) {
      for (int p=0;p<game.playersCount;p++) {
        APlayer player = fromState.players[p];
        if (player == null) {
          continue ;
        }
        biasedMovementAlgorithm.compute(player, fromState, possibilities);
        dir[p] = findARandomMove();
        
        if (player.bombsLeft > 0) {
          biasedMovementAlgorithm.computeBombs(player, fromState, p, bomb);
        } else {
          bomb[p] = false;
        }
      }
    }
    Integer getKeyFromActions(int[] dir, boolean bomb[]) {
//      String key = "";
//      String myKey = dir[game.myIndex]+(bomb[game.myIndex] ? "B" : "M");
//      key = myKey+myKey+myKey+myKey;
//      return key;
//      
      return 2 * dir[game.myIndex]+(bomb[game.myIndex] ? 1 : 0);
    }

    int simulate(GameState fromState, int depth) {
      this.depth = fromState.depth;
      this.simulatedCount++;
      fromState.depth++;

      // if we already now that the player will be dead, no excuse to continue this road
      if (playerIsDead) {
        return -1;
      }
      //do simulation here
      fromState.computeRound_MCTS();
      APlayer aPlayer = fromState.players[Game.myIndex];
      points = aPlayer.points;
      totalPoints += points;

      if (aPlayer.isDead()) {
        playerIsDead = true;
        return -1;
      }
      
      totalPoints = aPlayer.points;
      totalBombs = aPlayer.getTotalBombs();
      bombRange = aPlayer.bombRange;

      if (depth == 0) {
        // terminating node
        // total box killed by player to this leaf
        return 1;
      } else {
        // choose some new random moves
        int[] dir = new int[5];
        boolean[] bomb = new boolean[5];
        
        boolean simulationDone = false;
        int maxTests = 10;
        Integer key = null;
        MCTS chosenChild = null;
        // Find a non final child node
        while (!simulationDone && maxTests > 0) {
          maxTests--;
          calculateActions(fromState, dir, bomb);
          // prepare child
          key = getKeyFromActions(dir, bomb);
          chosenChild = childs.get(key);
          if (chosenChild == null || !chosenChild.playerIsDead) {
            simulationDone = true;
          }
        }

        for (int playerIndex=0;playerIndex<game.playersCount;playerIndex++) {
          int i = dir[playerIndex];
          APlayer player = fromState.players[playerIndex];
          if (player == null) {
            continue;
          }
          // drop bomb if needed
          if (bomb[playerIndex]) {
            player.dropBomb();
          }
          // then move
          int x = player.p.x + rotx[i];
          int y = player.p.y + roty[i];
          player.p = P.get(x, y);
        }

        if (chosenChild != null) {
          // we already go there, reuse the child !
        } else {
          // create a new one
          chosenChild = new MCTS();
          childs.put(key, chosenChild);
        }
        int childTotalPoints = chosenChild.simulate(fromState, depth-1);
        if (childTotalPoints >= 0) {
          totalPoints+=childTotalPoints;
          win++;
          return points+childTotalPoints;
        } else {
          return -1;
        }
      }
    }
    private boolean victoryFromPoints(GameState fromState) {
      APlayer me = fromState.players[Game.myIndex];
      for (int i=1;i<4;i++) {
        if (fromState.players[i] != null && me.points < fromState.players[i].points) {
          return false;
        }
      }
      win++; // don't forget to count our victory
      return true; // we won !
    }
    
    @Override
    public String toString() {
      return "w/s:"+win+"/"+simulatedCount+" p:"+points+",tp:"+totalPoints+", childs:"+childs.size();
    }
  }

  static class MCTSAI extends AI {
    String quotes[] = {
      "Walking on the moon",
      "Trying my best",
      "Live for yourself.",
      "Work hard. Dream big.",
      "Life is short.",
      "Bombs Everywhere"
    };
    int seed = ThreadLocalRandom.current().nextInt(20);
    int gameRound = 0;
    static final int MAX_STEPS = 16;
    MCTS root = new MCTS();
    public int steps = MAX_STEPS;
    
    @Override
    void compute() {
      evaluateAlgorithmSwitch();
      
      gameRound++;
      root = new MCTS();
      
      // debugBoxAndOptionsDistance();

      GameState copyOfRoot = new GameState(game.currentState.width, game.currentState.height, 0);
      int simulationCount = getAffordableSimulationCount();
      for (int i=0;i<simulationCount;i++) {
        copyOfRoot.duplicateFrom(game.currentState);
        copyOfRoot.depth = 0; // FIXME redondant ?
        root.simulate(copyOfRoot, steps);
      }
      
      Integer chosenKey = MCTS.biasedMovementAlgorithm.getBestChild(root);
      MCTS chosen = root.childs.get(chosenKey);
      
      if (chosen == null) {
        buildSayonaraAction();
        return;
      } else {
        buildBestActionFromKey(chosenKey);
      }
    }

    private void evaluateAlgorithmSwitch() {
//      if (game.currentState.boxes.isEmpty() && MCTS.biasedMovementAlgorithm.type == MovementAlgorithm.AlgoType.BOX) {
//        MCTS.biasedMovementAlgorithm = new AggressiveMovementAlgorithm();
//      }
      return; // FIXME Aggressive algo is KO
    }

    private void debugBoxAndOptionsDistance() {
      if(Game.nearestBox != null) {
        System.err.println("NEAREST Box is "+Game.nearestBox);
      } else {
        System.err.println("hmmm no nearest box ?");
      }
      if(Game.nearestOption != null) {
        System.err.println("NEAREST Option is "+Game.nearestOption);
      } else {
        System.err.println("hmmm no nearest option ?");
      }
    }

    private void buildBestActionFromKey(Integer chosenKey) {
      Action action = new Action();

      action.message = quotes[(seed+gameRound) % (quotes.length)];
 
      APlayer player = game.currentState.players[Game.myIndex];
      action.dropBomb = (chosenKey % 2 == 1);
 
      int actionIndex = chosenKey / 2 ;
      int newPosX = player.p.x+rotx[actionIndex];
      int newPosY = player.p.y+roty[actionIndex];
      action.pos = P.get(newPosX, newPosY);
 
      actions.clear();
      actions.add(action);
      
      //debugMCTS2(root);
    }

    int getAffordableSimulationCount() {
      switch(game.playersCount) {
        case 4:
          return 1_500;
        case 3:
          return 2_000;
        case 2 : 
        default:
          return 3_000;
      }
    }

    private void buildSayonaraAction() {
      Action action = new Action();
      action.pos = game.currentState.players[Game.myIndex].p;
      action.dropBomb = false;
      action.message = "Sayonara";
      actions.clear();
      actions.add(action);
      return;
    }

    static String keyToString(Integer chosenKey, int playerIndex) {
      return (chosenKey % 2 == 1 ? "BOMB" : "MOVE")+ " " +rotString[chosenKey / 2];
    }

    static void debugMCTS2(Player.MCTS root) {
      debugMCTS2(root, "");
    }
    static void debugMCTS2(Player.MCTS root, String decal) {
      int[] win = new int[5];
      int bombAndWin = 0, bombSimulation=0;
      int moveAndWin = 0, moveSimulation=0;
      
      int[] simulated = new int[5];
      for (Entry<Integer, Player.MCTS> m : root.childs.entrySet()) {
        Integer key = m.getKey();
        int index = key / 2;
        win[index] += m.getValue().win;
      
        if (key % 2 == 1) {
          bombAndWin+=m.getValue().win;
          bombSimulation+=m.getValue().simulatedCount;
        } else {
          moveAndWin+=m.getValue().win;
          moveSimulation+=m.getValue().simulatedCount;
        }
        simulated[index] += m.getValue().simulatedCount;
      }
      int cumul = 0;
      for (int i=0;i<5;i++) {
        cumul+=win[i];
        if (simulated[i] > 0) {
          System.err.println(decal+Player.rotString[i]+" -> "+win[i]+" / "+simulated[i]+" ("+String.format("%.2f",1.0*win[i]/simulated[i])+")");
        }
      }
      System.err.println(decal+"BOMB "+" -> "+bombAndWin+" / "+bombSimulation);
      System.err.println(decal+"MOVE "+" -> "+moveAndWin+" / "+moveSimulation);
      
      
      if (cumul == 0) {
        MCTS.game.currentState.debugBombs();
      }
    }

  }
  
  static class Game {
    public int playersCount=2;
    private static final String MOVE_STAY_NOBOMB = "  ";
    private static final int MAX_STEPS = 20;
    int width, height;
    GameState currentState;
    int depth = 0;
    private static P nearestOption;
    private static P nearestBox;
    
    public static int myIndex = 0;
    
    Game(int width, int height) {
      this.width = width;
      this.height = height;
      
      currentState = new GameState(width, height, 0);
    }
    
    private void play() {
      MCTS.game = this; // OUTCH, it's ugly
      
      AI ai = new MCTSAI();
      ai.game = this;

      while (true) {
        long long1 = System.currentTimeMillis();
        prepareGameState();
        long long2 = System.currentTimeMillis();
        updateNearestBoxes();
        updateNearestOption();
        //currentState.updateBoxInfluenza(currentState.players[Game.myIndex].bombRange);
        //currentState.computeRound();
        long long3 = System.currentTimeMillis();
        //updateNextStates();
        long long4 = System.currentTimeMillis();

        
/** debug informations */
//        System.err.println("Current grid:");
//        System.err.println("-------------");
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
        
        System.err.println(" ------- Stats ------------");
        System.err.println("prepareGame : "+(long2-long1));
        System.err.println("computeRound: "+(long3-long2));
//        System.err.println("updateStates: "+(long4-long3));
//        System.err.println("debug       : "+(long5-long4));
        System.err.println("AI          : "+(aiAfter-aiBefore));
        
        
        System.out.println(action.get());
      }
    }
    
    
    void updateNearestOption() {
      int minDist = 10000;
      nearestOption = null;
      P playerPos = currentState.players[myIndex].p;
      for (Entity e : currentState.entities) {
        if (e.type == ENTITY_ITEM) {
          int manhattanDistance = e.p.manhattanDistance(playerPos);
          if (manhattanDistance < minDist) {
            minDist = manhattanDistance;
            nearestOption = e.p;
          }
        }
      }
    }

    void updateNearestBoxes() {
      int minDist = 10000;
      P nearestBoxes = null;
      P playerPos = currentState.players[myIndex].p;
      for (P box : currentState.boxes) {
        int manhattanDistance = box.manhattanDistance(playerPos);
        if (manhattanDistance < minDist) {
          minDist = box.manhattanDistance(playerPos);
          nearestBoxes = box;
        }
      }
      this.nearestBox = nearestBoxes;
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
      int playersCount = 1;
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
          currentState.players[owner].droppedBombs++;
        } else if (entityType == ENTITY_PLAYER) {
          APlayer player = new APlayer(currentState, owner, x,y, param1, param2);
          currentState.players[owner] = player;
          entity = player;
          playersCount = Math.max(playersCount, owner+1);
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
        grid[p.x+13*p.y] = CELL_FLOOR;
      }else if (value == CELL_BOMBUP_BOX) {
        grid[p.x+13*p.y] = CELL_ITEM_BOMBUP;
      }else if (value == CELL_RANGEUP_BOX) {
        grid[p.x+13*p.y] = CELL_ITEM_RANGEUP;
      }
    }
    public void removeItem(P testedP) {
      Iterator<Entity> iteE = entities.iterator();
      while (iteE.hasNext()) {
        Entity e =iteE.next(); 
        if (e.type == ENTITY_ITEM && e.p.equals(testedP)) {
          iteE.remove();
          return; // only one item per cell
        }
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
      // correct way :
      // return !isFire(value) && !isABomb(value) && !isWall(value) && !isABox(value);
      // optimize for endGame (fire second)
      return !isWall(value) && !isFire(value) && !isABox(value) && !isABomb(value) ;
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
      return isBombUpItem(value) || isRangeUpItem(value);
    }

    public static boolean isBombUpItem(int value) {
      return value == CELL_ITEM_BOMBUP;
    }
    public static boolean isRangeUpItem(int value) {
      return value == CELL_ITEM_RANGEUP;
    }

    static private boolean isABox(int value) {
      return value == CELL_EMPTY_BOX || value == CELL_BOMBUP_BOX || value == CELL_RANGEUP_BOX;
    }
    boolean isHardBlocked(P pos) {
      return isHardBlocked(grid[pos.x+13*pos.y]);
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

    int[] grid;
    APlayer players[] = new APlayer[4];

    List<Entity> entities = new ArrayList<>();
    private List<P> boxes = new ArrayList<>();
    private List<P> hittedBoxes = new ArrayList<>();
    List<P> fireCells = new ArrayList<>();
    int depth;
    
    GameState(int width, int height, int depth) {
      this.width = width;
      this.height = height;
      this.depth = depth;
      grid = new int[13*11];
      
      if( depth < Game.MAX_STEPS) {
        childs.put("  ", new GameState(width,height, depth+1));
      }
    }

    // clean for next simulation on same spot
    void softReset() {
      hittedBoxes.clear();
      fireCells.clear();
    }
    // clean cumulative states
    void reset() {
      softReset();
      boxes.clear();
      entities.clear();
      players[0] = null;
      players[1] = null;
      players[2] = null;
      players[3] = null;
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

      System.arraycopy(fromState.grid, 0, grid, 0, 13*11);
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
          int value = fromState.grid[x+13*y];
          if (isABomb(value)) {
            value --;
            if (value == CELL_BOMB_0) {
            }
          } else if (isFire(value)) {
            value = CELL_FLOOR; // fire from bomb return to floor
          }
          grid[x+13*y] = value;
        }
      }
    }

    public void addEntity(Entity entity) {
      entities.add(entity);
      
      if (entity.type == ENTITY_PLAYER) {
        players[entity.owner] = (APlayer)entity;
      }

    }

    private int getCellAt(int x, int y) {
      if (x < 0 || x >= width || y < 0 || y >= height) {
        return CELL_WALL;
      }
      return grid[x+13*y];
    }

    public void computeRound() {
      for (Entity entity : entities) {
        entity.update(this);
      }
      removeHittedBoxes();
    }

    public void computeRound_MCTS() {
      // remove old fire
      for (P p : fireCells) {
        grid[p.x+13*p.y] = GameState.CELL_FLOOR;
      }
      
      for (Entity entity : entities) {
        entity.update(this);
      }
      removeHittedBoxes();
    }
    private void resetPlayerPoints() {
      for (int i=0;i<4;i++) {
        if (players[i] != null) {
          players[i].points = 0;
        }
      }
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
        int boxValue = grid[p.x+13*p.y];
        if (boxValue == CELL_EMPTY_BOX) {
          grid[p.x+13*p.y] = CELL_FLOOR;
        } else if (boxValue == CELL_BOMBUP_BOX ) {
          grid[p.x+13*p.y] = CELL_ITEM_BOMBUP;
        } else if (boxValue == CELL_RANGEUP_BOX) {
          grid[p.x+13*p.y] = CELL_ITEM_RANGEUP;
        }
      }
    }

    public void addRow(int y, String row) {
      for (int x = 0; x < row.length(); x++) {
        char c = row.charAt(x);
        if (c == '.') {
          grid[x+13*y] = CELL_FLOOR;
        } else if (c == 'X') {
          grid[x+13*y] = CELL_WALL;
        } else if (c == '0') {
          grid[x+13*y] = CELL_EMPTY_BOX;
          addABox(y, x);
        } else if (c == '1') {
          grid[x+13*y] = CELL_RANGEUP_BOX;
          addABox(y, x);
        } else if (c == '2') {
          grid[x+13*y] = CELL_BOMBUP_BOX;
          addABox(y, x);
        }
      }
    }
    private void addABox(int y, int x) {
      boxes.add(P.get(x, y));
    }

    void debugBombs() {
      for (int y = 0; y < height; y++) {
        String result="";
        for (int x = 0; x < width; x++) {
          int value = grid[x+13*y];
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
        int value = theState.grid[p.x+13*p.y];
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
    Game.myIndex = myIndex;

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
