package connect4;

public class Connect4Checker {
  
  
  public static boolean is4Connected(long cells, int posx, int posy) {
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
