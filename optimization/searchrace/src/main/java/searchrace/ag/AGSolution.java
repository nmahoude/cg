package searchrace.ag;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import searchrace.State;

public class AGSolution {
  private static final int DEPTH = 10;
  private static final int MAX_SPEED = 200;

  private static final Random random = ThreadLocalRandom.current();
  private static double[] depthFactor = new double[DEPTH];
  static {
    depthFactor[0] = 1.0;
    for (int i=1;i<DEPTH;i++) {
      depthFactor[i] = 0.6 * depthFactor[i-1];
    }
  }
  
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
      if (rThrust > 0.7) {
        thrusts[t] = 200;
      } else if (rThrust > 0.5) {
        thrusts[t] = 100 + random.nextInt(101);
      } else {
        thrusts[t] = random.nextInt(100);
      }
    }
  }

  public void apply(State work) {
    score = 0.0;
    for (int i=0;i<DEPTH;i++) {
      work.apply(angles[i], thrusts[i]);
      score += depthFactor[i] * eval(i, work);
    }
  }

  private double eval(int depth, State current) {
      double distToNextCheckPoint =Math.sqrt( 
          (current.x - State.checkpointX[current.checkpointIndex])*(current.x - State.checkpointX[current.checkpointIndex])
          + (current.y - State.checkpointY[current.checkpointIndex])*(current.y - State.checkpointY[current.checkpointIndex])
          )
          ;
      
      // 200 -> 0
      double speed = Math.sqrt(current.vx * current.vx + current.vy * current.vy);
      
      // 1 = same dir
      // -1 = opposite dir
      double directionToNextCheckpoint = 
              1.0 * ((State.checkpointX[current.checkpointIndex] - current.x) * current.vx
              + (State.checkpointY[current.checkpointIndex] - current.y) * current.vy)
          / (speed * distToNextCheckPoint);
          
      
      double score = 0.0;
      //score += 1000 * (current.checkpointIndex - original.checkpointIndex );
      
      score -= 2000 * 
          (State.distanceRemaining[current.checkpointIndex] + distToNextCheckPoint);
      
//      score -= 1 * distToNextCheckPoint;
      
      //score += 100 * directionToNextCheckpoint;
      //score += 1.0 * speed / MAX_SPEED;
      //score += 10.0 * exitSpeedFeature(original, current);
      score += current.finished ? 1_000_000 : 0;
      return score;
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
