import java.util.*;

import java.io.*;
import java.math.*;

class Player {
  static class P {
    int x;
    int y;

    P() {
    }

    P(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  static class Rectangle {
    int x1, y1;
    int x2, y2;

    Rectangle(int x1, int y1, int x2, int y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }

    P middle() {
      return new P( x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2 );
    }

    void debug() {
      System.err.println("x1,y1: " + x1 + "," + y1);
      System.err.println("  x2,y2: " + x2 + "," + y2);
    }

    public void copyFrom(Rectangle rect) {
      this.x1 = rect.x1;
      this.x2 = rect.x2;
      this.y1 = rect.y1;
      this.y2 = rect.y2;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + x1;
      result = prime * result + x2;
      result = prime * result + y1;
      result = prime * result + y2;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Rectangle other = (Rectangle) obj;
      if (x1 != other.x1)
        return false;
      if (x2 != other.x2)
        return false;
      if (y1 != other.y1)
        return false;
      if (y2 != other.y2)
        return false;
      return true;
    }
    
  }

  enum Action {
    CUT_VERTICAL, 
    CUT_HORIZONTAL, 
    REPROJECT,
    FORGET,
  }
  static Action nextAction = Action.CUT_VERTICAL;
  
  static final String UNKNOWN = "UNKNWON";
  static final String COLDER = "COLDER";
  static final String WARMER = "WARMER";
  static final String SAME = "SAME";

  boolean foundX = false;

  P lastPos = new P();
  P projectedPos = new P();
  P currentPos = new P();
  Rectangle rect;
  private int width;
  private int height;

  public Player(int width, int height) {
    this.width = width;
    this.height = height;
    rect = new Rectangle(0, 0, width-1, height-1);
  }
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int W = in.nextInt(); // width of the building.
    int H = in.nextInt(); // height of the building.
    int N = in.nextInt(); // maximum number of turns before game over.

    Player player = new Player(W, H);
    player.setInitPos(in.nextInt(), in.nextInt());

    // game loop
    while (true) {
      String bombTemp = in.next(); 
      System.err.println("bombTemp: " + bombTemp);

      player.calculateNextAction(bombTemp);

      System.out.println(player.projectedPos.x+" "+player.projectedPos.y);
    }
  }

  void setInitPos(int x, int y) {
    currentPos.x = x;
    currentPos.y = y;
  }

  void updatePos() {
    currentPos.x = projectedPos.x;
    currentPos.y = projectedPos.y;
  }
  public void calculateNextAction(String bombTemp) {
    if (COLDER.equals(bombTemp)) {
      // reduce rect
      rect.y1 = (lastPos.y+currentPos.y+1) / 2;
    } else if (WARMER.equals(bombTemp)) {
      rect.y2 = (lastPos.y+currentPos.y+1) / 2;
    }
    rect.debug();

    projectedPos.x = currentPos.x;
    projectedPos.y = currentPos.y;
    if (UNKNOWN.equals(bombTemp)) {
      projectedPos.y = 0;
    } else if (COLDER.equals(bombTemp)) {
      projectedPos.y = rect.middle().y;
    } else if (WARMER.equals(bombTemp)) {
      projectedPos.y = rect.middle().y;
    }
    

    System.err.println("Batman(curr): " + currentPos.x + "," + currentPos.y);
    System.err.println("Batman(proj): " + projectedPos.x + "," + projectedPos.y);
    System.out.println("" + projectedPos.x + " " + projectedPos.y);

    lastPos.x = currentPos.x;
    lastPos.y = currentPos.y;
  }
}