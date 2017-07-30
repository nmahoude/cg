package tron;

import java.util.ArrayList;
import java.util.Scanner;

import tron.algorithms.MultiBFS;
import tron.common.Cell;
import tron.common.Grid;
import tron.common.Point;

public class Player {
  public static Grid grid;
  public static int initPlayerCount = 0;
  public static Agent[] initAgents = new Agent[4];
  public static Agent me;
  private static int myId;
  
  public static int playerFE;
  public static Agent[] agents = new Agent[4];
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    init();
    
    // game loop
    while (true) {
      initPlayerCount = in.nextInt(); // total number of players (2 to 4).
      myId = in.nextInt();
      me = initAgents[myId];
      
      int alivePlayers = 0;
      for (int i = 0; i < initPlayerCount; i++) {
        int X0 = in.nextInt(); // starting X coordinate of lightcycle (or -1)
        int Y0 = in.nextInt(); // starting Y coordinate of lightcycle (or -1)
        int X1 = in.nextInt(); // starting X coordinate of lightcycle (can be the same as X0 if you play before this player)
        int Y1 = in.nextInt(); // starting Y coordinate of lightcycle (can be the same as Y0 if you play before this player)

        if (X0 == -1) {
          handleDeadAgent(i);
          initAgents[i].currentCell = Cell.invalid;
        } else {
          grid.get(Point.get(X0, Y0)).owner = i;
          initAgents[i].currentCell = grid.get(Point.get(X1, Y1));
          initAgents[i].currentCell.owner = i;
          alivePlayers++;
        }
      }

      orderAlivePlayers();

//      grid.toTDD();
      
      int floodFillPerAgent[] = MultiBFS.bfs();
//      for (int i=0;i<playerFE;i++) {
//        System.err.println("FloodFill for "+agents[i].id+" = "+floodFillPerAgent[i]);
//      }
      
      Agent enemy = null;
      int bestDist = Integer.MAX_VALUE;
      for (int i=1;i<playerFE;i++) {
        if (! MultiBFS.fighting[i]) continue;
        int dist = (int)agents[0].currentCell.position.manathan(agents[i].currentCell.position);
        if(dist < bestDist) {
          bestDist = dist;
          enemy = agents[i];
        }
      }
      if (enemy == null) {
        System.err.println("no ennemy");
      }
      Action bestAction = null;
      double bestScore = Double.NEGATIVE_INFINITY;
      
      for (int dir=0;dir<4;dir++) {
        Action action = new Action();
        action.agent = me;
        action.moveIndex = dir;
        double score;
        if (Simulation.play(action)) {

          score = scoreSolution(enemy, action);
          
          if ( score > bestScore) {
            bestScore = score;
            bestAction = action;
          }
          Simulation.unplay(action);
        }
      }
      System.out.println(bestAction.toOutput());
    }
  }

  private static double scoreSolution(Agent enemy, Action action) {
    int floodFillPerAgent[] = MultiBFS.bfs();
    double score = 0.0;
    score = floodFillPerAgent[0];
    if (enemy != null) {
      score-= 0.1*floodFillPerAgent[enemy.id];
    }
    // bonus because we still touch a player that will die sooner than us
    for (int i=1;i<playerFE;i++) {
      if (floodFillPerAgent[i] < floodFillPerAgent[0]) {
        System.err.println("more ff than "+i);
        if (MultiBFS.touching[i]) {
          System.err.println("and still touching it");
          score += 5.0;
        }
      }
      //score -= 0.01*floodFillPerAgent[i];
    }
    
    // bonus if we have a lot of non-empty neighbors (what ?)
    int neighbors = 0;
    for (int tdir=0;tdir<4;tdir++) {
      if (me.currentCell.neighbors[tdir].owner >=0) neighbors++;
    }
    score += (neighbors >= 2 ? 0.1 : 0);
    
    // check the score against best score
    System.err.println(action.toOutput()+" => bfs="+floodFillPerAgent[0]+" score="+score);
    return score;
  }

  static void init() {
    Point.init(30, 20);
    grid = new Grid(30, 20);
    
    for (int i=0;i<4;i++) {
      initAgents[i] = new Agent(i);
      initAgents[i].currentCell = Cell.invalid;
    }
  }

  private static void orderAlivePlayers() {
    playerFE = 0;
    int i = myId;
    do {
      agents[playerFE] = initAgents[i++];
      agents[playerFE].id = playerFE++;
      if (i >= initPlayerCount) i = 0;
      while (initAgents[i].currentCell == Cell.invalid ) {
        i++;
        if (i >= initPlayerCount) i = 0;
      }
    } while (i != myId);
  }

  private static void handleDeadAgent(int i) {
    grid.resetDeadOwner(i);
  }
}
