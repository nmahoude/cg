package stc2;

import stc.P;

public class BitBoard {
  public static final int TOP_ROW_MASK = 0b100000000000;
  public static final int TOP_2_ROWS_MASK = 0b110000000000;

  public static final int SKULL_LAYER = 0;
  public static final int BLUE_LAYER = 1;
  public static final int GREEN_LAYER = 2;
  public static final int PINK_LAYER = 3;
  public static final int RED_LAYER = 4;
  public static final int YELLOW_LAYER = 5;
  public static final int COMPLETE_LAYER_MASK = 6;
  public static final char[] cellTable = { '☠', '1', '2', '3', '4', '5' };

  BitLayer layers[] = new BitLayer[7]; 
  {
    for (int i=0;i<=6;i++) {
      layers[i] = new BitLayer();
    }
  }
  
  public void copyFrom(BitBoard bboard) {
    layers[0].copyFrom(bboard.layers[0]);
    layers[1].copyFrom(bboard.layers[1]);
    layers[2].copyFrom(bboard.layers[2]);
    layers[3].copyFrom(bboard.layers[3]);
    layers[4].copyFrom(bboard.layers[4]);
    layers[5].copyFrom(bboard.layers[5]);
    layers[6].copyFrom(bboard.layers[6]);
  }
  
  public void updateRow(int y, String row) {
    for (int x = 0; x < 6; x++) {
      char value = row.charAt(x);
      if (isColor(value)) {
        int layer = value-'0';
        layers[layer].setCell(x,11-y);
      } else if (isSkull(value)) {
        layers[0].setCell(x,11-y);
      } else if (isEmpty(value)) { 
        // nothing
      }
    }
  }

  public void buildCompleteLayerMask() {
    layers[COMPLETE_LAYER_MASK].clear();
    layers[COMPLETE_LAYER_MASK].merge(layers[SKULL_LAYER]);
    layers[COMPLETE_LAYER_MASK].merge(layers[BLUE_LAYER]);
    layers[COMPLETE_LAYER_MASK].merge(layers[GREEN_LAYER]);
    layers[COMPLETE_LAYER_MASK].merge(layers[PINK_LAYER]);
    layers[COMPLETE_LAYER_MASK].merge(layers[RED_LAYER]);
    layers[COMPLETE_LAYER_MASK].merge(layers[YELLOW_LAYER]);
  }
  
  public boolean isEmpty(char value) {
    return value == '.';
  }

  public boolean isSkull(char value) {
    return value == '0' || value == '@' || value == '☠';
  }

  public boolean isColor(char value) {
    return value >= '1' && value <= '5';
  }

  public String getDebugString() {
    char[] output = new char[7*12];
    for (int y=0;y<12;y++) {
      output[6+7*y] = '\n';
    }
    for (int x = 0; x < 6; x++) {
      for (int y = 0; y < 12; y++) {
        output[x + 7 * (11-y)] = '.';
        for (int l = 0; l < 6; l++) {
          BitLayer layer = layers[l];
          if (layer.isCellSetAt(x, y)) {
            output[x + 7 * (11-y)] = cellTable[l];
          }
        }
      }
    }
    return new String(output);
  }

  public String getJunitString() {
    int decal = 1;
    int rowDecal = 10;
    char[] output = new char[10*12];
    for (int y=0;y<12;y++) {
      output[0+rowDecal*y] = '"';
      output[7+rowDecal*y] = '"';
      output[8+rowDecal*y] = ',';
      output[9+rowDecal*y] = '\n';
    }
    for (int x = 0; x < 6; x++) {
      for (int y = 0; y < 12; y++) {
        output[decal+x + rowDecal * (11-y)] = '.';
        for (int l = 0; l < 6; l++) {
          BitLayer layer = layers[l];
          if (layer.isCellSetAt(x, y)) {
            output[decal+x + rowDecal * (11-y)] = cellTable[l];
          }
        }
      }
    }
    return new String(output);
  }

  public P pushBall(int color, int column) {
    int y;
    y = layers[COMPLETE_LAYER_MASK].pushFromTopOfColumn(column);
    if (y < 12) {
      layers[color].setCell(column, y);
      return P.get(column, y);
    } else {
      return null;
    }
  }

  public boolean canPutBalls(int rotation, int baseColumn) {
    BitLayer allLayers = layers[COMPLETE_LAYER_MASK];
    
    if ((baseColumn == 0 && rotation == 2) || (baseColumn == 5 && rotation == 0)) {
      return false;
    }
    
    boolean result = false;
    switch (rotation) {
    case 0:
      result = (allLayers.getCol(baseColumn) & TOP_ROW_MASK) == 0
          && (allLayers.getCol(baseColumn+1) & TOP_ROW_MASK) == 0;
//      if (!result) {
//        System.err.println("rot, pos : "+rotation+","+baseColumn);
//        System.err.println("Mask stopped "+allLayers.getCol(baseColumn)+" or "+allLayers.getCol(baseColumn+1));
//        System.err.println(layers[1].getDebugString());
//      }
      break;
    case 1:
    case 3:
      result = (allLayers.getCol(baseColumn) & TOP_2_ROWS_MASK) == 0;
//      if (!result) {
//        System.err.println("rot, pos : "+rotation+","+baseColumn);
//        System.err.println("Mask stopped "+allLayers.getCol(baseColumn)+" or "+allLayers.getCol(baseColumn));
//        System.err.println(layers[1].getDebugString());
//      }
      break;
    case 2:
      result = (allLayers.getCol(baseColumn) & TOP_ROW_MASK) == 0
      && (allLayers.getCol(baseColumn-1) & TOP_ROW_MASK) == 0;
//      if (!result) {
//        System.err.println("rot, pos : "+rotation+","+baseColumn);
//        System.err.println("Mask stopped "+allLayers.getCol(baseColumn)+" or "+allLayers.getCol(baseColumn-1));
//        System.err.println(layers[1].getDebugString());
//      }
      break;
    }
    return result;
  }

  public void update() {
    buildCompleteLayerMask();
    int mask;
    int mvs[] = new int[5];
    
    for (int col =0;col<6;col++) {
      mask = layers[COMPLETE_LAYER_MASK].getCol(col);
      BitLayer.generateMvs(mask, mvs);

      for (int l=0;l<7;l++) {
        layers[l].setCol(col, BitLayer.compress(layers[l].getCol(col), mask, mvs));
      }
    }
  }

  public int getColHeight(int column) {
    int col = layers[COMPLETE_LAYER_MASK].getCol(column);
    return Integer.bitCount(col);
  }
  
  public int getTotalColumnHeight() {
    return Long.bitCount(layers[COMPLETE_LAYER_MASK].col1)
        +Long.bitCount(layers[COMPLETE_LAYER_MASK].col2);
  }
  
  public int getMinimalColumnHeight() {
    int mergeCols =  
        layers[COMPLETE_LAYER_MASK].getCol(0)
        & layers[COMPLETE_LAYER_MASK].getCol(1)
        & layers[COMPLETE_LAYER_MASK].getCol(2)
        & layers[COMPLETE_LAYER_MASK].getCol(3)
        & layers[COMPLETE_LAYER_MASK].getCol(4)
        & layers[COMPLETE_LAYER_MASK].getCol(5);
    return Integer.bitCount(mergeCols);
  }
  
  public int getColorFromLayers(long[] mask) {
    long mask0 = mask[0];
    long mask1 = mask[1];

    if ((layers[1].isMaskSetted(mask0, mask1)))
      return 1;
    if ((layers[2].isMaskSetted(mask0, mask1)))
      return 2;
    if ((layers[3].isMaskSetted(mask0, mask1)))
      return 3;
    if ((layers[4].isMaskSetted(mask0, mask1)))
      return 4;
    if ((layers[5].isMaskSetted(mask0, mask1)))
      return 5;
    return 0;
  }

  public void clear() {
    layers[0].clear();
    layers[1].clear();
    layers[2].clear();
    layers[3].clear();
    layers[4].clear();
    layers[5].clear();
    layers[6].clear();
  }

}
