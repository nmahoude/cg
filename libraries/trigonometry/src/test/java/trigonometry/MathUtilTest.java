package trigonometry;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MathUtilTest {
  @Test
  public void secondDegree() throws Exception {
    double[] results = MathUtil.resolve2ndDegree(1, 3, -4);
    assertThat(results.length, is(2));
    assertThat(results[0], is(1.0));
    assertThat(results[1], is(-4.0));
  }
}
