package csbtools;

import csb.game.Referee;
import csb.simulation.AGSolution;

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
    referee.initReferee(6, 1);

    int turns = 0;
    
    int currentCheckPoint = 0;
    int lastCheckPoint = 0;
    int lapToGo = 3;
    while (lapToGo > 0) {
      if (lastCheckPoint == 0 && currentCheckPoint == 1) { 
        lapToGo--;
      }
      AGSolution best = doSimulation(referee);
      referee.handlePlayerOutput(0, 0, 0, new String[] {best.actionOutput(0)});
      referee.updateGame(0);
      lastCheckPoint = currentCheckPoint;
      currentCheckPoint = referee.pods[0].nextCheckPointId;
      turns++;
    }
    System.out.println("Turn to do 3 laps : "+turns);
  }

  private static AGSolution doSimulation(Referee referee) {
    AGSolution best = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int i=0;i<10_000;i++) {
      AGSolution solution = new AGSolution(referee.pods, referee.checkPoints);
      solution.test();
      solution.moveAndEvaluate();
      if (solution.score1 > bestScore) {
        bestScore = solution.score1;
        best = solution;
      }
      solution.reset();
    }
    return best;
  }
}
