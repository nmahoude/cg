package stc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class BitLayerTest {

  private BitLayer layer;

  @Before
  public void setup() {
    layer = new BitLayer();
  }

  @Test
  public void setFirstBitOfCol0() throws Exception {
    layer.setCell(0, 0);
    assertThat(layer.getCol(0) , is(1));
  }
  
  @Test
  public void setFirstBitOfCol1() throws Exception {
    layer.setCell(1, 0);
    assertThat(layer.getCol(1) , is(1));
  }
  
  @Test
  public void setFirstBitOfCol2() throws Exception {
    layer.setCell(2, 0);
    assertThat(layer.getCol(2) , is(1));
  }
  
  @Test
  public void setTwelfthBit() throws Exception {
    layer.setCell(0, 11);
    assertThat(layer.getCol(0) , is((int)Math.pow(2, 11)));
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
    NeighborInfo info = new NeighborInfo();
    setLayersFromString(
        "0000000000001",
        "0000000000000",
        "0000000000000",
        "0000000000000",
        "0000000000000",
        "0000000000000"
        );
    
    layer.getNeighbors(info, 0,0);
    
    assertThat(info.count, is(1));
  }

  @Test
  public void neighborsCount_1() throws Exception {
    NeighborInfo info = new NeighborInfo();
    setLayersFromString(
        "000000000001",
        "000000000001",
        "000000000001",
        "000000000001",
        "000000000001",
        "000000000001"
        );
    
    layer.getNeighbors(info, 0,0);
    
    assertThat(info.count, is(6));
  }
  
  @Test
  public void neighborsCount_full() throws Exception {
    NeighborInfo info = new NeighborInfo();
    setLayersFromString(
        "111111111111",
        "111111111111",
        "111111111111",
        "111111111111",
        "111111111111",
        "111111111111"
        );
    
    layer.getNeighbors(info, 0,0);
    
    assertThat(info.count, is(72));
  }
  
  @Test
  public void nextSettedBits_all_and_one() throws Exception {
    setLayerFromString(0, "111111111111");
    int mask = BitLayer.getNextSettedBit(layer.getCol(0), 0);
    assertThat(mask, is(1));
  }

  @Test
  public void nextSettedBits_all_and_6() throws Exception {
    setLayerFromString(0, "111111111111");
    int mask = BitLayer.getNextSettedBit(layer.getCol(0), 6);
    assertThat(mask, is(7));
  }
  
  @Test
  public void nextSettedBits_all_and_11() throws Exception {
    setLayerFromString(0, "111111111111");
    int mask = BitLayer.getNextSettedBit(layer.getCol(0), 11);
    assertThat(mask, is(12));
  }
  
  @Test
  public void nextSettedBits_none_and_0() throws Exception {
    setLayerFromString(0, "000000000000");
    int mask = BitLayer.getNextSettedBit(layer.getCol(0), 0);
    assertThat(mask, is(12));
  }

  @Test
  public void nextSettedBits_random() throws Exception {
    setLayerFromString(0, "100100001101");
    int mask = BitLayer.getNextSettedBit(layer.getCol(0), 3);
    assertThat(mask, is(8));
  }

  @Test
  public void nextSettedBits_0() throws Exception {
    setLayerFromString(0, "100100001101");
    int mask = BitLayer.getNextSettedBit(layer.getCol(0), 0);
    assertThat(mask, is(2));
  }
  
  @Test
  public void nextSettedBits_1() throws Exception {
    setLayerFromString(0, "100100001101");
    int mask = BitLayer.getNextSettedBit(layer.getCol(0), 1);
    assertThat(mask, is(2));
  }
  
  @Test
  public void nextSettedBits_afterLast() throws Exception {
    setLayerFromString(0, "000100001101");
    int mask = BitLayer.getNextSettedBit(layer.getCol(0), 8);
    assertThat(mask, is(12));
  }
  
  @Test
  public void findBitNeighborsForOneRow() throws Exception {
    setLayerFromString(0, "000100011101");
    int mask = BitLayer.getNeighborBitsOnOneRowForPos(layer.getCol(0), 2);
    assertThat(BitLayer.toString(mask), is("000000011100"));
  }
  
  @Test
  public void findBitNeighborsForOneRow_decal() throws Exception {
    setLayerFromString(0, "000000000110");
    int mask = BitLayer.getNeighborBitsOnOneRowForPos(layer.getCol(0), 1);
    assertThat(BitLayer.toString(mask), is("000000000110"));
  }

  @Test
  public void findBitNeighborsForOneRow_single() throws Exception {
    setLayerFromString(0, "000100011101");
    int mask = BitLayer.getNeighborBitsOnOneRowForPos(layer.getCol(0), 0);
    assertThat(BitLayer.toString(mask), is("000000000001"));
  }
  
  @Test
  public void findBitNeighborsForOneRow_all() throws Exception {
    setLayerFromString(0, "111111111111");
    int mask = BitLayer.getNeighborBitsOnOneRowForPos(layer.getCol(0), 0);
    assertThat(BitLayer.toString(mask), is("111111111111"));
  }
  
  @Test
  @Parameters( {
    "000000000000, 111111111111 | 000000000000",
    "111111111111, 000000000000 | 000000000000",
    "111111111111, 111111111111 | 111111111111",
    "000100100000, 111100111111 | 111100111111",
    "000100100000, 001111110000 | 001111110000",
    "000100000000, 101110111111 | 001110000000"
  })
  public void getNeighborsFromIntersection(String inter, String v, String exp) throws Exception {
    int intersection = Integer.parseInt(inter, 2);
    int value = Integer.parseInt(v, 2);
    int expected = Integer.parseInt(exp, 2);
    assertThat(BitLayer.getNeighborsFromIntersection(intersection, value), is (expected));
  }
  
  
  @Test
  public void getMaskFromTo_all() throws Exception {
    int mask = BitLayer.getMaskFromTo(0, 12);
    assertThat(BitLayer.toString(mask), is("111111111111"));
  }
  
  @Test
  public void setAllBits() throws Exception {
    for (int y=0;y<12;y++) {
      layer.setCell(0, y);
    }
    assertThat(layer.getCol(0), is (0b111111111111));
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
  
  @Test
  public void allShiftDown() throws Exception {
    setLayersFromString2(layer,
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010"
        );

    layer.shiftDown();
    
    assertThat(layer.getDebugString(), is(
        "000000\n"+
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
        "010101\n"
        ));
  }
  
  @Test
  public void allShiftUp() throws Exception {
    setLayersFromString2(layer,
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101"
        );

    layer.shiftUp();
    
    assertThat(layer.getDebugString(), is(
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
        "000000\n"
        ));
  }  
  
  @Test
  public void allRightLeft() throws Exception {
    setLayersFromString2(layer,
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101"
        );

    layer.shiftRight();
    
    assertThat(layer.getDebugString(), is(
        "010101\n"+
        "001010\n"+
        "010101\n"+
        "001010\n"+
        "010101\n"+
        "001010\n"+
        "010101\n"+
        "001010\n"+
        "010101\n"+
        "001010\n"+
        "010101\n"+
        "001010\n"
        ));
  }  

  @Test
  public void allShiftLeft() throws Exception {
    setLayersFromString2(layer,
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101",
        "101010",
        "010101"
        );

    layer.shiftLeft();
    
    assertThat(layer.getDebugString(), is(buildDebugString(
        "010100",
        "101010",
        "010100",
        "101010",
        "010100",
        "101010",
        "010100",
        "101010",
        "010100",
        "101010",
        "010100",
        "101010"
        )));
  }  
  
  @Test
  public void skullDiscovery_middle() throws Exception {
    BitLayer color = new BitLayer();
    BitLayer skulls = new BitLayer();
    setLayersFromString2(color,
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "001100",
        "001100",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000"
        );
    setLayersFromString2(skulls,
        "000000",
        "000000",
        "000000",
        "111111",
        "111111",
        "110011",
        "110011",
        "111111",
        "111111",
        "000000",
        "000000",
        "000000"
        );
    
    skulls.removeSkullsFrom(color);
    
    assertThat(skulls.getDebugString(), is(
        buildDebugString(
            "000000",
            "000000",
            "000000",
            "111111",
            "110011",
            "100001",
            "100001",
            "110011",
            "111111",
            "000000",
            "000000",
            "000000"
        )));

  }
  
  @Test
  public void skullDiscovery_corners() throws Exception {
    BitLayer color = new BitLayer();
    BitLayer skulls = new BitLayer();
    setLayersFromString2(color,
        "100001",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "100001"
        );
    setLayersFromString2(skulls,
        "011110",
        "110011",
        "100001",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "100001",
        "110011",
        "011110"
        );
    
    skulls.removeSkullsFrom(color);
    
    assertThat(skulls.getDebugString(), is(
        buildDebugString(
            "001100",
            "010010",
            "100001",
            "000000",
            "000000",
            "000000",
            "000000",
            "000000",
            "000000",
            "100001",
            "010010",
            "001100"
        )));

  }

  @Test
  public void pushFromTopOf_emptyColumn() throws Exception {
    int y = layer.pushFromTopOfColumn(0);
    assertThat(y, is(0));
  }

  @Test
  public void pushFromTopOf_almostFilledColumn() throws Exception {
    setLayersFromString2(layer,
      "001010",
      "110101",
      "101010",
      "010101",
      "101010",
      "010101",
      "101010",
      "010101",
      "101010",
      "010101",
      "101010",
      "010101"
      );
    
    int y = layer.pushFromTopOfColumn(0);
    assertThat(y, is(11));
  }

  @Test
  @Parameters({
    // value     , mask         | expectedResult
    "000000000000, 000000000000 | 000000000000",
    "111111111111, 000000000000 | 000000000000",
    "000000000000, 111111111111 | 000000000000",
    
    "000000000001, 000000000001 | 000000000001",
    "000000000011, 000000000001 | 000000000001",
    "111111111111, 101010101010 | 000000111111",
    "101010101010, 101010101010 | 000000111111",
    "010101010101, 101010101010 | 000000000000",
    "100010001000, 101010101010 | 000000101010",
    "100000000000, 101010101010 | 000000100000",
  })
  public void compress_loop(String strValue, String strMask, String strExpected) throws Exception {
    int value = Integer.parseInt(strValue, 2);
    int mask = Integer.parseInt(strMask, 2);
    int expected = Integer.parseInt(strExpected, 2);
    
    assertThat(Integer.toBinaryString(BitLayer.compress(value, mask)), is (Integer.toBinaryString(expected)));
  }

  @Test
  @Parameters({
    // value     , mask         | expectedResult
    "000000000000, 000000000000 | 000000000000",
    "111111111111, 000000000000 | 000000000000",
    "000000000000, 111111111111 | 000000000000",
    
    "000000000001, 000000000001 | 000000000001",
    "000000000011, 000000000001 | 000000000001",
    "111111111111, 101010101010 | 000000111111",
    "101010101010, 101010101010 | 000000111111",
    "010101010101, 101010101010 | 000000000000",
    "100010001000, 101010101010 | 000000101010",
    "100000000000, 101010101010 | 000000100000",
  })
  public void compress_Optmize(String strValue, String strMask, String strExpected) throws Exception {
    int value = Integer.parseInt(strValue, 2);
    int mask = Integer.parseInt(strMask, 2);
    int expected = Integer.parseInt(strExpected, 2);
    
    int[] mvs = BitLayer.generateMvs(mask);
    assertThat(Integer.toBinaryString(BitLayer.compress(value, mask, mvs)), is (Integer.toBinaryString(expected)));
    
    assertThat(Integer.toBinaryString(BitLayer.compress(value, mask)), is (Integer.toBinaryString(expected)));
  }
  
  // ----------------- utils ------------------------------
  private String buildDebugString(String...rows) {
    String result = "";
    for (int y=0;y<12;y++) {
      result+=rows[y]+"\n";
    }
    return result;
  }

  private void setLayersFromString2(BitLayer layer, String...rows) {
    for (int y=0;y<12;y++) {
      for (int x=0;x<6;x++) {
        if (rows[y].charAt(x) == '1') {
          layer.setCell(x, 11-y);
        } else {
          layer.unsetCell(x, 11-y);
        }
      }
    }
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

}
