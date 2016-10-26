package hypersonic;

import java.util.ArrayList;
import java.util.List;

import hypersonic.utils.P;

public class Board {
  int rot[][] = {
      {1, 0},
      {0, 1},
      {-1,0},
      {0,-1}
  };
  
  int cells[][] = new int[13][11];
  List<P> boxes = new ArrayList<>();
  List<Bomb> bombs =  new ArrayList<>();
  
  P player1 = new P();

  private int destructedBox;
  
  public void init() {
    boxes.clear();
    bombs.clear();
    destructedBox = 0;
  }
  public void init(int y, String row) {
    for (int x=0;x<13;x++) {
      char value = row.charAt(x);
      cells[x][y] = value;
      if (value >= '0' && value <='3') {
        addBox(x,y);
      }
    }
  }
  public void addBox(int x, int y) {
    boxes.add(new P(x,y));
  }
  
  public P findClosestBox() {
    int bestDist = Integer.MAX_VALUE;
    P closest = null;
    for (P p : boxes) {
      int sqDist = p.distTo(player1);
      if (sqDist < bestDist) {
        bestDist = sqDist;
        closest = p;
      }
    }
    return closest;
  }
  
  public void simulate() {
    // simulate one turn
    for (Bomb b : bombs) {
      b.timer-=1;
      if (b.timer == 0) {
        b.explode(this);
      }
    }
  }
  public void addBomb(Bomb bomb) {
    bombs.add(bomb);
  }
  public void explode(P p, int range) {
    for (int r = 0;r<4;r++) {
      int dx = rot[r][0];
      int dy = rot[r][1];
      
      for (int d=0;d<range;d++) {
        int x = p.x + dx;
        int y = p.y + dy;
        if (isInRange(x,y)) {
          if (cells[x][y] >= '0' && cells[x][y] <=  '3') {
            destructedBox++;
            cells[x][y] = '.';
          }
          if (cells[x][y] == 'b') {
            
          }
        }
      }
    }
  }
  private boolean isInRange(int x, int y) {
    if (x < 0 || x > 12) { return false;}
    if (y<0 || y > 10) { return false;}
    return true;
  }
}
