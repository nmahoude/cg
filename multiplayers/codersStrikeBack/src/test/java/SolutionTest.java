import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class SolutionTest {

  @Test
  public void test7_14() {
    Solution s = new Solution();
    assertThat(s.find(7,14), is(2));
  }

  @Test
  public void test6_15() {
    Solution s = new Solution();
    assertThat(s.find(6,15), is(6));
  }
  
  @Test
  public void test15_88() {
    Solution s = new Solution();
    assertThat(s.find(15,88), is(20));
  }
  
  @Test
  public void test_bug() {
    Solution s = new Solution();
    assertThat(s.find(13512,3411598), is(20));
  }
  
}
