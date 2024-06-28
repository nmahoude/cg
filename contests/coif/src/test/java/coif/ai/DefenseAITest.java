package coif.ai;

import java.util.Scanner;

import org.junit.Test;

import coif.State;

public class DefenseAITest {

  
  @Test
  public void countCorrectlyCellsToDefend() throws Exception {
    String input ="52 19\r\n" + 
        "37 30\r\n" + 
        "OOO#####..##\r\n" + 
        "OOO####....#\r\n" + 
        "OOO###.....#\r\n" + 
        "OOO##O....##\r\n" + 
        "OOOOOOO...##\r\n" + 
        "OOOOOXXX..X#\r\n" + 
        "#OXOXXXXXXX.\r\n" + 
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
        "13\r\n" + 
        "0 1 1 3 6\r\n" + 
        "0 2 1 4 5\r\n" + 
        "0 5 1 2 4\r\n" + 
        "0 7 1 2 5\r\n" + 
        "0 9 1 5 3\r\n" + 
        "0 11 1 1 6\r\n" + 
        "0 12 1 5 4\r\n" + 
        "0 13 1 6 4\r\n" + 
        "1 3 1 10 5\r\n" + 
        "1 4 1 6 5\r\n" + 
        "1 6 1 5 5\r\n" + 
        "1 8 1 4 6\r\n" + 
        "1 10 1 2 6";
    
    State state = new State();
    Simulation sim= new Simulation(state);
    state.readTurn(new Scanner(input));
    
    DefenseAI ai = new DefenseAI(sim, state);
    ai.think();
    
    // should be 6 cells to defend ...
  }
}
