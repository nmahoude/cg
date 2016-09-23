package codersStrikeBack.v1;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    
    Scanner in;
    int x;
    int y;
    int nextCheckpointX; // x position of the next check point
    int nextCheckpointY; // y position of the next check point
    int nextCheckpointDist; // distance to the next checkpoint
    int nextCheckpointAngle; // angle between your pod orientation and the direction of the next checkpoint
    int opponentX;
    int opponentY;
    
    List<Point> checkpoints = new ArrayList<Point>();
    boolean allCheckpointsDiscovered = false;
    
    int boostLeft = 1;
    public static void main(String args[]) {
        new Player().play();
    }
   
   boolean knownCheckpoint(Point p) {
    for (Point pp : checkpoints) {
        if (pp.equals(p)) {
            return true;
        }
    }
    return false;
   }
   
   void updateCheckPoints() {
    if (allCheckpointsDiscovered) {
        return;
    }
    Point p = new Point(nextCheckpointX, nextCheckpointY);
    if (!knownCheckpoint(p)) {
        //System.err.println("Add checkpoint : "+p.print());
        checkpoints.add(p);
     } else {
        if (checkpoints.size() > 1 && p.equals(checkpoints.get(0))) {
            //System.err.println("All checkpoint discovered");
            allCheckpointsDiscovered = true;
        }
     }
   }
   
   void updateGameInput() {
        x = in.nextInt();
        y = in.nextInt();
        nextCheckpointX = in.nextInt(); // x position of the next check point
        nextCheckpointY = in.nextInt(); // y position of the next check point
        nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
        nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next checkpoint
        opponentX = in.nextInt();
        opponentY = in.nextInt();

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
            double ratio = (1.0*(slowdownDistance-nextCheckpointDist)) / slowdownDistance;
            ratio = Math.pow(ratio, 1);
            thrust = 75+(int)(25*ratio);    
        }
        return ""+thrust;
    }
   void play() {
       in = new Scanner(System.in);

        // game loop
        int lastDistance = -1;
        boolean boostDone = false;
        while (true) {
            updateGameInput();

            String result = "";
            if (shouldBoost()) {
                result = "BOOST";
                boostLeft--;
            } else if (shouldFullBreak()) {
                result = "0";
            } else {
                result = calculateThrust();
            }
            
            System.out.println(nextCheckpointX + " " + nextCheckpointY + " "+result);
        }
   }
   
   class Point {
        int x;
        int y;
        Point(int x, int y) {
            this.x = x; this.y =y;
        }
        String print() {
            return "("+x+","+y+")";
        }
        boolean equals(Point p) {
            return p.x == x && p.y == y;
        }
        // hashcode?
    };

}