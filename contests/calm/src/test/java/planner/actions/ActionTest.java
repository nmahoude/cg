package planner.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import calmBronze.Item;
import planner.actionplanner.PlannerTest;
import planner.state.State;

public class ActionTest {

  private State state;

  @Before
  public void setup() {
    state = new State();

  }
  
  @Test
  public void createCroissant() throws Exception {
    PlannerTest.readStandardWorld(state);
    
    List<Action> actions = Arrays.asList(
        new UseEquipmentIngredient(State.equipmentDough, Item.DOUGH),
        new UseOven(),
        new Wait(1),
        new Wait(1),
        new Wait(1),
        new Wait(1),
        new Wait(1),
        new Wait(1),
        new Wait(1),
        new Wait(1),
        new Wait(1),
        new Wait(1),
        new UseOven()
        );
    for (Action a : actions) {
      if (!a.prerequisites(state)) throw new RuntimeException("Interdit de faire "+a.describe());
      a.applyEffect(state);
    }
    assertThat(state.agent1.items, is(Item.CROISSANT));
  }
}
