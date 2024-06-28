package spring2023.simulation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import spring2023.State;
import spring2023.StateTest;

public class SimulationTest {
  TrueSimulation sim = new TrueSimulation();
  
  @Nested 
  class Map1 {
    @Test
    void twoBeaconsFromBaseAtStart() throws Exception {
      State state = map1();
      
      state.cells[12].beacon = 100;
      state.cells[10].beacon = 100;
      
      sim.simulate(state);
      
      
      assertThat(state.cells[12].myAnts).isEqualTo(5);
      assertThat(state.cells[10].myAnts).isEqualTo(5);
      
    }
  
    @Test
    void threeBeaconsWithDispatchedAnts() throws Exception {
      State state = map1();
      
      state.cells[12].myAnts = 5;
      state.cells[10].myAnts = 5;
      
      
      state.cells[12].beacon = 100;
      state.cells[10].beacon = 100;
      state.cells[8].beacon = 1;
      
      sim.simulate(state);
      
      
      assertThat(state.cells[12].myAnts).isEqualTo(5);
      assertThat(state.cells[10].myAnts).isEqualTo(4);
      assertThat(state.cells[8].myAnts).isEqualTo(1);
      
    }
    
    @Test
    void moreComplicated() throws Exception {
      State state = map1();
      
      state.cells[3].beacon = 1;
      state.cells[4].beacon = 1;
      state.cells[6].beacon = 1;
      state.cells[8].beacon = 1;
      state.cells[10].beacon = 1;
      state.cells[12].beacon = 1;
      state.cells[14].beacon = 1;
      state.cells[15].beacon = 1;
      state.cells[22].beacon = 1;
      state.cells[30].beacon = 1;
      
      sim.simulate(state);
      
      
      assertThat(state.cells[12].myAnts).isEqualTo(1);
      assertThat(state.cells[10].myAnts).isEqualTo(4);
      assertThat(state.cells[14].myAnts).isEqualTo(5);
      
    }
  }
  
  @Nested
  class Map2 {
    State state;
  
    @BeforeEach
    public void init() {
      state = map2();
    }
    
    @Test
    void simpleBeaconsTakesFourTurns() throws Exception {
      state.cells[20].beacon = 7; // base
      state.cells[22].beacon = 1;
      state.cells[24].beacon = 1;
      state.cells[26].beacon = 1;
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(7);
      assertThat(state.cells[22].myAnts).isEqualTo(3);
      assertThat(state.cells[24].myAnts).isEqualTo(0);
      assertThat(state.cells[26].myAnts).isEqualTo(0);
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(7);
      assertThat(state.cells[22].myAnts).isEqualTo(1);
      assertThat(state.cells[24].myAnts).isEqualTo(2);
      assertThat(state.cells[26].myAnts).isEqualTo(0);
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(7);
      assertThat(state.cells[22].myAnts).isEqualTo(1);
      assertThat(state.cells[24].myAnts).isEqualTo(1);
      assertThat(state.cells[26].myAnts).isEqualTo(1);
      
      
      state.cells[20].beacon = 6; // base
      state.cells[22].beacon = 1;
      state.cells[24].beacon = 1;
      state.cells[26].beacon = 1;
      state.cells[36].beacon = 1;

      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(6);
      assertThat(state.cells[22].myAnts).isEqualTo(2);
      assertThat(state.cells[24].myAnts).isEqualTo(1);
      assertThat(state.cells[26].myAnts).isEqualTo(1);
      assertThat(state.cells[36].myAnts).isEqualTo(0);
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(6);
      assertThat(state.cells[22].myAnts).isEqualTo(1);
      assertThat(state.cells[24].myAnts).isEqualTo(2);
      assertThat(state.cells[26].myAnts).isEqualTo(1);
      assertThat(state.cells[36].myAnts).isEqualTo(0);
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(6);
      assertThat(state.cells[22].myAnts).isEqualTo(1);
      assertThat(state.cells[24].myAnts).isEqualTo(1);
      assertThat(state.cells[26].myAnts).isEqualTo(2);
      assertThat(state.cells[36].myAnts).isEqualTo(0);
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(6);
      assertThat(state.cells[22].myAnts).isEqualTo(1);
      assertThat(state.cells[24].myAnts).isEqualTo(1);
      assertThat(state.cells[26].myAnts).isEqualTo(1);
      assertThat(state.cells[36].myAnts).isEqualTo(1);
    }
    
    
    
    @Test
    void optimalChainedBeacons() throws Exception {
      
      state.cells[20].beacon = 7; // base
      state.cells[22].beacon = 1;
      state.cells[24].beacon = 1;
      state.cells[26].beacon = 1;
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(7);
      assertThat(state.cells[22].myAnts).isEqualTo(3);
      assertThat(state.cells[24].myAnts).isEqualTo(0);
      assertThat(state.cells[26].myAnts).isEqualTo(0);
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(7);
      assertThat(state.cells[22].myAnts).isEqualTo(1);
      assertThat(state.cells[24].myAnts).isEqualTo(2);
      assertThat(state.cells[26].myAnts).isEqualTo(0);
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(7);
      assertThat(state.cells[22].myAnts).isEqualTo(1);
      assertThat(state.cells[24].myAnts).isEqualTo(1);
      assertThat(state.cells[26].myAnts).isEqualTo(1);
      
      
      state.cells[20].beacon = 6; // base
      state.cells[22].beacon = 0;
      state.cells[24].beacon = 0;
      state.cells[26].beacon = 0;
      state.cells[36].beacon = 4;
      
      sim.simulate(state);
      assertThat(state.cells[20].myAnts).isEqualTo(6);
      assertThat(state.cells[22].myAnts).isEqualTo(1);
      assertThat(state.cells[24].myAnts).isEqualTo(1);
      assertThat(state.cells[26].myAnts).isEqualTo(1);
      assertThat(state.cells[36].myAnts).isEqualTo(1);
    }
  
  }
  
  
  private State map1() {
    State state = StateTest.buildFromInput("""
        *** INIT ***
^31
^ 2 0 -1 -1 1 -1 -1 2 
^ 0 0 -1 9 11 13 -1 0 0 0 14 -1 0 -1 10 12 1 0 -1 15 5 -1 14 22 1 0 -1 13 21 -1 16 6 2 0 15 17 7 -1 -1 3 2 0 -1 -1 4 16 18 8 2 0 17 -1 -1 9 -1 5 2 0 10 -1 6 18 -1 -1 0 0 7 -1 -1 11 1 -1 0 0 12 2 -1 8 -1 -1 
^ 0 0 9 -1 -1 19 13 1 0 0 20 14 2 10 -1 -1 0 0 1 11 19 21 4 -1 0 0 22 3 -1 2 12 20 1 0 25 27 17 5 3 -1 1 0 6 4 -1 26 28 18 0 0 27 -1 -1 7 5 15 0 0 8 6 16 28 -1 -1 0 0 11 -1 -1 -1 21 13 0 0 -1 22 14 12 -1 -1 
^ 0 0 13 19 -1 29 -1 4 0 0 30 -1 3 14 20 -1 0 0 -1 -1 25 -1 30 -1 0 0 -1 29 -1 -1 -1 26 0 0 -1 -1 27 15 -1 23 0 0 16 -1 24 -1 -1 28 0 0 -1 -1 -1 17 15 25 0 0 18 16 26 -1 -1 -1 1 0 21 -1 -1 -1 24 -1 1 0 -1 23 -1 22 -1 -1 
^ 
^1
^ 12 11 
*** OPTIONAL ***
^1
*** TURN
^ 0 0 

^ 49 
^ 0 0 21 21 42 42 42 42 0 0 10000 1000000 0 0 15 15 0 0 0 0 
^ 0 0 0 0 0 0 0 0 33 33 *** END
        """ );
    return state;
  }


private State map2() {
  State state = StateTest.buildFromInput("""
*** INIT ***
^61
^ 0 0 1 3 -1 2 4 -1 
^ 0 0 5 -1 3 0 -1 14 0 0 0 -1 13 6 -1 4 0 0 -1 7 9 -1 0 1 0 0 -1 0 2 -1 8 10 1 0 15 17 -1 1 14 28 1 0 2 13 27 16 18 -1 2 0 -1 19 21 9 3 -1 2 0 10 4 -1 -1 20 22 2 0 7 21 23 11 -1 3 2 0 12 -1 4 8 22 24 
^ 0 0 9 23 25 -1 13 -1 0 0 -1 14 -1 10 24 26 0 0 -1 11 -1 27 6 2 0 0 28 5 1 -1 12 -1 0 0 -1 29 17 5 28 40 0 0 6 27 39 -1 30 18 1 0 29 31 -1 -1 5 15 1 0 -1 6 16 30 32 -1 0 0 33 -1 -1 21 7 -1 0 0 22 8 -1 34 -1 -1 
^ 0 0 19 -1 -1 23 9 7 0 0 24 10 8 20 -1 -1 0 0 21 -1 -1 25 11 9 0 0 26 12 10 22 -1 -1 0 0 23 -1 -1 35 -1 11 0 0 36 -1 12 24 -1 -1 0 0 13 -1 37 39 16 6 0 0 40 15 5 14 -1 38 0 0 43 45 31 17 15 -1 0 0 18 16 -1 44 46 32 
^ 1 0 45 -1 33 -1 17 29 1 0 -1 18 30 46 -1 34 0 0 -1 -1 -1 19 -1 31 0 0 20 -1 32 -1 -1 -1 0 0 25 -1 -1 47 37 -1 0 0 48 38 -1 26 -1 -1 2 0 -1 35 47 -1 39 27 2 0 -1 40 28 -1 36 48 0 0 27 37 -1 49 -1 16 0 0 50 -1 15 28 38 -1 
^ 0 0 51 -1 43 -1 50 60 0 0 -1 49 59 52 -1 44 0 0 -1 53 45 29 -1 41 0 0 30 -1 42 -1 54 46 0 0 53 55 -1 31 29 43 0 0 32 30 44 54 56 -1 0 0 35 -1 -1 57 -1 37 0 0 58 -1 38 36 -1 -1 0 0 39 -1 -1 59 42 -1 0 0 60 41 -1 40 -1 -1 
^ 1 0 -1 -1 -1 41 60 -1 1 0 42 59 -1 -1 -1 -1 0 0 -1 -1 55 45 43 -1 0 0 46 44 -1 -1 -1 56 0 0 -1 -1 -1 -1 45 53 0 0 -1 46 54 -1 -1 -1 0 0 47 -1 -1 -1 -1 -1 0 0 -1 -1 -1 48 -1 -1 0 0 49 -1 -1 -1 52 42 0 0 -1 51 41 50 -1 -1 
^ 
^1
^ 20 19 
*** OPTIONAL ***
^1
*** TURN
^ 0 0 
^ 0 
^ 0 0 0 0 36 36 11 11 18 18 0 0 0 0 0 0 12 12 10000 1000000 
^ 0 0 0 0 0 0 0 0 0 0 18 18 0 0 0 0 55 55 0 0 
^ 0 0 0 0 0 0 0 0 0 0 19 19 0 0 0 0 0 0 0 0 
^ 
*** END
      """ );
  return state;
}
}
