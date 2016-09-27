import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PlayerTest {

  static void computeGame(Player.Game game) {
    game.currentState.computeRound();
    game.updateNextStates();
  }
  
  static void buildBoard(Player.Game game, String...rows) {
    int rowIndex = 0;
    for (String row : rows) {
      game.currentState.addRow(rowIndex++, row);
    }
  }
  static class BombBuilder {
    Player.GameState state;
    int owner;
    int x;
    int y;
    int ticksLeft;
    int range;
    BombBuilder atState(Player.GameState state) {
      this.state = state;
      return this;
    }
    BombBuilder from(Player.APlayer p) {
      owner = p.owner;
      return this;
    }
    BombBuilder atPosition(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }
    BombBuilder withTicksLeft(int t) {
      ticksLeft = t;
      return this;
    }
    BombBuilder withRange(int r) {
      range = r;
      return this;
    }
    
    Player.Bomb build() {
      Player.Bomb bomb = new Player.Bomb(state, owner, x, y, ticksLeft, range);
      state.addEntity(bomb);
      return bomb;
    }
  }

  static class PlayerBuilder {
    int x,y;
    int bombsLeft=1;
    int range=3;
    int id = 0;
    Player.GameState state;
    
    PlayerBuilder withState(Player.GameState state) {
      this.state = state;
      return this;
    }
    PlayerBuilder withId(int id) {
      this.id = id;
      return this;
    }
    PlayerBuilder withPos(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }
    PlayerBuilder withBombsLeft(int bl) {
      this.bombsLeft = bl;
      return this;
    }
    PlayerBuilder withRange(int r) {
      this.range = r;
      return this;
    }
    Player.APlayer build() {
      Player.APlayer aPlayer = new Player.APlayer(state, id, x, y, bombsLeft, range);
      state.players[id] = aPlayer;
      return aPlayer;
    }
  }
  public static class CellAndBombs {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
    }
    
    @Test
    public void copyState() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder()
          .withId(0)
          .withPos(0, 0)
          .withState(game.currentState)
          .build();
      
      opponent = new PlayerBuilder()
          .withId(1)
          .withPos(5, 5)
          .withState(game.currentState)
          .build();

      buildBoard(game,
          ".....",
          ".X.X.",
          ".....",
          ".X.X.", 
          "....." 
          );
      
      Player.Bomb bomb = new BombBuilder()
          .atState(game.currentState)
          .from(me)
          .atPosition(0,0)
          .withRange(2)
          .withTicksLeft(2)
          .build();

      computeGame(game);
      
      assertThat(game.currentState.grid[0][0], is(Player.GameState.CELL_BOMB_0+1));
      assertThat(game.currentState.childs.get("  ").grid[0][0], is(Player.GameState.CELL_FIRE));
    }

    @Test
    public void chainExplosion() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder()
          .withId(0)
          .withPos(0, 0)
          .withState(game.currentState)
          .build();
      
      opponent = new PlayerBuilder()
          .withId(1)
          .withPos(5, 5)
          .withState(game.currentState)
          .build();


      buildBoard(game,
          ".....",
          ".X.X.",
          ".....",
          ".X.X.", 
          "....." 
          );
      
      Player.Bomb bomb = new BombBuilder()
          .atState(game.currentState)
          .from(me)
          .atPosition(0,0)
          .withRange(2)
          .withTicksLeft(1)
          .build();

      Player.Bomb bomb2 = new BombBuilder()
          .atState(game.currentState)
          .from(me)
          .atPosition(0,1)
          .withRange(2)
          .withTicksLeft(8)
          .build();

      computeGame(game);
      
      int bomb1Value = game.currentState.grid[0][0];
      int bomb2Value = game.currentState.grid[0][1];
      
      assertThat(bomb1Value, is(Player.GameState.CELL_FIRE));
      assertThat(bomb2Value, is(Player.GameState.CELL_FIRE));
    }
    
    @Test
    public void bombsTriggerFireWhichDisapearsNextRound() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder()
          .withId(0)
          .withPos(0, 0)
          .withState(game.currentState)
          .build();
      
      opponent = new PlayerBuilder()
          .withId(1)
          .withPos(5, 5)
          .withState(game.currentState)
          .build();

      buildBoard(game,
          ".....",
          ".X.X.",
          ".....",
          ".X.X.", 
          "....." 
          ); 
      
      Player.Bomb bomb = new BombBuilder()
          .atState(game.currentState)
          .from(me)
          .atPosition(0,0)
          .withRange(2)
          .withTicksLeft(1)
          .build();
      
      
      computeGame(game);
      
      int cellValue = game.currentState.grid[0][1];
      int cellValueNext = game.currentState.childs.get("  ").grid[0][1];
      
      assertThat(cellValue, is(Player.GameState.CELL_FIRE));
      assertThat(cellValueNext, is(Player.GameState.CELL_FLOOR));
    }
  }
  
  public static class PathFinding {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
    }
    
    @Test
    public void easy() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder()
          .withId(0)
          .withPos(0, 0)
          .withState(game.currentState)
          .build();
      
      opponent = new PlayerBuilder()
          .withId(1)
          .withPos(5, 5)
          .withState(game.currentState)
          .build();

      buildBoard(game,
          "..0..",
          ".X.X.",
          "0....",
          ".X.X.", 
          "....." 
          );
      
      computeGame(game);

      // path finding
      Player.Path path = new Player.Path(game.currentState, Player.P.get(0, 0), Player.P.get(1, 0));
      path.find();
      
      assertThat(path.path.isEmpty(), is(false));
    }
    
    @Test
    @Ignore
    public void blockingBoxWillExplodeBeforeArriving() throws Exception {
      Player.Game game = new Player.Game(5, 1);
      me = new PlayerBuilder()
          .withId(0)
          .withPos(0, 0)
          .withState(game.currentState)
          .build();
      
      opponent = new PlayerBuilder()
          .withId(1)
          .withPos(5, 5)
          .withState(game.currentState)
          .build();

      buildBoard(game, "..0..");

      Player.Bomb bomb = new BombBuilder()
          .atState(game.currentState)
          .atPosition(4, 0)
          .withRange(2)
          .withTicksLeft(2)
          .build();

      computeGame(game);

      // path finding
      Player.Path path = new Player.Path(game.currentState, 
          Player.P.get(0, 0), 
          Player.P.get(4, 0));
      path.find();
      
      assertThat(path.path.isEmpty(), is(false));
    }
  }
  
  public static class AI {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
    }
    
    @Test
    public void avoidBomb() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder()
          .withId(0)
          .withPos(0, 0)
          .withState(game.currentState)
          .build();
      
      opponent = new PlayerBuilder()
          .withId(1)
          .withPos(5, 5)
          .withState(game.currentState)
          .build();

      buildBoard(game,
          ".0...",
          ".X.X.",
          ".....",
          ".X.X.", 
          "....." 
          );
      
      Player.Bomb bomb = new Player.Bomb(game.currentState, 1, 0, 0 ,4, 5);
      game.currentState.addEntity(bomb);
      
      computeGame(game);

      // game ai
      Player.AI ai = new Player.AI1();
      ai.game = game;
      ai.myIndex = me.owner;
      ai.compute();
      Player.Action action = ai.actions.get(0);
      
      assertThat(action.pos, is(not(Player.P.get(0, 0))));
    }
    
    @Test
    public void dropBombAtStart() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder()
          .withId(0)
          .withPos(0, 0)
          .withState(game.currentState)
          .build();
      
      opponent = new PlayerBuilder()
          .withId(1)
          .withPos(5, 5)
          .withState(game.currentState)
          .build();

      buildBoard(game,
          "...00",
          ".X0X.",
          "00...",
          ".X.X.", 
          "....." 
          );
      
      computeGame(game);

      // game ai
      Player.AI ai = new Player.AI1();
      ai.game = game;
      ai.myIndex = me.owner;
      ai.compute();
      Player.Action action = ai.actions.get(0);

      assertThat(action.pos, is(not(Player.P.get(0, 0))));
    }
  }
  
  public static class MCTS {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
    }
    
    @Test
    public void findRandomMove() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      Player.MCTS.game = game;
      me = new PlayerBuilder()
          .withId(0)
          .withPos(0, 0)
          .withState(game.currentState)
          .build();
      
      opponent = new PlayerBuilder()
          .withId(1)
          .withPos(5, 5)
          .withState(game.currentState)
          .build();
      
      buildBoard(game,
          "..0..",
          ".X.X.",
          "0....",
          ".X.X.", 
          "....." 
          );

      Player.MCTS mcts = new Player.MCTS();
      int findARandomMove = mcts.findARandomMove(me, game.currentState);
      
      assertThat(mcts.possibilities[0], is(4));
      assertThat(mcts.possibilities[1], is(0));
      assertThat(mcts.possibilities[2], is(1));
    }
    
    
    @Test
    public void avoidBomb() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      Player.MCTS.game = game;
      
      me = new PlayerBuilder()
          .withId(0)
          .withPos(1, 0)
          .withState(game.currentState)
          .build();
      
      opponent = new PlayerBuilder()
          .withId(1)
          .withPos(4, 4)
          .withState(game.currentState)
          .build();
      
      buildBoard(game,
          "..0..",
          ".X.X.",
          "0....",
          ".X.X.", 
          "....." 
          );
      
      
      game.currentState.addEntity(me);
      game.currentState.addEntity(opponent);
      Player.Bomb bomb = new BombBuilder()
          .atState(game.currentState)
          .from(opponent)
          .atPosition(0, 1)
          .withRange(3)
          .withTicksLeft(2)
          .build();
      
      // game ai
      Player.MCTSAI ai = new Player.MCTSAI();
      ai.game = game;
      ai.myIndex = me.owner;
      
      ai.steps = 10;
      ai.compute();
      
      // debug
      //debugMCTS("ROOT", ai.root, 0);
      //Player.MCTSAI.debugMCTS2(ai.root);
      debugBestMove(ai.root, 10);
      
      Player.Action action = ai.actions.get(0);
      assertThat(action.pos, is(not(Player.P.get(0, 0))));
      
      assertThat(game.currentState.players[0].isDead(), is(false));
    }
    
    private void debugBestMove(Player.MCTS root, int depth) {
      String key = root.getBestChild();
      if (key == null) {
        // the end
        return;
      }
      System.out.println("--> "+Player.MCTSAI.keyToString(key));
      if (depth > 0) {
        Player.MCTS mcts = root.childs.get(key);
        debugBestMove(mcts, depth-1);
      }
    }

    private void debugMCTS(String key, Player.MCTS root, int step) {
      for (int i=0;i<step;i++) {
        System.out.print(" ");
      }
      if (!key.equals("ROOT")) {
        System.out.println(Player.MCTSAI.keyToString(key)+" : "+root.win+" / "+root.simulatedCount);
      }
      if (step < 1) {
        for (Entry<String, Player.MCTS> m : root.childs.entrySet()) {
          debugMCTS(m.getKey(), m.getValue(), step+1);
        }
      }
    }
  }
}
