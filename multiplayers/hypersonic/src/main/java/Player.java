import java.util.*;
import java.io.*;
import java.math.*;

class Player {
  enum EntityType {
    PLAYER,
    BOMB
  }
  static class P {
    final int x;
    final int y;
    public P(int x, int y) {
      super();
      this.x = x;
      this.y = y;
    }
    double distance(P p) {
      return Math.pow(p.x-x,2)+Math.pow(p.y-y,2);
    }
  }
  static class Bomb {
    public Bomb(int x, int  y) {
      pos = new P(x,y);
    }

    P pos;
    
  }
  static class Board {
    static final int EMPTY = 0;
    static final int BOX = 9;
    
    int[][] grid = new int[13][11];
    List<Bomb> bombs = new ArrayList<>();
    
    public void addRow(int rowIndex, String row) {
      for (int i=0;i<row.length();i++) {
        char c = row.charAt(i);
        if(c == '.') {
          grid[i][rowIndex] = EMPTY;
        } else if (c=='0') {
          grid[i][rowIndex] = BOX;
          bombs.add(new Bomb(i, rowIndex));
        }
      }
    }
    Bomb findClosestBomb(P point) {
      Bomb nearest = null;
      double minDistance = 10000000;
      for (Bomb bomb : bombs) {
        double distance = bomb.pos.distance(point);
        if (distance < minDistance) {
          minDistance = distance;
          nearest = bomb;
        }
      }
      return nearest;
    }
    public void removeBomb(Bomb nearest) {
      bombs.remove(nearest);
    }
    public void reset() {
      bombs.clear();
    }
  }

  static class Game {
    Player players[];
  }

  int posx;
  int posy;
  
  public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        int me = in.nextInt();
        in.nextLine();

        Board board = new Board();
        
        P myPos = new P(0,0);
        Bomb lastBomb = null;
        // game loop
        while (true) {
          board.reset();
            for (int y = 0; y < height; y++) {
                String row = in.nextLine();
                board.addRow(y, row);
            }
            int entities = in.nextInt();
            for (int i = 0; i < entities; i++) {
                int entityType = in.nextInt();
                int owner = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
               
                if (owner == me) {
                  myPos = new P(x,y);
                }
                int param1 = in.nextInt();
                int param2 = in.nextInt();
            }
            in.nextLine();

            Bomb nearest =  board.findClosestBomb(myPos);
            board.removeBomb(nearest);
            
            double distance = nearest.pos.distance(myPos);
            if (distance > 1.1) {
              System.out.println("MOVE "+nearest.pos.x+" "+nearest.pos.y);  
            } else {
              System.out.println("BOMB "+nearest.pos.x+" "+nearest.pos.y);  
            }
            lastBomb = nearest;
        }
    }
}