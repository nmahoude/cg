package lcm.cards;

public class CardTriplet {
  public Card cards[] = new Card[3];
  public double proba[] = new double[3];
  
  public CardTriplet(Card card1, Card card2, Card card3) {
    cards[0] = card1;
    cards[1] = card2;
    cards[2] = card3;
    proba[0] = proba[1] = proba[2] = 0.3333;
  }

  public boolean hasCard(Card card) {
    for (int i=0;i<3;i++) {
      if (cards[i] != null && cards[i].instanceId == card.instanceId) return true;
    }
    return false;
  }
}
