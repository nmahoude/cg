package direct_ai;

import java.util.List;

import lcm.Agent;
import lcm.PlayerOld;
import lcm.cards.Card;
import lcm.cards.CardTriplet;
import lcm.cards.CardType;

/**
 * use a gaussian for the mana curve
 * 
 * @author nmahoude
 *
 */
public class CardPickerV3 implements Picker {
  public int index;
  public String message;
  private double[][] gaussian = new double[13][13];
  int[] cardCount = new int[13];
  private int[] manaCurve = { 0, 4, 10, 5, 4, 2, 2, 2, 1, 0,0,0,0,0,0};
  
  public int pick(Agent agent, CardTriplet triplet) {

    for (int i=0;i<13;i++) {
      cardCount[i] = 0;
    }
    for (Card card : agent.pickedCards) {
      cardCount[card.cost]++;
    }
    
    double bestScore = Double.NEGATIVE_INFINITY;
    index = 0;
    message = "";
    for (int i=0;i<3;i++) {
      Card card = triplet.cards[i];

      double score = assessCardValue(i, card);
      
      
      if (score > bestScore) {
        bestScore = score;
        index = i;
      }
    }
    return index;
  }

  private double assessCardValue(int index, Card card) {
    double faceValueWeight = 1.0;
    double bucketWeight = 10.0;
    double itemsWeight = 1.0;
    double faceValue = 1.0 * Card.precalculatedFaceValue[card.instanceId]; //faceValue();
    double bucketValue = getBucketValue(card);
    double itemsValue = 0; //card.type != CardType.CREATURE ? 1 : 0;
    
    double score = faceValueWeight * faceValue
        + bucketWeight * bucketValue
        + itemsWeight * itemsValue;

    
    if (PlayerOld.DEBUG_PICKER) {
    System.err.println(String.format("(Card %d): ["+card.cost+"]  "
        + "fv( %.3f * %.3f = %.3f ) "
        + "bv( %.3f * %.3f = %.3f ) "
        //+ "iv( %.3f * %.3f = %.3f ) "
        + "=> score = %.3f",
          index,
          faceValueWeight, faceValue, faceValueWeight * faceValue,
          bucketWeight,bucketValue, bucketWeight * bucketValue,
//            itemsWeight, itemsValue, itemsWeight * itemsValue,
          score
        ));
    }
    return score;
  }

  {
    // prepare gaussian factors
    for (int cost=0;cost<13;cost++) {
      for (int contributionOf=Math.max(0,  cost-13);contributionOf<Math.min(13, cost+13);contributionOf++) {
        gaussian[cost][contributionOf] = pdf(contributionOf, cost, 0.7);
      }
    }
  }
  double getBucketValue(Card card) {
    cardCount[card.cost]++;

    double score = 0;
    for (int i=0;i<13;i++) {
      score += gaussian [card.cost][i] * Math.max(0, manaCurve[i] - cardCount[i]);
    }
    
    cardCount[card.cost]--;

    return score;
  }

  public static double pdf(double x) {
      return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
  }

  public static double pdf(double x, double mu, double sigma) {
      return pdf((x - mu) / sigma) / sigma;
  }
}
