package cvz.simulation;

import cvz.GameState;
import cvz.entities.Ash;
import cvz.entities.Human;
import cvz.entities.Zombie;

public class AGSolution {
  private GameState state;

  public double energy;
  public int aliveHumans;

  private Human bestHuman;
  
  public void reset() {
  }
  
  public void setup(GameState state) {
    this.state = state;
    bestHuman = closerHuman();
  }
  
  public void calculateEnergy(Simulation simulation) {
    state = simulation.state;

    energy = 0;
    updateAliveHumans();
    if (aliveHumans == 0) {
      energy = -1_000_000;
      return;
    }
    
    energy = 0.0
        - state.ash.p.distTo(bestHuman.p);
        //+ 100*deadZombies()
        //+ minDistToZombies()
        ;
  }

  private Human closerHuman() {
    Human bestHuman = null;
    double bestScore = 0;
    for (Human h : state.humans) {
      if (h.dead) continue;
      Zombie z = h.getCloserZombie(state.zombies);
      double score = 1.0*z.p.distTo(h.p ) / 400 - 1.0*state.ash.p.distTo(h.p) / 1000;
      if (bestHuman == null || score > bestScore) {
        bestScore = score;
        bestHuman = h;
      }
    }
    System.err.println("Best human is "+bestHuman.id+" at "+bestHuman.p);
    return bestHuman;
  }

  private void updateAliveHumans() {
    aliveHumans = state.humans.length;
    for (Human h : state.humans) {
      if (h.dead) aliveHumans--;
    }
  }

  private double deadZombies() {
    int deadZombies = 0;
    for (Zombie z : state.zombies) {
      if (z.dead) deadZombies ++;
    }
    return deadZombies;
  }

  private double minDistToZombies() {
    Ash ash = state.ash;

    double minDist = 1_000_000;
    for (Zombie z : state.zombies) {
      if (z.dead) continue;
      
      double dist = ash.p.distTo(z.p);
      if (dist < minDist) {
        minDist = dist;
      }
    }
    return 1.0 - minDist / (16000+9000);
  }

}
