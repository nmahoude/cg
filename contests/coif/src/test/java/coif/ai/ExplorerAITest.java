package coif.ai;

import java.util.Scanner;

import org.junit.Test;

import coif.State;

public class ExplorerAITest {

  
  @Test
  public void dontCrash() throws Exception {
    String input = "21 1\r\n" + 
        "20 2\r\n" + 
        "O.....######\r\n" + 
        "..........##\r\n" + 
        "...........#\r\n" + 
        "............\r\n" + 
        "............\r\n" + 
        "............\r\n" + 
        "............\r\n" + 
        "............\r\n" + 
        "............\r\n" + 
        "#...........\r\n" + 
        "##.........X\r\n" + 
        "######.....X\r\n" + 
        "2\r\n" + 
        "0 0 0 0\r\n" + 
        "1 0 11 11\r\n" + 
        "0";
    
    State state = new State();
    Simulation sim = new Simulation(state);
    state.readTurn(new Scanner(input));
    
    new QuickWinAI(sim, state).think();
    ExplorerAI ai = new ExplorerAI(sim, state);
    ai.think();
  }
}
