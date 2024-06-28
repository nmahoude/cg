package spring2022.ag;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fast.read.FastReader;
import spring2022.State;

public class AGEvaluatorTest {

  
  State state = new State();
  AGEvaluator evaluator = new AGEvaluator();
  LightState current = new LightState();

  @BeforeEach 
  public void setup() {
    state = new State();
    evaluator = new AGEvaluator();
    current = new LightState();

    State.DEBUG_INPUTS = false;

    // always in topleft corner
    state.readGlobal(FastReader.fromString("0 0 3 "));
  }

  @Test
  void oppAttackerFartherIsBetter() throws Exception {
    state.read(FastReader.fromString("""
        ^3 264 3 322
        ^4
        ^0 1 945 673 0 0 -1 -1 -1 -1 -1
        ^1 1 2492 1259 0 0 -1 -1 -1 -1 -1
        ^2 1 6010 4504 0 0 -1 -1 -1 -1 -1
        ^3 2 3878 4897 0 0 -1 -1 -1 -1 -1
        """.replace("^", "") ));

    current.createFrom(state);
    current.oppHeroes[0].pos.set(0, 0);
    double eval1 = evaluator.evaluate(current);
    
    current.oppHeroes[0].pos.set(1000, 1000);
    double eval2 = evaluator.evaluate(current);
    
    
    assertThat(eval2 > eval1).isTrue();
    
  }
  
  
}
