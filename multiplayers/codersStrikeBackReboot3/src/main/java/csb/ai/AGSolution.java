package csb.ai;

import java.util.concurrent.ThreadLocalRandom;

import csb.State;
import csb.entities.CheckPoint;
import csb.entities.Pod;
import trigonometry.Vector;

public class AGSolution {
  private static final Evaluator EVALUATOR = new Evaluator();
  private static double PATIENCE_COEFF = 0.9;
  private static double[] patience = new double[AGSolution.DEPTH];
  static {
    for (int i=0;i<patience.length;i++) {
      patience[i] = Math.pow(PATIENCE_COEFF, i);
    }
  }
  
  
  private static final ThreadLocalRandom random = ThreadLocalRandom.current();
  
  public static final int DEPTH = 6;
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
    state.restore();

    score = 0.0;
    
    for (int d=0;d<DEPTH;d++) {
      apply(state, d);
      if (state.pods[0].lap == 3 || state.pods[1].lap == 3 ) {
      	score = 10_000_000+ (DEPTH - d);
      	return score;
      }
    	score += patience[d] * EVALUATOR.evaluate(state, this);
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
      if (state.turn == 1) {
      	System.out.println(""+(int)(state.pods[0].x+dot1.vx)
            +" "+(int)(state.pods[0].y+dot1.vy)
            +" "+"BOOST");
      } else {
      	System.out.println(""+(int)(state.pods[0].x+dot1.vx)
              +" "+(int)(state.pods[0].y+dot1.vy)
              +" "+(int)getThrust(0));
      }
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
  	double genome = angles[depth];
  	if (genome <= 0.2) genome = -1.0;
  	else if (genome >= 0.4 && genome <= 0.6) genome = 0.0;
  	else if (genome >= 0.8) genome = 1.0;
  	else if (genome <= 0.4) genome = -1.0 + (genome - 0.2) * 5;
  	else if (genome >= 0.6) genome = 0.0 + (genome - 0.6) * 5;
  	
  	
    double angle = genome; // [-1 ; 1]
    return angle * Math.PI / 10;
  }
  public double getThrust(int depth) {
    // TODO handle shield and BOOST
    double genome = thrusts[depth];
    if (genome < 0) {
      return -1;
    } else if (genome > 0.6) {
    	return 200;
    } else {
      return (genome / 0.6) * 200;
    }
  }

	public static void merge(AGSolution parent1, 
													 AGSolution parent2, 
													 AGSolution child1,
													 AGSolution child2) {
		
		double chance = random.nextDouble();
		if (chance < 0.4) {
			child1.fullRandom();
			child2.fullRandom();
		} else if (chance < 0.6) {
			child1.oneRandom(parent1);
			child2.oneRandom(parent2);
		} else if (chance < 0.7) {
			mix(parent1, parent2, child1, child2);
		} else {
			child1.swap(parent1, parent2);
			child2.swap(parent2, parent1);
		}
	}

	public static void randomize(AGSolution parent1, 
			 AGSolution parent2, 
			 AGSolution child1,
			 AGSolution child2) {
		
		child1.fullRandom();
		child2.fullRandom();
		
		
	}

	
	private static void mix(AGSolution parent1, AGSolution parent2, AGSolution child1, AGSolution child2) {
		double coef = random.nextDouble();

		for (int i=0;i<2*DEPTH;i++) {
			child1.angles[i] = coef * parent1.angles[i] + (1-coef)*parent2.angles[i];
			child1.thrusts[i] = coef * parent1.thrusts[i] + (1-coef)*parent2.thrusts[i];

		
			child2.angles[i] = (1.0 - coef) * parent1.angles[i] + coef*parent2.angles[i];
			child2.thrusts[i] = (1.0 - coef) * parent1.thrusts[i] + coef*parent2.thrusts[i];
		
		
		}

	}

	private void oneRandom(AGSolution parent) {

		for (int i=0;i<2*DEPTH;i++) {
			this.angles[i] = parent.angles[i];
			this.thrusts[i] = parent.thrusts[i];
		}
		
		int index = random.nextInt(2*DEPTH);
		this.angles[index] = random.nextDouble();
		this.thrusts[index] = random.nextDouble();
	}

	private void swap(AGSolution parent1, AGSolution parent2) {
		for (int i=0;i<DEPTH;i++) {
			this.angles[i] = parent1.angles[i];
			this.thrusts[i] = parent1.thrusts[i];
		}
		for (int i=DEPTH;i<DEPTH*2;i++) {
			this.angles[i] = parent2.angles[i];
			this.thrusts[i] = parent2.thrusts[i];
		}
	}


	public void directBot(State state, int ev, double thrust) {
		state.restore();
		for (int i=0;i<DEPTH;i++) {
			for (int p=0;p<2;p++) {
				Pod pod = state.pods[p];
				CheckPoint cp = State.checkPoints[pod.nextCheckPointId];
				// target + ev * speed
				double targetx = cp.x; // + ev * pod.vx;
				double targety = cp.y; // + ev * pod.vy;
				
				Vector target = new Vector(targetx - pod.x, targety - pod.y).normalize();
		    double desiredAngle = Math.acos(state.pods[0].direction.dot(target)); // rads -PI -> PI
	
		    double angle = Math.min(Math.max(0.5 + Math.signum(state.pods[0].direction.ortho().dot(target)) * (desiredAngle / Math.PI), 0.0), 1.0);

		    this.angles[i + p*DEPTH] = angle; 
		    this.thrusts[i + p*DEPTH] = thrust; 
			}
			state.apply(this.angles[i],this.thrusts[i], this.angles[i+DEPTH],this.thrusts[i+DEPTH]);
		}
		
		
		
	}


}
