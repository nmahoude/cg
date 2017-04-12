package csb.ai.ag;

import java.util.Random;

import csb.GameState;
import csb.ai.AISolution;
import trigonometry.Point;
import trigonometry.Vector;

public class AGSolution implements AISolution {
  public static final int DEPTH = 2*6;
  public static final Random rand = new Random();
  private static final int INV_MUTATION_RATE = 100;
  
  GameState state;
  
  public double angles[] = new double[DEPTH];
  public double thrusts[] = new double[DEPTH];
  public double energy = 0;

  String output[] = new String[2];
  public Point finalPosition0 = new Point(0,0);
  public Point finalPosition1 = new Point(0,0);
  
  public AGSolution(GameState state) {
    this.state = state;
  }
  
  public void copyFrom(AGSolution sol) {
    for (int i=0;i<DEPTH;i++) {
      angles[i] = sol.angles[i];
      thrusts[i] = sol.thrusts[i];
    }
  }
  
  public void copyFromPreviousBest(AGSolution sol) {
    for (int i=0;i<DEPTH-1;i++) {
      angles[i] = sol.angles[i+1];
      thrusts[i] = sol.thrusts[i+1];
    }
    randomize(DEPTH-1);
  }
  
  public void randomize() {
    for (int i=0;i<DEPTH;i++) {
      randomize(i);
    }
  }

  private void randomize(int depth) {
    angles[depth] = rand.nextDouble();                 // 0->1 linear
    thrusts[depth] = Math.min(1.0, Math.abs(rand.nextGaussian())); // 0->1 in a gaussian curve (thrust max is better) 
  }
  
  public void cross(AGSolution parent1, AGSolution parent2) {
    for (int i=0;i<DEPTH;i++) {
      angles[i] = rand.nextBoolean() ? parent1.angles[i] : parent2.angles[i];
      thrusts[i] = rand.nextBoolean() ? parent1.thrusts[i] : parent2.thrusts[i];
    }
  }
  
  public void mutate() {
    for (int i=0;i<DEPTH;i++) {
      if (rand.nextInt(INV_MUTATION_RATE) == 0) {
        randomize(i);
      }
    }    
  }
  
  public double getAngle(int depth) {
    double angle = (0.5 - angles[depth]) * 2; // [-1 ; 1]
    return angle * Math.PI / 10;
  }
  public double getThrust(int depth) {
    // TODO handle shield and BOOST
    double thrust = thrusts[depth];
    return thrust * 100;
  }

  @Override
  public String[] output() {
    Vector dot1 = state.pods[0].direction.rotate(getAngle(0)).dot(1000.0);
    output[0] = ""+(int)(state.pods[0].x+dot1.vx)
              +" "+(int)(state.pods[0].y+dot1.vy)
              +" "+(int)getThrust(0);

    Vector dot2 = state.pods[1].direction.rotate(getAngle(DEPTH/2+0)).dot(1000.0);
    output[1] = ""+(int)(state.pods[1].x+dot2.vx)
        +" "+(int)(state.pods[1].y+dot2.vy)
        +" "+(int)getThrust(DEPTH/2+0);
    
    return output;
  }
}
