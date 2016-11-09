package stc2;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MCTS {
  Game game;
  
  boolean target = false;
  double targetScore = 0;
  int targetPos = 0;
  private int targetCombos[] = new int[16];
  
  
  double bestPoints[] = new double[8];
  int bestCombos[][] = new int[8][16];
  int combo[] = new int[16];
  
  public void simulate() {
    ThreadLocalRandom random = ThreadLocalRandom.current();

    Simulation sim = new Simulation();
    BitBoard board = new BitBoard();
    sim.board = board;
    
    clearBestPoints();
    
    double points;
    double patience;
    
    for (int ply=0;ply<10_000;ply++) {
      points = 0;
      patience = 1.0;
      board.copyFrom(game.myBoard);
      
      for (int i=0;i<8;i++) {
        int color1 = game.nextBalls[i];
        int color2 = game.nextBalls2[i];
  
        int rotation = random.nextInt(4);
        int baseColumn = random.nextInt(6);
        combo[2*i] = baseColumn;
        combo[2*i+1] = rotation;
        
        sim.init();
        if (!sim.putBalls(color1,color2,rotation,baseColumn)) {
          points = -1000;
          break;
        }
//        System.err.println(sim.board.getDebugString());
//        System.err.println(" for "+sim.points+" points");
        points=points + patience * (
            sim.points +sim.groupsCount[2]+2*sim.groupsCount[3]-sim.groupsCount[1]);
        patience *=0.5;
        if (points > bestPoints[i]) {
//          if (points > 70*6*3) {
//            target = true;
//            targetScore = points;
//            targetPos = i;
//            System.arraycopy(combo, 0, targetCombos, 0, 16);
//            break;
//          }
          //System.err.println("find best point : "+points+" for round "+i);
          System.arraycopy(combo, 0, bestCombos[i], 0, 16);
          bestPoints[i] = points;
        }
      }
    }
  }

  private void clearBestPoints() {
    for (int i=0;i<8;i++) {
      bestPoints[i] = -1;
    }
  }

  public String output() {
    int position;
    int rotation;
    String message;
    if (target) {
      message =" target to "+targetScore;
      position = targetCombos[0];
      rotation = targetCombos[1];
      System.arraycopy(targetCombos, 2, targetCombos, 0, 14);
      targetPos--;
      if (targetPos < 0) {
        target = false;
      }
    } else {
      position = bestCombos[7][0];
      rotation = bestCombos[7][1];
      message = "from exploration";
      System.err.println("Best points calculated : ");
      for (int i=0;i<8;i++) {
        System.err.println("for "+(i+1)+" : "+bestPoints[i] +" next ply p("+bestCombos[i][0]+") r("+bestCombos[i][1]+")");
      }
    }
    return ""+position+" "+rotation+ " "+message;
  }
}
