package coif;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Scanner;

import org.junit.Test;

public class StateTest {

  @Test
  public void SinglePointFrontier() {
    String input = "100 31\r\n" + 
        "9 2\r\n" + 
        "O#.#...#####\r\n" + 
        "O#.....#####\r\n" + 
        "O#.#....####\r\n" + 
        "O#.###..####\r\n" + 
        "O#..#XX..###\r\n" + 
        "O#...XX..X##\r\n" + 
        "O#.XXX...XX#\r\n" + 
        "O##.X..#XXX#\r\n" + 
        "O###XX###XX#\r\n" + 
        "O##########.\r\n" + 
        "OOOOOOOOOOOX\r\n" + 
        "###########X\r\n" + 
        "2\r\n" + 
        "0 0 0 0\r\n" + 
        "1 0 11 11\r\n" + 
        "0\r\n" +
        "";
    
    State state = new State();
    state.readTurn(new Scanner(input));
    
    List<Pos> frontierIn = state.board.getFrontierIn(Pos.get(0, 0), Board.P0_ACTIVE);
    
    assertThat(frontierIn).containsExactly(Pos.get(10,10));
  
  }
}
