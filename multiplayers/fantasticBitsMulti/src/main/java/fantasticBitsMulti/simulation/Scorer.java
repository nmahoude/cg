package fantasticBitsMulti.simulation;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.units.Snaffle;

public class Scorer {
  private static final double COEF_PATIENCE = 0.9;
  private static double patiences[] = new double[100];
  static {
    for (int i=0;i<100;++i) {
      patiences[i] = Math.pow(COEF_PATIENCE, i);
    }
  }
  
  double eval = 0;

  public void reset() {
    eval = 0;
  }
  
  public void evalTurn(int depth) {
    if (Player.myScore >= Player.victory) {
      eval += patiences[depth] * 1_000_000;
    }
    if (Player.hisScore >= Player.victory) {
      eval -= patiences[depth] * 1_000_000;
    }
    
    eval +=  patiences[depth] * (0.0
                + 1000.0 * (Player.myScore - Player.hisScore)
        
        );
  }

  public void finalEval() {
    double energy = 0;

    energy -= distanceToClosestSnaffle();
    energy -= snafflesDistanceToGoals();
    //energy += snaffleAvgPosition();
    //energy += wizardDistanceToSnaffles();
    // energy += distanceBetweenMyWizards();
    
    //energy += Player.myMana * 200;
    
    // ------------------------
    eval += energy;
  }

  private static double snafflesDistanceToGoals() {
    double dist = 0;
    int snaffleCount=0;
    for (int i=0;i<Player.snafflesFE;i++) {
      Snaffle snaffle = Player.snaffles[i];
      if (snaffle.dead) continue;
      dist += snaffle.position.squareDistance(Player.myGoal);
      dist -= snaffle.position.squareDistance(Player.hisGoal);
      snaffleCount++;
    }
    return dist / (Player.MAP_MAX_DISTANCE_2 * snaffleCount);
  }

  private static double distanceToClosestSnaffle() {
    double bestDist1 = Double.POSITIVE_INFINITY, bestDist2 = Double.POSITIVE_INFINITY;
    
    for (int i=0;i<Player.snafflesFE;i++) {
      Snaffle snaffle = Player.snaffles[i];
      if (snaffle.dead) continue;
      
      double dist1 = Player.myWizard1.position.squareDistance(snaffle.position);
      double dist2 = Player.myWizard2.position.squareDistance(snaffle.position);
      if (dist1 < bestDist1) { bestDist1 = dist1;  }
      if (dist2 < bestDist2) { bestDist2 = dist2;  }
    }
    return (bestDist1 + bestDist2) / ( 2 * Player.MAP_MAX_DISTANCE_2);
  }

  private static double distanceBetweenMyWizards() {
    double distBetweenWizards = Player.myWizard1.position.distTo(Player.myWizard2.position);
    return 0.1*distBetweenWizards ;
  }

  private static double wizardDistanceToSnaffles(double energy) {
    double wizardAvgDist = 0;
    for (int i=0;i<Player.snafflesFE;i++) {
      Snaffle snaffle = Player.snaffles[i];
      if (snaffle.dead) continue;
      wizardAvgDist += Player.myWizard1.position.distTo(snaffle.position);
      wizardAvgDist += Player.myWizard2.position.distTo(snaffle.position);
    }
    energy -= wizardAvgDist / (2 * 16_000 *Player.snafflesFE) ;
    return energy;
  }

  private static double snaffleAvgPosition(double energy) {
    double avgPos = 0;
    for (int i=0;i<Player.snafflesFE;i++) {
      avgPos+=Player.snaffles[i].position.distTo(Player.myGoal);
    }
    energy -= avgPos / (16_000 *Player.snafflesFE) ;
    return energy;
  }

  public double eval() {
    return eval;
  }

}
