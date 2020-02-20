package lcm.ai.eval;

import lcm.State;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.Location;

public class NewEval implements IEval {

  private State state;

  @Override
  public double eval(State state, boolean debug) {
    this.state = state;
    
    if (state.opp.health <= 0) return 1_000_000;
    if (state.me.health <= 0) return -1_000_000;

    
    double total = 0.0;
    
    
    total += lifeScore();
    
    if (state.opp.getBoardCardsCount() == 0) {
      total += 10.0; // bonus to clear
    }
    
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_BOARD) {
        total += evaluateMyCard(card);
      } else if (card.location == Location.HIS_BOARD) {
        total -= evaluateEnnemyCard(card);
      }
    }
    
    return total;
  }

  private double lifeScore() {
    double retval = 0;
    
    int hpboarder = 10;
    int aggroboarder = 11;

    retval += state.me.handCardsCount * 5;

    retval += state.me.maxMana;
    retval -= state.opp.maxMana;
    
    if (state.me.health > hpboarder) {
      retval += state.me.health;
    } else {
      retval -= 2 * (hpboarder + 1 - state.me.health) * (hpboarder + 1 - state.me.health);
    }

    if (state.opp.health > aggroboarder) {
      retval += -state.opp.health;
    } else {
      retval += 3 * (aggroboarder + 1 - state.opp.health);
    }
    
    return retval;
  }

  private double evaluateMyCard(Card m) {
    double retval = 5;
    retval += m.defense * 2;
    retval += m.attack * 2;
    if (m.hasWard()) retval += 1;
    if (m.attack <= 2 && m.defense <= 2 && !m.hasWard()) {
      retval -= 5;
    }
    
    if (m.hasWard() && m.isGuard()) retval += 4;

    return retval;
  }

  private double evaluateEnnemyCard(Card card) {
    double score = 5;
    
    score += 2*card.defense;
    score += 2*card.attack;
    if (card.attack > 4) score+=10;
    if (card.attack > 7) {
      if (isProtected(card)) score+=30;
    }
    if (card.isGuard()) score+=5;
    if ((card.abilities & Abilities.WARD) != 0) {
      score+=card.attack;
      if (card.isGuard()) {
        score+=5;
      }
    }
    
    return score;
    
  }

  private boolean isProtected(Card card) {
    if (card.isGuard()) return false;

    int enemyAttackerscount = 0;
    int ownTauntCount = 0;
    int ownTauntHP = 0;
    int enemyTotalAttack = 0;

    for (int i=0;i<state.cardsFE;i++) {
      Card c = state.cards[i];
      if (c.location == Location.HIS_BOARD) {
        if (c.attack >=1) {
          enemyTotalAttack += c.attack;
          enemyAttackerscount++;
        }          
      } else if (c.location == Location.MY_BOARD) {
        if (c.isGuard()) {
          ownTauntCount++;
          ownTauntHP += c.defense;
        }
      }
    }

    if (ownTauntCount < enemyAttackerscount 
        && ownTauntHP <= enemyTotalAttack) return true;
    
    return false;
  }

}
