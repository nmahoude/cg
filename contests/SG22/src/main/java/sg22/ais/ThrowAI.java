package sg22.ais;

import sg22.Application;
import sg22.Cards;
import sg22.State;
import sg22.Actions.Action;
import sg22.nodes.Node;

public class ThrowAI {
  private final Action action = new Action();

  final int cardByValueOrder[] = new int[] { 0, 1, 6, 7, 8, 5, 3, 2, 4 };
  final int[] totalNeeded = new int[9];
  
  public Action think(Node root) {
    State state = root.s;
    System.err.println("Throw AI ...");
    
    calculateNeeded(state);
    
    
    // 1st throw the card not needed in applications
    for (int j=0;j<9;j++) {
      int i = cardByValueOrder[j];
      
      if (state.hand.getHandCount(i) == 0) continue;
      if (totalNeeded[i] == 0) {
        System.err.println("Throw a card that can't be used anymore => "+Cards.toName(i));
        return action.throwCard(i);
      }
    }
    
    // 2nd throw card when we have a lot
    Cards myTotal = state.myTotal();
    for (int j=0;j<9;j++) {
      int i = cardByValueOrder[j];
      if (state.hand.getHandCount(i) == 0) continue;
      
      if (myTotal.count[i] > 2) {
        System.err.println("Throw a card that we have a lot "+ Cards.toName(i)+" with count "+myTotal.count[i]);
        return action.throwCard(i);
      }
    }
    
    
    // last in order ...
    for (int j=0;j<9;j++) {
      int i = cardByValueOrder[j];
      if (state.hand.getHandCount(i) > 0) {
        System.err.println("Throw carfd by order ... "+ Cards.toName(i));
        return action.throwCard(i);
      }
    }
    
    
    return action;
  }
  
  private void calculateNeeded(State state) {
    // calculate how many cards of each are needed for applications
    for (int i=0;i<8;i++) {
      totalNeeded[i]=0; 
    }
    
    for (Application a : State.applications) {
      for (int i=0;i<8;i++) {
        if (a.needed[i]> 0) totalNeeded[i]+=2; 
      }
    }
    totalNeeded[Cards.BONUS] = State.applications.size(); // weird TODO better ?
  }
}
