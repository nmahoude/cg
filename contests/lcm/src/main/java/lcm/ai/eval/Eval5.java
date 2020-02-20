package lcm.ai.eval;

import lcm.State;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.Location;
import lcm.sim.Constants;

/** 
 * @see https://www.pdf-archive.com/2017/12/20/hearthstone-ai-thesis-ejnar-h-konsen/hearthstone-ai-thesis-ejnar-h-konsen.pdf
 * 
 * https://elie.net/blog/hearthstone/predicting-hearthstone-game-outcome-with-machine-learning
 *
 */
public class Eval5 implements IEval {

  private static final double EPSILON = 0.001;
  private static final double WIN = Double.POSITIVE_INFINITY;
  private static final double LOSE = Double.NEGATIVE_INFINITY;

  static double healthScore[] = new double[256];
  static double cardsCountScore[] = new double[20];
  static double handCardsCountScore[] = new double[20];
  static double deckCountScore[] = new double[30];
  static {
    for (int i=0;i<healthScore.length;i++) {
      healthScore[i] = Math.sqrt(i);
    }
    for (int i=0;i<cardsCountScore.length;i++) {
      if (i <= 3) {
        cardsCountScore[i] = 3 * i;
      } else {
        cardsCountScore[i] = 3 * 3 + 2 * (i-3);
      }
    }
    for (int i=0;i<handCardsCountScore.length;i++) {
      handCardsCountScore[i] = Math.sqrt(i);
    }
    for (int i=0;i<deckCountScore.length;i++) {
      if (i >= 10) {
        deckCountScore[i] = Math.sqrt(i);
      } else {
        deckCountScore[i] = 0.0;
      }
    }
  }

  enum VALUES {
    HEALTH("health"),
    HAND_CARDS_COUNT("hand count"),
    BOARD_CARD_COUNT("board count"),
    BOARD_SCORE("board score"),
    BOARD_CARD_COUNT_BONUS("wipe bonus"),
    DECK_COUNT_SCORE("deck card"),
    USED_MANA("used mana"),
    ;
    
    String name;
    VALUES(String name) {
      this.name = name;
    }
  }
  
  private double[] myValues = new double[VALUES.values().length];
  private double[] hisValues = new double[VALUES.values().length];
  private double total;
  private State state;

  
  private void initValues() {
    for (int i=VALUES.values().length-1; i>=0;i--) {
      myValues[i] = 0;
      hisValues[i] = 0;
    }
  }
  
  public double eval(State state, boolean debug) {
    this.state = state;
    initValues();
    
    if (state.opp.health <= 0) return WIN;
    if (state.me.health <= 0) return LOSE;
    
    int myHandCardsCount = 0;
    int myBoardCardsCount = 0;
    double myBoardCardsScore = 0;

    int hisBoardCardsCount = 0;
    int hisHandCardsCount = state.opp.handCardsCount;
    double hisBoardCardsScore = 0;

    int myThreat = 0;
    int hisThreat = 0;
    
    for (int i = 0; i < state.cardsFE; i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_BOARD) {
        myBoardCardsCount++;
        myThreat+= card.attack;
        myBoardCardsScore += card.score > 0 ? card.score : scoreCard(state.turn, card);
      }
      if (card.location == Location.HIS_BOARD) {
        hisBoardCardsCount++;
        hisThreat+= card.attack;
        hisBoardCardsScore += card.score > 0 ? card.score : scoreCard(state.turn, card);
      }
      if (card.location == Location.MY_HAND) {
        myHandCardsCount++;
      }
    }

    myValues[VALUES.HEALTH.ordinal()] = healthScore[state.me.health];
    hisValues[VALUES.HEALTH.ordinal()] = healthScore[state.opp.health];
    
    myValues[VALUES.BOARD_CARD_COUNT.ordinal()] = 5 * myBoardCardsCount;
    hisValues[VALUES.BOARD_CARD_COUNT.ordinal()] = 5 * hisBoardCardsCount;
    
    myValues[VALUES.HAND_CARDS_COUNT.ordinal()] = cardsCountScore[myHandCardsCount];
    hisValues[VALUES.HAND_CARDS_COUNT.ordinal()] = cardsCountScore[hisHandCardsCount];
    if (state.turn - Constants.DRAFT_TURNS <=  2) {
      // in the first N turns, try to maximize the card played (so less in the hand)
      myValues[VALUES.HAND_CARDS_COUNT.ordinal()] = - myValues[VALUES.HAND_CARDS_COUNT.ordinal()];
      hisValues[VALUES.HAND_CARDS_COUNT.ordinal()] = -hisValues[VALUES.HAND_CARDS_COUNT.ordinal()];
    }
    
    myValues[VALUES.BOARD_SCORE.ordinal()] = myBoardCardsScore;
    hisValues[VALUES.BOARD_SCORE.ordinal()] = hisBoardCardsScore;
    
    myValues[VALUES.BOARD_CARD_COUNT_BONUS.ordinal()] = (myBoardCardsCount == 0 ? - (2 + Math.min(10, state.turn - Constants.DRAFT_TURNS)) : 0);
    hisValues[VALUES.BOARD_CARD_COUNT_BONUS.ordinal()] = (hisBoardCardsCount == 0 ? - (2 + Math.min(10, state.turn - Constants.DRAFT_TURNS)) : 0);
    
    myValues[VALUES.DECK_COUNT_SCORE.ordinal()] = deckCountScore[state.me.deck];
    hisValues[VALUES.DECK_COUNT_SCORE.ordinal()] = deckCountScore[state.opp.deck];
    
    myValues[VALUES.USED_MANA.ordinal()] = -1 * state.me.mana;
    hisValues[VALUES.USED_MANA.ordinal()] = 0;
    
    total = 0.0;
    for (int i=VALUES.values().length-1; i>=0;i--) {
      total += (myValues[i] - hisValues[i]);
    }

    return total;
  }

  private double scoreCard(int turn, Card card) {
    double score = 0;
    score += 4 * card.attack + 2 * card.defense;
    if ((card.abilities & Abilities.GUARD) != 0) score += card.defense;
    if ((card.abilities  & Abilities.DRAIN) != 0) score += card.attack;
    if ((card.abilities  & Abilities.LETHAL) != 0) score += 2*(card.attack + card.defense);
    if ((card.abilities  & Abilities.CHARGE) != 0) score += 0;
    if ((card.abilities  & Abilities.BREAKTHROUGH) != 0) score += 3;
    if ((card.abilities  & Abilities.WARD) != 0) score += card.defense;
    
    card.score = score;
    return score;
  }
  
  public static void comparator(Eval5 first, Eval5 second) {
    VALUES[] v = VALUES.values();
    for (int i=0;i<v.length;i++) {
      comparatorOutput(true, v[i].name, "my", first.myValues[i], second.myValues[i]);
      comparatorOutput(false, v[i].name, "his", -first.hisValues[i], -second.hisValues[i]);
    }
    System.err.println("--------------------------------------------------");
    comparatorOutput(true, "TOTAL", "", first.total, second.total);
    
    System.err.println();
    System.err.println("--------------------------------------------------");
    for (int i=0;i<first.state.cardsFE;i++) {
      Card card = first.state.cards[i];
      Card card2 = second.state.cards[i];
      
      
      comparatorOutput(true, "card "+card.id, "", card.score, card2.score);
    }
  }

  private static void comparatorOutput(boolean bestIsBetter, String values, String prefix, double first, double second) {
    String sign = "=";
    if (first-second > EPSILON) sign = ">";
    if (second-first > EPSILON) sign = "<";
    
    System.err.println(String.format("%20s =>   %(10f   %s   %(10f   {%+2.3f}", 
        prefix + " "+values,
        first,
        sign,
        second,
        first-second
        
        ));
  }
}
