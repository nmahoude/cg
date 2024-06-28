package sg22.ais;

import sg22.Application;
import sg22.Cards;
import sg22.Hand;
import sg22.State;
import sg22.Actions.Action;
import sg22.nodes.Node;

/**
 * simple IFFFT to play cards
 * 
 *
 */
public class PlayCardAI {
  final Action action = new Action();
  final int[] totalNeeded = new int[9];

  public Action think(Node root) {
    State state = root.s;
    
    System.err.println("AI to play cards ");

    action.doWait();
    
    updateTotalNeeded(state);
    
    
    if (state.hand.getHandCount(Cards.CI) > 0) {
      int bestIndex = cardsToCI(state);
      if (bestIndex != -1) {
        return action.ci(bestIndex);
      }
    }
    
    
    // 1st the action where i can play again
    if (state.hand.getHandCount(Cards.CODING) > 0) {
      if (state.hand.getHandCount(Cards.CODING) == 1 && cardsToCI(state) == Cards.CODING) {
      // keep the card
      } else {
        // can play twice
        return action.coding();
      }
    }
    if (state.hand.getHandCount(Cards.TRAINING) > 0) {
      if (state.hand.getHandCount(Cards.TRAINING) == 1 && cardsToCI(state) == Cards.TRAINING) {
      // keep the card
      } else {
        // can play again
        return action.training();
      }
    }

    
      
    // play archi & daily
    if (state.agents[0].score == 4) {
      if (state.hand.getHandCount(Cards.ARCHITECTURE_STUDY) > 0) {
        return action.archi();
      }
      if (state.hand.getHandCount(Cards.DAILY_ROUTINE) > 0) {
        return action.daily();
      }
    }
    
    // 2nd 'permanent' actions
    if (state.hand.getHandCount(Cards.ARCHITECTURE_STUDY) > 0) {
      return action.archi();
    }
    
    if (state.hand.getHandCount(Cards.DAILY_ROUTINE) > 0) {
      return action.daily();
    }
    
    // 3rd other actions
    if (state.hand.getHandCount(Cards.TASK_PRIORIZATION) > 0) {
      // TODO
      state.hand.remove(Hand.LOC_HAND, Cards.TASK_PRIORIZATION);
      int uselessCardIndex = uselessCard(state);
      state.hand.put(Hand.LOC_HAND, Cards.TASK_PRIORIZATION);

      if (uselessCardIndex != -1) {
        // we found a card to discard, let's go
        
        
        int bestScore = 0;
        int bestIndex = -1;
        for (int i=0;i<=8;i++) {
          // TODO check if interesting relative to remaining applications ...
          if (state.cardsOnDesks.count[i] <= 0) continue;
          
          if (totalNeeded[i] > bestScore && state.hand.get(Hand.LOC_AUTOMATED, i) < 2) {
            bestScore = totalNeeded[i];
            bestIndex = i;
          }
        }
        
        if (bestIndex != -1) {
          return action.taskPriorization(uselessCardIndex, bestIndex);
        }

      }      
    }
    
    
    if (state.hand.getHandCount(Cards.CODE_REVIEW) > 0) {
      return action.codeReview();
    }
    
    if (state.hand.getHandCount(Cards.REFACTORING) > 0) {
      if (state.hand.getHandCount(Cards.DEBT) > 0) {
        return action.refactoring(); // remove one debt
      }
    }
    
    
    
    
    return action;
  }



  private int cardsToCI(State state) {
    if (state.hand.getHandCount(Cards.CI) <= 0 ) return -1;
    
    int bestScore = 0;
    int bestIndex = -1;
    
    if (bestIndex == -1 && state.hand.getHandCount(Cards.BONUS) >= 1 && state.hand.get(Hand.LOC_AUTOMATED, Cards.BONUS) < 4) {
      return Cards.BONUS;
    }
    
    state.hand.remove(Hand.LOC_HAND, Cards.CI);
    
    for (int i=0;i<=8;i++) {
      if (state.hand.getHandCount(i) <= 0) continue;
      
      if (totalNeeded[i] > bestScore) {
        bestScore = totalNeeded[i];
        bestIndex = i;
      }
    }
    state.hand.put(Hand.LOC_HAND, Cards.CI);

    return bestIndex;
  }
  
  
  
  private void updateTotalNeeded(State state) {
    for (int i=0;i<8;i++) {
      totalNeeded[i] = 0; 
    }
    
    for (Application a : State.applications) {
      for (int i=0;i<8;i++) {
        if (a.needed[i]> 0) totalNeeded[i]+=2; 
      }
    }
    
    totalNeeded[Cards.BONUS] = State.applications.size();
  }



  private int uselessCard(State state) {

    for (int i=0;i<8;i++) {
      // TODO check if interesting relative to remaining applications ...
      if (state.hand.getHandCount(i) <= 0) continue;
      if (totalNeeded[i] == 0 || state.hand.get(Hand.LOC_AUTOMATED, i) == 2) {
        return i;
      }
    }    
    
    return -1;
  }
}
