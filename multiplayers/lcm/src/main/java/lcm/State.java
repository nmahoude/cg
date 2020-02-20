<<<<<<< Updated upstream
package lcm;

import java.util.Scanner;

import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.CardTriplet;
import lcm.cards.Location;
import lcm.sim.Cache;

public class State {
  public static final int FIRST = 0;
  public static final int SECOND = 1;
  public int side;
  
  public static CardTriplet triplet = null;

  
  public int turn = 0;
  public Agent me = new Agent(this, 0);
  public Agent opp = new Agent(this, 1);
  public  Card cards[] = new Card[30];
  public int cardsFE = 0;
  public long hash = 0;

  public State() {
  }
  
  public void copyFrom(State model) {
    copyFrom(model, false);
  }
  public void copyFrom(State model, boolean reinit) {
    side = model.side;
    turn = model.turn;
    this.hash = model.hash;
    me.copyFrom(this, model.me);
    opp.copyFrom(this, model.opp);
    
    this.cardsFE = model.cardsFE;
    if (!reinit) {
      System.arraycopy(model.cards, 0, cards, 0, cardsFE);
    } else {
      for (int i=0;i<model.cardsFE;i++) {
        Card c = model.cards[i];
        if (c.location == Location.MY_BOARD || c.location == Location.HIS_BOARD) {
          Card copy = Cache.pop();
          copy.copyFrom(c);
          copy.canAttack = true;
          copy.hasAttacked = false;
          this.cards[i] = copy;
        } else {
          this.cards[i] = model.cards[i];
        }
      }
    }
  }

  public void read(Scanner in) {
    turn++;
    turnInit();

    me.read(in);
    opp.read(in);
    
    opp.handCardsCount = in.nextInt();
    int opponentActions = in.nextInt();
    if (in.hasNextLine()) {
        in.nextLine();
    }
    for (int i = 0; i < opponentActions; i++) {
        String cardNumberAndAction = in.nextLine();
    }
    
    int cardToReadCount = in.nextInt();
    
    if (PlayerOld.DEBUG_INPUT) {
      System.err.println(String.format("\"%d %d\"+NL+", opp.handCardsCount, cardToReadCount));
    }

    if (isInDraft()) {
      triplet = new CardTriplet(null, null, null);
    }
    cardsFE = 0;
    for (int i = 0; i < cardToReadCount; i++) {
      Card card = new Card();
      card.read(in);
      
      if (isInDraft()) {
        triplet.cards[i] = card;
      } else {
        addCard(card);
      }
    }
    
    
    if (turn == 1) {
      determineSide();
    }
    if (PlayerOld.DEBUG_INPUT) {
      System.err.println();
      System.err.println("\"\";");
      System.err.println("    state.read(new Scanner(input));");
      System.err.println();

//      if (turn == Constants.DRAFT_TURNS) {
//        for (Card card : allCards) {
//          card.debugInput();
//        }
//      }
    
    }
  }

  private void determineSide() {
    // when we pick our first card, if opp has one card, we are p2 !
    if (opp.deck == 1) {
      side = SECOND;
    } else {
      side = FIRST;
    }
  }

  public void addCard(Card card) {
    card.stateIndex = cardsFE;
    card.calculateHash();
    cards[cardsFE++] = card;
    
    if (card.location == Location.HIS_BOARD) {
      opp.incrementCoardsOnBoard();
      if ((card.abilities & Abilities.GUARD) != 0) opp.guardsCardsCount++;
    }
    if (card.location == Location.MY_BOARD) {
      me.incrementCoardsOnBoard();
      if ((card.abilities & Abilities.GUARD) != 0) me.guardsCardsCount++;
    }
    if (card.location == Location.MY_HAND) {
      me.handCardsCount++;
    }
    if (card.location == Location.HIS_HAND) {
      opp.handCardsCount++;
    }
  }

  private void turnInit() {
    me.turnInit();
    opp.turnInit();
  }

  public boolean isInDraft() {
    return turn <= 30;
  }

  public int myHandCardsCount() {
    return me.handCardsCount;
  }
  public int  myBoardCardsCount() {
    return me.getBoardCardsCount();
  }
  public int hisBoardCardsCount() {
    return opp.getBoardCardsCount();
  }

  public void debugCards() {
    System.err.println("Before something went wrong : ");
    System.err.println("--------------------");
    for (int i=0;i<cardsFE;i++) {
      cards[i].debugInput();
    }
    System.err.println("--------------------");
  }
  
  public Card card(int id) {
    if (id == -1) return Card.opponent;
    for (int i=0;i<cardsFE;i++) {
      Card card = cards[i];
      if (card.id == id) 
        return card;
    }
    return null; // impossible card
  }

  public State swapPlayer() {
    State switched = Cache.popState();
    switched.side = 1-side;
    switched.me.copyFrom(this, opp);
    switched.opp.copyFrom(this, me);
    
    switched.cardsFE = cardsFE;
    for (int i=0;i<cardsFE;i++) {
      Card card = Cache.pop();
      Card original = cards[i];
      card.copyFrom(original);
      card.location = original.location.mirror;
      switched.cards[i] = card;
    }
    return switched;
  }


  public long getHash() {
    long hash = 0;
    hash = me.getHash();
    hash ^= opp.getHash();
    for (int i=0;i<cardsFE;i++) {
      Card card = cards[i];
      if (card.location == Location.GRAVEYARD) continue;
      hash^= card.hash;
    }
        
    return hash;
  }

  public long getDebugHash() {
    long hash = 0;
    hash = me.getHash();
    hash ^= opp.getHash();
    System.err.println("Me : "+me.getHash());
    System.err.println("Opp: "+opp.getHash());
    for (int i=0;i<cardsFE;i++) {
      Card card = cards[i];
      System.err.println("Card ["+card.instanceId+"]" + card.hash + "" + (card.location == Location.GRAVEYARD ? "*" : ""));
      if (card.location == Location.GRAVEYARD) continue;
      hash^= card.hash;
    }
        
    return hash;
  }
  
}
=======
package lcm;

import java.util.Scanner;

import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.CardTriplet;
import lcm.cards.Location;
import lcm.sim.Cache;

public class State {
  public static final int FIRST = 0;
  public static final int SECOND = 1;
  public int side;
  
  public static CardTriplet triplet = null;

  
  public int turn = 0;
  public Agent me = new Agent(this, 0);
  public Agent opp = new Agent(this, 1);
  public  Card cards[] = new Card[30];
  public int cardsFE = 0;
  public long hash = 0;

  public State() {
  }
  
  public void copyFrom(State model) {
    copyFrom(model, false);
  }
  public void copyFrom(State model, boolean reinit) {
    side = model.side;
    turn = model.turn;
    this.hash = model.hash;
    me.copyFrom(this, model.me);
    opp.copyFrom(this, model.opp);
    
    this.cardsFE = model.cardsFE;
    if (!reinit) {
      System.arraycopy(model.cards, 0, cards, 0, cardsFE);
    } else {
      for (int i=0;i<model.cardsFE;i++) {
        Card c = model.cards[i];
        if (c.location == Location.MY_BOARD || c.location == Location.HIS_BOARD) {
          Card copy = Cache.pop();
          copy.copyFrom(c);
          copy.canAttack = true;
          copy.hasAttacked = false;
          this.cards[i] = copy;
        } else {
          this.cards[i] = model.cards[i];
        }
      }
    }
  }

  public void read(Scanner in) {
    turn++;
    turnInit();

    me.read(in);
    opp.read(in);
    
    opp.handCardsCount = in.nextInt();
    int opponentActions = in.nextInt();
    if (in.hasNextLine()) {
        in.nextLine();
    }
    for (int i = 0; i < opponentActions; i++) {
        String cardNumberAndAction = in.nextLine();
    }
    
    int cardToReadCount = in.nextInt();
    
    if (PlayerOld.DEBUG_INPUT) {
      System.err.println(String.format("\"%d %d\"+NL+", opp.handCardsCount, cardToReadCount));
    }

    if (isInDraft()) {
      triplet = new CardTriplet(null, null, null);
    }
    cardsFE = 0;
    for (int i = 0; i < cardToReadCount; i++) {
      Card card = new Card();
      card.read(in);
      
      if (isInDraft()) {
        triplet.cards[i] = card;
      } else {
        addCard(card);
      }
    }
    
    
    if (turn == 1) {
      determineSide();
    }
    if (PlayerOld.DEBUG_INPUT) {
      System.err.println();
      System.err.println("\"\";");
      System.err.println("    state.read(new Scanner(input));");
      System.err.println();

//      if (turn == Constants.DRAFT_TURNS) {
//        for (Card card : allCards) {
//          card.debugInput();
//        }
//      }
    
    }
  }

  private void determineSide() {
    // when we pick our first card, if opp has one card, we are p2 !
    if (opp.deck == 1) {
      side = SECOND;
    } else {
      side = FIRST;
    }
  }

  public void addCard(Card card) {
    card.stateIndex = cardsFE;
    card.calculateHash();
    cards[cardsFE++] = card;
    
    if (card.location == Location.HIS_BOARD) {
      opp.incrementCoardsOnBoard();
      if ((card.abilities & Abilities.GUARD) != 0) opp.guardsCardsCount++;
    }
    if (card.location == Location.MY_BOARD) {
      me.incrementCoardsOnBoard();
      if ((card.abilities & Abilities.GUARD) != 0) me.guardsCardsCount++;
    }
    if (card.location == Location.MY_HAND) {
      me.handCardsCount++;
    }
    if (card.location == Location.HIS_HAND) {
      opp.handCardsCount++;
    }
  }

  private void turnInit() {
    me.turnInit();
    opp.turnInit();
  }

  public boolean isInDraft() {
    return turn <= 30;
  }

  public int myHandCardsCount() {
    return me.handCardsCount;
  }
  public int  myBoardCardsCount() {
    return me.getBoardCardsCount();
  }
  public int hisBoardCardsCount() {
    return opp.getBoardCardsCount();
  }

  public void debugCards() {
    System.err.println("Before something went wrong : ");
    System.err.println("--------------------");
    for (int i=0;i<cardsFE;i++) {
      cards[i].debugInput();
    }
    System.err.println("--------------------");
  }
  
  public Card card(int id) {
    if (id == -1) return Card.opponent;
    for (int i=0;i<cardsFE;i++) {
      Card card = cards[i];
      if (card.id == id) 
        return card;
    }
    return null; // impossible card
  }

  public State swapPlayer() {
    State switched = Cache.popState();
    switched.side = 1-side;
    switched.me.copyFrom(this, opp);
    switched.opp.copyFrom(this, me);
    
    switched.cardsFE = cardsFE;
    for (int i=0;i<cardsFE;i++) {
      Card card = Cache.pop();
      Card original = cards[i];
      card.copyFrom(original);
      card.location = original.location.mirror;
      switched.cards[i] = card;
    }
    return switched;
  }


  public long getHash() {
    long hash = 0;
    hash = me.getHash();
    hash ^= opp.getHash();
    for (int i=0;i<cardsFE;i++) {
      Card card = cards[i];
      if (card.location == Location.GRAVEYARD) continue;
      hash^= card.hash;
    }
        
    return hash;
  }

  public long getDebugHash() {
    long hash = 0;
    hash = me.getHash();
    hash ^= opp.getHash();
    System.err.println("Me : "+me.getHash());
    System.err.println("Opp: "+opp.getHash());
    for (int i=0;i<cardsFE;i++) {
      Card card = cards[i];
      System.err.println("Card ["+card.instanceId+"]" + card.hash + "" + (card.location == Location.GRAVEYARD ? "*" : ""));
      if (card.location == Location.GRAVEYARD) continue;
      hash^= card.hash;
    }
        
    return hash;
  }
  
}
>>>>>>> Stashed changes
