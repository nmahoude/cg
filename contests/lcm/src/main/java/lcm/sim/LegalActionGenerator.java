package lcm.sim;

import lcm.Agent;
import lcm.State;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.CardType;
import lcm.cards.Location;

public class LegalActionGenerator {
  static Location MY_HAND, MY_BOARD, HIS_BOARD;
  static Agent me, opp;
  
  public static int updateLegalActions(Action[] legalActions, State state, Action lastAction, boolean p1) {
    setInitialValue(state, p1);
    
    int legalActionsFE = 0;

    legalActions[legalActionsFE++] = Action.endTurn();
    if (me.health <=0 || opp.health <=0) {
      // winning condition, end game
      return legalActionsFE; 
    }
    
    
    int cardsOnMyBoard = me.getBoardCardsCount();
    boolean hasGuard = opp.hasGuard();

    for (int i = state.cardsFE-1; i >=0 ; i--) {
      Card card = state.cards[i];
      if (card.location == MY_HAND) {
        if (card.cost > me.mana) continue;
        if (card.type == CardType.CREATURE && cardsOnMyBoard == Constants.MAX_CREATURES_ON_BOARD) continue;
        
        legalActionsFE = computeHandActions(legalActionsFE, legalActions, state, card, hasGuard);
      } else if (card.location == MY_BOARD) {
        if (!card.canAttack) continue;
        if (card.attack <= 0) continue;
        if (lastAction.target != -1) {
          Card oppC = state.cards[lastAction.target];
          if (oppC.location == HIS_BOARD) {
            legalActions[legalActionsFE++] = Action.attack(card.stateIndex, oppC.stateIndex);
          } else {
            // attack all, our target is dead ..
            legalActionsFE = computeAttackActions(legalActions, state, legalActionsFE, hasGuard, card);
          }
        } else {
          legalActionsFE = computeAttackActions(legalActions, state, legalActionsFE, hasGuard, card);
        }
      }
    }
    return legalActionsFE;
  }

  private static void setInitialValue(State state, boolean p1) {
    if (p1) {
      MY_HAND = Location.MY_HAND;
      MY_BOARD = Location.MY_BOARD;
      HIS_BOARD = Location.HIS_BOARD;
      me = state.me;
      opp = state.opp;
    } else {
      MY_HAND = Location.HIS_HAND;
      MY_BOARD = Location.HIS_BOARD;
      HIS_BOARD = Location.MY_BOARD;
      me = state.opp;
      opp = state.me;
    }
  }

  private static int computeAttackActions(Action[] legalActions, State state, int legalActionsFE, boolean hasGuard, Card card) {
    if (!hasGuard ) {
      legalActions[legalActionsFE++] = Action.attack(card.stateIndex, Card.opponent.stateIndex);
    }
    legalActionsFE = attackCreatures(legalActions, state, legalActionsFE, hasGuard, card);
    return legalActionsFE;
  }

  private static int attackCreatures(Action[] legalActions, State state, int legalActionsFE, boolean hasGuard, Card card) {
    for (int j = 0; j < state.cardsFE; j++) {
      Card oppC = state.cards[j];
      if (oppC.location != HIS_BOARD) continue;
      if (hasGuard && (oppC.abilities & Abilities.GUARD) == 0) continue;
      legalActions[legalActionsFE++] = Action.attack(card.stateIndex, oppC.stateIndex);
    }
    return legalActionsFE;
  }

  private static int computeHandActions(int legalActionsFE, Action[] legalActions, State state, Card card, boolean hasGuard) {
    if (card.type == CardType.CREATURE) {
      legalActions[legalActionsFE++] = Action.summon(card.stateIndex);
    } else if (card.type == CardType.ITEM_RED) {
      for (int j = 0; j < state.cardsFE; j++) {
        Card oppC = state.cards[j];
        if (oppC.location != HIS_BOARD)
          continue;
        legalActions[legalActionsFE++] = Action.use(card.stateIndex, oppC.stateIndex);
      }
    } else if (card.type == CardType.ITEM_GREEN) {
      for (int j = 0; j < state.cardsFE; j++) {
        Card target = state.cards[j];
        if (target.location == MY_BOARD) {
          legalActions[legalActionsFE++] = Action.use(card.stateIndex, target.stateIndex);
        }
      }
    } else if (card.type == CardType.ITEM_BLUE) {
      // if the card deals damage to creatures, it will always be better to send it to creature than sending to opponent directly, 
      // but in case there is no creatures, try the opponent anyway

      boolean foundSuitableCreature = false;
      if (card.defense < 0) {
        for (int j = 0; j < state.cardsFE; j++) {
          Card oppC = state.cards[j];
          if (oppC.location != HIS_BOARD) continue;
          legalActions[legalActionsFE++] = Action.use(card.stateIndex, oppC.stateIndex);
          foundSuitableCreature = true;
        }
      }
      if (!foundSuitableCreature) {
        // it may be worth attacking the opponent directly in this case
        legalActions[legalActionsFE++] = Action.use(card.stateIndex);
      }
    }
    return legalActionsFE;
  }

}
