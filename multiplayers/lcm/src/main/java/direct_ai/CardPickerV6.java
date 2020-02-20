<<<<<<< Updated upstream
package direct_ai;

import java.util.List;

import lcm.Agent;
import lcm.PlayerOld;
import lcm.State;
import lcm.cards.Card;
import lcm.cards.CardTriplet;
import lcm.cards.CardType;

/**
 * use a gaussian for the mana curve
 * 
 * @author nmahoude
 *
 */
public class CardPickerV6 implements Picker {
  public int index;
  public String message;
  int[] cardCount = new int[13];
  
  int[] manaCurve;
  private int[] cardIndex;
  
  public int pick(Agent agent, CardTriplet triplet) {
    if (agent.state.side == State.FIRST) {
      cardIndex = cardIndexP0;
      manaCurve = manaCurveP0;
    } else {
      cardIndex = cardIndexP1;
      manaCurve = manaCurveP1; 
    }
    
    
    for (int i=0;i<8;i++) {
      cardCount[i] = 0;
    }
    for (Card card : agent.pickedCards) {
      cardCount[bucketCost(card)]++;
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

  int bucketCost(Card card) {
    return Math.min(7, card.cost);
  }

  double assessCardValue(int index, Card card) {

    int faceValue = cardIndex[card.instanceId];
    
    int bucketMalus = getBucketMalus(card);
    
    int itemMalus = getItemMalus(card);
    
    int score = faceValue + bucketMalus + itemMalus;

    if (PlayerOld.DEBUG_PICKER) {
    System.err.println(String.format("(Card %d): ["+card.cost+"]  "
          + " fv: %d bmalus: %d imalus: %d => %d",
          card.instanceId,
          faceValue, bucketMalus, itemMalus,
          score
        ));
    }
    return score;
  }

  int getItemMalus(Card card) {
    if (card.type == CardType.CREATURE || card.type == CardType.ITEM_RED) {
      return 0;
    }
    return -20;
  }

  int getBucketMalus(Card card) {
    int cost = bucketCost(card); // put it in [0,7]
    double participationOfNeighbors = 0;
    for (int i=0;i<8;i++) {
      if (i == cost) continue;
      int overQuota = Math.max(0, (cardCount[i] - manaCurve[i]));
      int distance = Math.abs(i - cost);
      participationOfNeighbors += overQuota * Math.pow(0.5, distance);
    }
    
    int over = (int)Math.max(0, (1 + cardCount[cost] + participationOfNeighbors - manaCurve[cost]));
    
    return -10 * over;
  }

  public static double pdf(double x) {
      return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
  }

  public static double pdf(double x, double mu, double sigma) {
      return pdf((x - mu) / sigma) / sigma;
  }
  
  //private int[] manaCurveP0 = { 0, 3, 4, 7, 6, 4, 3, 3};
  private int[] manaCurveP0 = { 0, 3, 5, 6, 6, 4, 3, 3};
  private int[] cardIndexP0 = {0,
      66,32,115,53,81,87,147,106,123,17,92,85,75,26,113,42,103,148,122,30,114,54,130,20,59,91,31,129,150,61,10,156,142,89,35,95,146,60,46,38,79,11,33,136,40,23,57,145,159,127,154,134,153,112,7,51,13,41,68,18,90,52,14,131,152,132,140,157,144,108,71,73,69,72,111,25,62,21,83,149,116,138,76,151,141,82,125,143,58,78,49,8,65,50,124,118,99,80,133,77,44,27,117,84,97,100,15,28,120,1,101,67,12,98,121,160,19,74,45,47,119,63,34,22,56,86,48,93,104,24,29,37,135,107,102,36,64,9,155,2,94,39,3,109,88,43,139,128,70,105,158,126,4,16,96,5,110,137,55,6,
  };

  //private int[] manaCurveP1 = { 0, 3, 5, 8, 5, 3, 3, 3};
  private int[] manaCurveP1 = { 0, 3, 6, 7, 5, 3, 3, 3};
  private int[] cardIndexP1 = {0,
      78,34,122,70,91,95,146,93,112,6,110,84,59,32,103,40,98,148,114,29,109,47,125,20,53,88,30,119,150,57,14,149,134,83,35,81,144,74,61,38,87,12,33,136,39,22,52,152,159,135,151,130,154,111,9,43,17,42,58,23,82,65,18,138,153,127,132,156,145,105,68,55,69,60,106,25,50,15,73,147,117,140,90,157,131,80,128,142,54,75,71,5,79,46,123,129,96,66,133,63,37,24,116,72,89,101,16,26,121,1,104,62,13,102,113,160,19,86,56,76,124,77,44,21,67,85,51,107,108,27,28,36,137,94,97,41,64,10,155,7,120,45,8,126,92,31,141,139,49,118,158,115,2,11,100,3,99,143,48,4,
  };
}
=======
package direct_ai;

import java.util.List;

import lcm.Agent;
import lcm.PlayerOld;
import lcm.State;
import lcm.cards.Card;
import lcm.cards.CardTriplet;
import lcm.cards.CardType;

/**
 * use a gaussian for the mana curve
 * 
 * @author nmahoude
 *
 */
public class CardPickerV6 implements Picker {
  public int index;
  public String message;
  int[] cardCount = new int[13];
  
  int[] manaCurve;
  private int[] cardIndex;
  
  public int pick(Agent agent, CardTriplet triplet) {
    if (agent.state.side == State.FIRST) {
      cardIndex = cardIndexP0;
      manaCurve = manaCurveP0;
    } else {
      cardIndex = cardIndexP1;
      manaCurve = manaCurveP1; 
    }
    
    
    for (int i=0;i<8;i++) {
      cardCount[i] = 0;
    }
    for (Card card : agent.pickedCards) {
      cardCount[bucketCost(card)]++;
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

  int bucketCost(Card card) {
    return Math.min(7, card.cost);
  }

  double assessCardValue(int index, Card card) {

    int faceValue = cardIndex[card.instanceId];
    
    int bucketMalus = getBucketMalus(card);
    
    int itemMalus = getItemMalus(card);
    
    int score = faceValue + bucketMalus + itemMalus;

    if (PlayerOld.DEBUG_PICKER) {
    System.err.println(String.format("(Card %d): ["+card.cost+"]  "
          + " fv: %d bmalus: %d imalus: %d => %d",
          card.instanceId,
          faceValue, bucketMalus, itemMalus,
          score
        ));
    }
    return score;
  }

  int getItemMalus(Card card) {
    if (card.type == CardType.CREATURE || card.type == CardType.ITEM_RED) {
      return 0;
    }
    return -20;
  }

  int getBucketMalus(Card card) {
    int cost = bucketCost(card); // put it in [0,7]
    double participationOfNeighbors = 0;
    for (int i=0;i<8;i++) {
      if (i == cost) continue;
      int overQuota = Math.max(0, (cardCount[i] - manaCurve[i]));
      int distance = Math.abs(i - cost);
      participationOfNeighbors += overQuota * Math.pow(0.5, distance);
    }
    
    int over = (int)Math.max(0, (1 + cardCount[cost] + participationOfNeighbors - manaCurve[cost]));
    
    return -10 * over;
  }

  public static double pdf(double x) {
      return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
  }

  public static double pdf(double x, double mu, double sigma) {
      return pdf((x - mu) / sigma) / sigma;
  }
  
  //private int[] manaCurveP0 = { 0, 3, 4, 7, 6, 4, 3, 3};
  private int[] manaCurveP0 = { 0, 3, 5, 6, 6, 4, 3, 3};
  private int[] cardIndexP0 = {0,
      66,32,115,53,81,87,147,106,123,17,92,85,75,26,113,42,103,148,122,30,114,54,130,20,59,91,31,129,150,61,10,156,142,89,35,95,146,60,46,38,79,11,33,136,40,23,57,145,159,127,154,134,153,112,7,51,13,41,68,18,90,52,14,131,152,132,140,157,144,108,71,73,69,72,111,25,62,21,83,149,116,138,76,151,141,82,125,143,58,78,49,8,65,50,124,118,99,80,133,77,44,27,117,84,97,100,15,28,120,1,101,67,12,98,121,160,19,74,45,47,119,63,34,22,56,86,48,93,104,24,29,37,135,107,102,36,64,9,155,2,94,39,3,109,88,43,139,128,70,105,158,126,4,16,96,5,110,137,55,6,
  };

  //private int[] manaCurveP1 = { 0, 3, 5, 8, 5, 3, 3, 3};
  private int[] manaCurveP1 = { 0, 3, 6, 7, 5, 3, 3, 3};
  private int[] cardIndexP1 = {0,
      78,34,122,70,91,95,146,93,112,6,110,84,59,32,103,40,98,148,114,29,109,47,125,20,53,88,30,119,150,57,14,149,134,83,35,81,144,74,61,38,87,12,33,136,39,22,52,152,159,135,151,130,154,111,9,43,17,42,58,23,82,65,18,138,153,127,132,156,145,105,68,55,69,60,106,25,50,15,73,147,117,140,90,157,131,80,128,142,54,75,71,5,79,46,123,129,96,66,133,63,37,24,116,72,89,101,16,26,121,1,104,62,13,102,113,160,19,86,56,76,124,77,44,21,67,85,51,107,108,27,28,36,137,94,97,41,64,10,155,7,120,45,8,126,92,31,141,139,49,118,158,115,2,11,100,3,99,143,48,4,
  };
}
>>>>>>> Stashed changes
