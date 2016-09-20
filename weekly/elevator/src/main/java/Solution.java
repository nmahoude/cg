import java.util.*;
import java.io.*;
import java.math.*;

class Solution {
  static ElevatorStep[] visitedPositions;
  static int n;
  static int a;
  static int b;
  static int k;
  static int target;
  static ElevatorStep bestStep = null;
  static boolean bottomReached = true;
  static boolean topReached = true;
  
    static class ElevatorStep {
      int step ;
      int currentFloor;
      
      public ElevatorStep(int floor, int step) {
        currentFloor = floor;
        this.step = step;
      }
      
      void simulate() {
        if (currentFloor > n) { 
          if (topReached) {
            return;
          }
          topReached = true;
          currentFloor = n; 
        }
        if (currentFloor < 0) { 
          if (bottomReached) {
            return;
          }
          bottomReached = true;
          currentFloor = 0; 
        }
        if (currentFloor == target) {
          if (bestStep == null) {
            bestStep = this;
          } else if (bestStep.step > this.step) {
            bestStep = this;
          }
          return;
        }
        ElevatorStep oldElevatorStep = visitedPositions[currentFloor];
        if (oldElevatorStep != null) {
          if (oldElevatorStep.step > this.step) {
            visitedPositions[currentFloor] = this;
          } else {
            return; // stop better has go through here
          }
        } else {
          visitedPositions[currentFloor] = this;
        }
        new ElevatorStep(currentFloor+a, step+1).simulate();
        new ElevatorStep(currentFloor-b, step+1).simulate();
      }
    }
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        a = in.nextInt();
        b = in.nextInt();
        k = in.nextInt();
        target = in.nextInt();

        visitedPositions = new ElevatorStep[n+1];
        
        System.err.println("init(top="+n+") : "+k+"->"+target+", +"+a+", -"+b);
        int firstStep = 0;
        new ElevatorStep(k, 0).simulate();
        if (bestStep == null) {
          System.out.println("IMPOSSIBLE");
        } else {
          System.out.println(bestStep.step);
        }
    }
}