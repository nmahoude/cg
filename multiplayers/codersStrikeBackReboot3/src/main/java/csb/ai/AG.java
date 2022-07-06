package csb.ai;

import csb.State;

public class AG {

  public static final int POPULATION_SIZE = 100;
  public static final int SURVIVOR_SIZE = 30;
  public static final int DEPTH = 10;
  
  public AGSolution solutions[] = new AGSolution[POPULATION_SIZE];
  
  public AG() {
    for (int i=0;i<POPULATION_SIZE;i++) {
      solutions[i] = new AGSolution();
    }
  }
  
  
  
  public void doOnePly(State state) {
    // TODO Auto-generated method stub
    
  }



  public void resetAG() {
    // TODO Auto-generated method stub
    
  }



  public void initFullRandomPopulation(State state) {
    for (int i=0;i<POPULATION_SIZE;i++) {
      solutions[i].fullRandom();
    }
  }

}
