import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SolutionTest {

  
  @Test
  public void oneTest() throws Exception {
    Solution.Node n0 = new Solution.Node(0);
    Solution.Node n1 = new Solution.Node(1);
    Solution.Node n2 = new Solution.Node(2);
    Solution.Node n3 = new Solution.Node(3);
    Solution.Node n4 = new Solution.Node(4);
    
    Solution.Edge e0 = new Solution.Edge(n0, n1);
    Solution.Edge e1 = new Solution.Edge(n1, n2);
    Solution.Edge e2 = new Solution.Edge(n2, n3);
    Solution.Edge e3 = new Solution.Edge(n2, n4);
    
    List<Solution.EdgeRunner> runners = new ArrayList<>();
    Solution.EdgeRunner r1 = new Solution.EdgeRunner();
    r1.currentNode = n0;
    runners.add(r1);
    Solution.EdgeRunner r2 = new Solution.EdgeRunner();
    r2.currentNode = n3;
    runners.add(r2);
    Solution.EdgeRunner r3 = new Solution.EdgeRunner();
    r3.currentNode = n4;
    runners.add(r3);
    
    boolean stillOneActive = false;
    Solution.EdgeRunner lastOne = null;
    do {
      stillOneActive = false;
      for (Solution.EdgeRunner runner : runners) {
        if (runner.active) {
          lastOne = runner;
          runner.advance();
          if (runner.active) {
            stillOneActive = true;
          }
        }
      }
    } while( stillOneActive);
    
    assertThat(lastOne.length, is(2));
  }
}
