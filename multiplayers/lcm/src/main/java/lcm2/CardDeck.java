package lcm2;

import lcm2.cards.Card;

public class CardDeck {

  public static Card get(int cardNumber, int instanceId, int cardType, int cost, int attack, int defense, String abilities, int myHealthChange,
      int opponentHealthChange, int cardDraw) {

    return new Card(cardNumber, instanceId,
                    cardType, 
                    cost, 
                    attack, 
                    defense, 
                    abilities, 
                    myHealthChange, 
                    opponentHealthChange, 
                    cardDraw);
  }

}
