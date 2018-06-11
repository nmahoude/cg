package thales.ai;

import thales.Flag;
import thales.Player;
import thales.Team;
import thales.UFO;

public class TestEvaluator {
  public final static double patiences[] = new double[] { 1.0, 0.8, 0.6, 0.4, 0.3, 0.2, 0.1 ,0.1 };
  
  public static UFO attacker = Player.teams[0].ufos[0];
  public static UFO defender = Player.teams[0].ufos[1];
  public static UFO hisAttacker = Player.teams[1].ufos[0];
  public static UFO hisDefender = Player.teams[1].ufos[1];
  public static Flag myFlag = Player.teams[0].flag;
  
  public void clear() {
  }

  public void evaluate(AGSolution sol, int depth) {
    
    double total = 0.0;
    
    total += attackerToFlag(sol, depth);
    total += defense(sol, depth);
    
    sol.energy += patiences[depth] * total;
  }

  public double defense(AGSolution sol, int depth) {
    double total = 0.0;
    
    
    if (myFlag.onMap()) {
      total += 10.0;
      total -= defender.distanceAsScore(myFlag);
    } else {
      if (hisAttacker.flag) {
        total -= defender.distanceAsScore(hisAttacker);
      } else if (hisDefender.flag) {
        total -= defender.distanceAsScore(hisDefender);
      }
    }
    return total;
  }
  
  public double attackerToFlag(AGSolution sol, int depth) {
    double total = 0.0;
    Flag oppFlag = Player.teams[1].flag;
    
    total += 100 * Player.teams[0].score;
    
    if (oppFlag.onMap()) {
      total -= attacker.distanceAsScore(oppFlag);
    } else {
      total += 10.0;
      total += attacker.vx * attacker.myTeam.dirx;
      total -= distanceToGoal(attacker);
    }
    
    return total;
  }
  
  
  private double distanceToGoal(UFO attacker) {
    return Math.abs(attacker.x - attacker.myTeam.depX) / Player.WIDTH;
  }

  public void bothToCenter(AGSolution sol, int depth) {
    Flag center = new Flag(null, null);
    center.x = 5000;
    center.y = 4000;
    
    double total = 0.0;
    
    total -= attacker.distanceAsScore(center);
    total -= defender.distanceAsScore(center);
    
    sol.energy = total;
  }

  private void swap() {
    UFO tmp = defender;
    defender = attacker;
    attacker = tmp;
  }

  public void initRound() {
    // who is attacker & defender
    Team oppTeam = Player.teams[1];
    if (oppTeam.ufos[0].flag) {
      hisAttacker = oppTeam.ufos[0];
      hisDefender = oppTeam.ufos[1];
    } else if (oppTeam.ufos[1].flag) {
      hisAttacker = oppTeam.ufos[1];
      hisDefender = oppTeam.ufos[0];
    } else {
      if (oppTeam.ufos[0].distance2(myFlag) < oppTeam.ufos[1].distance2(myFlag)) {
        hisAttacker = oppTeam.ufos[0];
        hisDefender = oppTeam.ufos[1];
      } else {
        hisAttacker = oppTeam.ufos[1];
        hisDefender = oppTeam.ufos[0];
      }
    }

    if (defender.flag) {
      swap();
    } else if (!attacker.flag) {
      // reevaluate would should be attacker !
      if (hisAttacker.flag) {
        if (attacker.distance2(hisAttacker) < defender.distance2(hisAttacker)) {
          // attacker has no flag, and his closer to attacker
          swap();
        }
      } else {
        // nobody has the flag
        if (distanceToGoal(attacker) < distanceToGoal(defender)) {
          // attacker is closer to his own goal ... swap
          swap();
        }
      }
    }

    
    
    System.err.println("his attacker at " + hisAttacker.x+","+hisAttacker.y + (hisAttacker.flag ? "(flag)": ""));
    System.err.println("his defender at " + hisDefender.x+","+hisDefender.y);
  }
}
