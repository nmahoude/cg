package lcm.predictors;

import lcm.State;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.Location;

public class AggressorDetector {

  public static boolean aggressor(State state) {
    if (state.opp.getBoardCardsCount() == 0) {
      return true;
    }
    
    double myAtk = 0;
    double hisDef = 0;
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_BOARD) {
        myAtk += card.attack;
      }
      if (card.location == Location.HIS_BOARD) {
        hisDef+= card.defense;
      }
    }
    double myBonusAtk = 0;
    double hisBonusDef = 0;
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_BOARD) {
        if ((card.abilities & Abilities.LETHAL) != 0) {
          myBonusAtk += 1.0 * hisDef / state.opp.getBoardCardsCount();
        }
      }
      if (card.location == Location.HIS_BOARD) {
        if ((card.abilities & Abilities.GUARD) != 0) {
          myBonusAtk += 1.0 * myAtk / Math.max(1, state.me.getBoardCardsCount());
        }
      }
    }
    
    myAtk += myBonusAtk;
    hisDef += hisBonusDef;
        
    if (myAtk > hisDef) {
      return true;
    } else {
      return false;
    }
  }
}
