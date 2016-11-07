package stc2;

public class BitBoard {
  public static final int SKULL_LAYER = 0;
  public static final int BLUE_LAYER = 1;
  public static final int GREEN_LAYER = 2;
  public static final int PINK_LAYER = 3;
  public static final int RED_LAYER = 4;
  public static final int YELLOW_LAYER = 5;

  BitLayer layers[] = new BitLayer[6];
  
  public void updateRow(int y, String row) {
    for (int x = 0; x < 6; x++) {
      char value = row.charAt(x);
      if (isColor(value)) {
        int layer = value-'0';
        layers[layer].setCell(x,y);
      } else if (isSkull(value)) {
        layers[0].setCell(x,y);
      } else if (isEmpty(value)) { 
      
      }
    }
  }

  private boolean isEmpty(char value) {
    return value == '.';
  }

  private boolean isSkull(char value) {
    return value == '0' || value == '@' || value == '☠';
  }

  private boolean isColor(char value) {
    return value >= '1' && value <= '5';
  }

}
