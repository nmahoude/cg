package bttc;

import java.util.Random;
import java.util.Scanner;

public class Player {
  static final int MAX_TIME = 50;
  static long start;
  static Map map = new Map();
  static Random rand = new Random(0);
  private static P currentPos = new P(0,0);
  
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int opponentCount = in.nextInt(); // Opponent count

		// game loop
		while (true) {
			int gameRound = in.nextInt();
			start = System.currentTimeMillis();
			int x = in.nextInt(); // Your x position
			int y = in.nextInt(); // Your y position
			currentPos.x = x;
			currentPos.y = y;
			int backInTimeLeft = in.nextInt(); // Remaining back in time

			for (int i = 0; i < opponentCount; i++) {
				int opponentX = in.nextInt(); // X position of the opponent
				int opponentY = in.nextInt(); // Y position of the opponent
				int opponentBackInTimeLeft = in.nextInt(); // Remaining back in time of the opponent
			}
			
			System.err.println("I'm at "+x+","+y);
      map.read(in);
      //map.draw();

      AI ai = new AI();
      ai.think(currentPos, map);
      ai.output();
      
		}
	}



  private static void testRectanglesForEnnemy(int x, int y) {
    int maxX1 = 0, maxX2 = 0, maxY1 = 0, maxY2 = 0;
    int maxArea=0;
    for (int x1=0;x1<35;x1++) {
      for (int x2=x1;x2<35;x2++) {
        for (int y1=0;y1<20;y1++) {
          for (int y2=y1;y2<20;y2++) {
            boolean onBorder = false;
            if ((x == x1 || x == x2) && (y>=y1 && y<y2)) onBorder = true;
            if ((y == y1 || y == y2) && (x>=x1 && x<x2)) onBorder = true;
            if (!onBorder) continue;
            
            boolean hasEnemy = map.hasEnemyIn(x1, x2, y1, y2);
            if (!hasEnemy) {
              int area = (x2-x1+1)*(y2-y1+1);
              if (area > maxArea) {
                maxArea = area;
                maxX1 = x1;
                maxX2 = x2;
                maxY1 = y1;
                maxY2 = y2;
              }
            }
          }
        }
      }
    }
    
    if (maxArea > 0) {
      System.err.println("Max area is "+maxArea);
      System.err.println("with coords : "+maxX1+","+maxX2+","+maxY1+","+maxY2);
    }
  }
}
