package fantasticBitsMulti.simulation;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.ag.AG;
import fantasticBitsMulti.units.Snaffle;

public class Scorer {
  private final int coef = 100_000;

  double eval = 0;

  private int myInitScore;

  private int hisInitScore;
  
  public void reset() {
    myInitScore = Player.myScore;
    hisInitScore = Player.hisScore;

    eval = 0;
  }
  
  public void evalTurn1() {
    eval += AG.patiences[0] * coef * ((Player.myScore - myInitScore) - (Player.hisScore - hisInitScore));
  }
  
  public void evalTurn(int depth) {
    eval += AG.patiences[depth] * coef * ((Player.myScore - myInitScore) - (Player.hisScore - hisInitScore));
  }

  public void finalEval() {
    double energy = 0;

    energy -= distanceToClosestSnaffle();
    energy -= snafflesNearOppGoal();
    //energy += snaffleAvgPosition();
    //energy += wizardDistanceToSnaffles();
    energy += distanceBetweenMyWizards();
    
    energy += Player.myMana * 200;
    
    energy += 500*(
        + (Player.myWizard1.snaffle != null ? 1 : 0)
        + (Player.myWizard2.snaffle != null ? 1 : 0)
        - (Player.hisWizard1.snaffle != null ? 1 : 0)
        - (Player.myWizard2.snaffle != null ? 1 : 0)
        );
        
    // last one !
    if (Player.myScore >= Player.victory) {
      energy = Double.POSITIVE_INFINITY;
    }
    
    // ------------------------
    eval += energy;
  }

  private static double snafflesNearOppGoal() {
    double dist = 0;
    for (int i=0;i<Player.snafflesFE;i++) {
      Snaffle snaffle = Player.snaffles[i];
      if (snaffle.dead) continue;
      dist +=snaffle.position.distTo(Player.myGoal);
    }
    return dist;
  }

  private static double distanceToClosestSnaffle() {
    Snaffle closest1 = null, closest2 = null;
    double bestDist1 = Double.POSITIVE_INFINITY, bestDist2 = Double.POSITIVE_INFINITY;
    
    for (int i=0;i<Player.snafflesFE;i++) {
      Snaffle snaffle = Player.snaffles[i];
      if (snaffle.dead) continue;
      
      double dist1 = Player.myWizard1.position.distTo(snaffle.position);
      double dist2 = Player.myWizard2.position.distTo(snaffle.position);
      if (dist1 < bestDist1) { bestDist1 = dist1; closest1 = snaffle; }
      if (dist2 < bestDist2) { bestDist2 = dist2; closest2 = snaffle; }
    }
    return bestDist1 + bestDist2;
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
