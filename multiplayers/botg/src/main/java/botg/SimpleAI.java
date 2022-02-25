package botg;

public class SimpleAI {
  Action bestAction = Action.WAIT;
  
  public void think(State state) {
    if (state.roundType < 0) {
      bestAction = Action.CHOOSE_HULK;
    } else {
      bestAction = Action.ATTACK_NEAREST_HERO;
    }
    
  }

  
  public void output() {
    System.out.println(bestAction);
  }


}
