import java.util.List;
import java.util.Scanner;

import trigonometry.Point;
import trigonometry.Vector;

public class Player {
  static Land land = new Land();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    Ship ship = new Ship();
                                 // surface of Mars.
    int surfaceN = in.nextInt(); // the number of points used to draw the
    land.points = new Point[surfaceN];
    int prevX = -1;
    int prevY = -1;
    for (int i = 0; i < surfaceN; i++) {
      int landX = in.nextInt(); 
      int landY = in.nextInt();
      land.points[i] = new Point(landX, landY);
      if (prevY == landY) {
        ship.setTarget(prevX, landX, prevY);
      }
      prevY = landY;
      prevX = landX;
    }

    // game loop
    while (true) {
      int X = in.nextInt();
      int Y = in.nextInt();
      int hSpeed = in.nextInt(); // the horizontal speed (in m/s), can be
                                 // negative.
      int vSpeed = in.nextInt(); // the vertical speed (in m/s), can be
                                 // negative.
      int fuel = in.nextInt(); // the quantity of remaining fuel in liters.
      int rotate = in.nextInt(); // the rotation angle in degrees (-90 to 90).
      int power = in.nextInt(); // the thrust power (0 to 4).

      ship.position = new Point(X, Y);
      ship.speed = new Vector(hSpeed, vSpeed);
      
      if (ship.isOverTarget()) {
        System.err.println("Over Target: "+X+","+Y);
        ship.slowToLand();
      } else {
        // need to rotate to go in correct direction
        ship.goToTarget();
      }
    }
  }

  private static int getDistanceFromTraj(List<Point> traj) {
    Point lastPoint = null;
    int distance = 0;
    for (Point p : traj) {
      if (lastPoint == null) {
        lastPoint = p;
        continue;
      }
      distance+=p.sub(lastPoint).length();
      lastPoint = p;
    }
    return distance;
  }

  static class Ship {
    private static final int MAX_HORIZONTAL_SPEED = 20;
    private static final double GRAVITY = 3.711;
    static Vector gravity = new Vector(0, -3.711);
    Vector speed;
    Vector rotation;
    Point position;
    private int prevX;
    private int landX;
    private int prevY;
    
    void update(int thrust) {
      position = position.add(speed.add(rotation.dot(thrust).add(gravity)));
    }

    public void slowToLand() {
      int rot = 0;
      if (Math.abs(speed.vx) > MAX_HORIZONTAL_SPEED) {
        rot = getAngleToSlow();
        System.out.println(""+rot+" 4");
      } else if (speed.vx != 0) {
        rot = getAngleToSlow();
        System.out.println(""+rot+" 4");
      } else {
        if (Math.abs(speed.vy) > 39) {
          System.out.println("0 4");
        } else if (position.y > prevY) {
          System.out.println("0 2");
        } else {
          System.out.println("0 4");
        }
      }
    }

    private int getAngleToSlow() {
      double s = speed.length();
      double rot = Math.toDegrees(speed.vx  / s);
      if (Math.abs(rot) < 5) rot = 0;
      return (int)rot;
    }

    public void goToTarget() {
      int highest = getNextHigh();
      System.err.println("Next hight is "+highest);
      if (highest+350 > position.y) {
        System.err.println("oops under");
        if (Math.abs(speed.vx) > 40 || Math.abs(speed.vy) > 40) {
          System.err.println("need to go up");
          int rot = getAngleToTargetWithUp();
          System.out.println(""+rot+" 4");
        } else {
          System.err.println("up up up");
          System.out.println("0 4");
        }
      } else {
        if (Math.abs(speed.vx) > 40 || Math.abs(speed.vy) > 40) {
          int rot = getAngleToSlow();
          System.out.println(""+rot+" 4");
        } else if (position.y < prevY) {
          System.out.println("0 4");
        } else {
          System.out.println(""+getAngleToTarget()+" 4");
        }
      }
    }

    public int distToTarget() {
      return (int)Math.abs(position.x - (prevX+landX)/2);
    }

    private int getNextHigh() {
      if (position.x < prevX) {
        int nextSegment1Index = -1;
        //1. find the current segment of ship
        for (int i=0;i<land.points.length;i++) {
          if (land.points[i].x > position.x) {
            nextSegment1Index = i;
            break;
          }
        }
        //System.err.println("nextSegment : "+nextSegment1Index + "with high: "+land.points[nextSegment1Index].y);
        // 2. check if there is higher ground from here to landing pad
        int highest = prevY;
        for (int i=nextSegment1Index;i<land.points.length;i++) {
          if (land.points[i].x > prevX) {
            break;
          }
          if (land.points[i].y > highest) {
            highest = (int) land.points[i].y;
          }
        }
        return highest;
      } else {
        int nextSegment1Index = -1;
        //1. find the current segment of ship
        for (int i=land.points.length-1;i>=0;i--) {
          if (land.points[i].x < position.x) {
            nextSegment1Index = i;
            break;
          }
        }
        // 2. check if there is higher ground from here to landing pad
        int highest = prevY;
        for (int i=nextSegment1Index;i>=0;i--) {
          if (land.points[i].x > prevX) {
            break;
          }
          if (land.points[i].y > position.y && land.points[i].y > highest) {
            highest = (int) land.points[i].y;
          }
        }
        return highest;
      }
    }

    public boolean isOverTarget() {
      return position.x > prevX && position.x < landX;
    }

    public int getAngleToTarget() {
      int angle = (int) Math.toDegrees(Math.acos(GRAVITY / 4.0));
      if (position.x < prevX)
          return -angle;
      else if (landX < position.x)
          return angle;
      else
          return 0;
    }
    public int getAngleToTargetWithUp() {
      int angle = (int) Math.toDegrees(Math.acos(GRAVITY / 4.0));
      angle /=8;
      if (position.x < prevX)
          return -angle;
      else if (landX < position.x)
          return angle;
      else
          return 0;
    }
    
    public void setTarget(int prevX, int landX, int prevY) {
      this.prevX = prevX;
      this.landX = landX;
      this.prevY = prevY;
      
      System.err.println("Target landing is between "+prevX+" and "+landX+" at alltitude "+prevY);
      
    } 
  }
}