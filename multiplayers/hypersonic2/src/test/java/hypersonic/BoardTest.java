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

public class BoardTest {

  public static class PlayerMove {
    @Test
    public void canWalkOnEmptyCell() throws Exception {
      final Board board = createBasicBoard();
      board.me = new Bomberman(1, P.get(0,0), 1, 3);
      
      board.me.move(board, P.get(1,0));
      
      assertThat(board.me.position, is(P.get(1,0)));
    }
  
    @Test
    public void playerCantMoveIntoWall() throws Exception {
      final Board board = createBasicBoard();
      board.me = new Bomberman(1, P.get(1,0), 1, 3);
      
      board.me.move(board, P.get(1,1));
      
      assertThat(board.me.position, is(P.get(1,0)));
    }
  
    @Test
    public void playerCantMoveIntoBox() throws Exception {
      final Board board = createBasicBoard();
      board.me = new Bomberman(1, P.get(2,0), 1, 3);
      
      board.me.move(board, P.get(2,1));
      
      assertThat(board.me.position, is(P.get(2,0)));
    }

    @Test
    public void playerCantMoveIntoBomb() throws Exception {
      final Board board = createBasicBoard();
      board.me = new Bomberman(1, P.get(2,0), 1, 3);
      createBomb(board).at(3,0).withRange(3).build();
      
      board.me.move(board, P.get(3,0));
      
      assertThat(board.me.position, is(P.get(2,0)));
    }

    @Test
    public void playerMoveOnItemConsumeIt() throws Exception {
      final Board board = createBasicBoard();
      board.cells[cell(3,0)] = Board.ITEM_1;
      board.me = new Bomberman(1, P.get(2,0), 1, 3);
      
      board.me.move(board, P.get(3,0));
      
      assertThat(board.me.position, is(P.get(3,0)));
      assertThat(board.cells[cell(3,0)], is((int)Board.EMPTY));
    }

    @Test
    public void playerMoveOnRangeUpItem() throws Exception {
      final Board board = createBasicBoard();
      board.cells[cell(3,0)] = Board.ITEM_1;
      board.me = new Bomberman(1, P.get(2,0), 1, 3);
      
      board.me.move(board, P.get(3,0));
      
      assertThat(board.me.position, is(P.get(3,0)));
      assertThat(board.me.currentRange, is(4));
    }

    @Test
    public void playerMoveOnBombUpItem() throws Exception {
      final Board board = createBasicBoard();
      board.cells[cell(3,0)] = Board.ITEM_2;
      board.me = new Bomberman(1, P.get(2,0), 1, 3);
      
      board.me.move(board, P.get(3,0));
      
      assertThat(board.me.position, is(P.get(3,0)));
      assertThat(board.me.bombsLeft, is(2));
    }
  }
  
  
  public static class Explosions {
    @Test
    public void bombsExplodesAfter8turns() throws Exception {
      final Board board = createBasicBoard();
      createBomb(board).at(2,0).withRange(3).build();

      simulateToBombExplosion(board);

      assertThat(board.bombs[0], is(nullValue()));
      assertThat(board.cells[cell(2,1)], is((int)Board.EMPTY));
    }

    @Test
    public void bombsRespectsRange() throws Exception {
      final Board board = createBasicBoard();
      createBomb(board).at(7,2).withRange(3).build();

      simulateToBombExplosion(board);

      assertThat(board.bombs[0], is(nullValue()));
      assertThat(board.cells[cell(3,2)], is(not((int)Board.EMPTY)));
    }

    @Test
    public void bombsExplodesBoxes() throws Exception {
      final Board board = createBasicBoard();
      final Bomb bomb = createBomb(board).at(2,2).withTimer(1).withRange(3).build();

      board.explode(bomb);
      
      assertThat(board.cells[cell(2,1)], is((int)Board.EMPTY));
      assertThat(board.cells[cell(1,2)], is((int)Board.EMPTY));
      assertThat(board.cells[cell(2,3)], is((int)Board.EMPTY));
      assertThat(board.cells[cell(3,2)], is((int)Board.EMPTY));
    }
    
    @Test
    public void explodedBoxesGivePointsToPlayer() throws Exception {
      final Board board = createBasicBoard();
      createBasicPlayer(board);
      final Bomb bomb = createBomb(board).at(2,2).withTimer(1).withRange(3).build();

      board.explode(bomb);

      assertThat(board.players[0].points, is(4));
    }

    static Bomberman createBasicPlayer(final Board board) {
      final Bomberman bomberman = new Bomberman(1, P.get(0,0), 1, 3);
      board.addPlayer(bomberman);
      return bomberman;
    }

    @Test
    public void bombsKillsPlayers() throws Exception {
      final Board board = createBasicBoard();
      final Bomberman bomberman = new Bomberman(1, P.get(1,0), 1, 3);
      board.addPlayer(bomberman);
      final Bomb bomb = createBomb(board).at(0,0).withTimer(1).withRange(3).build();

      board.updateBombs();
      
      assertThat(bomberman.isDead, is(true));
    }
    
    @Test
    public void explodedBombsReplacedByEmptyCell() throws Exception {
      final Board board = createBasicBoard();
      final Bomb bomb = createBomb(board).at(0,0).withTimer(1).withRange(3).build();

      board.updateBombs();
      
      assertThat(board.cells[0*Board.WIDTH*0], is(Board.EMPTY));
    }
    
    @Test
    public void wallsBlocksBombs() throws Exception {
      final Board board = new Board();
      initBoard(board, 
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
      final Bomb bomb = createBomb(board).at(0,2).withTimer(1).withRange(3).build();

      board.explode(bomb);
      
      assertThat(board.cells[cell(2,1)], is(not((int)Board.EMPTY)));
    }

    @Test
    public void boxesBlockBombs() throws Exception {
      final Board board = new Board();
      initBoard(board, 
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
      final Bomb bomb = createBomb(board).at(0,0).withTimer(1).withRange(10).build();
      
      board.updateBombs();
      
      assertThat(board.cells[cell(1,0)], is(Board.EMPTY));
      assertThat(board.cells[cell(2,0)], is(not(Board.EMPTY)));
    }

    
    @Test
    public void copyBoard() throws Exception {
      final Board model = new Board();
      initBoard(model, 
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

      Board board = new Board();
      board.copyFrom(model);
      
      assertThat(board.cells[0], is(Board.EMPTY));
      assertThat(board.cells[1], is(Board.BOX));
      
    }
    @Test
    public void itemsBlockBombs() throws Exception {
      final Board board = new Board();
      initBoard(board, 
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
      createItem(board).withType(1).withPosition(P.get(1,0)).build();
      
      final Bomb bomb = createBomb(board).at(0,0).withTimer(1).withRange(10).build();
      
      board.updateBombs();
      
      assertThat(board.cells[cell(1,0)], is((int)Board.EMPTY));
      assertThat(board.cells[cell(2,0)], is(not((int)Board.EMPTY)));
    }

    @Test
    public void bombsBlockBombs() throws Exception {
      final Board board = new Board();
      initBoard(board, 
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
      final Bomb bomb = createBomb(board).at(12,0).withTimer(1).withRange(100).build();
      final Bomb blockingbomb = createBomb(board).at(6,0).withTimer(1).withRange(2).build();

      board.explode(bomb);
      
      assertThat(board.cells[cell(2,0)], is(not((int)Board.EMPTY)));
    }

    @Test
    public void explosionChains() throws Exception {
      final Board board = new Board();
      initBoard(board, 
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
      final Bomb bomb = createBomb(board).at(0,0).withTimer(1).withRange(10).build();
      final Bomb chainedBomb = createBomb(board).at(2,0).withTimer(10).withRange(10).build();
      
      // bomb should chain with chainedBomb and destroy the box 
      board.explode(bomb);
      
      assertThat(board.cells[cell(2,1)], is(Board.EMPTY));
    }
    
    @Test
    public void explodedBoxesRevealsItems_ofType1() throws Exception {
      final Board board = new Board();
      initBoard(board, 
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
      final Bomb bomb = createBomb(board).at(2,0).withTimer(1).withRange(10).build();
      board.updateBombs();
      
      assertThat(board.cells[2 + Board.WIDTH * 1], is(Board.ITEM_1));
    }

    @Test
    public void explodedBoxesRevealsItems_ofType2() throws Exception {
      final Board board = new Board();
      initBoard(board, 
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
      final Bomb bomb = createBomb(board).at(2,0).withTimer(1).withRange(10).build();
      board.updateBombs();
      
      assertThat(board.cells[2 + Board.WIDTH * 1], is(Board.ITEM_2));
    }
    
    @Test
    public void doubleBombsDontDestroyTwoBoxes() throws Exception {
      final Board board = new Board();
      initBoard(board, 
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
      final Bomb bomb = createBomb(board).at(2,0).withTimer(1).withRange(50).build();
      final Bomb blockingbomb = createBomb(board).at(1,0).withTimer(8).withRange(4).build();

      board.updateBombs();
      
      assertThat(board.cells[cell(3,0)], is(Board.EMPTY));
      assertThat(board.cells[cell(4,0)], is(not(Board.EMPTY)));
    }

  }


  public static Board createBasicBoard() {
    final Board board = new Board();
    initBoard(board, 
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
    return board;
  }


  public static void initBoard(final Board board, final String... rows) {
    board.init();
    for (int y=0;y<11;y++)
      board.init(y, rows[y]);
  }
  
  public static BombBuilder createBomb(final Board board) {
    return new BombBuilder(board);
  }
  public static class BombBuilder {
    private final Board board;
    private int x;
    private int y;
    private int range = 3;
    private int timer = 8;
    private int owner = 1;

    public BombBuilder(final Board board) {
      this.board = board;
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
      final Bomb bomb = BombCache.pop(owner, P.get(x,y), timer, range);
      board.addBomb(bomb);
      return bomb;
    }
  }


  public static void simulateToBombExplosion(final Board board) {
    for (int i=0;i<8;i++) {
      board.updateBombs();
    }
  }

  public static ItemBuilder createItem(final Board board) {
    return new ItemBuilder(board);
  }
  public static class ItemBuilder {

    private final Board board;
    private int type;
    private P p;

    public ItemBuilder(final Board board) {
      this.board = board;
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
      final Item item = Item.create(board, 0, p, type, 0);
      board.addItem(item);
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
