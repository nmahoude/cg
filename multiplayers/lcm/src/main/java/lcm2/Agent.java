package lcm2;

import java.util.Scanner;

import lcm2.cards.Card;

public class Agent {
  public Card boardCards[] = new Card[8+1]; // first card is agent himself
  public int boardCardsFE = 0;
  public Card handCards[] = new Card[8];
  public int handCardsFE = 0;
  
  public Card face;
  public int mana;
  public int deck;
  public int rune;
  public int nextTurnDraw;
  public int guardsCount;

  public Agent(int index) {
  	face = new Card(-1-index, -1, -1, 0, 0, 30, "", 0, 0, 0);
  	boardCards[0] = face;
	}
  
  public void copyFrom(Agent model) {
    this.face = model.face;
    for (int i=0;i<9;i++) {
      boardCards[i] = model.boardCards[i];
      // boardCardsId, handCardsId => should be not needed 
    }
    for (int i=0;i<8;i++) {
    	handCards[i] = model.handCards[i];
    }
    boardCardsFE = model.boardCardsFE;
    handCardsFE = model.handCardsFE;
    guardsCount = model.guardsCount;

    mana = model.mana;
    deck = model.deck;
    rune = model.rune;
    nextTurnDraw = model.nextTurnDraw;
  }
  
  public void read(Scanner in) {
    face.defense = in.nextInt();
    mana = in.nextInt();
    deck = in.nextInt();
    rune = in.nextInt();
    nextTurnDraw = in.nextInt();
  }

  public void clearCards() {
    boardCardsFE = 1;
    handCardsFE = 0;
    guardsCount = 0;
  }
  
  public void readOpponentActions(Scanner in) {
    int opponentHand = in.nextInt();
    int opponentActions = in.nextInt();
    if (in.hasNextLine()) {
      in.nextLine();
    }
    for (int i = 0; i < opponentActions; i++) {
      String cardNumberAndAction = in.nextLine();
    }
  }

  public void addBoardCard(int instanceId, Card card) {
    boardCards[boardCardsFE++] = card;
    if (card.isGuard()) {
      guardsCount++;
    }
  }

  public void addHandCard(int instanceId, Card card) {
    handCards[handCardsFE++] = card;
  }

  public void debugHandCards() {
    for (int i=0;i<handCardsFE;i++) {
      handCards[i].debug();
    }
  }

  public void debugBoardCards() {
    for (int i=0;i<boardCardsFE;i++) {
      boardCards[i].debug();
    }
  }

  public void decreaseLife(int life) {
    face.defense -= life;
    if (life < 0) life = 0;
  }

	public void modifyHealth(int mod) {
    face.defense += mod;

    if (mod >= 0) return;
    if (face.defense < 0) face.defense = 0;
    
    int h2r = face.defense / 5;
    if (h2r <= rune) {
      nextTurnDraw += (rune-h2r);
      rune = h2r;
    }

	}

	public void kill(Card card) {
		updateGuardCountOnDead(card);
		removeBoardCard(card);
	}

  private void updateGuardCountOnDead(Card card) {
    if (card.isGuard()) {
			guardsCount--;
		}
  }

	private void removeBoardCard(Card card) {
	  for (int i=0;i<boardCardsFE;i++) {
	    if (boardCards[i] == card) {
	      boardCards[i] = Card.EMPTY;
	    }
	  }
  }

	public void applyOppSummonEffects(Card card) {
    this.modifyHealth(card.model.opponentHealthChange);
	}

	public void useItem(Card item) {
    this.mana -= item.model.cost;
    this.modifyHealth(item.model.myHealthChange);
    this.nextTurnDraw += item.model.cardDraw;
	}

	public void oppUseItem(Card item) {
    this.modifyHealth(item.model.opponentHealthChange);
	}

	public boolean hasEmptySpaceOnBoard() {
		return boardCardsFE < 9;
	}

	/*
	 * return a 'fresh' card instead of the immutable cards of our parent
	 */
  public Card getFreshCard(int index) {
    Card newCard = Card.pop();
    newCard.copyFrom(boardCards[index]);
    boardCards[index] = newCard;
    if (index == 0) {
      face = newCard;
    }
    return newCard;
  }

  public Card summonCard(int index) {
    Card newCard = Card.pop();
    newCard.copyFrom(handCards[index]);
    handCards[index] = Card.EMPTY;
    boardCards[boardCardsFE++] = newCard;
    
    // apply effects
    this.mana -= newCard.model.cost;
    this.modifyHealth(newCard.model.myHealthChange);
    this.nextTurnDraw += newCard.model.cardDraw;
    if (newCard.isGuard()) guardsCount++;

    newCard.canAttack = newCard.isCharge();
    
    return newCard;
  }

  public Card useCard(int index) {
    Card card = handCards[index];
    handCards[index] = Card.EMPTY;
    return card;
  }

  public int attackSum() {
    int attack = 0;
    for (int m=0;m<boardCardsFE;m++) {
      Card card = boardCards[m];
      if (card.isDead()) continue;
      attack += card.attack;
    }
    return attack;
  }
}
