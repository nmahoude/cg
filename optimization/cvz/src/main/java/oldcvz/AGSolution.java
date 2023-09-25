package oldcvz;

public class AGSolution {

  private State state;

  public double energy;

  public void reset() {
  }

  public void setup(State state) {
    this.state = state;
  }

  public void calculateEnergy(Simulation simulation) {
    state = simulation.state;
    energy = 0;
    
    Human bestHuman = closerHuman(state);
    if (bestHuman == null || state.aliveHumans == 0) {
      energy = -1_000_000;
      return;
    }
    
    
    double ashToBest = simulation.nextPos.distTo(bestHuman.p);
    energy = 0.0 
        - ashToBest 
        + 10_000 * state.aliveHumans
        + 0.0;

    for (int i=0;i<simulation.steps;i++) {
      energy += 1000 * simulation.scores[i] * Simulation.depth[i];
    }
    
    
    if (state.aliveZombies == 0) {
      energy += 1_000_000; // game end
    }
  }

  static int fibValues[];

  static {
    fibValues = new int[] { 0, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89 };
  }

  int fib(int n) {
    return fibValues[n];
  }

  public Human closerHuman(State state) {
    Human bestHuman = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (Human h : state.humans) {
      if (h.dead) continue;
      
      Zombie z = h.getCloserZombie(state.zombies);
      int aStepsToH = (int)(1.0 * (state.ash.p.distTo(h.p)) / Simulation.ASH_MOVE);
      int zStepsToH;
      if (z != null) {
        zStepsToH = (int)(z.p.distTo(h.p) / Simulation.ZOMBIE_MOVE);
      } else {
        zStepsToH = 1_000_000;
      }
      
      double score = zStepsToH - aStepsToH;
      if (score > bestScore) {
        bestScore = score;
        bestHuman = h;
      }
    }
/*
 *     if (bestHuman != null) {
 
      System.err.println("Best human is " + bestHuman.id + " at " + bestHuman.p);
    }
*/
    
    return bestHuman;
  }

  int deadZombiesThisTurn() {
    int deadZombies = 0;
    for (Zombie z : state.zombies) {
      if (z.deadThisTurn)
        deadZombies++;
    }
    return deadZombies;
  }

  private double minDistToZombies() {
    Ash ash = state.ash;
    double minDist = 1_000_000;
    for (Zombie z : state.zombies) {
      if (z.dead)
        continue;
      double dist = ash.p.distTo(z.p);
      if (dist < minDist) {
        minDist = dist;
      }
    }
    return 1.0 - minDist / (16000 + 9000);
  }
}