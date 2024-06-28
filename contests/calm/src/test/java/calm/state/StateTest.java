package calm.state;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import calm.actions.Action;
import calm.actions.UseKnife;

public class StateTest {

  private State state;

  @Before
  public void setup() {
    state = new State();

  }

  
  @Test
  public void holdingStrawberriesNearChoppingBoard_canKnife() throws Exception {
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
    String input= "69\r\n"+
        "9 1 STRAWBERRIES\r\n"+
        "1 1 CROISSANT\r\n"+
        "1\r\n"+
        "1 0 TART\r\n"+
        "NONE 0\r\n"+
        "3\r\n"+
        "DISH-TART-CROISSANT-ICE_CREAM-CHOPPED_STRAWBERRIES 2382\r\n"+
        "DISH-BLUEBERRIES-TART-CHOPPED_STRAWBERRIES 1782\r\n"+
        "DISH-ICE_CREAM-BLUEBERRIES 582\r\n"+
        "";
        state.read(new Scanner(input));
        
    boolean found = false;
    Actions actions = State.actions[state.agent1.pos.index];
    for (int i=0;i<actions.actionsFE;i++) {
      Action a = actions.actions[i];
      if (a instanceof UseKnife) {
        found = true;
      }
    }
    assertThat(found, is(true));
  }
}
