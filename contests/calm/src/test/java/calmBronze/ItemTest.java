package calmBronze;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import calm.state.State;

public class ItemTest {

  private State state;

  @Before
  public void setup() {
    state = new State();
    State.equipmentsFE = 0;

  }

  @Test
  public void readWorldIsOk() throws Exception {
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
    String input= "35\r\n"+
        "2 1 NONE\r\n"+
        "1 1 NONE\r\n"+
        "0\r\n"+
        "131072 38\r\n"+
        "3\r\n"+
        "DISH-TART-CROISSANT-ICE_CREAM-CHOPPED_STRAWBERRIES 2416\r\n"+
        "DISH-BLUEBERRIES-TART-CHOPPED_STRAWBERRIES 1816\r\n"+
        "DISH-ICE_CREAM-BLUEBERRIES 616\r\n"+
        "";
        state.read(new Scanner(input));

        
        boolean result = Item.canBeUseWithAgent(Item.EQUIPMENT_OVEN, state, state.agent1);
        
        assertThat(result, is(false));
  }
  
  @Test
  public void cannotTakeBakingFromOven() throws Exception {
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
    String input= "53\r\n"+
        "1 1 NONE\r\n"+
        "5 5 NONE\r\n"+
        "1\r\n"+
        "1 0 TART\r\n"+
        "DOUGH 3\r\n"+
        "3\r\n"+
        "DISH-TART-CROISSANT-ICE_CREAM-CHOPPED_STRAWBERRIES 2398\r\n"+
        "DISH-BLUEBERRIES-TART-CHOPPED_STRAWBERRIES 1798\r\n"+
        "DISH-ICE_CREAM-BLUEBERRIES 598\r\n"+
        "";
    state.read(new Scanner(input));
    
    boolean result = Item.canBeUseWithAgent(Item.EQUIPMENT_OVEN, state, state.agent1);
    
    assertThat(result, is(false));
  }
}
