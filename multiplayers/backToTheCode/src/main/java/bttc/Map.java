package bttc;

import java.util.Scanner;

public class Map {
  public static int LAYER_P0 = 0;
  public static int LAYER_P1 = 1;
  public static int LAYER_P2 = 2;
  public static int LAYER_P3 = 3;
  public static int LAYER_TREATED = 4;
  public static int LAYER_ALL = 5;
  public static int LAYER_GOOD = 6;
  public static int LAYER_BAD = 7;
  
  public static int MAX_LAYERS = 8;
  
  long board[] = new long[MAX_LAYERS * 20]; // 5 board(p , p1,p2, p3, all) * 35 x 2 

  long rectangle[] = new long[20];
  
  
  public void copy(Map model) {
    // TODO cache
    for (int i=0;i<20;i++) {
      for (int p=0;p<MAX_LAYERS;p++) {
        board[20 * p +i] = model.board[20 * p + i];
      }
    }
  }
  
  public int fillForPoints() {
    clearLayer(LAYER_GOOD);
    clearLayer(LAYER_BAD);
    
    // fill traited layer with all player cells
    int score = 0;
    for (int y=0;y<20;y++) {
      for (int x=0;x<35;x++) {
        // TODO optimize here check if there is still holes in the line ?
        if ((board[20 * LAYER_ALL + y] & (1L << (63-x))) == 0) {
          int tmpScore = fill(x,y);
          if (tmpScore > 0) {
            score+=tmpScore;
          } else {
            copyLayer(LAYER_TREATED, LAYER_BAD);
            clearLayer(LAYER_TREATED);
          }
        }
      }
    }
    return score;
  }

  private void copyLayer(int from, int to) {
    for (int y=0;y<20;y++) {
      board[20 * to + y] = board[20 * from + y];
    }
  }

  private void clearLayer(int layer) {
    for (int y=0;y<20;y++) {
      board[20 * layer + y] = 0;
    }
  }
  
  int[][] dirs = { {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, { 1, 0}, {1, 1} };
  public int fill(int x, int y) {
    int score = 0;
    if ((x)<0 || (x)>=35 || (y) <0 || (y)>=20) {
      return -1;
    }

    int cell = getPlayerAt(x, y);
    boolean bad = false;
    if (cell == -1) {
      move(LAYER_TREATED, x, y);
      score +=1;
      for (int[] dir : dirs) {
        int tmpScore = fill(x + dir[0], y + dir[1]);
        if (tmpScore == -1) {
          bad = true;
        } else {
          score += tmpScore;
        }
      }
    } else if (cell != 0 && cell != LAYER_TREATED) {
      return -1;
    }
    if (bad) { 
      return -1;
    } else {
      return score;
    }
  }
  
  int getPlayerAt(int x, int y) {
    long mask = 1L << (63-x);
    if ((board[20 * 0 + y] & mask) != 0) return 0;
    if ((board[20 * 1 + y] & mask) != 0) return 1;
    if ((board[20 * 2 + y] & mask) != 0) return 2;
    if ((board[20 * 3 + y] & mask) != 0) return 3;
    if ((board[20 * LAYER_TREATED + y] & mask) != 0) return LAYER_TREATED;
    if ((board[20 * LAYER_BAD + y] & mask) != 0) return LAYER_BAD;
    
    return -1;
  }

  public void read(Scanner in) {
    for (int y = 0 ; y < 20; y++) {
      for (int l=0;l<MAX_LAYERS;l++) {
        board[20 * l + y] = 0;
      }
    }    
    for (int y = 0 ; y < 20; y++) {
      String line = in.next();
      for (int x= 0;x<35;x++) {
        char value = line.charAt(x);
        if (value == '.') continue;
        move(value-'0', x, y);
      }
    }
  }

  public void move(int player, int x, int y) {
    long mask = 1L << (63-x);
    board[20 * player + y] |= mask;
    board[20 * LAYER_ALL + y] |= mask;
  }
  
  public void draw() {
    String mask = "                                                                ";
    System.err.println("Map : ");
    for (int y= 0;y<20;y++) {
      String str = Long.toBinaryString((long) board[20 * LAYER_ALL + y]);
      str = str.replace('0', ' ');
      System.err.println("|"+mask.substring(0 , mask.length()-str.length())+str+"|");
    }
  }
  
  public boolean hasEnemyIn(int x1, int x2, int y1, int y2) {
    buildRectangle(x1, x2, y1, y2);
    for (int y=0;y<20;y++) {
      if ((board[20 * 1 + y] & rectangle[y]) != 0) return true;
      if ((board[20 * 2 + y] & rectangle[y]) != 0) return true;
      if ((board[20 * 3 + y] & rectangle[y]) != 0) return true;
    }
    
    return false;
  }

  private void buildRectangle(int x1, int x2, int y1, int y2) {
    //TODO optimize : precache all values ?
    for (int y=y1;y<=y2;y++) {
      long value = 0L;
      for (int x=x1;x<=x2;x++) {
        value |= (1L << (63-x));
      }
      rectangle[y] = value;
    }
  }

  public P findNearestEmptyCell(P currentPos) {
//    double baryX = currentPos.x;
//    double baryY = currentPos.y;
//    for (int y=0;y<20;y++) {
//      for (int x=0;x<35;x++) {
//        if (getPlayerAt(x, y) == -1) {
//          if (x != baryX) {
//            baryX += -0.1 * Math.signum(x-baryX);
//          }
//          if (y != baryY) {
//            baryY += -0.1 * Math.signum(y-baryY);
//          }
//        }
//      }
//    }
//    
//    P baryCenter = new P((int)baryX, (int)baryY);
    
    P baryCenter = new P(currentPos.x, currentPos.y);
    P bestPos = new P(currentPos.x, currentPos.y);
    int closestDist = Integer.MAX_VALUE;
    for (int y=0;y<20;y++) {
      for (int x=0;x<35;x++) {
        if (getPlayerAt(x, y) == -1) {
          int distance = baryCenter.manhattanDistance(x,y);
          if (distance < closestDist) {
            bestPos.x =x;
            bestPos.y = y;
          }
        }
      }
    }
    return bestPos;
  }
}
