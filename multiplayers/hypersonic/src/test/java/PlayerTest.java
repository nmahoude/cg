import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class PlayerTest {
  Player.APlayer me;
  Player.APlayer opponent;
  
  @Before
  public void setup() {
  }

  @Test
  public void testPerfs() throws Exception {
    
    int i = 1;
    i = i +1;
    
    Player.Game game = new Player.Game(13, 11);
    Player.MCTS.game = game;
    
    me = new PlayerBuilder(game.currentState)
        .withId(0)
        .withPos(0, 0)
        .build();
    
    opponent = new PlayerBuilder(game.currentState)
        .withId(1)
        .withPos(12, 10)
        .build();
    
    buildBoard(game,
        "             ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "             "
          );
    
   
    // game ai
    Player.MCTSAI ai = new Player.MCTSAI();
    ai.game = game;
    
    ai.steps = 16;
    ai.compute();
    
  }
  
  
  public static class CellAndBombs {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
    }
    
    @Test
    public void itemsStopBombs() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();

      buildBoard(game,
          ".....",
          ".X0X.",
          ".0.0.",
          ".X0X.", 
          "....." 
          );
      Player.Bomb b1 = new BombBuilder(game.currentState)
          .atPosition(0, 4)
          .from(opponent)
          .withRange(5)
          .withTicksLeft(1)
          .build();
      Player.Item i3 = new ItemBuilder(game.currentState)
          .atPosition(0,3)
          .build();
      
      game.currentState.computeRound_MCTS();
      
      assertThat(me.isDead(), is(false));
    }
    @Test
    public void influenza() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();

      buildBoard(game,
          ".....",
          ".X0X.",
          ".0.0.",
          ".X0X.", 
          "....." 
          );
      
      game.updateNearestBoxes();
      game.currentState.updateBoxInfluenza(game.currentState.players[Player.Game.myIndex].bombRange);
      
      game.currentState.debugBoxInfluenza();
    }
    @Test
    public void copyState() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();

      buildBoard(game,
          ".....",
          ".X.X.",
          ".....",
          ".X.X.", 
          "....." 
          );
      
      Player.Bomb bomb = new BombBuilder(game.currentState)
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
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();


      buildBoard(game,
          ".....",
          ".X.X.",
          ".....",
          ".X.X.", 
          "....." 
          );
      
      Player.Bomb bomb = new BombBuilder(game.currentState)
          .from(opponent)
          .atPosition(4,4)
          .withRange(10)
          .withTicksLeft(1)
          .build();

      Player.Bomb bomb2 = new BombBuilder(game.currentState)
          .from(me)
          .atPosition(2,4)
          .withRange(10)
          .withTicksLeft(8)
          .build();

      game.currentState.computeRound_MCTS();
      
      int bomb1Value = game.currentState.grid[4][4];
      int bomb2Value = game.currentState.grid[2][4];
      int bomb3Value = game.currentState.grid[2][3];
      
      assertThat(bomb1Value, is(Player.GameState.CELL_FIRE));
      assertThat(bomb2Value, is(Player.GameState.CELL_FIRE));
      assertThat(bomb3Value, is(Player.GameState.CELL_FIRE));
    }

    @Test
    public void explosionChaining_2() throws Exception {
      Player.Game game = new Player.Game(13, 11);
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(4, 9)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();


      buildBoard(game,
        "             ",
        " X X XbX X X ",
        "      3      ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "    4 2      "
          );
      
      Player.Bomb bomb = new BombBuilder(game.currentState)
          .from(me)
          .atPosition(4,10)
          .withRange(4)
          .withTicksLeft(3)
          .build();

      Player.Bomb bomb2 = new BombBuilder(game.currentState)
          .from(me)
          .atPosition(6, 10)
          .withRange(4)
          .withTicksLeft(1)
          .build();

      game.currentState.computeRound_MCTS();

      checkCellOnFire(game, 1, 10 );
      checkCellOnFire(game, 2, 10 );
      checkCellOnFire(game, 3, 10 );
      checkCellOnFire(game, 4, 10 );
      checkCellOnFire(game, 5, 10 );
      checkCellOnFire(game, 6, 10 );
      checkCellOnFire(game, 7, 10 );
      checkCellOnFire(game, 8, 10 );
      checkCellOnFire(game, 9, 10 );
      
      checkCellOnFire(game, 4, 9 );
      checkCellOnFire(game, 4, 8 );
      checkCellOnFire(game, 4, 7 );
      
      checkCellOnFire(game, 6, 9 );
      checkCellOnFire(game, 6, 8 );
      checkCellOnFire(game, 6, 7 );
    }
    
    private void checkCellOnFire(Player.Game game, int i, int j) {
      assertThat(game.currentState.grid[i][j], is(Player.GameState.CELL_FIRE));
    }

    @Test
    public void bombsTriggerFireWhichDisapearsNextRound() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();

      buildBoard(game,
          ".....",
          ".X.X.",
          ".....",
          ".X.X.", 
          "....." 
          ); 
      
      Player.Bomb bomb = new BombBuilder(game.currentState)
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
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
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
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();

      buildBoard(game, "..0..");

      Player.Bomb bomb = new BombBuilder(game.currentState)
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
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
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
      ai.compute();
      Player.Action action = ai.actions.get(0);
      
      assertThat(action.pos, is(not(Player.P.get(0, 0))));
    }
    
    @Test
    public void dropBombAtStart() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
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
    public void buildRandomMove() throws Exception {
      class MyMCTS extends Player.MCTS {
        int randomNumber;
        
        public MyMCTS(int random) {
          randomNumber = random;
        }
        @Override
        int getRandom(int range) {
          return randomNumber;
        }
      }
      
      MyMCTS m = new MyMCTS(0);
      Player.MCTS.possibilities[0] = 0;
      Player.MCTS.possibilities[1] = 0;
      Player.MCTS.possibilities[2] = 0;
      Player.MCTS.possibilities[3] = 0;
      Player.MCTS.possibilities[4] = 1;
      
      int result = m.findARandomMove();
      assertThat(result, is(4));
    }
    
    @Test
    public void findRandomMove() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      Player.MCTS.game = game;
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();
      
      buildBoard(game,
          "..0..",
          ".X.X.",
          "0....",
          ".X.X.", 
          "....." 
          );

      Player.MCTS mcts = new Player.MCTS();
      int sum = Player.MCTS.biasedMovementAlgorithm.compute(me, game.currentState, Player.MCTS.possibilities);
      
      assertThat(sum, is(3));
      assertThat(Player.MCTS.possibilities[Player.MOVE_RIGHT], is(1));
      assertThat(Player.MCTS.possibilities[Player.MOVE_DOWN], is(1));
      assertThat(Player.MCTS.possibilities[Player.MOVE_LEFT], is(0));
      assertThat(Player.MCTS.possibilities[Player.MOVE_UP], is(0));
      assertThat(Player.MCTS.possibilities[Player.MOVE_STAY], is(1));
    }
    
    
    @Test
    public void avoidBomb() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      Player.MCTS.game = game;
      
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(1, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(4, 4)
          .build();
      
      buildBoard(game,
          "..0..",
          ".X.X.",
          "0....",
          ".X.X.", 
          "....." 
          );
      
      
      Player.Bomb bomb = new BombBuilder(game.currentState)
          .from(opponent)
          .atPosition(0, 1)
          .withRange(3)
          .withTicksLeft(2)
          .build();
      
      // game ai
      Player.MCTSAI ai = new Player.MCTSAI();
      ai.game = game;
      
      ai.steps = 16;
      ai.compute();
      
      // debug
      //debugMCTS("ROOT", ai.root, 0);
      //Player.MCTSAI.debugMCTS2(ai.root);
      debugBestMove(ai.root, 10);
      
      Player.Action action = ai.actions.get(0);
      assertThat(action.pos, is(not(Player.P.get(0, 0))));
      
      assertThat(game.currentState.players[0].isDead(), is(false));
    }
    
    @Test
    public void ImNotTrapped() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      Player.MCTS.game = game;
      
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 1)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(4, 4)
          .build();
      
      buildBoard(game,
          ".....",
          ".X.X.",
          ".....",
          ".X.X.", 
          "....." 
          );
      
      
      Player.Bomb bomb = new BombBuilder(game.currentState)
          .from(opponent)
          .atPosition(0, 2)
          .withRange(3)
          .withTicksLeft(3)
          .build();
      
      // game ai
      Player.MCTSAI ai = new Player.MCTSAI();
      ai.game = game;
      
      ai.steps = 10;
      ai.compute();
      
      // debug
      //debugMCTS("ROOT", ai.root, 0);
      //Player.MCTSAI.debugMCTS2(ai.root);
      debugBestMove(ai.root, 10);
      
      Player.Action action = ai.actions.get(0);
      assertThat(action.pos, is(Player.P.get(0, 0)));
      
      assertThat(game.currentState.players[0].isDead(), is(false));
    }
    
    @Test
    @Ignore
    //FIXME fix this test !
    public void ImNotTrapped_2() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      Player.MCTS.game = game;
      
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 3)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(4, 4)
          .build();
      
      buildBoard(game,
          ".....",
          ".X.X.",
          ".X...",
          ".X.X.", 
          ".X..." 
          );
      

      Player.Bomb bomb = new BombBuilder(game.currentState)
          .from(opponent)
          .atPosition(0, 4)
          .withRange(10)
          .withTicksLeft(5)
          .build();
      
      // game ai
      Player.MCTSAI ai = new Player.MCTSAI();
      ai.game = game;
      
      ai.compute();
      
      // debug
      //System.err.println("//////////////////////");
      //debugMCTS("ROOT", ai.root, 0);
      //Player.MCTSAI.debugMCTS2(ai.root);
      //debugBestMove(ai.root, 10);
      
      Player.Action action = ai.actions.get(0);
      assertThat(action.pos, is(Player.P.get(0, 2)));
      
      assertThat(game.currentState.players[0].isDead(), is(false));
    }
    
    @Test
    public void explosionChaining_2() throws Exception {
      Player.Game game = new Player.Game(13, 11);
      Player.MCTS.game = game;
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(1, 0)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();


      buildBoard(game,
        "             ",
        " X X X0X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X0X X X X ",
        "             "
          );
      
      Player.Bomb bomb = new BombBuilder(game.currentState)
          .from(me)
          .atPosition(4,10)
          .withRange(4)
          .withTicksLeft(3)
          .build();

      Player.Bomb bomb2 = new BombBuilder(game.currentState)
          .from(me)
          .atPosition(6, 10)
          .withRange(4)
          .withTicksLeft(1)
          .build();

      // game ai
      Player.MCTSAI ai = new Player.MCTSAI();
      ai.game = game;
      
      ai.compute();

      debugBestMove(ai.root, 2);
    }
    
    @Test
    public void moveDownBombUp_isAGoodMove() throws Exception {
      Player.Game game = new Player.Game(13, 11);
      Player.MCTS.game = game;
      me = new PlayerBuilder(game.currentState)
          .withId(0)
          .withPos(0, 1)
          .build();
      
      opponent = new PlayerBuilder(game.currentState)
          .withId(1)
          .withPos(5, 5)
          .build();


      buildBoard(game,
        "    0 0      ",
        ".X X0X0X X X ",
        " 0000        ",
        "0X X X X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X X X X X ",
        "             ",
        " X X0X X X X ",
        "             "
          );
      
      // game ai
      Player.MCTSAI ai = new Player.MCTSAI();
      ai.game = game;
      
      ai.compute();

      debugBestMove(ai.root.childs.get("1M1M1M1M").childs.get("3B3B3B3B"), 2);
    }
    
    private void debugBestMove(Player.MCTS root, int depth) {
      String key = Player.MCTS.biasedMovementAlgorithm.getBestChild(root);
      if (key == null) {
        // the end
        return;
      }
      System.out.println("--> "+Player.MCTSAI.keyToString(key, 0));
      if (depth > 0) {
        Player.MCTS mcts = root.childs.get(key);
        debugBestMove(mcts, depth-1);
      }
    }

    private void debugMCTS(String key, Player.MCTS root, int step) {
      String decal="";
      for (int i=0;i<step;i++) {
        decal+=" ";
      }
      if (!key.equals("ROOT")) {
        System.err.println(decal+Player.MCTSAI.keyToString(key, 0)+" : "+root.win+" / "+root.simulatedCount);
        Player.MCTSAI.debugMCTS2(root, decal+"-> ");
      }
      if (step < 1) {
        for (Entry<String, Player.MCTS> m : root.childs.entrySet()) {
          debugMCTS(m.getKey(), m.getValue(), step+1);
        }
      }
    }
  }
  static void buildBoard(Player.Game game, String...rows) {
    int rowIndex = 0;
    for (String row : rows) {
      row = row.replace(" ", ".");
      game.currentState.addRow(rowIndex++, row);
    }
    game.updateNearestBoxes();
    game.updateNearestOption();
    game.currentState.updateBoxInfluenza(game.currentState.players[Player.Game.myIndex].bombRange);
  }
  
  static class BombBuilder {
    Player.GameState state;
    int owner;
    int x;
    int y;
    int ticksLeft;
    int range;
    public BombBuilder(Player.GameState currentState) {
      this.state = currentState;
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
    
    public PlayerBuilder(Player.GameState currentState) {
      state = currentState;
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
      state.addEntity(aPlayer);
      return aPlayer;
    }
  }
  static void computeGame(Player.Game game) {
    game.currentState.computeRound();
    game.updateNextStates();
  }
  static class ItemBuilder {
    int x;
    int y;
    private Player.GameState state;

    public ItemBuilder(Player.GameState currentState) {
      state = currentState;
    }

    public ItemBuilder atPosition(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }

    public Player.Item build() {
      Player.Item item = new Player.Item(state, 0, x, y, 0, 0);
      state.addEntity(item);
      state.grid[x][y] = Player.GameState.CELL_ITEM_RANGEUP;
      return item;
    }
  }

  
}
