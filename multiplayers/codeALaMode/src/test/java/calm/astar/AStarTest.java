package calm.astar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import calm.Item;
import calm.ItemMask;
import calm.P;
import calm.State;

public class AStarTest {

  @Test
  public void madeChoppedStrawberries() throws Exception {
    State state = new State();
    state.me.hands.mask = 0; // ItemMask.DISH | ItemMask.CHOPPED_STRAWBERRIES;
    Item goal = new Item(P.INVALID);
    goal.mask = ItemMask.CHOPPED_STRAWBERRIES;

    AStar astar = new AStar();
    AStarNode finalOrder = astar.find(state, goal);

    Assertions.assertThat(finalOrder).isEqualTo(Action.USE_SW);
  }

  @Test
  public void madeCroissant() throws Exception {
    State state = new State();
    state.me.hands.mask = 0; // ItemMask.DISH | ItemMask.CHOPPED_STRAWBERRIES;
    Item goal = new Item(P.INVALID);
    goal.mask = ItemMask.CROISSANT;

    AStar astar = new AStar();
    AStarNode finalOrder = astar.find(state, goal);

    Assertions.assertThat(finalOrder).isEqualTo(Action.USE_SW);
  }

  @Test
  public void makePlateAndCroissantFromDough() throws Exception {
    State state = new State();
    state.me.hands.mask = ItemMask.DOUGH;
    Item goal = new Item(P.INVALID);
    goal.mask = ItemMask.CROISSANT | ItemMask.DISH;

    AStar astar = new AStar();
    AStarNode finalOrder = astar.find(state, goal);

    Assertions.assertThat(finalOrder).isEqualTo(Action.USE_OVEN_IN);
  }

  @Test
  public void makePlateAndCroissantFromBlueBerries() throws Exception {
    State state = new State();
    state.me.hands.mask = ItemMask.BLUEBERRIES;
    Item goal = new Item(P.INVALID);
    goal.mask = ItemMask.CROISSANT | ItemMask.DISH;

    AStar astar = new AStar();
    AStarNode finalOrder = astar.find(state, goal);

    Assertions.assertThat(finalOrder).isEqualTo(Action.USE_OVEN_IN);
  }
  
  @Test
  public void makeFullDesert() throws Exception {
    State state = new State();
    state.me.hands.mask = 0;
    Item goal = new Item(P.INVALID);
    goal.mask = ItemMask.BLUEBERRIES_TART | ItemMask.CROISSANT | ItemMask.DISH | ItemMask.BLUEBERRIES;

    AStar astar = new AStar();
    AStarNode finalOrder = astar.find(state, goal);

    Assertions.assertThat(finalOrder).isEqualTo(Action.USE_OVEN_IN);
  }
}
