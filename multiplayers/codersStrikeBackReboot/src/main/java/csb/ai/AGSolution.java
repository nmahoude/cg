package csb.ai;

import java.util.Random;

import csb.simulation.Action;

public class AGSolution {
  private static Random random = new Random();
  
  public static final int ACTION_SIZE = 6;

  public static final Action[] noActions;
  static {
    noActions = new Action[ACTION_SIZE];
    for (int i=0;i<ACTION_SIZE;i++) {
      noActions[i] = new Action();
    }
  }
  
  public Action[] actions0 = new Action[ACTION_SIZE];
  public Action[] actions1 = new Action[ACTION_SIZE];
  public double energy;
 
  
  public void copy(AGSolution model) {
    for (int i=0;i<ACTION_SIZE;i++) {
      actions0[i] = model.actions0[i];
      actions1[i] = model.actions1[i];
    }    
  }

  public void copyFromPreviousTurnBest(AGSolution best) {
    for (int i=0;i<ACTION_SIZE-1;i++) {
      actions0[i] = best.actions0[i+1];
      actions1[i] = best.actions1[i+1];
    }    
    randomizeMoveAtIndex(ACTION_SIZE-1);
  }


  public void randomizeLastMove() {
    randomizeMoveAtIndex(ACTION_SIZE-1);
  }


  public void randomize() {
    for (int i=0;i<ACTION_SIZE;i++) {
      randomizeMoveAtIndex(i);
    }
  }


  private void randomizeMoveAtIndex(int i) {
    actions0[i] = new Action();
    getRandomAction0(i);
    
    actions1[i] = new Action();
    getRandomAction1(i);
  }

  private void getRandomAction0(int i) {
    actions0[i].angle = (0.5 - random.nextDouble())* 2 *  Math.PI / 10;
    actions0[i].thrust = 100; //0 + 1+random.nextInt(100); // TODO review the distribution toward 100 !
  }

  private void getRandomAction1(int i) {
    actions1[i].angle = (0.5 - random.nextDouble())* 2 * Math.PI / 10;
    actions1[i].thrust = 100;//0 + 1+random.nextInt(100); // TODO review the distribution toward 100 !
  }

  public void cross(AGSolution parent1, AGSolution parent2) {
    for (int i=0;i<ACTION_SIZE;i++) {
      actions0[i] = random.nextBoolean() ? parent1.actions0[i] : parent2.actions0[i]; 
      actions1[i] = random.nextBoolean() ? parent1.actions1[i] : parent2.actions1[i]; 
    }    
  }

  public void mutate() {
    for (int i=0;i<ACTION_SIZE;i++) {
      if (random.nextInt(10) >= 9) {
        getRandomAction0(i);
      }
      if (random.nextInt(10) >= 9) {
        getRandomAction1(i);
      }
    }    
  }
}
