package surface;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
public class Solution {
  private static final int MAX_WIDTH = 10000;
  static short WATER = 1;
  static short GROUND = 0;

  static short memo[] = new short[10000 * 10000];
  static List<Integer> cellsToEvaluate = new ArrayList<>();
  static int values[] = new int[1000];
  static short index = 10;

  private static int l;
  private static int h;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    l = in.nextInt();
    h = in.nextInt();

    if (in.hasNextLine()) {
      in.nextLine();
    }
    for (int y = 0; y < h; y++) {
      String row = in.nextLine();
      for (int x = 0; x < l; x++) {
        memo[x + y * MAX_WIDTH] = row.charAt(x) == 'O' ? WATER : GROUND;
        // if (memo[x][y] == WATER) {
        // System.err.println("Water at "+x +", "+y);
        // }
      }
    }

    int N = in.nextInt();
    for (int i = 0; i < N; i++) {
      int X = in.nextInt();
      int Y = in.nextInt();
      // System.err.println("getting value at "+X +","+Y);
      int memoValue = memo[X + Y * MAX_WIDTH];
      if (memoValue == GROUND) {
        System.out.println("" + values[memoValue]);
      } else if (memoValue != WATER) {
        System.out.println("" + values[memoValue]);
      } else {
        int value = floodfill(X, Y, index);
        values[index++] = value;
        System.out.println("" + value);
      }
    }
  }

  static int floodfill(int x, int y, short value) {
    cellsToEvaluate.add(x + MAX_WIDTH * y);
    int total = 0;
    while (!cellsToEvaluate.isEmpty()) {
      int cell = cellsToEvaluate.remove(0);
      y = (int)(cell / MAX_WIDTH);
      x = cell % MAX_WIDTH;
      if (memo[cell] == WATER) {
        memo[cell] = value;
        total++;
        if (x > 0) cellsToEvaluate.add((x-1)+MAX_WIDTH*(y));
        if (x < l) cellsToEvaluate.add((x+1)+MAX_WIDTH*(y));
        if (y > 0) cellsToEvaluate.add((x)+MAX_WIDTH*(y-1));
        if (y < h) cellsToEvaluate.add((x)+MAX_WIDTH*(y+1));
      } 
    }
    return total;
  }

}