package hypersonic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import org.junit.Test;

import hypersonic.entities.Bomb;
import hypersonic.utils.P;

public class BoardTest {

  public static class Explosions {
    @Test
    public void bombsExplodesBoxes() throws Exception {
      Board board = createBasicBoard();
      
      Bomb bomb = new Bomb(new P(2,2), 1, 3);
      board.addBomb(bomb);
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

}
