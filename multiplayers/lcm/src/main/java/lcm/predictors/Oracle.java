package lcm.predictors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lcm.Player;
import lcm.State;
import lcm.cards.Card;
import lcm.cards.CardTriplet;
import lcm.cards.Location;

public class Oracle {
  List<CardTriplet> triplets = new ArrayList<>();
  List<Card> seenCards = new ArrayList<>();
  
  
  List<Card> chosenCards = new ArrayList<>();
  List<Card> myDeck = new ArrayList<>();
  List<Card> mySeenCards = new ArrayList<>();
  
  /**
   * Call this to init the draft for all choicies 
   * @param triplet
   */
  public void feed(CardTriplet triplet, int myChosenIndex) {
    triplets.add(triplet);
    
    
    Card card = new Card();
    card.copyFrom(triplet.cards[myChosenIndex]);
    chosenCards.add(card);
    myDeck.add(card);
  }
  
  public List<Card> myRemainingCards() {
    return myDeck;
  }
  /**
   * call this for each cad you saw
   * The oracle will try to know which draft triplet it was and so mark it as seen
   * @param card
   */
  void addOpponentSeenCard(Card newCard) {
    // TODO evict triplets, update possible cards in his deck
    for (Card card : seenCards) {
      if (card.instanceId == card.instanceId) return;
    }
    seenCards.add(newCard);
  }
  
  /**
   * Return the potential list of cards he has
   * @return
   */
  public List<Card> oppPotential() {
    return oppPotential(13); 
  }
  
  public List<Card> oppPotential(int maxMana) {
    List<Card> cards = new ArrayList<>();
    for (CardTriplet triplet : triplets) {
      for (int i=0;i<3;i++) {
        if (triplet.cards[i].cost <= maxMana) {
          cards.add(triplet.cards[i]);
        }
      }
    }
    for (Card card : seenCards) {
      Iterator<Card> iterator = cards.iterator();
      while(iterator.hasNext()) {
        if (iterator.next().instanceId == card.instanceId) {
          iterator.remove();
          break;
        }
      }
    }
    return cards;
  }

  public boolean hasSeenOnBoard(Card card) {
    for (Card c : seenCards) {
      if (c.id == card.id) return true;
    }
    return false;
  }

  /**
   * give count cards possible to the opponent
   */
  public List<Card> giveHimCards(int count) {
    List<Card> cards = oppPotential(13);
    Collections.shuffle(cards);
    return cards.subList(0, Math.min(cards.size(), count+1));
  }

  
  private void removeCardsFromMyDeck(Card card) {
    if (mySeenCards.stream().filter(c -> c.id == card.id).count() == 1) return;
    
    Iterator<Card> ite = myDeck.iterator();
    while (ite.hasNext()) {
      Card c = ite.next();
      if (c.instanceId == card.instanceId) {
        c.id = card.id;
        mySeenCards.add(c);
        ite.remove();
        return;
      }
    }
  }

  public void update(State state) {
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_HAND) {
        removeCardsFromMyDeck(card);
      } else if (card.location == Location.HIS_HAND) {
        addOpponentSeenCard(card);
      } else if (card.location == Location.HIS_BOARD) {
        addOpponentSeenCard(card);
      }
    }

    if (Player.DEBUG_ORACLE) {
      System.err.println("Cards remaining in my deck (size = "+myDeck.size()+")");
      for (Card card : myDeck) {
        System.err.print(card);
        System.err.print(" / ");
      }
      System.err.println();
    }
    
  }
}
