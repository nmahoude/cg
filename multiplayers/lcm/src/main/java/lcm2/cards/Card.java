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
  public CardModel model;
  
  public Card(int cardNumber, int instanceId, int cardType, int cost, int attack, int defense, String abilitiesStr, int myHealthChange,
              int opponentHealthChange, int cardDraw) {
  	canAttack = true;
  	hasAttacked = false;
  	
    this.attack = attack;
    this.defense = defense;
    this.abilities = Abilities.read(abilitiesStr);

    model = CardModel.get(instanceId, cardNumber, cardType, cost, myHealthChange, opponentHealthChange, cardDraw);
  }

  public void copyFrom(Card copyFrom) {
    canAttack = copyFrom.canAttack;
    hasAttacked = copyFrom.hasAttacked;
    
    this.attack = copyFrom.attack;
    this.defense = copyFrom.defense;
    this.abilities =copyFrom.abilities;

    model = copyFrom.model;
  }
  
  public void debug() {
    if (model.type == CardType.PLAYER) {
      System.err.print("-- ");
    }
    System.err.print(String.format("  Card id(%d) n°(%d) %s att(%d) def(%d) %s %d %d %d %d ", 
	        model.instanceId, model.cardNumber,
	    		model.type.toString(),
          attack, defense,
          abilities,
          model.myHealthChange, model.opponentHealthChange, model.cardDraw
          , model.cost
        ));
    System.err.println();
    
  }

  public boolean isGuard() {
    return (abilities & Abilities.GUARD) != 0;
  }
  public boolean isCharge() {
    return (abilities & Abilities.CHARGE) != 0;
  }
  public boolean isLethal() {
    return (abilities & Abilities.LETHAL) != 0;
  }

  public boolean isWarded() {
    return (abilities & Abilities.WARD) != 0;
  }

  public boolean isBreakthrough() {
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

  public static Card deadCard() {
    return EMPTY;
  }

  public static Card pop() {
    return new Card(-1, -1, 0, 1000, 0, 0, "------", 0, 0, 0);
  }

}
