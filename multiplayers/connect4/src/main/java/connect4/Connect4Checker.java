package connect4;

public class Connect4Checker {
  static long connect4Masks[][] = new long[9*7][100];
  static int connect4MasksFE[] = new int[9*7];
  
  
  static {
    // prepare the masks to detects lines from a given position
    int dx[] = new int[] {1, 0, 1, 1};
    int dy[] = new int[] {0, 1, 1, -1};
    
    
    
    for (int y=0;y<7;y++) {
      for (int x=0;x<9;x++) {
        int index = x*7+y;
        
        for (int dd=0;dd<4;dd++) {
          for (int start=-3;start<=3;start++) {
            
            long mask = 0L;
            for (int d=0;d<4;d++) {
              int px = x + dx[dd] * (start+d);
              int py = y + dy[dd] * (start+d);
              
              if (px < 0 || px > 8 || py < 0 || py > 6) {
                mask = 0L;
                break;
              } else {
                mask |= 1L << (7*px + py);
              }
            }
            
            if (mask != 0) {
              connect4Masks[index][connect4MasksFE[index]++] = mask;
            }
            
          }
        }
      }
    }
    
  }
  
  public static boolean is4Connected(long cells, int posx, int posy) {
    int index = posx*7+posy;
    for (int i=0;i<connect4MasksFE[index];i++) {
      long bits = connect4Masks[index][i];
      if ((cells & bits) == bits) return true;
    }
    
    return false;
  }
  
  public static boolean old_is4Connected(long cells, int posx, int posy) {
    int connect4;
    
    // check col
    connect4 = 0;
    for (int d=-3;d<4;d++) {
      int px = posx;
      int py = posy+d;
      if (py < 0) continue;
      if (py > 6) break;

      long mask = 1L << (7*px+py);
      
      if ((cells & mask) != 0) {
        connect4++; 
        if (connect4 == 4) {
          return true;
        }
      } else {
        connect4 = 0;
      }
    }
    
    // check row
    connect4 = 0;
    for (int d=-3;d<4;d++) {
      int px = posx+d;
      int py = posy;
      if (px < 0) continue;
      if (px > 8) break;
      
      long mask = 1L << (7*px+py);
      if ((cells & mask) != 0) {
        connect4++; 
        if (connect4 == 4) {
          return true;
        }
      } else {
        connect4 = 0;
      }
    }
    // diagonal upright
    connect4 = 0;
    for (int d=-3;d<4;d++) {
      int px = posx+d;
      int py = posy+d;
      if (px < 0 || py < 0) continue;
      if (px > 8 || py > 6) break;
      
      long mask = 1L << (7*px+py);
      if ((cells & mask) != 0) {
        connect4++; 
        if (connect4 == 4) {
          return true;
        }
      } else {
        connect4 = 0;
      }
    }
    
    connect4 = 0;
    for (int d=-3;d<4;d++) {
      int px = posx+d;
      int py = posy-d;
      if (px < 0 || py > 6) continue;
      if (px > 8 || py < 0) break;
      
      long mask = 1L << (7*px+py);
      if ((cells & mask) != 0) {
        connect4++; 
        if (connect4 == 4) {
          return true;
        }
      } else {
        connect4 = 0;
      }
    }
    
    return false;
  }
  
  
}
