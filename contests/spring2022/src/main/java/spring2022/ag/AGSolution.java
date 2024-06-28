package spring2022.ag;

import java.util.concurrent.ThreadLocalRandom;

import spring2022.ai.TriAction;

public class AGSolution {

  private static ThreadLocalRandom random = ThreadLocalRandom.current();
  public static AGEvaluator evaluator = new AGEvaluator();
  private TriAction actions = new TriAction();
  
  public static final double[] cos = new double[360];
  public static final double[] sin = new double[360];
  static {
    for (int a=0;a<360;a++) {
      cos[a] = Math.cos(Math.PI * a / 180);
      sin[a] = Math.sin(Math.PI * a / 180);
    }
  }
  public static final double[] depthFactor = new double[AG.MAX_DEPTH];
  static {
    for (int i=0;i<AG.MAX_DEPTH;i++) {
      depthFactor[i] = Math.pow(0.9, i);
    }
  }
  
  
  // 0 = angle, 1 = speed
  public Chromosome[][] c = new Chromosome[AG.MAX_DEPTH][2]; 
  {
    for (int i=0;i<AG.MAX_DEPTH;i++) {
      for (int agent = 0;agent<2;agent++) {
        c[i][agent] = new Chromosome();
      }
    }
  }

  public double score;
  
  
  public void random() {
    for (int i=0;i<AG.DEPTH;i++) {
      for (int agent = 0;agent<2;agent++) {
        c[i][agent].random();

        if (i < 3 && random.nextDouble() > 0.95) {
          c[i][agent].speed = -1;
          c[i][agent].angle = -1;
        }
      }
    }
  }
  
  public void dispatch(int agent, int d, int max, int speed) {
    for (int i=0;i<AG.DEPTH;i++) {
      c[i][agent].angle = 360 * d / max;
      c[i][agent].speed= speed;
    }
  }

  public void dispatchOne(int agent, int d, int max, int speed) {
    c[0][agent].angle = 360 * d / max;
    c[0][agent].speed= speed;

    for (int i=1;i<AG.DEPTH;i++) {
      c[i][agent].random();
    }
  }  

  
  
  public void informedRandom(AGInformation information) {
    double pushThreshold = 0.97;
    int pushDephtThreshold = 3;
    
    if (information.deadlyDanger) {
      pushThreshold = 0.7;
      pushDephtThreshold = 1;
    } else if (information.oppAttacker != null) {
      pushThreshold = 0.9;
      pushDephtThreshold = AG.DEPTH;
    }
    
    for (int i=0;i<AG.DEPTH;i++) {
      for (int agent = 0;agent<2;agent++) {
        
        if (information.isInCorner[agent]) {
          c[i][agent].randomFromCorner();
        } else {
          c[i][agent].random();
        }

        if (i < pushDephtThreshold && random.nextDouble() > pushThreshold) {
          c[i][agent].speed = -1;
          c[i][agent].angle = -1;
        }
        
        if (information.shouldShield[agent]) {
          c[i][agent].speed = -9000;
          c[i][agent].angle = agent;
        }
        
      }
    }
    
    /*
    if (information.controlAttacker) {
      // Tant pis on choisi de maniere fixe
      if (information.canControl[0]) {
        c[0][0].speed = -666;
        c[0][0].angle = information.unitToControl;
      } else if (information.canControl[1]) {
        c[0][1].speed = -666;
        c[0][1].angle = information.unitToControl;
      }
    }
    */
    
  }

  public void applyOn(LightState state, TriAction oppActions) {
    score = 0.0;

    // 1st step with the opp actions
    actions.actions[0].updateFromAGValues(state.hero[0], c[0][0].angle, c[0][0].speed);
    actions.actions[1].updateFromAGValues(state.hero[1], c[0][1].angle, c[0][1].speed);
    Simulator.apply(state, 0, actions, oppActions);
    score += depthFactor[0] * evaluator.evaluate(state);


    // remaining steps without opp actions
    for (int d=1;d<AG.DEPTH;d++) {
      actions.actions[0].updateFromAGValues(state.hero[0], c[d][0].angle, c[d][0].speed);
      actions.actions[1].updateFromAGValues(state.hero[1], c[d][1].angle, c[d][1].speed);
      Simulator.apply(state, d, actions);
      score += depthFactor[d] * evaluator.evaluate(state);
    }
    score += evaluator.evaluate(state);
  }

  public void copyFrom(AGSolution model) {
    for (int i=0;i<AG.DEPTH;i++) {
      c[i][0].copyFrom(model.c[i][0]);
      c[i][1].copyFrom(model.c[i][1]);
      
      score = model.score;
    }
  }

  public static void merge(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    double choice = random.nextDouble();
    if (choice < 0.2) {
      crossOver(sol1, sol2, dest1, dest2);
    } else if (choice < 0.4) {
      softRandom(sol1, sol2, dest1, dest2);
    } else if (choice < 0.45) {
      fullRandom(dest1, dest2);
    } else {
      // last choice
      copyAndMutate(sol1, sol2, dest1, dest2);
    }
    
  }

  private static void crossOverAndMutate(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    for (int i=0;i<AG.DEPTH;i++) {
      if (random.nextDouble() > 0.9) {
        dest1.c[i][0].mutate(sol1.c[i][0]);
      } else {
        dest1.c[i][0].copyFrom(sol1.c[i][0]);
      }
      if (random.nextDouble() > 0.9) {
        dest1.c[i][1].mutate(sol2.c[i][1]);
      } else {
        dest1.c[i][1].copyFrom(sol2.c[i][1]);
      }
      
      if (random.nextDouble() > 0.9) {
        dest2.c[i][0].mutate(sol1.c[i][0]);
      } else {
        dest2.c[i][0].copyFrom(sol1.c[i][0]);
      }
      if (random.nextDouble() > 0.9) {
        dest2.c[i][1].mutate(sol2.c[i][1]);
      } else {
        dest2.c[i][1].copyFrom(sol2.c[i][1]);
      }
    }
  }

  private static void fullRandom(AGSolution dest1, AGSolution dest2) {
    dest1.random();
    dest2.random();
  }

  private static void crossOver(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    for (int i=0;i<AG.DEPTH;i++) {
      dest1.c[i][0].copyFrom(sol1.c[i][0]);
      dest1.c[i][1].copyFrom(sol2.c[i][1]);
 
      dest2.c[i][0].copyFrom(sol2.c[i][0]);
      dest2.c[i][1].copyFrom(sol1.c[i][1]);
    }
  }

  private static void copyAndMutate(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    double mutationThreshold = 0.75;
    for (int i = 0; i < AG.DEPTH; i++) {
      if (random.nextDouble() > mutationThreshold) {
        dest1.c[i][0].mutate(sol1.c[i][0]);
      } else {
        dest1.c[i][0].copyFrom(sol1.c[i][0]);
      }
      if (random.nextDouble() > mutationThreshold) {
        dest1.c[i][1].mutate(sol1.c[i][1]);
      } else {
        dest1.c[i][1].copyFrom(sol1.c[i][1]);
      }
      if (random.nextDouble() > mutationThreshold) {
        dest2.c[i][0].mutate(sol2.c[i][0]);
      } else {
        dest2.c[i][0].copyFrom(sol2.c[i][0]);
      }
      if (random.nextDouble() > mutationThreshold) {
        dest2.c[i][1].mutate(sol2.c[i][1]);
      } else {
        dest2.c[i][1].copyFrom(sol2.c[i][1]);
      }
    }
  }

  private static void softRandom(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    double rerandomThreshold = 0.90;
    
    for (int i=0;i<AG.DEPTH;i++) {
      if (random.nextDouble() > rerandomThreshold) {
        dest1.c[i][0].random();
      } else {
        dest1.c[i][0].copyFrom(sol1.c[i][0]);
      }
      if (random.nextDouble() > rerandomThreshold) {
        dest1.c[i][1].random();
      } else {
        dest1.c[i][1].copyFrom(sol1.c[i][1]);
      }
      
      if (random.nextDouble() > rerandomThreshold) {
        dest2.c[i][0].random();
      } else {
        dest2.c[i][0].copyFrom(sol2.c[i][0]);
      }
      if (random.nextDouble() > rerandomThreshold) {
        dest2.c[i][1].random();
      } else {
        dest2.c[i][1].copyFrom(sol2.c[i][1]);
      }
    }
  }
  
  public void decalFromLastTurn() {
    for (int i=0;i<AG.DEPTH-1;i++) {
      c[i][0].copyFrom(c[i+1][0]);
      c[i][1].copyFrom(c[i+1][1]);
    }    

    c[AG.DEPTH-1][0].random();
    c[AG.DEPTH-1][1].random();
  }

  public void doWind(int id, int step, int maxStep) {
    c[0][id].speed = -1;
    c[0][id].angle = 360 * step / maxStep;
    
  }
  
}
