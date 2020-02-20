<<<<<<< Updated upstream
package lcm.sim;

import lcm.PlayerOld;
import lcm.State;
import lcm.cards.Card;

public class Cache {

  static final State states[] = new State[PlayerOld.STATE_CACHE];
  static int statesFE = 0;

  static final Card cards[] = new Card[PlayerOld.CARD_CACHE];
  static int cardsFE = 0;

  static {
      for (int i = 0; i < cards.length; i++) {
          cards[i] = new Card();
      }
      for (int i = 0; i < states.length; i++) {
          states[i] = new State();
      }
  }

  public static final void initCache() {
      cardsFE = 0;
      statesFE = 0;
  }

  public static final Card pop() {
      Card card = cards[cardsFE++];
      card.score = -1;
      return card;
  }

  public static final State popState() {
      return states[statesFE++];
  }
}
=======
package lcm.sim;

import lcm.PlayerOld;
import lcm.State;
import lcm.cards.Card;

public class Cache {

  static final State states[] = new State[PlayerOld.STATE_CACHE];
  static int statesFE = 0;

  static final Card cards[] = new Card[PlayerOld.CARD_CACHE];
  static int cardsFE = 0;

  static {
      for (int i = 0; i < cards.length; i++) {
          cards[i] = new Card();
      }
      for (int i = 0; i < states.length; i++) {
          states[i] = new State();
      }
  }

  public static final void initCache() {
      cardsFE = 0;
      statesFE = 0;
  }

  public static final Card pop() {
      Card card = cards[cardsFE++];
      card.score = -1;
      return card;
  }

  public static final State popState() {
      return states[statesFE++];
  }
}
>>>>>>> Stashed changes
