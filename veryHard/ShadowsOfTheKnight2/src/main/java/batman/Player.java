package batman;

import java.util.Scanner;

public class Player {
  static Rectangle initRectangle = new Rectangle();
  static Rectangle rectangle = new Rectangle();
  static Point batman = new Point();
  
  public static void main(String args[]) {
    @SuppressWarnings("resource")
    Scanner in = new Scanner(System.in);
    int W = in.nextInt(); // width of the building.
    int H = in.nextInt(); // height of the building.
    @SuppressWarnings("unused")
    int N = in.nextInt(); // maximum number of turns before game over.
    int X0 = in.nextInt();
    int Y0 = in.nextInt();

    initRectangle.p1 = new Point(W, H); // remember the initial rectangle
    rectangle.p1 = new Point(W, H); // work rectangle, at first, it's the same as initial
    batman = new Point(X0, Y0);
    
    // game loop
    @SuppressWarnings("unused")
    String unknownState = in.next();
    while (true) {
      int x =0 ;
      int y=batman.y;
      System.err.println(rectangle.debug());
      if (rectangle.width() > 1) {
        x = rectangle.p1.x - (batman.x - rectangle.p0.x);
      }
      System.out.println(""+x+" "+y);
      batman.x = x;
      batman.y = y;
      
      String bomb = in.next(); // (COLDER, WARMER, SAME or UNKNOWN)
      if ("COLDER".equals(bomb)) {
        rectangle.p1.x = (rectangle.p1.x - rectangle.p0.x) / 2;
      }
      if ("WARMER".equals(bomb)) {
        rectangle.p0.x = (rectangle.p1.x - rectangle.p0.x) / 2;
      }
      if ("SAME".equals(bomb)) {
        rectangle.p1.x = (rectangle.p1.x - rectangle.p0.x) / 2;
        rectangle.p0.x = rectangle.p1.x;
      }

    }
  }

  public static Point get_X_SymmetryProjection(Point position, Rectangle rectangle) {
    int newPositionX = rectangle.p1.x+rectangle.p0.x - position.x;
    return new Point(newPositionX,position.y);
  }

  public static Point reduction(String temp, Point oldBatman, Point newBatman, Rectangle r) {
    if ("WARMER".equals(temp)) {
      
    }
    
    return null;
  }
  
  
}