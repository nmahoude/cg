package hypersonic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
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
      Board board = createBasicBoard();
      board.me = new Bomberman(board, 1, new P(0,0), 1, 3);
      
      board.me.move(new P(1,0));
      
      assertThat(board.me.position, is(new P(1,0)));
    }
  
    @Test
    public void playerCantMoveIntoWall() throws Exception {
      Board board = createBasicBoard();
      board.me = new Bomberman(board, 1, new P(1,0), 1, 3);
      
      board.me.move(new P(1,1));
      
      assertThat(board.me.position, is(new P(1,0)));
    }
  
    @Test
    public void playerCantMoveIntoBox() throws Exception {
      Board board = createBasicBoard();
      board.me = new Bomberman(board, 1, new P(2,0), 1, 3);
      
      board.me.move(new P(2,1));
      
      assertThat(board.me.position, is(new P(2,0)));
    }

    @Test
    public void playerCantMoveIntoBomb() throws Exception {
      Board board = createBasicBoard();
      board.me = new Bomberman(board, 1, new P(2,0), 1, 3);
      createBomb(board).at(3,0).withRange(3).build();
      
      board.me.move(new P(3,0));
      
      assertThat(board.me.position, is(new P(2,0)));
    }

    @Test
    public void playerMoveOnItemConsumeIt() throws Exception {
      Board board = createBasicBoard();
      board.cells[cell(3,0)] = Board.ITEM_1;
      board.me = new Bomberman(board, 1, new P(2,0), 1, 3);
      
      board.me.move(new P(3,0));
      
      assertThat(board.me.position, is(new P(3,0)));
      assertThat(board.cells[cell(3,0)], is((int)Board.EMPTY));
    }

    @Test
    public void playerMoveOnRangeUpItem() throws Exception {
      Board board = createBasicBoard();
      board.cells[cell(3,0)] = Board.ITEM_1;
      board.me = new Bomberman(board, 1, new P(2,0), 1, 3);
      
      board.me.move(new P(3,0));
      
      assertThat(board.me.position, is(new P(3,0)));
      assertThat(board.me.currentRange, is(4));
    }

    @Test
    public void playerMoveOnBombUpItem() throws Exception {
      Board board = createBasicBoard();
      board.cells[cell(3,0)] = Board.ITEM_2;
      board.me = new Bomberman(board, 1, new P(2,0), 1, 3);
      
      board.me.move(new P(3,0));
      
      assertThat(board.me.position, is(new P(3,0)));
      assertThat(board.me.bombsLeft, is(2));
    }
  }
  
  
  public static class Explosions {
    @Test
    public void bombsExplodesAfter8turns() throws Exception {
      Board board = createBasicBoard();
      createBomb(board).at(2,0).withRange(3).build();

      simulateToBombExplosion(board);

      assertThat(board.bombs.isEmpty(), is(true));
      assertThat(board.cells[cell(2,1)], is((int)Board.EMPTY));
    }

    @Test
    public void bombsRespectsRange() throws Exception {
      Board board = createBasicBoard();
      createBomb(board).at(7,2).withRange(3).build();

      simulateToBombExplosion(board);

      assertThat(board.bombs.isEmpty(), is(true));
      assertThat(board.cells[cell(3,2)], is(not((int)Board.EMPTY)));
    }

    @Test
    public void bombsExplodesBoxes() throws Exception {
      Board board = createBasicBoard();
      Bomb bomb = createBomb(board).at(2,2).withTimer(1).withRange(3).build();

      bomb.explode();
      
      assertThat(board.cells[cell(2,1)], is((int)Board.EMPTY));
      assertThat(board.cells[cell(1,2)], is((int)Board.EMPTY));
      assertThat(board.cells[cell(2,3)], is((int)Board.EMPTY));
      assertThat(board.cells[cell(3,2)], is((int)Board.EMPTY));
    }
    
    @Test
    public void explodedBoxesGivePointsToPlayer() throws Exception {
      Board board = createBasicBoard();
      createBasicPlayer(board);
      Bomb bomb = createBomb(board).at(2,2).withTimer(1).withRange(3).build();

      bomb.explode();

      assertThat(board.players.get(0).points, is(4));
    }

    static Bomberman createBasicPlayer(Board board) {
      Bomberman bomberman = new Bomberman(board, 1, new P(0,0), 1, 3);
      board.players.add(bomberman);
      return bomberman;
    }

    @Test
    public void bombsKillsPlayers() throws Exception {
      Board board = createBasicBoard();
      Bomberman bomberman = new Bomberman(board, 1, new P(1,0), 1, 3);
      board.players.add(bomberman);
      Bomb bomb = createBomb(board).at(0,0).withTimer(1).withRange(3).build();

      bomb.explode();
      
      assertThat(bomberman.isDead, is(true));
    }
    
    @Test
    public void wallsBlocksBombs() throws Exception {
      Board board = new Board();
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
      Bomb bomb = createBomb(board).at(0,2).withTimer(1).withRange(3).build();

      bomb.explode();
      
      assertThat(board.cells[cell(2,1)], is(not((int)Board.EMPTY)));
    }

    @Test
    public void boxesBlockBombs() throws Exception {
      Board board = new Board();
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
      Bomb bomb = createBomb(board).at(0,0).withTimer(1).withRange(10).build();
      board.addBomb(bomb);
      
      board.explode(bomb);
      
      assertThat(board.cells[cell(1,0)], is((int)Board.EMPTY));
      assertThat(board.cells[cell(2,0)], is(not((int)Board.EMPTY)));
    }

    @Test
    public void itemsBlockBombs() throws Exception {
      Board board = new Board();
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
      createItem(board).withType(1).withPosition(new P(1,0)).build();
      
      Bomb bomb = createBomb(board).at(0,0).withTimer(1).withRange(10).build();
      
      board.explode(bomb);
      
      assertThat(board.cells[cell(1,0)], is((int)Board.EMPTY));
      assertThat(board.cells[cell(2,0)], is(not((int)Board.EMPTY)));
    }

    @Test
    public void bombsBlockBombs() throws Exception {
      Board board = new Board();
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
      Bomb bomb = createBomb(board).at(12,0).withTimer(1).withRange(100).build();
      Bomb blockingbomb = createBomb(board).at(6,0).withTimer(1).withRange(2).build();

      board.explode(bomb);
      
      assertThat(board.cells[cell(2,0)], is(not((int)Board.EMPTY)));
    }

    @Test
    public void explosionChains() throws Exception {
      Board board = new Board();
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
      Bomb bomb = createBomb(board).at(0,0).withTimer(1).withRange(10).build();
      Bomb chainBomb = createBomb(board).at(2,0).withTimer(1).withRange(10).build();
      
      bomb.explode();
      
      assertThat((char)board.cells[cell(2,1)], is(Board.EMPTY));
    }
    
    @Test
    public void explodedBoxesRevealsItems_ofType1() throws Exception {
      Board board = new Board();
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
      Bomb bomb = createBomb(board).at(2,0).withTimer(1).withRange(10).build();
      bomb.explode();
      
      assertThat(board.items.size(), is(1));
      Item item=  board.items.get(0);
      assertThat(item.type, is(1));
      assertThat(item.position, is (new P(2,1)));
    }

    @Test
    public void explodedBoxesRevealsItems_ofType2() throws Exception {
      Board board = new Board();
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
      Bomb bomb = createBomb(board).at(2,0).withTimer(1).withRange(10).build();
      bomb.explode();
      
      assertThat(board.items.size(), is(1));
      Item item=  board.items.get(0);
      assertThat(item.type, is(2));
      assertThat(item.position, is (new P(2,1)));
    }
  }


  public static Board createBasicBoard() {
    Board board = new Board();
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


  public static void initBoard(Board board, String... rows) {
    board.init();
    for (int y=0;y<11;y++)
      board.init(y, rows[y]);
  }
  
  public static BombBuilder createBomb(Board board) {
    return new BombBuilder(board);
  }
  public static class BombBuilder {
    private Board board;
    private int x;
    private int y;
    private int range = 3;
    private int timer = 8;
    private int owner = 1;

    public BombBuilder(Board board) {
      this.board = board;
    }

    public BombBuilder at(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }

    public BombBuilder withRange(int range) {
      this.range = range;
      return this;
    }

    public BombBuilder withTimer(int timer) {
      this.timer  = timer;
      return this;
    }

    public BombBuilder withOwner(int owner) {
      this.owner  = owner;
      return this;
    }
    
    public Bomb build() {
      Bomb bomb = new Bomb(board, owner, new P(x,y), timer, range);
      board.addBomb(bomb);
      return bomb;
    }
  }


  public static void simulateToBombExplosion(Board board) {
    for (int i=0;i<8;i++) {
      board.simulate();
    }
  }

  public static ItemBuilder createItem(Board board) {
    return new ItemBuilder(board);
  }
  public static class ItemBuilder {

    private Board board;
    private int type;
    private P p;

    public ItemBuilder(Board board) {
      this.board = board;
    }
    public ItemBuilder withType(int type) {
      this.type = type;
      return this;
    }
    public ItemBuilder withPosition(P p) {
      this.p = p;
      return this;
    }
    public Item build() {
      Item item = new Item(board, 0, p, type, 0);
      board.addItem(item);
      return item;
    }
  };
  static int cell(int x, int y) {
    return x+13*y;
  }


}
