package calm.sim;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import calm.ai.AI;
import calm.state.State;

public class SimulationTest {
  private State state;

  @Before
  public void setup() {
    state = new State();

  }

  
  @Test
  public void crash() throws Exception {
    String init = "20\r\n"+
        "DISH-TART-CROISSANT-ICE_CREAM-CHOPPED_STRAWBERRIES 2450\r\n"+
        "DISH-BLUEBERRIES-TART-CHOPPED_STRAWBERRIES 1850\r\n"+
        "DISH-ICE_CREAM-BLUEBERRIES 650\r\n"+
        "DISH-BLUEBERRIES-CHOPPED_STRAWBERRIES-ICE_CREAM-CROISSANT 1700\r\n"+
        "DISH-BLUEBERRIES-CHOPPED_STRAWBERRIES-CROISSANT 1500\r\n"+
        "DISH-ICE_CREAM-TART 1400\r\n"+
        "DISH-CHOPPED_STRAWBERRIES-CROISSANT-BLUEBERRIES 1500\r\n"+
        "DISH-TART-ICE_CREAM 1400\r\n"+
        "DISH-CROISSANT-TART-ICE_CREAM-BLUEBERRIES 2300\r\n"+
        "DISH-CROISSANT-BLUEBERRIES 1100\r\n"+
        "DISH-TART-CROISSANT 1850\r\n"+
        "DISH-TART-ICE_CREAM-CHOPPED_STRAWBERRIES 1800\r\n"+
        "DISH-CROISSANT-CHOPPED_STRAWBERRIES 1250\r\n"+
        "DISH-BLUEBERRIES-TART-CHOPPED_STRAWBERRIES 1850\r\n"+
        "DISH-CHOPPED_STRAWBERRIES-TART 1600\r\n"+
        "DISH-BLUEBERRIES-CROISSANT 1100\r\n"+
        "DISH-ICE_CREAM-CHOPPED_STRAWBERRIES-BLUEBERRIES 1050\r\n"+
        "DISH-CROISSANT-BLUEBERRIES 1100\r\n"+
        "DISH-BLUEBERRIES-CHOPPED_STRAWBERRIES 850\r\n"+
        "DISH-TART-CROISSANT 1850\r\n"+
        "H#O##D#####\r\n"+
        "#1........I\r\n"+
        "#.####.##.C\r\n"+
        "#.#..#..#.#\r\n"+
        "#.##.####.#\r\n"+
        "B.....0...#\r\n"+
        "#####W###S#\r\n"+
        "";
        State.readWorldInit(new Scanner(init));
        
    String input= "3\r\n"+
        "6 5 NONE\r\n"+
        "1 1 DOUGH\r\n"+
        "0\r\n"+
        "NONE 0\r\n"+
        "3\r\n"+
        "DISH-TART-CROISSANT-ICE_CREAM-CHOPPED_STRAWBERRIES 2448\r\n"+
        "DISH-BLUEBERRIES-TART-CHOPPED_STRAWBERRIES 1848\r\n"+
        "DISH-ICE_CREAM-BLUEBERRIES 648\r\n"+
        "";
        state.read(new Scanner(input));
        
        AI ai = new AI();
        ai.think(state);
  }
}
