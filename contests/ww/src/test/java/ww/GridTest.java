package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class GridTest {

  public static class FloodFill {
    Grid grid;

    @Before
    public void setup() {
      grid = new Grid();
    }

    @Test
    public void onePointMap() throws Exception {
      long position = TU.toLong(
          "10000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");

      long toFill = TU.toLong(
          "10000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");
      
      long mask = grid.getFloodFillMask(position, toFill);
      
      assertThat(Long.bitCount(mask), is(1));
    }
    
    @Test
    public void directionXMap() throws Exception {
      long position = TU.toLong(
          "10000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");

      long toFill = TU.toLong(
          "11111110",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");
      
      long mask = grid.getFloodFillMask(position, toFill);
      
      assertThat(Long.bitCount(mask), is(7));
    }
    
    @Test
    public void directionMinusXMap() throws Exception {
      long position = TU.toLong(
          "00000010",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");

      long toFill = TU.toLong(
          "11111110",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");
      
      long mask = grid.getFloodFillMask(position, toFill);
      
      assertThat(Long.bitCount(mask), is(7));
    }
    
    @Test
    public void directionYMap() throws Exception {
      long position = TU.toLong(
          "10000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");

      long toFill = TU.toLong(
          "10000000",
          "10000000",
          "10000000",
          "10000000",
          "10000000",
          "10000000",
          "10000000",
          "00000000");
      
      long mask = grid.getFloodFillMask(position, toFill);
      
      assertThat(Long.bitCount(mask), is(7));
    }
    
    @Test
    public void randomeIsle() throws Exception {
      long position = TU.toLong(
          "00000000",
          "00000000",
          "00000000",
          "00010000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");

      long toFill = TU.toLong(
          "10000000",
          "10000000",
          "10011100",
          "10011100",
          "10011000",
          "10010000",
          "10000000",
          "00000000");
      
      long mask = grid.getFloodFillMask(position, toFill);
      
      assertThat(Long.bitCount(mask), is(9));
    }
    
    @Test
    public void diagonal() throws Exception {
      long position = TU.toLong(
          "10000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");

      long toFill = TU.toLong(
          "10000000",
          "01000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");
      
      long mask = grid.getFloodFillMask(position, toFill);
      
      assertThat(Long.bitCount(mask), is(2));
    }
    
    @Test
    public void snail() throws Exception {
      long position = TU.toLong(
          "00000000",
          "00000000",
          "00000000",
          "00010000",
          "00000000",
          "00000000",
          "00000000",
          "00000000");

      long toFill = TU.toLong(
          "11111100",
          "00000100",
          "01110100",
          "01010100",
          "01000100",
          "01111100",
          "00000000",
          "00000000");
      
      long mask = grid.getFloodFillMask(position, toFill);
      
      assertThat(Long.bitCount(mask), is(21));
    }
  }
}
