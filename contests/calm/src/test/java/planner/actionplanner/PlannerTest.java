package planner.actionplanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import calmBronze.Item;
import planner.PlannerPlayer;
import planner.actions.DropItem;
import planner.actions.UseDishWasher;
import planner.actions.UseEquipmentIngredient;
import planner.actions.UseKnife;
import planner.actions.UseOven;
import planner.state.State;

public class PlannerTest {
  private State state;

  @Before
  public void setup() {
    PlannerPlayer.DEBUG_PLANNER = false; // reset debugger
    state = new State();

  }

  @Test
  public void fromNothingToBlueBerries() throws Exception {
    String init = "" +
        "#####D#S###\r\n" +
        "#.........#\r\n" +
        "O.####.##.#\r\n" +
        "#0#1.#..#.C\r\n" +
        "#.#H.##IB.#\r\n" +
        "#.........#\r\n" +
        "#####W#####\r\n" +
        "";
    State.readWorldInit(new Scanner(init));
    String input = "" +
        "199\r\n" +
        "1 3 NONE\r\n" +
        "3 3 NONE\r\n" +
        "0\r\n" +
        "NONE 0\r\n" +
        "3\r\n" +
        "DISH-ICE_CREAM-CHOPPED_STRAWBERRIES 800\r\n" +
        "DISH-CROISSANT-TART-ICE_CREAM-BLUEBERRIES 2300\r\n" +
        "DISH-CROISSANT-TART-BLUEBERRIES 2100\r\n" +
        "";
    state.read(new Scanner(input));

    Planner planner = new Planner();
    State goal = new State();
    goal.agent1.items = Item.BLUEBERRIES;
    List<Node> actions = planner.findPlan(state, goal);

    assertThat(actions.size(), is(1));
    assertThat(actions.get(0).action instanceof UseEquipmentIngredient, is(true));
  }

  @Test
  public void noErrorToOven() throws Exception {
    String init = "#####D#S###\r\n"+
        "#.........#\r\n"+
        "O.####.##.#\r\n"+
        "#0#1.#..#.C\r\n"+
        "#.#H.##IB.#\r\n"+
        "#.........#\r\n"+
        "#####W#####\r\n"+
        "";
        State.readWorldInit(new Scanner(init));
        String input= "43\r\n"+
            "9 4 NONE\r\n"+
            "4 1 NONE\r\n"+
            "3\r\n"+
            "5 3 DISH-ICE_CREAM\r\n"+
            "8 2 CHOPPED_STRAWBERRIES\r\n"+
            "8 3 ICE_CREAM\r\n"+
            "16384 47\r\n"+
            "3\r\n"+
            "DISH-ICE_CREAM-CHOPPED_STRAWBERRIES 758\r\n"+
            "DISH-CROISSANT-TART-ICE_CREAM-BLUEBERRIES 2258\r\n"+
            "DISH-CROISSANT-TART-BLUEBERRIES 2058\r\n"+
            "";
        state.read(new Scanner(input));

        Planner planner = new Planner();
        State goal = new State();
        goal.agent1.items = Item.BLUEBERRIES_TART;
        List<Node> actions = planner.findPlan(state, goal);

        assertThat(actions.size(), is(7));
  }
  
  
  @Test
  public void fromBlueBerriesToNothing() throws Exception {
    String init = "" +
        "#####D#S###\r\n" +
        "#.........#\r\n" +
        "O.####.##.#\r\n" +
        "#0#1.#..#.C\r\n" +
        "#.#H.##IB.#\r\n" +
        "#.........#\r\n" +
        "#####W#####\r\n" +
        "";
    State.readWorldInit(new Scanner(init));
    String input = "" +
        "199\r\n" +
        "1 3 BLUEBERRIES\r\n" +
        "3 3 NONE\r\n" +
        "0\r\n" +
        "NONE 0\r\n" +
        "3\r\n" +
        "DISH-ICE_CREAM-CHOPPED_STRAWBERRIES 800\r\n" +
        "DISH-CROISSANT-TART-ICE_CREAM-BLUEBERRIES 2300\r\n" +
        "DISH-CROISSANT-TART-BLUEBERRIES 2100\r\n" +
        "";
    state.read(new Scanner(input));

    Planner planner = new Planner();
    State goal = new State();
    goal.agent1.items = Item.NOTHING;
    List<Node> actions = planner.findPlan(state, goal);

    assertThat(actions.size(), is(1));
    assertThat(actions.get(0).action instanceof DropItem, is(true));
  }

  
  @Test
  public void goToDishWithBlueBerries() throws Exception {
    String init = "#####D#S###\r\n" +
        "#.........#\r\n" +
        "O.####.##.#\r\n" +
        "#0#1.#..#.C\r\n" +
        "#.#H.##IB.#\r\n" +
        "#.........#\r\n" +
        "#####W#####\r\n" +
        "";
    State.readWorldInit(new Scanner(init));
    String input = "183\r\n" +
        "7 5 BLUEBERRIES\r\n" +
        "9 2 STRAWBERRIES\r\n" +
        "0\r\n" +
        "NONE 0\r\n" +
        "3\r\n" +
        "DISH-ICE_CREAM-CHOPPED_STRAWBERRIES 784\r\n" +
        "DISH-CROISSANT-TART-ICE_CREAM-BLUEBERRIES 2284\r\n" +
        "DISH-CROISSANT-TART-BLUEBERRIES 2084\r\n" +
        "";
    state.read(new Scanner(input));

    Planner planner = new Planner();
    State goal = new State();
    goal.agent1.items = Item.BLUEBERRIES | Item.DISH;
    List<Node> actions = planner.findPlan(state, goal);

    assertThat(actions.size(), is(1));
    assertThat(actions.get(0).action instanceof UseDishWasher, is(true));
  }

  @Test
  public void createChoppedStrawberries() throws Exception {
    readStandardWorld(state);

    Planner planner = new Planner();
    State goal = new State();
    goal.agent1.items = Item.CHOPPED_STRAWBERRIES;
    List<Node> actions = planner.findPlan(state, goal);

    assertThat(actions.size(), is(2));
    assertThat(actions.get(0).action instanceof UseEquipmentIngredient, is(true));
    assertThat(actions.get(1).action instanceof UseKnife, is(true));
  }

  @Test
  public void createChoppedDough() throws Exception {
    readStandardWorld(state);

    Planner planner = new Planner();
    State goal = new State();
    goal.agent1.items = Item.CHOPPED_DOUGH;
    List<Node> actions = planner.findPlan(state, goal);

    assertThat(lastNode(actions).turn, is(6));
    assertThat(actions.get(0).action instanceof UseEquipmentIngredient, is(true));
    assertThat(actions.get(1).action instanceof UseKnife, is(true));
  }

  private Node lastNode(List<Node> nodes) {
    return nodes.get(nodes.size()-1);
  }

  @Test
  public void createCroissant() throws Exception {
    readStandardWorld(state);

    Planner planner = new Planner();
    State goal = new State();
    goal.agent1.items = Item.CROISSANT;
    List<Node> nodes= planner.findPlan(state, goal);

    assertThat(lastNode(nodes).turn, is(16));
    assertThat(nodes.get(0).action instanceof UseEquipmentIngredient, is(true));
    assertThat(nodes.get(1).action instanceof UseOven, is(true));
  }
  
  @Test
  public void createRawTart() throws Exception {
    //Player.DEBUG_PLANNER = true;
    readStandardWorld(state);

    Planner planner = new Planner();
    State goal = new State();
    goal.agent1.items = Item.RAW_TART;
    List<Node> actions = planner.findPlan(state, goal);

    assertThat(actions.size(), is(3));
    assertThat(actions.get(0).action instanceof UseEquipmentIngredient, is(true));
    assertThat(actions.get(1).action instanceof UseKnife, is(true));
    assertThat(actions.get(2).action instanceof UseEquipmentIngredient, is(true));
  }
  
  @Test
  public void createBlueberriesTart() throws Exception {
    //Player.DEBUG_PLANNER = true;
    readStandardWorld(state);

    Planner planner = new Planner();
    State goal = new State();
    goal.agent1.items = Item.BLUEBERRIES_TART;
    List<Node> nodes = planner.findPlan(state, goal);

    assertThat(lastNode(nodes).turn, is(22));
  }
  
  public static void readStandardWorld(State state) {
    String init = "#####D#S###\r\n" +
        "#.........#\r\n" +
        "O.####.##.#\r\n" +
        "#0#1.#..#.C\r\n" +
        "#.#H.##IB.#\r\n" +
        "#.........#\r\n" +
        "#####W#####\r\n" +
        "";
    State.readWorldInit(new Scanner(init));
    String input = "" +
        "199\r\n" +
        "1 3 NONE\r\n" +
        "3 3 NONE\r\n" +
        "0\r\n" +
        "NONE 0\r\n" +
        "3\r\n" +
        "DISH-ICE_CREAM-CHOPPED_STRAWBERRIES 800\r\n" +
        "DISH-CROISSANT-TART-ICE_CREAM-BLUEBERRIES 2300\r\n" +
        "DISH-CROISSANT-TART-BLUEBERRIES 2100\r\n" +
        "";
    state.read(new Scanner(input));
  }
}
