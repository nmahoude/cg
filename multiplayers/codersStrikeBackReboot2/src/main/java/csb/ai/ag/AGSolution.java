package csb.ai.ag;

import csb.GameState;
import csb.Player;
import csb.ai.AISolution;
import trigonometry.Point;
import trigonometry.Vector;

public class AGSolution implements AISolution {
  
  public static final int DEPTH = 6;
  public static final int WIDTH = 2*DEPTH;
  
  private static final int INV_MUTATION_RATE = 100;
  
  GameState state;
  
  public double angles[] = new double[WIDTH];
  public double thrusts[] = new double[WIDTH];
  public double energy = 0;

  String output[] = new String[2];
  public Point finalPosition0 = new Point(0,0);
  public Point finalPosition1 = new Point(0,0);
  
  public AGSolution(GameState state) {
    this.state = state;
  }
  
  public void clear() {
    energy = 0;
  }
  
  public void copyFrom(AGSolution sol) {
    energy = sol.energy;
    for (int i=0;i<WIDTH;i++) {
      angles[i] = sol.angles[i];
      thrusts[i] = sol.thrusts[i];
    }
  }
  
  public void copyFromPreviousBest(AGSolution sol) {
    for (int i=0;i<WIDTH-1;i++) {
      angles[i] = sol.angles[i+1];
      thrusts[i] = sol.thrusts[i+1];
    }
    randomize(WIDTH-1);
  }
  
  public void randomize() {
    for (int i=0;i<WIDTH;i++) {
      randomize(i);
    }
  }

  void randomize(int depth) {
    angles[depth] = Player.rand.nextDouble();  // 0->1 linear
    if (Player.rand.nextDouble() < 0.05) {
      thrusts[depth] = -1; // shield 
    } else {
      thrusts[depth] = Player.rand.nextDouble(); // 0->1  linear
    }
  }
  
  public static void crossOver(AGSolution child1, AGSolution child2, AGSolution parent1, AGSolution parent2) {
    double beta = Player.rand.nextDouble();
    for (int i=0;i<WIDTH;i++) {
      child1.angles[i] = getAcceptableAngle(beta, parent1.angles[i], parent2.angles[i]);
      child1.thrusts[i] = getAcceptableThrust(beta, parent1.thrusts[i], parent2.thrusts[i]);

      child2.angles[i] = getAcceptableAngle(1.0-beta, parent1.angles[i], parent2.angles[i]);
      child2.thrusts[i] = getAcceptableThrust(1.0-beta, parent1.thrusts[i], parent2.thrusts[i]);
    }
  }

  private static double getAcceptableThrust(double beta, double d, double e) {
    if (d < 0 || e < 0) {
      return -1; // shield
    }
    return beta * (d-e) + e;
  }

  private static double getAcceptableAngle(double beta, double a, double b) {
    double angle = beta * (b-a) + a;
    
    if (angle < 0) angle += 1.0;
    if (angle > 1) angle -= 1.0;
    return angle;
  }
  
  public void mutate() {
    for (int i=0;i<WIDTH;i++) {
      if (Player.rand.nextInt(INV_MUTATION_RATE) == 0) {
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
    if (thrust < 0) {
      return -1;
    } else {
      return thrust * 200;
    }
  }

  @Override
  public String[] output() {
    Vector dot1 = state.pods[0].direction.rotate(getAngle(0)).dot(1000.0);
    if (getThrust(0) < 0) {
      output[0] = ""+(int)(state.pods[0].x+dot1.vx)
          +" "+(int)(state.pods[0].y+dot1.vy)
          +" SHIELD";
      state.pods[0].shield = 3;
    } else {
      output[0] = ""+(int)(state.pods[0].x+dot1.vx)
              +" "+(int)(state.pods[0].y+dot1.vy)
              +" "+(int)getThrust(0);
    }
    
    Vector dot2 = state.pods[1].direction.rotate(getAngle(WIDTH/2+0)).dot(1000.0);
    if (getThrust(WIDTH/2+0) < 0) {
      output[1] = ""+(int)(state.pods[1].x+dot2.vx)
          +" "+(int)(state.pods[1].y+dot2.vy)
          +" SHIELD";
      state.pods[1].shield = 3;
    } else {
      output[1] = ""+(int)(state.pods[1].x+dot2.vx)
        +" "+(int)(state.pods[1].y+dot2.vy)
        +" "+(int)getThrust(WIDTH/2+0);
    }    
    return output;
  }
}
