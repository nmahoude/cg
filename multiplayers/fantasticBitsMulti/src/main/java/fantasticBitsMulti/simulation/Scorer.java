package fantasticBitsMulti.simulation;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.State;
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
    State state = Player.state;
    if (state.teamInfos[0].score >= Player.victory) {
      eval += patiences[depth] * 1_000_000;
    }
    if (state.teamInfos[1].score >= Player.victory) {
      eval -= patiences[depth] * 1_000_000;
    }
    
    eval +=  100_000 * patiences[depth] * (0.0
                + 1000.0 * (state.teamInfos[0].score - state.teamInfos[1].score)
        
        );
  }

  public void finalEval() {
    double energy = 0;
    energy -= distanceToClosestSnaffle();
    energy -= snafflesNearOppGoal();
    //energy += snaffleAvgPosition();
    //energy += wizardDistanceToSnaffles();
    energy += distanceBetweenMyWizards();
    energy += Player.state.teamInfos[0].mana * 200;
    energy += 500 * (+(Player.state.wizards[0].snaffle != null ? 1 : 0) + (Player.state.wizards[1].snaffle != null ? 1 : 0) 
        - (Player.state.wizards[2].snaffle != null ? 1 : 0) - (Player.state.wizards[3].snaffle != null ? 1 : 0));
    // last one !
    if (Player.state.teamInfos[0].score >= Player.victory) {
        energy = Double.POSITIVE_INFINITY;
    }

    // ------------------------
    eval+= energy;

  }

  private static double snafflesNearOppGoal() {
    double dist = 0;
    for (int i = 0; i < Player.state.snafflesFE; i++) {
        Snaffle snaffle = Player.state.snaffles[i];
        if (snaffle.dead)
            continue;
        dist += snaffle.position.distTo(Player.myGoal);
    }
    return dist;
}

private static double distanceToClosestSnaffle() {
    Snaffle closest1 = null, closest2 = null;
    double bestDist1 = Double.POSITIVE_INFINITY, bestDist2 = Double.POSITIVE_INFINITY;
    for (int i = 0; i < Player.state.snafflesFE; i++) {
        Snaffle snaffle = Player.state.snaffles[i];
        if (snaffle.dead)
            continue;
        double dist1 = Player.state.wizards[0].position.distTo(snaffle.position);
        double dist2 = Player.state.wizards[1].position.distTo(snaffle.position);
        if (dist1 < bestDist1) {
            bestDist1 = dist1;
            closest1 = snaffle;
        }
        if (dist2 < bestDist2) {
            bestDist2 = dist2;
            closest2 = snaffle;
        }
    }
    return bestDist1 + bestDist2;
  }
  
  private double distanceBetweenMyWizards() {
      double distBetweenWizards = Player.state.wizards[0].position.distTo(Player.state.wizards[1].position);
      return 0.1 * distBetweenWizards;
  }
  
  private double wizardDistanceToSnaffles(double energy) {
      double wizardAvgDist = 0;
      for (int i = 0; i < Player.state.snafflesFE; i++) {
          Snaffle snaffle = Player.state.snaffles[i];
          if (snaffle.dead)
              continue;
          wizardAvgDist += Player.state.wizards[0].position.distTo(snaffle.position);
          wizardAvgDist += Player.state.wizards[1].position.distTo(snaffle.position);
      }
      energy -= wizardAvgDist / (2 * 16_000 * Player.state.snafflesFE);
      return energy;
  }
  
  private double snaffleAvgPosition(double energy) {
      double avgPos = 0;
      for (int i = 0; i < Player.state.snafflesFE; i++) {
          avgPos += Player.state.snaffles[i].position.distTo(Player.myGoal);
      }
      energy -= avgPos / (16_000 * Player.state.snafflesFE);
      return energy;
  }

  
 

  public double eval() {
    return eval;
  }

}
