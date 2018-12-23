package lcm.sim;


import java.util.List;

import lcm.Agent;
import lcm.Player;
import lcm.State;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.CardType;
import lcm.cards.Location;

public class Simulation {
  private State state;
  private Agent me;
  private Agent opp;
  Location MY_BOARD = Location.MY_BOARD;
  Location HIS_BOARD = Location.HIS_BOARD;
  
  public static int simulations;
  
  public State simulate(State state, List<Action> actions) {
    return simulate(state, actions, true);
  }
  public State simulate(State state, List<Action> actions, boolean p1) {
    State newState = Cache.popState();
    newState.copyFrom(state);

    this.state = newState;
    if (p1) {
      this.me = newState.me;
      this.opp = newState.opp;
      MY_BOARD = Location.MY_BOARD;
      HIS_BOARD = Location.HIS_BOARD;
    } else {
      this.me = newState.opp;
      this.opp = newState.me;
      MY_BOARD = Location.HIS_BOARD;
      HIS_BOARD = Location.MY_BOARD;
    }
    for (Action action : actions) {
      switch(action.type) {
      case ATTACK:
        resolveAttack(action.from, action.target);
        break;
      case PASS:
        resolveCardDrawForNextPlayer();
        break;
      case SUMMON:
        summon(action.from);
        break;
      case USE:
        useCard(action.from, action.target);
        break;
      default:
        throw new RuntimeException("Unknown action : " + action.type);
      }
    }
    return newState;
  }

  public void resolveCardDrawForNextPlayer() {
    int delta = opp.deck - opp.nextTurnDraw;
    if (delta >= 0) {
      opp.deck -= delta;
      // TODO actually draw some cards ?
    } else {
      delta =-delta;
      if (delta >= opp.rune) {
        opp.rune = 0;
        opp.health = 0;
      } else {
        opp.rune -= delta;
        opp.health = 5 * opp.rune;
      }
    }
  }
  
  public void simulate(State state, Action action) {
    simulate(state, action, true);
  }
  
  public void simulate(State state, Action action, boolean p1) {
    this.state = state;
    if (p1) {
      this.me = state.me;
      this.opp = state.opp;
      MY_BOARD = Location.MY_BOARD;
      HIS_BOARD = Location.HIS_BOARD;
    } else {
      this.me = state.opp;
      this.opp = state.me;
      MY_BOARD = Location.HIS_BOARD;
      HIS_BOARD = Location.MY_BOARD;
    }
    
    simulations++;
    
    switch(action.type) {
    case ATTACK:
      resolveAttack(action.from, action.target);
      break;
    case PASS:
      resolveCardDrawForNextPlayer();
      break;
    case SUMMON:
      summon(action.from);
      break;
    case USE:
      useCard(action.from, action.target);
      break;
    default:
      throw new RuntimeException("Unknown action : " + action.type);
    }
  }

  private void useCard(int index, int targetIndex) {
    Card item = state.cards[index];
    
    Card target = targetIndex == -1 ? Card.opponent : state.cards[targetIndex];
    
    if (Player.SIM_CHECK_LEGAL_ACTIONS && !checkLegalUse(item, target)) {
      if (Player.ILLEGAL_ACTION_CRASH) {
        throw new RuntimeException("Illegal use " + item.id + " on "+target.id);
      }
      return;
    }
    
    Card copyOfItem = replaceCard(item);
    
    me.removeHandCard(copyOfItem);
    me.mana -= copyOfItem.cost;
    
    Card copyOfTarget = replaceCard(target);
    copyOfTarget.applyItem(copyOfTarget.location == MY_BOARD ? me : opp, copyOfItem);
    
    me.modifyHealth(copyOfItem.myHealthChange);
    opp.modifyHealth(copyOfItem.opponentHealthChange);
    me.nextTurnDraw += copyOfItem.cardDraw;

    copyOfItem.calculateHash();
    copyOfTarget.calculateHash();
  }

  private boolean checkLegalUse(Card item, Card target) {
    if (target.location != MY_BOARD 
        && target.location != HIS_BOARD
        && target != Card.opponent) return false;
    if (item.cost > me.mana) return false;
    if (item.type == CardType.ITEM_GREEN) return target.location == MY_BOARD;
    if (item.type == CardType.ITEM_RED) return target.location == HIS_BOARD;

    
    if (item.type == CardType.ITEM_BLUE) {
      return target == Card.opponent 
          || (item.defense < 0 && target.location == HIS_BOARD);
    }
    return true;
  }

  private void resolveAttack(int index, int ocIndex) {
    final Card card = state.cards[index];
    final Card target = ocIndex == -1 ? Card.opponent : state.cards[ocIndex];
    if (Player.SIM_CHECK_LEGAL_ACTIONS && !checkLegalAttack(card, target)) {
      if (Player.ILLEGAL_ACTION_CRASH) {
        throw new RuntimeException("Illegal attack " + card.id + " on "+target.id);
      }
      return;
    }

    Card copyOfCard = replaceCard(card);

    if (target == Card.opponent) {
      attackPlayer(copyOfCard);
      copyOfCard.calculateHash();

    } else {
      Card copyOfTarget = replaceCard(target);
      attackCreature(copyOfCard, copyOfTarget);

      copyOfCard.calculateHash();
      copyOfTarget.calculateHash();
    }
  }

  private boolean checkLegalAttack(Card attacker, Card oc) {
    if (!attacker.canAttack)  {
      return false;
    }
    
    if ((oc.abilities & Abilities.GUARD) == 0 && opp.hasGuard()) {
      return false; 
    }
    
    return true;
  }

  private void attackPlayer(Card attacker) {
    attacker.canAttack = false;
    attacker.hasAttacked = true;

    int healthGain = ((attacker.abilities & Abilities.DRAIN) != 0) ? attacker.attack : 0;
    int healthTaken = -attacker.attack;

    me.modifyHealth(healthGain);
    opp.modifyHealth(healthTaken);
    
  }

  private void attackCreature(Card attacker, Card defender) {
    
    attacker.canAttack = false;
    attacker.hasAttacked = true;

    int damageGiven = ((defender.abilities & Abilities.WARD) != 0) ? 0 : attacker.attack;
    int damageTaken = ((attacker.abilities & Abilities.WARD) != 0) ? 0 : defender.attack;

    if ((defender.abilities & Abilities.WARD) != 0 && attacker.attack > 0) {
      defender.abilities = defender.abilities & ~Abilities.WARD;
    }
    if ((attacker.abilities & Abilities.WARD) != 0 && defender.attack > 0) {
      attacker.abilities = attacker.abilities & ~Abilities.WARD;
    }

    int healthGain = 0;
    int healthTaken = 0;

    // attacking
    if (damageGiven >= defender.defense) opp.kill(defender);
    if ((attacker.abilities & Abilities.BREAKTHROUGH) != 0 && defender.location == Location.GRAVEYARD) healthTaken = defender.defense - damageGiven;
    if ((attacker.abilities & Abilities.LETHAL) != 0 && damageGiven > 0) opp.kill(defender);
    if ((attacker.abilities & Abilities.DRAIN) != 0 && damageGiven > 0) healthGain = attacker.attack;
    if (defender.location != Location.GRAVEYARD) defender.defense -= damageGiven;

    // defending
    if (damageTaken >= attacker.defense) me.kill(attacker);
    if ((defender.abilities & Abilities.LETHAL) != 0 && damageTaken > 0) me.kill(attacker);
    if (attacker.location != Location.GRAVEYARD) attacker.defense -= damageTaken;

    if (healthGain > 0) {
      me.modifyHealth(healthGain);
    } 
    if (healthTaken > 0) {
      opp.modifyHealth(healthTaken);
    }
  }

  private void summon(int index) {
    Card card = state.cards[index];
    if (Player.SIM_CHECK_LEGAL_ACTIONS && !checkLegalSummon(card)) {
      if (Player.ILLEGAL_ACTION_CRASH) {
        throw new RuntimeException("Illegal summon " + card.id );
      }

      return;
    }

    Card copyOfCard = replaceCard(card);
    
    me.summon(copyOfCard);
    
    me.mana -= copyOfCard.cost;
    me.modifyHealth(copyOfCard.myHealthChange);
    me.nextTurnDraw += copyOfCard.cardDraw;
    opp.modifyHealth(copyOfCard.opponentHealthChange);
    
    copyOfCard.calculateHash();

  }

  private final Card replaceCard(final Card card) {
    if (card == Card.opponent) return card;
    
    Card copyOfCard = Cache.pop();
    copyOfCard.copyFrom(card);
    state.cards[copyOfCard.stateIndex] = copyOfCard;
    return copyOfCard;
  }

  private boolean checkLegalSummon(Card card) {
    if (me.getBoardCardsCount() == Constants.MAX_CREATURES_ON_BOARD) {
      if (Player.DEBUG_SIM) System.err.println("Too many creature, can't summon another");
      return false;
    }
    if (card.cost > me.mana) {
      if (Player.DEBUG_SIM) System.err.println("Can't summon card " + card.id + " cost : " + card.cost + " vs " + me.mana);
      return false;
    }
    if (card.type != CardType.CREATURE) {
      if (Player.DEBUG_SIM) System.err.println("Can't summon items, use USE instead");
      return false;
    }
    return true;
  }
}
