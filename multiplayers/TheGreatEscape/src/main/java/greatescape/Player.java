package greatescape;

import java.util.List;
import java.util.Scanner;

import greatescape.ai.AI;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
public class Player {
  static GameState state = new GameState();
  public final static int W = 9;
  public final static int H = 9;
  
  private static int turn;
  public static long start;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
  
    turn = 0;
    state.readInitialInput(in);

    // game loop
    while (true) {
      turn++;
      state.readInput(in);

      AI ai = new AI();
      ai.evaluate(state);
      //String finalMove = calculateMove();
      //System.err.println("Perf : "+(System.currentTimeMillis()-start));
      //System.out.println(finalMove);
    }
  }

  private static String calculateMove() {
    int bestScore = evaluateAction(0, 0, 0 , WallOrientation.H)-state.myId;// substract myId to counter turn disadvantage
    System.err.println("Score for move is "+bestScore);
    String finalMove = null;
    
    if (turn > 4 && bestScore < 0 && state.agents[state.myId].wallsLeft > 0) {
      for (int x=0;x<W-1;x++) {
        for (int y=0;y<H-1;y++) {
          for (WallOrientation wo : WallOrientation.values()) {
            if (x == 0 && wo == WallOrientation.V) continue;
            if (y == 0 && wo == WallOrientation.H) continue;
            
            if (!state.board.addWall(state.wallCount+1,x, y, wo)) {
              continue;
            }
            int score = evaluateAction(state.wallCount+1,x, y, wo)-state.myId; 
            if (score < -1000) {
              System.err.println("Doing "+x+" "+y+" "+wo+" would block a player");
            }
            
            if (score > bestScore) {
              bestScore = score;
              finalMove = ""+x+" "+y+" "+wo.toString();
              System.err.println("New best score: "+bestScore+" for move "+finalMove);
              System.err.println("My trajectory");
              List<PathItem> trajectory = new AStar(state.board, state.board.getCell(state.agents[state.myId].position), state.me.goal).find();
              debugTrajectory(trajectory);
              System.err.println("others trajectorys");
              for (int i=0;i<state.playerCount;i++) {
                if (i == state.myId) continue;
                if (state.agents[i].isDead()) continue;
                System.err.println("player "+i);
                List<PathItem> trajectory2 = new AStar(state.board, state.board.getCell(state.agents[i].position), state.agents[i].goal).find();
                debugTrajectory(trajectory2);
              }
            }
            
            state.restore();
          }
        }
      }
    } 
    if (finalMove == null) {
      // need to get the trajectory back
      Cell currentCell = state.board.getCell(state.me.position);
      List<PathItem> trajectory = new AStar(state.board, currentCell, state.me.goal).find();
      debugTrajectory(trajectory);
      if (trajectory.size() > 1) {
        finalMove = getMoveFromCells(currentCell, trajectory.get(1).pos);
      } else {
        finalMove = "RIGHT ERROR";
      }
    }
    return finalMove;
  }

  private static int evaluateAction(int wallId, int x, int y, WallOrientation wo) {
    int lengths[] = new int[3];
    int bestLength = Integer.MAX_VALUE;
    int bestId = 0;
    List<PathItem> myTrajectory = null;
    Cell myCurrentCell = null;
    
    for (int i=0;i<state.playerCount;i++) {
      if ( state.agents[i].isDead()) continue;
      
      Cell currentCell = state.board.getCell(state.agents[i].position);
      List<PathItem> trajectory = new AStar(state.board, currentCell, state.agents[i].goal).find();
      lengths[i] = trajectory.size();
      if (lengths[i]<bestLength) {
        bestLength = lengths[i];
        bestId = i;
      }
      if (i == state.myId) {
        myCurrentCell = currentCell;
        myTrajectory = trajectory;
      }
    }

    int score = 0;
    for (int i=0;i<state.playerCount;i++) {
      if ( state.agents[i].isDead()) continue;
      
      if (lengths[i] <= 1) {
        return -5000; // bad bad
      }
      
      if (i!= state.myId) {
        score +=100*lengths[i];
        if (wallId != 0) {
          score -=( Math.abs(state.agents[i].position.x - x) + Math.abs(state.agents[i].position.y - y));
        }
      } else {
        score -=100*(state.alivePlayer-1)*lengths[i];
      }
    }
    return score;
  }

  static void debugTrajectory(List<PathItem> trajectory) {
    for (PathItem item : trajectory) {
      System.err.print("(" + item.pos.x + "," + item.pos.y + ") -> ");
    }
    System.err.println("");
  }

  public static String getMoveFromCells(Cell origin, Cell destination) {
    if (origin.up == destination) {
      return "UP";
    }
    if (origin.right == destination) {
      return "RIGHT";
    }
    if (origin.down == destination) {
      return "DOWN";
    }
    if (origin.left == destination) {
      return "LEFT";
    }
    return "RIGHT";
  }
}