package direct_ai;

import java.util.List;

import lcm.Agent;
import lcm.PlayerOld;
import lcm.State;
import lcm.cards.Card;
import lcm.cards.CardTriplet;
import lcm.cards.CardType;

public class CardPickerTest implements Picker {
  private static final int LOW = 0;
  private static final int MID = 1;
  private static final int HIGH = 2;

  int bucketTarget[] = { 10, 15, 5 };
  private int[] bucketCount = new int[3];

  public int index;
  public String message;
  
  public int pick(Agent agent, CardTriplet triplet) {
    double faceValueWeight = 1.0;
    double bucketWeight = 0.5;

    updateBuckets(agent.pickedCards);
    
    int itemCount = 0;
    for (Card card : agent.pickedCards) {
      if (card.type == CardType.CREATURE) continue;
      itemCount++;
    }

    double itemMalus = 0; //- 10 * Math.max(0, itemCount - 5 );
    
    double bestScore = Double.NEGATIVE_INFINITY;
    index = 0;
    message = "";
    for (int i=0;i<3;i++) {
      Card card = triplet.cards[i];
      double faceValue = Card.precalculatedFaceValue[card.instanceId]; //faceValue();
      double bucketValue = getBucketValue(card);
      double itemsValue = card.type != CardType.CREATURE ? itemMalus : 0;
      
      if (agent.state.side == State.SECOND && card.cost < 4) {
        faceValue -= 20;
      }
      
      double score = faceValueWeight * faceValue
          + bucketWeight * bucketValue
          + itemsValue;
      
      if (PlayerOld.DEBUG_PICKER) {
        System.err.println(String.format("(Card %d): ["+getBucketIndex(card)+"]"
          + "fv( %.3f * %.3f = %.3f ) "
          + "bv( %.3f * %.3f = %.3f ) "
          + "iv( %.3f ) "
          + "=> score = %.3f",
            i,
            faceValueWeight, faceValue, faceValueWeight * faceValue,
            bucketWeight,bucketValue, bucketWeight * bucketValue,
            itemsValue,
//            itemsWeight, itemsValue, itemsWeight * itemsValue,
            score
          ));
      }
      
      if (score > bestScore) {
        bestScore = score;
        index = i;
      }
    }
    return index;
  }

  private double getBucketValue(Card card) {
    int bucketIndex = getBucketIndex(card);
    
    int startMalusing = (int)(bucketTarget[bucketIndex] * (80 / 100.0));
    if (bucketCount[bucketIndex] > startMalusing) {
      int delta = Math.min(5,  bucketCount[bucketIndex] - startMalusing);
      return  -20.0 * delta;
    }
    return 0;
  }

  private int getBucketIndex(Card card) {
    return card.cost <= 2 ? LOW : card.cost <=5 ? MID : HIGH;
  }

  private void updateBuckets(List<Card> pickedCards) {
    bucketCount[LOW] = bucketCount[MID] = bucketCount[HIGH] = 0;
    
    for (Card card : pickedCards) {
      int bucketIndex = getBucketIndex(card);
      bucketCount[bucketIndex]++;
    }
  }

}
