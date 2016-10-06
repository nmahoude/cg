package theAccountant;
import java.util.List;
import java.util.Scanner;

import trigonometry.Point;
import trigonometry.Vector;

/**
 * Shoot enemies before they collect all the incriminating data! The closer you
 * are to an enemy, the more damage you do but don't get too close or you'll get
 * killed.
 **/
public class Player {
  static Vector vectorUsedWaitingForTheDamnedBugInNDXLibrary_ThanksAnyway;
  
  static GameEngine gameEngineMain = new GameEngine();
  static Ai ai = new Ai(gameEngineMain);
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    // game loop
    int turn = 0;
    while (true) {
      gameEngineMain.reset();
      
      int x = in.nextInt();
      int y = in.nextInt();
      gameEngineMain.createWolff(x,y);
      
      int dataCount = in.nextInt();
      for (int i = 0; i < dataCount; i++) {
        int dataId = in.nextInt();
        int dataX = in.nextInt();
        int dataY = in.nextInt();
        gameEngineMain.createDataPoint(dataId, dataX, dataY);
      }
      int enemyCount = in.nextInt();
      for (int i = 0; i < enemyCount; i++) {
        int enemyId = in.nextInt();
        int enemyX = in.nextInt();
        int enemyY = in.nextInt();
        int enemyLife = in.nextInt();
        gameEngineMain.createEnemy(enemyId, enemyX, enemyY, enemyLife);
      }
      if (turn == 0) {
        gameEngineMain.init();
      }
      turn++;

      //debugEnemiesMoveToTheirTargets();
      
      ai.doYourStuff();

      System.out.println(ai.command.get()); 
    }
  }
  private static void debugEnemiesMoveToTheirTargets() {
    for (Enemy e : gameEngineMain.enemies) {
      String moves ="";
      DataPoint dp = e.findNearestDataPoint();
      List<Point> ps = e.stepsToTarget(dp.p);
      for (Point p : ps) {
        moves+=p.toString()+" -> ";
      }
      System.err.println("e: "+e.id+ "will move : "+moves);
    }
  }

  static class Movable {}
  static class Zone {
    static int width = 16000;
    static int height = 9000;
    
  }
  
}