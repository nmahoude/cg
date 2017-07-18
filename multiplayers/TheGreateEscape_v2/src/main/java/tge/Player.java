package tge;

import java.util.Scanner;

import tge.minimax.Minimax;
import tge.minimax.Node;
import tge.paths.AStar;
import tge.paths.FloodFill;
import tge.simulation.Action;

public class Player {
  public static final long MAX_TIME = 100;
  public static long startTime;
  
  public static int playerCount;
  public static int myId;
  public static Agent agents[] = new Agent[3];
  public static Grid grid = new Grid();
  public static int round;
  
  public static void main(String args[]) {
      Scanner in = new Scanner(System.in);
      int w = in.nextInt(); // width of the board
      int h = in.nextInt(); // height of the board
      
      playerCount = in.nextInt();
      myId = in.nextInt();

      for (int i=0;i<playerCount;i++) {
        agents[i] = new Agent(i);
      }
      round = 0;
      // game loop
      while (true) {
        round++;
          for (int i = 0; i < playerCount; i++) {
              int x = in.nextInt(); // x-coordinate of the player
              int y = in.nextInt(); // y-coordinate of the player
              int wallsLeft = in.nextInt(); // number of walls available for the player
              if (x == -1) {
                agents[i].position = Point.unknown;
              } else {
                agents[i].position = Point.get(x, y);
              }
              agents[i].wallLefts = wallsLeft;
          }
          startTime = System.currentTimeMillis();

          grid.reset();
          int wallCount = in.nextInt(); // number of walls on the board
          for (int i = 0; i < wallCount; i++) {
              int wallX = in.nextInt(); // x-coordinate of the wall
              int wallY = in.nextInt(); // y-coordinate of the wall
              String wallOrientation = in.next(); // wall orientation ('H' or 'V')
              grid.setWall(Point.get(wallX, wallY), "H".equals(wallOrientation) ? WallOrientation.HORIZONTAL : WallOrientation.VERTICAL);
          }

          for (int i=0;i<playerCount;i++) {
            if (agents[i].position != Point.unknown) {
              agents[i].currentDist = AStar.astar(grid.get(agents[i].position), i).size();
              agents[i].currentMax= FloodFill.floodFillFromExit(i);
            } else {
              agents[i].currentDist = 0;
              agents[i].currentMax = 0;
            }
          }
          // Debug some informations
          //grid.toTDD();

//          Agent agent = agents[myId];
//          Cell cell = grid.get(agent.position);
//          System.err.println("walls around :");
//          System.err.println("  right : "+cell.wall[0]);
//          System.err.println("  down  : "+cell.wall[1]);
//          System.err.println("  left  : "+cell.wall[2]);
//          System.err.println("  up     : "+cell.wall[3]);
          
          // Write an action using System.out.println()
          // To debug: System.err.println("Debug messages...");
          Minimax minimax = new Minimax();
          Action bestAction = minimax.think(1);

          // action: LEFT, RIGHT, UP, DOWN or "putX putY putOrientation" to place a wall
          System.out.println(bestAction.toOutput());
      }
  }
}
