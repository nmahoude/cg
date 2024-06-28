package sg22;

import sg22.Actions.Action;
import sg22.nodes.Node;

public class Simulator {

  public void simulate(Node node) {

    simulate(node.s, node.action);
  }

  
  public void simulate(State s, Action action) {
    if (action.type == Action.WAIT) {
      if (s.phase == GamePhase.PLAY) {
        s.playCount = 0;
        s.phase = GamePhase.RELEASE;
      } else if (s.phase == GamePhase.RELEASE) {
        s.phase = GamePhase.END;
        s.discardAllCards();
      } else {
        throw new RuntimeException("can't wait on phase "+s.phase +" ????");
      }
      return;
    }
    
    
    if (action.type == Action.MOVE || action.type == Action.MOVE_AND_GET) {
      int newLoc = action.target;
      
      int throwCount = 0;
      int giveCount = 0;
      if (newLoc < s.agents[0].location) {
        throwCount = 2;
      }
      if (s.isOppNear(newLoc)) {
        giveCount = 1;
      }
      
      s.throwCount = throwCount;
      s.giveCount = giveCount;
      s.playCount = 1;
      s.cardToGet = s.cardsOnDesks.count[action.target2] > 0 ? action.target2 : Cards.BONUS; 
      
      s.agents[0].location = newLoc;
      s.updateNextPhase();
      return;
    }
    
    
    if (action.type == Action.THROW) {
      s.throwCount--;
      s.playCount = 1;

      if (action.target == -1) {
        s.hand.addDebt(Hand.LOC_HAND, 1); 
      } else {
        s.hand.remove(Hand.LOC_HAND, action.target);
        s.cardsOnDesks.put(action.target);
      }
      
      s.updateNextPhase();
      return;
      
    }
    
    if (action.type == Action.GIVE) {
      s.giveCount = 0;
      s.playCount = 1;

      if (action.target != -1) {
        s.hand.remove(Hand.LOC_HAND, action.target);
        // s.opponent.put(action.target);
      } else {
        s.hand.addDebt(Hand.LOC_HAND, 2);
      }
      s.updateNextPhase();
      return;
    }

    if (action.type == Action.RELEASE) {
      s.agents[0].score++;

      int debt = State.applicationById[action.target].canFinish(s.hand);
      s.hand.addDebt(Hand.LOC_HAND, debt);
      
      // replace "permanent" skills
      s.hand.put(Hand.LOC_DISCARD, Hand.DAILY_ROUTINE, s.agents[0].permanentDailyRoutineCards);
      s.hand.put(Hand.LOC_DISCARD, Hand.ARCHITECTURE_STUDY, s.agents[0].permanentArchitectureStudyCards);
      s.agents[0].permanentDailyRoutineCards = 0;
      s.agents[0].permanentArchitectureStudyCards = 0;

      s.phase = GamePhase.END; // TODO MOVE ?
      return;
    }
    
    
    // all the play from here
    s.playCount--;
    
    if (action.type == Action.PLAY_TRAINING) {
      s.playCard(Cards.TRAINING);
      s.hand.cardsToDraw+=2;
      tryToDrawCards(s, 2);
      s.playCount+=1;
      return;
    }
    
    if (action.type == Action.PLAY_CODING) {
      s.playCard(Cards.CODING);
      s.hand.cardsToDraw+=1;
      tryToDrawCards(s, 1);
      s.playCount+=2;
    }

    if (action.type == Action.PLAY_DAILY) {
      s.hand.remove(Hand.LOC_HAND, Cards.DAILY_ROUTINE);
      s.agents[0].permanentDailyRoutineCards++;
    }
    
    if (action.type == Action.PLAY_TASK_PRIORIZATION) {
      s.playCard(Cards.TASK_PRIORIZATION);
      
      s.hand.remove(Hand.LOC_HAND, action.target);
      s.cardsOnDesks.put(action.target);
      
      s.cardsOnDesks.remove(action.target2);
      s.hand.put(Hand.LOC_HAND, action.target2);
    }
    
    if (action.type == Action.PLAY_ARCHI) {
      s.hand.remove(Hand.LOC_HAND, Cards.ARCHITECTURE_STUDY);
      s.agents[0].permanentArchitectureStudyCards++;
    }
    
    if (action.type == Action.PLAY_CI) {
      s.playCard(Cards.CI);
      s.hand.remove(Hand.LOC_HAND, action.target);
      s.hand.put(Hand.LOC_AUTOMATED, action.target);
    }
    
    if (action.type == Action.PLAY_CODE_REVIEW) {
      s.playCard(Cards.CODE_REVIEW);
      s.hand.addBonus(Hand.LOC_DISCARD, 2);
    }
    
    if (action.type == Action.PLAY_REFACTORING) {
      s.playCard(Cards.REFACTORING);
      s.hand.removeDebt(Hand.LOC_HAND, 1);
    }

    s.updateNextPhase();
    return;
  }

  
  public void tryToDrawCards(State state, int cardsToDraw) {
    int count = state.hand.totalCount(Hand.LOC_DRAW);
    
    if (count == 0 ) {
      // get the same with discarded
      count = state.hand.totalCount(Hand.LOC_DISCARD);
      if (count > 0 && count <= cardsToDraw) {
        // transfer all discarded cards to hand
        for (int i=0;i<10;i++) {
          state.hand.put(Hand.LOC_HAND, i, state.hand.get(Hand.LOC_DISCARD, i));
          state.hand.remove(Hand.LOC_DISCARD, i, state.hand.get(Hand.LOC_DISCARD, i));
        }
      }
      
    } else {
      if (count <= cardsToDraw) {
        // transfer all draw cards to hand
        for (int i=0;i<10;i++) {
          state.hand.put(Hand.LOC_HAND, i, state.hand.get(Hand.LOC_DRAW, i));
          state.hand.remove(Hand.LOC_DRAW, i, state.hand.get(Hand.LOC_DRAW, i));

        }
      }
    }
  }
}
