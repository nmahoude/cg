package hypersonic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import org.junit.Test;

import hypersonic.entities.Bomb;
import hypersonic.utils.P;

public class BoardTest {

  private static Board board;


  public static class Explosions {
    @Test
    public void bombsExplodesAfter8turns() throws Exception {
      Board board = createBasicBoard();
      Bomb bomb = createBomb(board).at(2,0).withRange(3).build();

      simulateToBombExplosion(board);

      assertThat(board.bombs.isEmpty(), is(true));
      assertThat(board.cells[2][1], is((int)Board.EMPTY));
    }

    @Test
    public void bombsExplodesBoxes() throws Exception {
      Board board = createBasicBoard();
      Bomb bomb = createBomb(board).at(2,2).withTimer(1).withRange(3).build();

      bomb.explode(board);
      
      assertThat(board.cells[2][1], is((int)Board.EMPTY));
      assertThat(board.cells[1][2], is((int)Board.EMPTY));
      assertThat(board.cells[2][3], is((int)Board.EMPTY));
      assertThat(board.cells[3][2], is((int)Board.EMPTY));
    }
    
    @Test
    public void bombsBlockOnWall() throws Exception {
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
      Bomb bomb = new Bomb(new P(0,2), 1, 3);
      board.addBomb(bomb);
      bomb.explode(board);
      
      assertThat(board.cells[2][1], is(not((int)Board.EMPTY)));
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
      Bomb bomb = new Bomb(new P(0,0), 1, 10);
      board.addBomb(bomb);
      
      board.explode(bomb);
      
      assertThat(board.cells[1][0], is((int)Board.EMPTY));
      assertThat(board.cells[2][0], is(not((int)Board.EMPTY)));
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
      Bomb bomb = new Bomb(new P(0,0), 1, 10);
      board.addBomb(bomb);

      Bomb chainBomb = new Bomb(new P(2,0), 1, 10);
      board.addBomb(chainBomb);
      
      bomb.explode(board);
      
      assertThat((char)board.cells[2][1], is(Board.EMPTY));
    }
  }


  private static Board createBasicBoard() {
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


  private static void initBoard(Board board, String... rows) {
    board.init();
    for (int y=0;y<11;y++)
      board.init(y, rows[y]);
  }
  
  private static BombBuilder createBomb(Board board) {
    return new BombBuilder(board);
  }
  private static class BombBuilder {
    private Board board;
    private int x;
    private int y;
    private int range = 3;
    private int timer = 8;

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

    public Bomb build() {
      Bomb bomb = new Bomb(new P(x,y), timer, range);
      board.addBomb(bomb);
      return bomb;
    }
  }


  private static void simulateToBombExplosion(Board board) {
    for (int i=0;i<8;i++) {
      board.simulate();
    }
  }

}
