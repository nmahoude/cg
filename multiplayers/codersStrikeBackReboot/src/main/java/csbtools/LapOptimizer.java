package csbtools;

import csb.game.Referee;
import csb.simulation.AGSolution1;

/**
 * do 3 laps of a map and mesure how many turns it takes
 * 
 * Objective : pure performance measure (only one pod)
 * @author nmahoude
 *
 */
public class LapOptimizer {

  public static void main(String[] args) throws Exception {
    Referee referee = new Referee();
    referee.initReferee(104, 1);

    int turns = 0;
    
    int currentCheckPoint = 0;
    int lastCheckPoint = 0;
    int lapToGo = 3;
    while (lapToGo > 0) {
      if (lastCheckPoint == 0 && currentCheckPoint == 1) { 
        lapToGo--;
      }
      AGSolution1 best = doSimulation(referee);
      referee.handlePlayerOutput(0, 0, 0, new String[] {best.actionOutput(0)});
      referee.updateGame(0);
      lastCheckPoint = currentCheckPoint;
      currentCheckPoint = referee.pods[0].nextCheckPointId;
      turns++;
    }
    System.out.println("Turn to do 3 laps : "+turns);
  }

  private static AGSolution1 doSimulation(Referee referee) {
    AGSolution1 best = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int i=0;i<10_000;i++) {
      AGSolution1 solution = new AGSolution1(referee.pods, referee.checkPoints);
      solution.test();
      double score = solution.moveAndEvaluate();
      if (score > bestScore) {
        bestScore = score;
        best = solution;
      }
      solution.reset();
    }
    return best;
  }
}
