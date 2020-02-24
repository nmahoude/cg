package lcm2;

import java.util.Scanner;

import lcm2.cards.Card;
import lcm2.simulation.Abilities;

public class Agent {
  public Card boardCards[] = new Card[8+1]; // first card is agent himself
  int boardCardsFE = 0;
  public Card handCards[] = new Card[8];
  int handCardsFE = 0;
  
  public Card face;
  int mana;
  int deck;
  int rune;
  int nextTurnDraw;
  public int guardsCount;

  public Agent(int index) {
  	face = new Card(-1-index, -1, -1, 0, 0, 30, "", 0, 0, 0);
  	boardCards[0] = face;
	}
  
  public void copyFrom(Agent model) {
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

    // TODO needed for copy ??
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

	public void kill(Card myCard) {
		if (myCard.isGuard()) {
			guardsCount--;
		}
	}

	public void summon(int index, Card card) {
    this.mana -= card.model.cost;
    this.modifyHealth(card.model.myHealthChange);
    this.nextTurnDraw += card.model.cardDraw;
    handCards[index] = Card.EMPTY;
    boardCards[boardCardsFE++] = card;
    
    if ((card.abilities & Abilities.GUARD) != 0) guardsCount++;
	}

	public void oppSummon(Card card) {
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
}
