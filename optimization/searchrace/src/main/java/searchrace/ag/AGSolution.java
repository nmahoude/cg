package searchrace.ag;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import searchrace.State;

public class AGSolution {
  private static final int DEPTH = 10;
  private static final Random random = ThreadLocalRandom.current();

  int angles[] = new int[DEPTH];
  int thrusts[] = new int[DEPTH];
  double score;
  
  public void pseudoRandom() {
    for (int t=0;t<DEPTH;t++) {
      
      double rAngle = random.nextDouble();
      if (rAngle > 0.7) {
        angles[t] = 0;
      } else {
        angles[t] = random.nextInt(36+1) - 18;
      }
      
      double rThrust = random.nextDouble();
      thrusts[t] = random.nextInt(201);
    }
  }

  public void apply(State work) {
    for (int i=0;i<DEPTH;i++) {
      work.apply(angles[i], thrusts[i]);
      if (work.finished) break;
    }
  }

  public void updateScore(double eval) {
    this.score = eval;
  }

  public void copyFrom(AGSolution model) {
    for (int i=0;i<DEPTH;i++) {
      angles[i] = model.angles[i];
      thrusts[i] = model.thrusts[i];
    }
    this.score = model.score;
  }

  public void merge(AGSolution sol1, AGSolution sol2) {
    if ((sol1 == sol2 || random.nextDouble() > 0.9)) {
      pseudoRandom(); // TODO find better
    } else {
      for (int i=0;i<DEPTH;i++) {
        
        double randA = random.nextDouble();
        double randT = random.nextDouble();
        this.angles[i] = (int)(randA * sol1.angles[i] + (1.0-randA) * sol2.angles[i]);
        this.thrusts[i] = (int)(randT * sol1.thrusts[i] + (1.0-randT) * sol2.thrusts[i]);
      }
    }
  }

  public void decal() {
    for (int i=0;i<DEPTH-1;i++) {
      this.angles[i] = this.angles[i+1];
      this.thrusts[i] = this.thrusts[i+1];
    }    
  }
  
  
  
}
