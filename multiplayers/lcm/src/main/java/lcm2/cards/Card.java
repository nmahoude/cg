package lcm2.cards;

import lcm2.Agent;
import lcm2.CardType;
import lcm2.simulation.Abilities;

public class Card {

  public static final Card EMPTY = new Card(-1, -1, 0, 1000, 0, 0, "------", 0, 0, 0);
  public boolean canAttack;
  public boolean hasAttacked;
  
  public int attack;
  public int defense;
  public int abilities;
  public final CardModel model;
  
  public Card(int cardNumber, int instanceId, int cardType, int cost, int attack, int defense, String abilitiesStr, int myHealthChange,
              int opponentHealthChange, int cardDraw) {
  	canAttack = true;
  	hasAttacked = false;
  	
    this.attack = attack;
    this.defense = defense;
    this.abilities = Abilities.read(abilitiesStr);

    model = CardModel.get(instanceId, cardNumber, cardType, cost, myHealthChange, opponentHealthChange, cardDraw);
  }

  public void debug() {
    System.err.println(String.format("  Card id(%d) n°(%d) %s att(%d) def(%d) %s %d %d %d %d ", 
	        model.instanceId, model.cardNumber,
	    		model.type.toString(),
          attack, defense,
          abilities,
          model.myHealthChange, model.opponentHealthChange, model.cardDraw
          , model.cost
        ));
    
    
  }

  public boolean isGuard() {
    return (abilities & Abilities.GUARD) != 0;
  }
  private boolean isLethal() {
    return (abilities & Abilities.LETHAL) != 0;
  }

  private boolean isWarded() {
    return (abilities & Abilities.WARD) != 0;
  }

  private boolean isBreakthrough() {
    return (abilities & Abilities.BREAKTHROUGH) != 0;
  }

  
  public void attack(Agent opp) {
    opp.decreaseLife(attack);
  }

  public void attack(Agent from, Agent opp, Card oppCard) {
    this.affect(oppCard);
    oppCard.affect(this);
  }

  private void affect(Card card) {
    if (this.isWarded()) {
      this.removeWard();
      return;
    }
    int dealtDamande = card.isLethal() ? card.defense : Math.min(card.attack, this.defense);
    this.defense -= dealtDamande;
    if (card.isBreakthrough()) {
      
    }
  }

  private void removeWard() {
    abilities &= ~Abilities.WARD;
  }

  public boolean isDead() {
    return defense <= 0;
  }

	public void applyItemEffects(Agent me, Agent opp, Card item) {
		if (item.model.type == CardType.ITEM_GREEN) {
			// only on our cards
      if ((item.abilities & Abilities.GUARD) != 0 && (this.abilities & Abilities.GUARD) == 0) {
        me.guardsCount++;
      }
      abilities = Abilities.upgrade(abilities, item);
      if ((item.abilities & Abilities.CHARGE) != 0) {
        canAttack = !hasAttacked;
      }
    } else /* RED or BLUE */{
      if ((item.abilities & Abilities.GUARD) != 0 && (this.abilities & Abilities.GUARD) != 0) {
        opp.guardsCount--;
      }

      abilities = Abilities.downgrade(abilities, item);
    }

    attack = Math.max(0, attack + item.attack);
    if ((abilities & Abilities.WARD) != 0 && item.defense < 0) {
      abilities = abilities & ~Abilities.WARD;
    } else {
      defense += item.defense;
    }

    if (defense <= 0) {
      if ((this.abilities & Abilities.GUARD) != 0) {
        opp.guardsCount--;
      }
    }		
	}

}
