package othello;

public class PosMask {
  private static final int WIDTH = Pos.WIDTH;
  private static final int HEIGHT = Pos.HEIGHT;
  private static final int MAX = WIDTH*HEIGHT;
  
  static long[] positionMasks = new long[WIDTH*HEIGHT]; 
  static long[] positionInvMasks = new long[WIDTH*HEIGHT]; 
  
  static long[] neighbors8Masks = new long[WIDTH*HEIGHT]; 
  
  static {
    for (int i=0;i<MAX;i++) {
        positionMasks[i] = 1L << i;
        positionInvMasks[i] = ~(1L << i);
    }
    
    for (int i=0;i<MAX;i++) {
      Pos pos = Pos.from(i);
      long mask = 0L;
      for (Pos n : pos.neighbors8) {
        mask |= positionMasks[n.offset];
      }
      neighbors8Masks[i] = mask;
    }    
    
  }
  
}
