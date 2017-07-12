package ww.paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.GameState;
import ww.TU;

import ww.paths.AccessibleCellsCalculator;

public class AccessibleCellsCalculatorTest {
  GameState state;

  @Before
  public void setup() {
    state = new GameState();

  }
  
  @Test
  public void aLotOfAccessibleWithANarrowPath() {
    state.size = 7;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state,
        "...2...",
        "..033..",
        ".30443.",
        "0034334",
        ".33443.",
        "..344..",
        "...3...");
    TU.setAgent(state, 0, 5, 3);
    TU.setAgent(state, 1, 5, 2);
    TU.setAgent(state, 2, -1, -1);
    TU.setAgent(state, 3, -1, -1);
    
    int count = AccessibleCellsCalculator.count(state, state.agents[1]);
    
    assertThat(count, is(9));
  }
  
}
