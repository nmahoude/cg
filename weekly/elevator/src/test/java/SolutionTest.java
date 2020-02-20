import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SolutionTest {

  @Test
  public void upAndDown() {
    // init(top=11) : 10->6, +2, -3
    Solution.n = 11;
    Solution.a = 2;
    Solution.b = 3;
    Solution.k = 10;
    Solution.target = 6;
    Solution.visitedPositions = new Solution.ElevatorStep[Solution.n+1];
    Solution.bottomReached = false;
    Solution.topReached = false;
    
    new Solution.ElevatorStep(Solution.k, 0).simulate();
    
    assertThat(Solution.bestStep.step, is(3));
  }
  
  @Test
  public void big() {
    // init(top=1999) : 2->1, +1000, -999
    Solution.n = 1999;
    Solution.a = 1000;
    Solution.b = 999;
    Solution.k = 2;
    Solution.target = 1;
    Solution.visitedPositions = new Solution.ElevatorStep[Solution.n+1];
    Solution.bottomReached = false;
    Solution.topReached = false;
    
    new Solution.ElevatorStep(Solution.k, 0).simulate();
    
    assertThat(Solution.bestStep.step, is(4));
  }
  
}
