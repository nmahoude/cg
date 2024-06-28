package coif.ai;

import java.util.Scanner;

import org.junit.Test;

import coif.Board;
import coif.Pos;
import coif.State;

public class QuickWin2AITest {

  
  @Test
  public void findCosts() throws Exception {
    String input = "91 21\r\n" + 
        "66 31\r\n" + 
        "OOO#####..##\r\n" + 
        "OOO####....#\r\n" + 
        "OOO###.....#\r\n" + 
        "OOO##O....##\r\n" + 
        "OOOOOOX...##\r\n" + 
        "OOOOOXXX.XX#\r\n" + 
        "#OOOXXXXXXX.\r\n" + 
        "##XXXXXXXXX.\r\n" + 
        "##.....##XX.\r\n" + 
        "#.....###XX.\r\n" + 
        "#....####XXX\r\n" + 
        "##..#####XXX\r\n" + 
        "4\r\n" + 
        "0 0 0 0\r\n" + 
        "1 0 11 11\r\n" + 
        "1 1 10 11\r\n" + 
        "1 2 3 7\r\n" + 
        "11\r\n" + 
        "0 1 1 3 6\r\n" + 
        "0 2 1 2 6\r\n" + 
        "0 5 1 4 4\r\n" + 
        "0 7 1 5 3\r\n" + 
        "0 9 1 4 5\r\n" + 
        "0 11 1 5 4\r\n" + 
        "1 3 1 9 5\r\n" + 
        "1 4 1 6 4\r\n" + 
        "1 6 1 5 5\r\n" + 
        "1 8 1 4 6\r\n" + 
        "1 10 1 2 7";
    
    State state = new State();
    Simulation sim = new Simulation(state);
    state.readTurn(new Scanner(input));
    
    QuickWin2AI ai = new QuickWin2AI(sim, state);
    
    ai.findFor(state.opp.HQ, Board.P0_ACTIVE);

    for (int y=0;y<12;y++) {
      System.err.print("[");
      for (int x=0;x<12;x++) {
        Pos p = Pos.get(x, y);
        int cost = ai.pseudoCosts[p.index];
        if (state.getCell(p).getStatut() == Board.P0_ACTIVE) {
          System.err.print(String.format("(%3d) ", cost));
        } else {
          System.err.print(String.format(" %3d  ", cost));
        }
      }
      System.err.println("]");
    }
  }
}
