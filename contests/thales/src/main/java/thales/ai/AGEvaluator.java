package thales.ai;

import thales.Player;
import thales.UFO;

public class AGEvaluator {
  UFO attacker = Player.teams[0].ufos[0];
  UFO defender = Player.teams[0].ufos[1];
  
  
  public void clear() {
  }

  public void evaluate(AGSolution sol, int depth) {
    if (defender.flag) {
      swap();
    } else {
      // TODO if defender closer to flag, inverse
    }
    
    double total = 0.0;
    
    total += attacker(total);
    total += defender(total);
    
    for (int i=0;i<2;i++) {
      //total += opponent(total, i);
    }
    
    sol.energy = total;
  }

  private double opponent(double total, int i) {
    double score = 0.0;
    UFO other = Player.teams[1].ufos[i];
    if (other.flag) {
      score += 500;
      score -= other.distance_2_ToGoal();
    } else if (other.otherTeam.flag.x >= 0){
      score -= other.distance_2(Player.teams[0].flag);
    }
    total -= score;
    return total;
  }

  private double defender(double total) {
    double score = 0.0;
    if (Player.teams[0].flag.onMap()) {
      score += 10_000;
      score -= defender.distance_2(Player.teams[0].flag);
    } else {
      score += speed(defender);
      for (int i=0;i<2;i++) {
        UFO ufo = Player.teams[1].ufos[i];
        if (ufo.flag) {
          score -= defender.distance_2(ufo);
        }
      }
    }
    return score;
  }

  private double speed(UFO ufo) {
    return 1.0 * ufo.vx*ufo.vx + ufo.vy+ufo.vy;
  }

  private double attacker(double total) {
    // attacker
    if (attacker.flag) {
      double score = 0.0;
      score += 10_000;
      score -= attacker.distance_2_ToGoal();
      
      total += score;
    } else {
      double score = 0.0;
      score -= attacker.distance_2(Player.teams[1].flag);

      total += score;
    }
    return total;
  }

  private void swap() {
    UFO tmp = defender;
    defender = attacker;
    attacker = tmp;
  }

}
