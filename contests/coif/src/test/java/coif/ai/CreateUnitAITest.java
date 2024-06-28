package coif.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Scanner;

import org.junit.Test;

import coif.State;

public class CreateUnitAITest {

  
  @Test
  public void create2UnitsAtTurn1() throws Exception {
    String input = "21 1\r\n" + 
        "20 2\r\n" + 
        "O..#########\r\n" + 
        "...###..####\r\n" + 
        "...##....###\r\n" + 
        ".........###\r\n" + 
        "..........##\r\n" + 
        "#.........##\r\n" + 
        "##.........#\r\n" + 
        "##..........\r\n" + 
        "###.........\r\n" + 
        "###....##...\r\n" + 
        "####..###..X\r\n" + 
        "#########..X\r\n" + 
        "2\r\n" + 
        "0 0 0 0\r\n" + 
        "1 0 11 11\r\n" + 
        "0";
    
    State state = new State();
    Simulation sim = new Simulation(state);
    state.readTurn(new Scanner(input));
    CreateUnitAI ai = new CreateUnitAI(sim, state);
    ai.think();
    
    String output = sim.output();
    System.err.println(output);
    assertThat(output).contains("TRAIN", "TRAIN");
  }
}
