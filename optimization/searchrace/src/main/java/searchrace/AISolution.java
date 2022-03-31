package searchrace;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AISolution {
  private static final int DEPTH = 10;

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
  private boolean hasCrossedCheckpoint;
  
  private void fullRandom() {
    for (int t=0;t<DEPTH;t++) {
      
      angles[t] = random.nextInt(36+1) - 18;
      thrusts[t] = random.nextInt(201);

    }
  }

  private void pseudoRandomFar() {
    for (int t=0;t<DEPTH;t++) {
      
      angles[t] = random.nextInt(36+1) - 18;
      
      double rthrust = random.nextDouble();
      if (rthrust > 0.9) {
        thrusts[t] = Player.MAX_SPEED;
      } else {
        thrusts[t] = random.nextInt(Player.MAX_SPEED+1);
      }

    }
  }
  
  
  public void applyOn(State work) {
    aiScore = 0.0;
    hasCrossedCheckpoint = false;
    
    int originalCheckpoint = work.checkpointIndex;
    for (int d=0;d<DEPTH;d++) {
      int lastCp = work.checkpointIndex;
      
      work.apply(angles[d], thrusts[d]);
      aiScore += depthFactor[d] * eval(work, originalCheckpoint, lastCp);
    }
    
    if (work.checkpointIndex != originalCheckpoint) {
      hasCrossedCheckpoint = true;
    }
  }

  public void debug(State work) {
    aiScore = 0.0;
    int currentCheckpoint = work.checkpointIndex;
    for (int d=0;d<DEPTH;d++) {
      int lastCp = work.checkpointIndex;
      work.apply(angles[d], thrusts[d]);
      double scoreAtDepth = eval(work, currentCheckpoint, lastCp);
      System.err.println("Applying "+angles[d] + " "+thrusts[d]);
      System.err.println("  Score @depth "+d+" = "+scoreAtDepth);
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
      
      double distToEnd = State.distanceRemaining[current.checkpointIndex] + distToNextCheckPoint;
      
      // 200 -> 0
      double speed = Math.sqrt(current.vx * current.vx + current.vy * current.vy);


      double score = 0.0; 
      
      score += (-1.0 * distToEnd);
      score += (1_000_000 * (current.checkpointIndex - lastCheckpoint));

      double angleDirectionToNextCheckpoint = 1.0 * (
                (State.checkpointX[current.checkpointIndex] - current.x) * State.cosinuses[current.angle] 
              + (State.checkpointY[current.checkpointIndex] - current.y) * State.sinuses[current.angle] 
            ) / (distToNextCheckPoint);
      
      double angleDirectionToNext2Checkpoint = 1.0 * ((State.checkpointX[current.checkpointIndex+1] - State.checkpointX[current.checkpointIndex]) * State.cosinuses[current.angle] + (State.checkpointY[current.checkpointIndex+1] - State.checkpointY[current.checkpointIndex]) * State.sinuses[current.angle] ) / (distToNextCheckPoint);
      score += 1.0 * angleDirectionToNextCheckpoint;

      if (speed > 0) {
        double speedDirectionToNextCheckpoint = 1.0 * ((State.checkpointX[current.checkpointIndex] - current.x) * current.vx + (State.checkpointY[current.checkpointIndex] - current.y) * current.vy) / (speed * distToNextCheckPoint);
        double speedDirectionToNext2Checkpoint = 1.0 * ((State.checkpointX[current.checkpointIndex+1] - State.checkpointX[current.checkpointIndex]) * current.vx + (State.checkpointY[current.checkpointIndex+1] - State.checkpointY[current.checkpointIndex]) * current.vy) / (speed * distToNextCheckPoint);
        score += 50.0 * speedDirectionToNext2Checkpoint / (distToNextCheckPoint - 600);
      }

      
      if (current.finished) {
        score += 10_000_000;
      } else {
        //score += 2.0 * directionToNext2Checkpoint;
      }
      
      //score += 5_000.0 * angleDirectionToNextCheckpoint;
      //score += 10_000.0 * speedDirectionToNextCheckpoint;

      
      
      if (current.checkpointIndex != lastCheckpoint) {
          // 200 -> 0
          // 1 = same dir
          // -1 = opposite dir
      }

      
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
    this.hasCrossedCheckpoint = model.hasCrossedCheckpoint;
  }

  public void merge(AISolution sol1, AISolution sol2) {
    if ((sol1 == sol2 || random.nextDouble() > 0.9)) {
      createRandom(); // TODO find better ?
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
      this.angles[i] = old.angles[i+1];
      this.thrusts[i] = old.thrusts[i+1];
    }    
    this.angles[DEPTH-1] = 0;
    this.thrusts[DEPTH-1] = 0;
  }

  public void createRandom() {
    pseudoRandomFar();
  }

  public boolean hasCrossedCheckpoint() {
    return hasCrossedCheckpoint;
  }

}
