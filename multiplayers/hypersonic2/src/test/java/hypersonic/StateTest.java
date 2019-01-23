package hypersonic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import org.junit.Test;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.utils.P;

public class StateTest {

  public static class PlayerMove {
    @Test
    public void canWalkOnEmptyCell() throws Exception {
      final State state = createBasicstate();
      state.players[0] = new Bomberman(1, P.get(0,0), 1, 3);
      
      state.players[0].move(state, P.get(1,0));
      
      assertThat(state.players[0].position, is(P.get(1,0)));
    }
  
    @Test
    public void playerCantMoveIntoWall() throws Exception {
      final State state = createBasicstate();
      state.players[0] = new Bomberman(1, P.get(1,0), 1, 3);
      
      state.players[0].move(state, P.get(1,1));
      
      assertThat(state.players[0].position, is(P.get(1,0)));
    }
  
    @Test
    public void playerCantMoveIntoBox() throws Exception {
      final State state = createBasicstate();
      state.players[0] = new Bomberman(1, P.get(2,0), 1, 3);
      
      state.players[0].move(state, P.get(2,1));
      
      assertThat(state.players[0].position, is(P.get(2,0)));
    }

    @Test
    public void playerCantMoveIntoBomb() throws Exception {
      final State state = createBasicstate();
      state.players[0] = new Bomberman(1, P.get(2,0), 1, 3);
      createBomb(state).at(3,0).withRange(3).build();
      
      state.players[0].move(state, P.get(3,0));
      
      assertThat(state.players[0].position, is(P.get(2,0)));
    }

    @Test
    public void playerMoveOnItemConsumeIt() throws Exception {
      final State state = createBasicstate();
      state.board.cells[cell(3,0)] = Board.ITEM_1;
      state.players[0] = new Bomberman(1, P.get(2,0), 1, 3);
      
      state.players[0].move(state, P.get(3,0));
      
      assertThat(state.players[0].position, is(P.get(3,0)));
      assertThat(state.board.cells[cell(3,0)], is(Board.EMPTY));
    }

    @Test
    public void playerMoveOnRangeUpItem() throws Exception {
      final State state = createBasicstate();
      state.board.cells[cell(3,0)] = Board.ITEM_1;
      state.players[0] = new Bomberman(1, P.get(2,0), 1, 3);
      
      state.players[0].move(state, P.get(3,0));
      
      assertThat(state.players[0].position, is(P.get(3,0)));
      assertThat(state.players[0].currentRange, is(4));
    }

    @Test
    public void playerMoveOnBombUpItem() throws Exception {
      final State state = createBasicstate();
      state.board.cells[cell(3,0)] = Board.ITEM_2;
      state.players[0] = new Bomberman(1, P.get(2,0), 1, 3);
      
      state.players[0].move(state, P.get(3,0));
      
      assertThat(state.players[0].position, is(P.get(3,0)));
      assertThat(state.players[0].bombsLeft, is(2));
    }
  }
  
  
  public static class Explosions {
    @Test
    public void twoPlayersCanHaveBoxDestroyedBonus() throws Exception {
      final State state = createBasicstate();

      state.board.cells[cell(3,0)] = Board.BOX;
      
      state.players[0] = new Bomberman(0, P.get(0,1), 1, 3);
      state.players[1] = new Bomberman(1, P.get(0,1), 1, 3);
      
      state.addBomb(new Bomb(0, P.get(1, 0), 1, 8));
      state.addBomb(new Bomb(1, P.get(1, 0), 1, 8));
      
      state.updateBombs();
      
      assertThat(state.players[0].points, is(1));
      assertThat(state.players[1].points, is(1));
    }
    
    @Test
    public void twoBombsOfSamePlayerOnlyCountOnce() throws Exception {
      final State state = createBasicstate();

      state.board.cells[cell(3,0)] = Board.BOX;
      
      state.players[0] = new Bomberman(0, P.get(0,1), 1, 3);
      
      state.addBomb(new Bomb(0, P.get(1, 0), 1, 8));
      state.addBomb(new Bomb(0, P.get(5, 0), 1, 8));
      
      state.updateBombs();
      
      assertThat(state.players[0].points, is(1));
    }
    
    @Test
    public void twoChainedBombsOfSamePlayerOnlyCountOnce() throws Exception {
      final State state = createBasicstate();

      state.board.cells[cell(12,0)] = Board.BOX;
      
      state.players[0] = new Bomberman(0, P.get(0,1), 1, 3);
      
      state.addBomb(new Bomb(0, P.get(10, 0), 1, 8));
      state.addBomb(new Bomb(0, P.get(11, 0), 1, 8));
      
      state.updateBombs();
      
      assertThat(state.players[0].points, is(1));
    }
    @Test
    public void bombsExplodesAfter8turns() throws Exception {
      final State state = createBasicstate();
      createBomb(state).at(2,0).withRange(3).build();

      simulateToBombExplosion(state);

      assertThat(state.board.bombs[0], is(nullValue()));
      assertThat(state.board.cells[cell(2,1)], is(Board.EMPTY));
    }

    @Test
    public void bombsRespectsRange() throws Exception {
      final State state = createBasicstate();
      createBomb(state).at(7,2).withRange(3).build();

      simulateToBombExplosion(state);

      assertThat(state.board.bombs[0], is(nullValue()));
      assertThat(state.board.cells[cell(3,2)], is(not(Board.EMPTY)));
    }

    @Test
    public void bombsExplodesBoxes() throws Exception {
      final State state = createBasicstate();
      final Bomb bomb = createBomb(state).at(2,2).withTimer(1).withRange(3).build();

      state.updateBombs();
      
      assertThat(state.board.cells[cell(2,1)], is(Board.EMPTY));
      assertThat(state.board.cells[cell(1,2)], is(Board.EMPTY));
      assertThat(state.board.cells[cell(2,3)], is(Board.EMPTY));
      assertThat(state.board.cells[cell(3,2)], is(Board.EMPTY));
    }
    
    @Test
    public void explodedBoxesGivePointsToPlayer() throws Exception {
      final State state = createBasicstate();
      createBasicPlayer(state);
      final Bomb bomb = createBomb(state).at(2,2).withTimer(1).withRange(3).build();

      state.updateBombs();

      assertThat(state.players[0].points, is(4));
    }

    static Bomberman createBasicPlayer(final State state) {
      final Bomberman bomberman = new Bomberman(0, P.get(0,0), 1, 3);
      state.players[0] = bomberman;
      return bomberman;
    }

    @Test
    public void bombsKillsPlayers() throws Exception {
      final State state = createBasicstate();
      final Bomberman bomberman = new Bomberman(1, P.get(1,0), 1, 3);
      state.players[0] = bomberman;
      final Bomb bomb = createBomb(state).at(0,0).withTimer(1).withRange(3).build();

      state.updateBombs();
      
      assertThat(bomberman.isDead, is(true));
    }
    
    @Test
    public void explodedBombsReplacedByEmptyCell() throws Exception {
      final State state = createBasicstate();
      final Bomb bomb = createBomb(state).at(0,0).withTimer(1).withRange(3).build();

      state.updateBombs();
      
      assertThat(state.board.cells[0*Board.WIDTH*0], is(Board.EMPTY));
    }
    
    @Test
    public void wallsBlocksBombs() throws Exception {
      final State state = new State();
      initstate(state, 
       ".............",
       ".X0X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       "............."
      );
      final Bomb bomb = createBomb(state).at(0,2).withTimer(1).withRange(3).build();

      state.updateBombs();
      
      assertThat(state.board.cells[cell(2,1)], is(not(Board.EMPTY)));
    }

    @Test
    public void boxesBlockBombs() throws Exception {
      final State state = new State();
      initstate(state, 
       ".00..........",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       "............."
      );
      final Bomb bomb = createBomb(state).at(0,0).withTimer(1).withRange(10).build();
      
      state.updateBombs();
      
      assertThat(state.board.cells[cell(1,0)], is(Board.EMPTY));
      assertThat(state.board.cells[cell(2,0)], is(not(Board.EMPTY)));
    }

    
    @Test
    public void copystate() throws Exception {
      final State model = new State();
      initstate(model, 
       ".00..........",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       "............."
      );

      State state = new State();
      state.copyFrom(model);
      
      assertThat(state.board.cells[0], is(Board.EMPTY));
      assertThat(state.board.cells[1], is(Board.BOX));
      
    }
    @Test
    public void itemsBlockBombs() throws Exception {
      final State state = new State();
      initstate(state, 
       "..0..........",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       "............."
      );
      createItem(state).withType(1).withPosition(P.get(1,0)).build();
      
      final Bomb bomb = createBomb(state).at(0,0).withTimer(1).withRange(10).build();
      
      state.updateBombs();
      
      assertThat(state.board.cells[cell(1,0)], is(Board.EMPTY));
      assertThat(state.board.cells[cell(2,0)], is(not(Board.EMPTY)));
    }

    @Test
    public void bombsBlockBombs() throws Exception {
      final State state = new State();
      initstate(state, 
       "..0..........",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       "............."
      );
      final Bomb bomb = createBomb(state).at(12,0).withTimer(1).withRange(100).build();
      final Bomb blockingbomb = createBomb(state).at(6,0).withTimer(1).withRange(2).build();

      state.updateBombs();
      
      assertThat(state.board.cells[cell(2,0)], is(not(Board.EMPTY)));
    }

    @Test
    public void explosionChains() throws Exception {
      final State state = new State();
      initstate(state, 
       ".............",
       ".X0X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       "............."
      );
      final Bomb bomb = createBomb(state).at(0,0).withTimer(1).withRange(10).build();
      final Bomb chainedBomb = createBomb(state).at(2,0).withTimer(10).withRange(10).build();
      
      // bomb should chain with chainedBomb and destroy the box 
      state.updateBombs();
      
      assertThat(state.board.cells[cell(2,1)], is(Board.EMPTY));
    }
    
    @Test
    public void explodedBoxesRevealsItems_ofType1() throws Exception {
      final State state = new State();
      initstate(state, 
       ".............",
       ".X1X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       "............."
      );
      final Bomb bomb = createBomb(state).at(2,0).withTimer(1).withRange(10).build();
      state.updateBombs();
      
      assertThat(state.board.cells[2 + Board.WIDTH * 1], is(Board.ITEM_1));
    }

    @Test
    public void explodedBoxesRevealsItems_ofType2() throws Exception {
      final State state = new State();
      initstate(state, 
       ".............",
       ".X2X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       "............."
      );
      final Bomb bomb = createBomb(state).at(2,0).withTimer(1).withRange(10).build();
      state.updateBombs();
      
      assertThat(state.board.cells[2 + Board.WIDTH * 1], is(Board.ITEM_2));
    }
    
    @Test
    public void doubleBombsDontDestroyTwoBoxes() throws Exception {
      final State state = new State();
      initstate(state, 
       "...00........",
       ".X0X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       ".............",
       ".X.X.X.X.X.X.",
       "............."
      );
      final Bomb bomb = createBomb(state).at(2,0).withTimer(1).withRange(50).build();
      final Bomb blockingbomb = createBomb(state).at(1,0).withTimer(8).withRange(4).build();

      state.updateBombs();
      
      assertThat(state.board.cells[cell(3,0)], is(Board.EMPTY));
      assertThat(state.board.cells[cell(4,0)], is(not(Board.EMPTY)));
    }

  }


  public static State createBasicstate() {
    final State state = new State();
    initstate(state, 
     ".............",
     ".X0X.X.X.X.X.",
     ".0.0.........",
     ".X0X.X.X.X.X.",
     ".............",
     ".X.X.X.X.X.X.",
     ".............",
     ".X.X.X.X.X.X.",
     ".............",
     ".X.X.X.X.X.X.",
     "............."
    );
    return state;
  }


  public static void initstate(final State state, final String... rows) {
    state.init();
    for (int y=0;y<11;y++)
      state.init(y, rows[y]);
  }
  
  public static BombBuilder createBomb(final State state) {
    return new BombBuilder(state);
  }
  public static class BombBuilder {
    private final State state;
    private int x;
    private int y;
    private int range = 3;
    private int timer = 8;
    private int owner = 0;

    public BombBuilder(final State state) {
      this.state = state;
    }

    public BombBuilder at(final int x, final int y) {
      this.x = x;
      this.y = y;
      return this;
    }

    public BombBuilder withRange(final int range) {
      this.range = range;
      return this;
    }

    public BombBuilder withTimer(final int timer) {
      this.timer  = timer;
      return this;
    }

    public BombBuilder withOwner(final int owner) {
      this.owner  = owner;
      return this;
    }
    
    public Bomb build() {
      final Bomb bomb = Cache.popBomb(owner, P.get(x,y), timer, range);
      state.addBomb(bomb);
      return bomb;
    }
  }


  public static void simulateToBombExplosion(final State state) {
    for (int i=0;i<8;i++) {
      state.updateBombs();
    }
  }

  public static ItemBuilder createItem(final State state) {
    return new ItemBuilder(state);
  }
  public static class ItemBuilder {

    private final State state;
    private int type;
    private P p;

    public ItemBuilder(final State state) {
      this.state = state;
    }
    public ItemBuilder withType(final int type) {
      this.type = type;
      return this;
    }
    public ItemBuilder withPosition(final P p) {
      this.p = p;
      return this;
    }
    public Item build() {
      final Item item = Item.create(state, 0, p, type, 0);
      state.addItem(item);
      return item;
    }
    public ItemBuilder at(final int x, final int y) {
      this.p = P.get(x, y);
      return this;
    }
  };
  static int cell(final int x, final int y) {
    return x+13*y;
  }


}
