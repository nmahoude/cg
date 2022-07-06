package csb.ai;

import java.util.concurrent.ThreadLocalRandom;

import csb.State;
import trigonometry.Vector;

public class AGSolution {
  private static final Evaluator EVALUATOR = new Evaluator();

  private static final ThreadLocalRandom random = ThreadLocalRandom.current();
  
  public static final int DEPTH = 10;
  public double angles[] = new double[DEPTH * 2];
  public double thrusts[] = new double[DEPTH * 2];

  public double score;
  
  public void fullRandom() {
    for (int t=0;t<2 * DEPTH;t++) {
      angles[t] = random.nextDouble();  // 0->1 linear
      
      
      if (random.nextDouble() < 0.05) {
        thrusts[t] = -1; // shield 
      } else {
        thrusts[t] = random.nextDouble(); // 0->1  linear
      }
    }
  }

  public double apply(State state) {
    score = 0.0;
    
    for (int d=0;d<DEPTH;d++) {
      apply(state, d);
      score += EVALUATOR.evaluate(state, this, d);
    }
    return score;
  }

  public void apply(State state, int d) {
    state.apply(getAngle(d), getThrust(d), getAngle(d+DEPTH), getThrust(d+DEPTH));
  }

  
  public void copyFrom(AGSolution model) {
    for (int i=0;i<DEPTH*2;i++) {
      this.angles[i] = model.angles[i];
      this.thrusts[i] = model.thrusts[i];
    }
  }
  
  public void output(State state) {
    state.restore();
    
    
    Vector dot1 = state.pods[0].direction.rotate(getAngle(0)).dot(1000.0);
    if (getThrust(0) < 0) {
      System.out.println(""+(int)(state.pods[0].x+dot1.vx)
          +" "+(int)(state.pods[0].y+dot1.vy)
          +" SHIELD");
      state.pods[0].shield = 3;
    } else {
      System.out.println(""+(int)(state.pods[0].x+dot1.vx)
              +" "+(int)(state.pods[0].y+dot1.vy)
              +" "+(int)getThrust(0));
    }
    
    Vector dot2 = state.pods[1].direction.rotate(getAngle(DEPTH+0)).dot(1000.0);
    if (getThrust(DEPTH+0) < 0) {
      System.out.println(""+(int)(state.pods[1].x+dot2.vx)
          +" "+(int)(state.pods[1].y+dot2.vy)
          +" SHIELD");
      state.pods[1].shield = 3;
    } else {
      System.out.println(""+(int)(state.pods[1].x+dot2.vx)
        +" "+(int)(state.pods[1].y+dot2.vy)
        +" "+(int)getThrust(DEPTH+0));
    }    
  }
  
  public double getAngle(int depth) {
    double angle = (0.5 - angles[depth]) * 2; // [-1 ; 1]
    return angle * Math.PI / 10;
  }
  public double getThrust(int depth) {
    // TODO handle shield and BOOST
    double thrust = thrusts[depth];
    if (thrust < 0) {
      return -1;
    } else {
      return thrust * 200;
    }
  }

}
