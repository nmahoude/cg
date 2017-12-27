package meanmax.ai.wait;

import meanmax.ai.ag.AGSolution;

public class Wait {

  public int MAX_TIME;
  public AGSolution bestSolution = new AGSolution(null);

  public void think() {
    bestSolution.actions[0][0].thrust = 0;
    bestSolution.actions[0][1].thrust = 0;
    bestSolution.actions[0][2].thrust = 0;
  }

  public void output() {
    System.out.println("WAIT");
    System.out.println("WAIT");
    System.out.println("WAIT");
  }
}
