package ww2;

import org.junit.Test;

public class GridTest {

  @Test
  public void debugLayer() throws Exception {
    long mask = 0b1L << 3 +8*4;
    Grid.debugMask(mask);
  }
}
