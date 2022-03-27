package searchrace;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AISolution {
  private static final int DEPTH = 10;
  private static final int MAX_SPEED = 200;

  private static final Random random = ThreadLocalRandom.current();
  private static double[] depthFactor = new double[DEPTH];
  static {
    depthFactor[0] = 1.0;
    for (int i=1;i<DEPTH;i++) {
      depthFactor[i] = 0.9 * depthFactor[i-1];
    }
  }
  
  public int[] angles = new int[DEPTH];
  public int[] thrusts = new int[DEPTH];
  public double aiScore;
  
  public void pseudoRandom() {
    for (int t=0;t<DEPTH;t++) {
      
      if (random.nextDouble() > 0.5) {
        angles[t] = random.nextInt(11) - 5;
      } else {
        angles[t] = random.nextInt(36+1) - 18;
      }
      
      if (random.nextDouble() > 0.7) {
        thrusts[t] = 200;
      } else {
        thrusts[t] = random.nextInt(201);
      }

    }
  }

  public void apply(State work) {
    aiScore = 0.0;
    int originalCheckpoint = work.checkpointIndex;
    for (int d=0;d<DEPTH;d++) {
      int lastCp = work.checkpointIndex;
      
      work.apply(angles[d], thrusts[d]);
      aiScore += depthFactor[d] * eval(work, originalCheckpoint, lastCp);
    }
  }

  public void debug(State work) {
    aiScore = 0.0;
    int currentCheckpoint = work.checkpointIndex;
    for (int d=0;d<DEPTH;d++) {
      int lastCp = work.checkpointIndex;
      work.apply(angles[d], thrusts[d]);
      double scoreAtDepth = eval(work, currentCheckpoint, lastCp);
      System.err.println("Score @depth "+d+" = "+scoreAtDepth);
      if (lastCp != work.checkpointIndex) System.err.println("     => CP here");
      aiScore += depthFactor[d] * scoreAtDepth;
    }
    
    
    System.err.println("Score of best solution : " + aiScore);
    System.err.println("Angle : "+ angles[0]);
    System.err.println("Thrust : "+thrusts[0]);
    
  }

  
  private double eval(State current, int firstCheckpoint, int lastCheckpoint) {
    

      double distToNextCheckPoint =Math.sqrt( 
          (current.x - State.checkpointX[current.checkpointIndex])*(current.x - State.checkpointX[current.checkpointIndex])
          + (current.y - State.checkpointY[current.checkpointIndex])*(current.y - State.checkpointY[current.checkpointIndex])
          )
          ;
      // 200 -> 0
      double speed = Math.sqrt(current.vx * current.vx + current.vy * current.vy);


      double score = 0.0; 
      int distToCurrentCheckPoint = 
              (current.x - State.checkpointX[current.checkpointIndex]) * (current.x - State.checkpointX[current.checkpointIndex]) 
            + (current.y - State.checkpointY[current.checkpointIndex]) * (current.y - State.checkpointY[current.checkpointIndex]);
      
      score += (-0.000001 * distToCurrentCheckPoint);
      score += (1_000_000 * (current.checkpointIndex - lastCheckpoint));

      double directionToNextCheckpoint = 1.0 * ((State.checkpointX[current.checkpointIndex] - current.x) * current.vx + (State.checkpointY[current.checkpointIndex] - current.y) * current.vy) / (speed * distToNextCheckPoint);
      score += 1.0 * directionToNextCheckpoint;
      
      if (current.checkpointIndex != lastCheckpoint) {
          // 200 -> 0
          // 1 = same dir
          // -1 = opposite dir
          score += 100.0 * directionToNextCheckpoint;
      }

      if (current.finished) score += 10_000_000;
      
//      score += (10_000 * (current.checkpointIndex - firstCheckpoint ));
//      score -= 0.2 * distToNextCheckPoint;
//      score += 0.1 * speed;
//
//      double directionToNextCheckpoint = 
//          1.0 * ((State.checkpointX[current.checkpointIndex] - current.x) * current.vx
//              + (State.checkpointY[current.checkpointIndex] - current.y) * current.vy)
//          / (speed * distToNextCheckPoint) ;
//      
//      score +=  1_000.0 * directionToNextCheckpoint;
//      
//      
//      if (current.checkpointIndex != firstCheckpoint) {
//        
//        // 1 = same dir
//        // -1 = opposite dir
//        
//      }
      return score;
  }


  public void updateScore(double eval) {
    this.aiScore = eval;
  }

  public void copyFrom(AISolution model) {
    for (int i=0;i<DEPTH;i++) {
      angles[i] = model.angles[i];
      thrusts[i] = model.thrusts[i];
    }
    this.aiScore = model.aiScore;
  }

  public void merge(AISolution sol1, AISolution sol2) {
    if ((sol1 == sol2 || random.nextDouble() > 0.9)) {
      pseudoRandom(); // TODO find better ?
    } else {
      for (int i=0;i<DEPTH;i++) {
        
        double randA = random.nextDouble();
        double randT = random.nextDouble();
        this.angles[i] = (int)(randA * sol1.angles[i] + (1.0-randA) * sol2.angles[i]);
        this.thrusts[i] = (int)(randT * sol1.thrusts[i] + (1.0-randT) * sol2.thrusts[i]);
      }
    }
  }

  public void reinitFromLast(AISolution old) {
    for (int i=0;i<DEPTH-1;i++) {
      this.angles[i] = this.angles[i+1];
      this.thrusts[i] = this.thrusts[i+1];
    }    
    this.angles[DEPTH-1] = 0;
    this.thrusts[DEPTH-1] = 0;
    
  }

}
