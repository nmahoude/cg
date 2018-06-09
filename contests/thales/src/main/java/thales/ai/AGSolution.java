package thales.ai;

import thales.Player;
import thales.UFO;

public class AGSolution {
  
  public static final int DEPTH = 2;
  public static final int WIDTH = 2*DEPTH;
  
  private static final int INV_MUTATION_RATE = 100;
  
  public double angles[] = new double[WIDTH];
  public double thrusts[] = new double[WIDTH];
  public double energy = 0;

  String output[] = new String[2];
  
  public AGSolution() {
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
    double rndThrust = Player.rand.nextDouble();
    if (rndThrust < 0.05) {
      thrusts[depth] = -100; // TODO boost 
    } else if (rndThrust > 0.5) {
      thrusts[depth] = 1.0;
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
      return 0; // shield
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
    return angle * Math.PI;
  }
  
  public double getThrust(int depth) {
    double thrust = thrusts[depth];

    if (thrust < -1 ) {
      return 500;
    }
    return thrust * 100;
  }

  public String[] output() {
    UFO ufo ;
    
    ufo = Player.teams[0].ufos[0];
    int x = (int)(ufo.x + 1000 * Math.cos(getAngle(0)));
    int y = (int)(ufo.y + 1000 * Math.sin(getAngle(0)));
    double thrust = getThrust(0);
    if (thrust > 400) {
      output[0] = ""+x+" "+y+" BOOST";
    } else {
      output[0] = ""+x+" "+y+" "+(int)thrust;
    }
    
    ufo = Player.teams[0].ufos[1];
    x = (int)(ufo.x + 1000 * Math.cos(getAngle(AGSolution.DEPTH + 0)));
    y = (int)(ufo.y + 1000 * Math.sin(getAngle(AGSolution.DEPTH + 0)));
    thrust = getThrust(AGSolution.DEPTH + 0);
    if (thrust > 400) {
      output[1] = ""+x+" "+y+" BOOST";
    } else {
      output[1] = ""+x+" "+y+" "+(int)thrust;
    }
    
    return output;
  }

  public void apply(int depth) {
    Player.teams[0].ufos[0].apply(getAngle(depth), getThrust(depth));
    Player.teams[0].ufos[1].apply(getAngle(AGSolution.DEPTH + depth), getThrust(AGSolution.DEPTH + depth));
  }

  public void debug() {
    System.err.println(String.format("UFO 0 / angle : %f , thrust : %f",getAngle(0),getThrust(0)));
    System.err.println(String.format("UFO 1 / angle : %f , thrust : %f",getAngle(AGSolution.DEPTH +0),getThrust(AGSolution.DEPTH +0)));
  }
}

