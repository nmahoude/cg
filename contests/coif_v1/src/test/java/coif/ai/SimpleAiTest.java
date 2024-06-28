package coif.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import coif_old.State;
import coif_old.ai.SimpleAi;
import coif_old.ai.Simulation;

public class SimpleAiTest {

  private State state;
  private Simulation sim;
  private SimpleAi ai;

  @Before
  public void setup() {
    state = new State();
    sim = new Simulation(state);
    ai = new SimpleAi(sim, state);
  }
  
  @Test
  public void buildADefensiveTower() {
    String input = "82 39\r\n" + 
        "39 47\r\n" + 
        "OOO#OOOO...#\r\n" + 
        "OOOOOOOOOO.#\r\n" + 
        ".OOOOOOOO.##\r\n" + 
        "#OOOOOOOOO##\r\n" + 
        ".OOOOOOX.X.#\r\n" + 
        ".OOOOXXXXXXX\r\n" + 
        "OOO..ooXXXXX\r\n" + 
        "#.O..XXXXXXX\r\n" + 
        "##...XXXXXX#\r\n" + 
        "##...XXXXXXX\r\n" + 
        "#...XXXXXXXX\r\n" + 
        "#...XXXX#XXX\r\n" + 
        "5\r\n" + 
        "0 0 0 0\r\n" + 
        "1 0 11 11\r\n" + 
        "1 1 9 11\r\n" + 
        "1 1 10 11\r\n" + 
        "1 2 10 10\r\n" + 
        "19\r\n" + 
        "0 2 1 3 5\r\n" + 
        "0 6 1 0 6\r\n" + 
        "0 8 1 6 0\r\n" + 
        "0 11 1 8 2\r\n" + 
        "0 12 1 6 4\r\n" + 
        "0 14 1 7 0\r\n" + 
        "0 15 1 9 3\r\n" + 
        "0 16 1 2 7\r\n" + 
        "0 18 1 9 1\r\n" + 
        "1 3 1 5 5\r\n" + 
        "1 4 1 5 7\r\n" + 
        "1 5 1 7 6\r\n" + 
        "1 7 1 7 4\r\n" + 
        "1 9 1 9 4\r\n" + 
        "1 10 1 5 8\r\n" + 
        "1 13 1 6 9\r\n" + 
        "1 17 1 4 10\r\n" + 
        "1 19 1 10 5\r\n" + 
        "1 21 1 4 11";
    
    state.readTurn(new Scanner(input));
    
    ai.createDefense();

    assertThat(sim.output()).contains("BUILD ");
  }
}
