package sg22.ais;

import sg22.Application;
import sg22.Cards;
import sg22.State;
import sg22.Actions.Action;
import sg22.nodes.Node;

public class GiveAI {
  private final Action action = new Action();

  final int cardByValueOrder[] = new int[] { 0, 1, 6, 7, 8, 5, 3, 2, 4 };
  final int[] totalNeeded = new int[9];
  
  public Action think(Node root) {
    State state = root.s;
    System.err.println("Give AI ...");
    
    
    calculateNeeded(state);
    
    
    // 1st : give cards that can't be used for application
    for (int j=0;j<9;j++) {
      int i = cardByValueOrder[j];
      
      if (state.hand.getHandCount(i) == 0) continue;
      if (totalNeeded[i] == 0) {
        System.err.println("Give a card that can't be used anymore");
        return action.give(i);
      }
    }
    
    // 2nd : give cards he has a lot
    for (int j=0;j<9;j++) {
      int i = cardByValueOrder[j];
      if (state.hand.getHandCount(i) == 0) continue;
      
      if (state.opponent.count[i] + state.opponentAutomated.count[i] >= 2) {
        System.err.println("Give a card that he has a lot");
        return action.give(i);
      }
    }
    
    // 3rd : give the least used card
    int bestScore = Integer.MAX_VALUE;
    int bestIndex = -1;
    for (int j=0;j<9;j++) {
      int i = cardByValueOrder[j];
      if (state.hand.getHandCount(i) == 0) continue;
    
      if (totalNeeded[i] < bestScore) {
        bestScore = totalNeeded[i];
        bestIndex = i;
      }
    }
    if (bestIndex != -1) {
      System.err.println("Give a card that is least used in applications");

      return action.give(bestIndex);
    }
    
    // 4th ... give the first that is not 0
    for (int j=0;j<9;j++) {
      int i = cardByValueOrder[j];
      if (state.hand.getHandCount(i) == 0) continue;
      
      System.err.println("Give the first card that I have");
      return action.give(i);
    }
    
    return action.give(-1); // should not happen
  }

  private void calculateNeeded(State state) {
    for (int i=0;i<8;i++) {
      totalNeeded[i]=0; 
    }
    
    for (Application a : State.applications) {
      for (int i=0;i<8;i++) {
        if (a.needed[i] > 0 ) totalNeeded[i]+=2; 
      }
    }
    totalNeeded[Cards.BONUS] = state.applications.size(); // weird TODO better ?
  }
}
