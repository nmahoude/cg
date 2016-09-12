package codersStrikeBack.v2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
  Scanner in;

  private Point currentPosition;
  private Point nextCheckpoint;
  private Point opponentPosition;

  int nextCheckpointDist; // distance to the next checkpoint
  int nextCheckpointAngle; // angle between your pod orientation and the direction of the next checkpoint
  boolean allCheckpointsDiscovered = false;
  List<Point> checkpoints = new ArrayList<Point>();
  int boostLeft = 1;


  public static void main(String args[]) {
    new Player().play();
  }

  boolean isKnownCheckpoint(Point p) {
    for (Point checkpoint : checkpoints) {
      if (checkpoint.equals(p)) {
        return true;
      }
    }
    return false;
  }

  void updateCheckPoints() {
    if (allCheckpointsDiscovered) {
      return;
    }
    if (!isKnownCheckpoint(nextCheckpoint)) {
      checkpoints.add(nextCheckpoint);
    } else {
      if (checkpoints.size() > 1 && nextCheckpoint.equals(checkpoints.get(0))) {
        allCheckpointsDiscovered = true;
      }
    }
  }

  void readGameInput() {
    currentPosition = new Point(in.nextInt(), in.nextInt());
    nextCheckpoint = new Point(in.nextInt(), in.nextInt());
    nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
    nextCheckpointAngle = in.nextInt(); // angle between your pod orientation
    opponentPosition = new Point(in.nextInt(), in.nextInt());

    updateCheckPoints();
  }

  boolean shouldBoost() {
    return boostLeft > 0
        && (nextCheckpointDist > 4000 && Math.abs(nextCheckpointAngle) < 10);
  }

  boolean shouldFullBreak() {
    return (nextCheckpointAngle > 120 || nextCheckpointAngle < -120);
  }

  String calculateThrust() {
    int thrust = 100;
    int slowdownDistance = 1000;
    if (nextCheckpointDist - slowdownDistance < 0) {
      double ratio = (1.0 * (slowdownDistance - nextCheckpointDist)) / slowdownDistance;
      ratio = Math.pow(ratio, 1);
      thrust = 75 + (int) (25 * ratio);
    }
    return "" + thrust;
  }

  void play() {
    in = new Scanner(System.in);

    // game loop
    while (true) {
      readGameInput();

      
      String result = "";
      if (shouldBoost()) {
        result = "BOOST";
        boostLeft--;
      } else if (shouldFullBreak()) {
        result = "0";
      } else {
        result = calculateThrust();
      }

      System.out.println((int)(nextCheckpoint.x) + " " + (int)(nextCheckpoint.y) + " " + result);
    }
  }
}