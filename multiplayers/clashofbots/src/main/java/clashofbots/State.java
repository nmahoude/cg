package clashofbots;

import java.util.ArrayList;
import java.util.List;

import fast.read.FastReader;

public class State {
  
  List<Robot> robots = new ArrayList<>();
  
  // int[][] grid = new int[14][14];
  
  public int numberOfRobots;

  public void read(FastReader in) {
    clearGrid();

    this.numberOfRobots = in.nextInt();
    System.err.println("^ "+this.numberOfRobots);
    for (int i = 0; i < numberOfRobots; i++) {
      Robot robot = new Robot();
      robots.add(robot);
      for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
          int value = in.nextInt();
          robot.grid[x][y] = value;
          // if (value != 0) System.err.println(""+x+","+y+"="+value);
        }
      }
    }

    
    printGrid();
  }

  private void printGrid() {
//    for (int y = 0; y < 14; y++) {
//      for (int x = 0; x < 14; x++) {
//        if (grid[x][y] == 0) {
//          System.err.print(String.format(" %3s  ", " "));
//        } else if (grid[x][y] < 0) {
//          System.err.print(String.format("-%3s- ", -grid[x][y]));
//        } else {
//          System.err.print(String.format("[%3s] ", grid[x][y]));
//        }
//      }
//      System.err.println();
//    }
  }

  private void clearGrid() {
    robots.clear();
//    for (int y = 0; y < 14; y++) {
//      for (int x = 0; x < 14; x++) {
//        grid[x][y] = 0;
//      }
//    }
  }

}
