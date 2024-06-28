package spring2023.ais;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spring2023.State;
import spring2023.StateTest;
import spring2023.map.Map;

public class BeaconsOptimizerTest {
  State state;
  int[] beacons;
  
  @BeforeEach
  public void init() {
    beacons = new int[Map.MAX_CELLS];
        
    state = StateTest.buildFromInput("""
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
^ 0 0 21 21 42 42 42 42 0 0 10000 000000 0 0 15 15 0 0 0 0 
^ 0 0 0 0 0 0 0 0 33 33 *** END
        
        """ );
    
  }
  
  @Test
  void noOptimization() throws Exception {
    state.cells[12].myAnts = 4;
    beacons[12] = 3;
    beacons[10] = 1;
    
    
    new BeaconsOptimizer().think(state, beacons);
    
    assertThat(beacons[12]).isEqualTo(3);
    assertThat(beacons[10]).isEqualTo(1);
    
  }
  

  @Test
  void noOptimization2() throws Exception {
    state.cells[12].myAnts = 4;

    beacons[12] = 2;
    beacons[10] = 2;
    
    
    new BeaconsOptimizer().think(state, beacons);
    
    assertThat(beacons[12]).isEqualTo(2);
    assertThat(beacons[10]).isEqualTo(2);
  }
  
  @Test
  void simpleChainWithNoOptimization() throws Exception {
    state.cells[12].myAnts = 4;
    state.cells[10].myAnts = 0;
    state.cells[8].myAnts = 0;

    beacons[12] = 2;
    beacons[10] = 1;
    beacons[8]  = 1;
    
    
    new BeaconsOptimizer().think(state, beacons);
    
    assertInto(new int[]{12, 10, 8}, new int[][]{{2,0,2}, {2,1,1}});
  }

  @Test
  void simpleChainOptimization() throws Exception {
    state.cells[12].myAnts = 3;
    state.cells[10].myAnts = 1;

    beacons[12] = 2;
    beacons[10] = 1;
    beacons[8]  = 1;
    
    
    new BeaconsOptimizer().think(state, beacons);
    
    assertInto(new int[]{12, 10, 8}, new int[][]{{2,0,2}});
  }
  
  @Test
  void simpleChainWithComplexOptimization() throws Exception {
    state.cells[12].myAnts = 3;
    state.cells[10].myAnts = 2;
    state.cells[8].myAnts = 1;

    beacons[12] = 2;
    beacons[10] = 2;
    beacons[8]  = 2;
    
    
    new BeaconsOptimizer().think(state, beacons);
    
    assertInto(new int[]{12, 10, 8}, new int[][]{{2,1,3}});
  }
  
  private void assertInto(int[] cells, int[][] solutions ) {
    String beaconsStr = "[";
    for (int c=0;c<cells.length;c++) {
        beaconsStr += ""+beacons[cells[c]]+";";
    }    
    beaconsStr+="]";
    
    String solutionsStr = "";
    for (int s=0;s<solutions.length;s++) {
      boolean correct = true;
      solutionsStr+="[";
      for (int c=0;c<cells.length;c++) {
        solutionsStr+=solutions[s][c]+";";
        if (beacons[cells[c]] != solutions[s][c]) correct = false;
      }
      solutionsStr+="],";
      if (correct) {
        return;
      }
    }
    Assertions.fail("no solution for "+Arrays.toString(cells)+" in "+solutionsStr+" real beacons : "+beaconsStr);
  }

}
