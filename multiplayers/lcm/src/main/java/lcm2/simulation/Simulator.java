package lcm2.simulation;

import lcm2.Agent;
import lcm2.CardType;
import lcm2.cards.Card;

public class Simulator {
  Card deadCard = Card.deadCard();
  private Agent me;
  private Agent opp;
  private Action action;

  public void run(Agent me, Agent opp, Action action) {
    
    this.me = me;
    this.opp = opp;
    this.action = action;
    
    
    if (action.type == ActionType.ATTACK) {
      resolveAttack(action);
    } else if (action.type == ActionType.SUMMON) {
    	summon(action.from);
    } else if (action.type == ActionType.USE) {
      useCard(action.from, action.target);
    }
  }

  private void resolveAttack(Action action) {
    Card card = me.getFreshCard(action.from); 
    Card oppCard = opp.getFreshCard(action.target);

    if (action.target == 0) {
      attackFace(card, oppCard);
    } else {
      attackCreature(card, oppCard);
    }
  }

  private void attackFace(Card card, Card oppCard) {
    oppCard.defense -= card.attack;
    card.canAttack = false;
  }

  private void useCard(int index, int targetIndex) {
    Card item = me.useCard(index);
    me.getFreshCard(0);
    me.useItem(item);
    opp.getFreshCard(0);
    opp.oppUseItem(item);
    
    if (item.model.type == CardType.ITEM_GREEN) {
      me.getFreshCard(targetIndex).applyItemEffects(me, opp, item);
    } else if (item.model.type == CardType.ITEM_RED) {
      opp.getFreshCard(targetIndex).applyItemEffects(me, opp, item);
    }
  }
  
  private void attackCreature(Card attacker, Card defender) {
    attacker.canAttack = false;
    
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
    if ((attacker.abilities & Abilities.BREAKTHROUGH) != 0 && defender.isDead()) healthTaken = defender.defense - damageGiven;
    if ((attacker.abilities & Abilities.LETHAL) != 0 && damageGiven > 0) opp.kill(defender);
    if ((attacker.abilities & Abilities.DRAIN) != 0 && damageGiven > 0) healthGain = attacker.attack;
    if (!defender.isDead()) defender.defense -= damageGiven;

    // defending
    if (damageTaken >= attacker.defense) me.kill(attacker);
    if ((defender.abilities & Abilities.LETHAL) != 0 && damageTaken > 0) me.kill(attacker);
    if (!attacker.isDead()) attacker.defense -= damageTaken;

    if (healthGain > 0) {
      me.getFreshCard(0);
      me.modifyHealth(healthGain);
    } 
    if (healthTaken > 0) {
      opp.getFreshCard(0);
      opp.modifyHealth(healthTaken);
    }
  }

  private void summon(int index) {
    me.getFreshCard(0);
    opp.getFreshCard(0);
    Card card = me.summonCard(index);
    opp.applyOppSummonEffects(card);
  }
  
  private void simError(String message) {
    System.err.print("******** ");
    System.err.println(message);
  }
}
