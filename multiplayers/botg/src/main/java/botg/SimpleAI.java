package botg;

public class SimpleAI {
  Action bestAction = Action.WAIT;
  
  public void think(State state) {
    if (state.roundType < 0) {
      bestAction = Action.CHOOSE_HULK;
      return;
    }
   
    System.err.println("Gold "+state.gold);
    if (state.gold > 0) {
      System.err.println("Can buy for "+state.gold);

      int bestScore = 0;
      Item bestItem = null;
      
      for (Item item : state.items) {
        if (item.cost > state.gold) continue;
        
        int score = 0;
        if (item.damage > 0) score += 100*item.damage;
        if (item.health > 0) score += 10*item.health;
        
        if (score > bestScore) {
          bestScore = score;
          bestItem = item;
        }
      }
      
      if (bestItem != null) {
        System.err.println("buying item "+bestItem.name +" with score "+bestScore);
        bestAction = new Action("BUY "+bestItem.name);
        return;
      }
      
    }
    
      
    bestAction = Action.ATTACK_NEAREST_HERO;
    
  }

  
  public void output() {
    System.out.println(bestAction);
  }


}
