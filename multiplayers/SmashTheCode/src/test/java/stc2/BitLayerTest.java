package stc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BitLayerTest {

  private BitLayer layer;

  @Before
  public void setup() {
    layer = new BitLayer();
  }

  @Test
  public void setFirstBit() throws Exception {
    layer.setCell(0, 0);
    assertThat(layer.cols[0] , is(1));
  }

  @Test
  public void setTwelfthBit() throws Exception {
    layer.setCell(0, 11);
    assertThat(layer.cols[0] , is((int)Math.pow(2, 11)));
  }

  @Test
  public void slideRightGlobal_1() throws Exception {
    setLayerFromString(0, "101010101010");

    layer.slideRight(0, 1);
    
    assertThat(layer.getColAsString(0), is ("010101010101"));
  }
  
  @Test
  public void slideRightGlobal_2() throws Exception {
    setLayerFromString(0, "101010101010");

    layer.slideRight(0, 2);
    
    assertThat(layer.getColAsString(0), is ("001010101010"));
  }

  @Test
  public void slideRightGlobalLarger() throws Exception {
    setLayerFromString(0, "101010101010");
    
    layer.slideRight(0, 11);
    
    assertThat(layer.getColAsString(0), is ("000000000001"));
  }
  
  @Test
  public void slideRightFromTo() throws Exception {
    setLayerFromString(0, "101010101010");
    
    layer.slideRightFromTo(0, 5, 2);
    
    assertThat(layer.getColAsString(0), is ("000101010110"));
  }

  @Test
  public void slideRightFromToFullGap() throws Exception {
    setLayerFromString(0, "101010101010");
    
    layer.slideRightFromTo(0, 11, 0);
    
    assertThat(layer.getColAsString(0), is ("000000000001"));
  }

  @Test
  public void neighborsCount_0() throws Exception {
    setLayersFromString(
        "0000000000000",
        "0000000000000",
        "0000000000000",
        "0000000000000",
        "0000000000000",
        "0000000000000"
        );
    
    assertThat(layer.countNeighbors(0,0), is(0));
  }

  @Test
  public void neighborsCount_1() throws Exception {
    setLayersFromString(
        "000000000001",
        "000000000001",
        "000000000001",
        "000000000001",
        "000000000001",
        "000000000001"
        );
    
    assertThat(layer.countNeighbors(0,0), is(6));
  }
  
  @Test
  public void surroundingSetBits_all() throws Exception {
    setLayerFromString(0, "111111111111");
    
    int mask = layer.getSurroundingSetBitsMask(layer.cols[0], 0);
    
    assertThat(layer.toString(mask), is("111111111111"));
  }
  
  private void setLayersFromString(String... string) {
    for (int x=0;x<6;x++) {
      setLayerFromString(x, string[x]);
    }
  }
  private void setLayerFromString(int x, String string) {
    for (int y=0;y<12;y++) {
      if (string.charAt(y) == '1') {
        layer.setCell(x, 11-y);
      } else {
        layer.unsetCell(x, 11-y);
      }
    }
  }

  @Test
  public void setAllBits() throws Exception {
    for (int y=0;y<12;y++) {
      layer.setCell(0, y);
    }
    assertThat(layer.cols[0], is ((int)Math.pow(2, 11+1)-1));
  }

  @Test
  public void debugLayer() throws Exception {
    for (int x=0;x<6;x++) {
      for (int y=0;y<12;y++) {
        if ((x+y) % 2 == 0) {
          layer.setCell(x, y);
        }
      }
    }
    String debugString = layer.getDebugString();
    
    assertThat(debugString, is(
        "010101\n"+
        "101010\n"+
        "010101\n"+
        "101010\n"+
        "010101\n"+
        "101010\n"+
        "010101\n"+
        "101010\n"+
        "010101\n"+
        "101010\n"+
        "010101\n"+
        "101010\n"
        ));
  }
}
