package lcm.ai.eval;

import lcm.State;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.CardType;
import lcm.cards.Location;

public class Eval9 implements IEval {
  private static final double EPSILON = 0.001;
  private static final double WIN = Double.POSITIVE_INFINITY;
  private static final double LOSE = Double.NEGATIVE_INFINITY;
  enum VALUES {
    HEALTH("health"),
    HAND_CARDS_COUNT("hand count"),
    BOARD_CARD_COUNT("board count"),
    BOARD_SCORE("board score"),
    USED_MANA("used mana"),
    HAND_SCORE("hand score"),
    CLOCK("clock"),
    MISC_BONUS("misc divers"), // before having is own data
    ;
    
    String name;
    VALUES(String name) {
      this.name = name;
    }
  }
  private double total;
  private double[] myValues = new double[VALUES.values().length];
  private double[] hisValues = new double[VALUES.values().length];

  private void initValues() {
    for (int i=0; i<VALUES.values().length;i++) {
      myValues[i] = 0;
      hisValues[i] = 0;
    }
  }

  
  private double[] healthScore = new double[256];
  private State state;
  {
    for (int i=0;i<healthScore.length;i++) {
      healthScore[i] = 10 * Math.pow(i, 0.3);
    }
  }
  
  public double wHisHealthCoeff = 1.0;
  public double wMyHealthCoeff = 1.0;
  public double wMyBoardCoef = 0.8;
  public double wHisBoardCoef = 0.8;
  
  public Eval9() {
  }
  
  public Eval9(double my, double his) {
    wMyBoardCoef = my;
    wHisBoardCoef = his;
  }
  
  @Override
  public double eval(State state, boolean debug) {
    this.state = state;
    initValues();
    
    // early decision
    if (state.me.health <=0) return Double.NEGATIVE_INFINITY;
    if (state.opp.health <= 0) return Double.POSITIVE_INFINITY;
    
    
    double wHisHealth = wHisHealthCoeff;
    double wHand = 0.5;
    double wMyBoard = wMyBoardCoef;
    double wHisBoard = wHisBoardCoef;
    
    myValues[VALUES.HEALTH.ordinal()] = 0.1 * healthScore[state.me.health];
    hisValues[VALUES.HEALTH.ordinal()] = 1.0 * healthScore[state.opp.health];
    
    myValues[VALUES.BOARD_CARD_COUNT.ordinal()] = state.me.getBoardCardsCount();
    hisValues[VALUES.BOARD_CARD_COUNT.ordinal()] = state.opp.getBoardCardsCount();
    
    myValues[VALUES.HAND_CARDS_COUNT.ordinal()] = 0.1 * state.me.handCardsCount;
    hisValues[VALUES.HAND_CARDS_COUNT.ordinal()] = 0.1 * state.opp.handCardsCount;
    
    // BOARD SCORE
    double myGuardMalus = state.opp.hasGuard() ? 0.9 : 1.0;
    double hisGuardMalus = state.me.hasGuard() ? 0.9 : 1.0;
    
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_BOARD) {
        myValues[VALUES.BOARD_SCORE.ordinal()] += myGuardMalus * evaluate(card);
      } else if (card.location == Location.HIS_BOARD) {
        hisValues[VALUES.BOARD_SCORE.ordinal()] += hisGuardMalus * evaluate(card);
      }
    }
    
    
    // HAND SCORE
    myValues[VALUES.HAND_SCORE.ordinal()] = 0.0;
    
    
    
    total = 0.0;
    for (int i=0; i<VALUES.values().length;i++) {
      total += (myValues[i] - hisValues[i]);
    }    
    return total;
  }


  public double evaluate(Card card) {
    double score = 0.0;

    score += 2.0 * Math.abs(card.attack);
    score += 1.0 * Math.abs(card.defense);
    score += ((card.abilities & Abilities.BREAKTHROUGH) != 0) ? 0.3 : 0;
    score += ((card.abilities & Abilities.LETHAL) != 0) ? 3 : 0;
    score += ((card.abilities & Abilities.GUARD) != 0) ? 0.15 : 0;
    score += ((card.abilities & Abilities.WARD) != 0) ? 1.0 : 0;
    score += ((card.abilities & Abilities.DRAIN) != 0) ? 0.03 : 0;

    return score;
  }

  public static void comparator(Eval9 first, Eval9 second) {
    VALUES[] v = VALUES.values();
    for (int i=0;i<v.length;i++) {
      comparatorOutput(true, v[i].name, "my", first.myValues[i], "", second.myValues[i], "");
      comparatorOutput(false, v[i].name, "his", -first.hisValues[i], "", -second.hisValues[i], "");
    }
    System.err.println("--------------------------------------------------");
    comparatorOutput(true, "TOTAL", "", first.total, "", second.total, "");
    
    System.err.println();
    System.err.println("--------------------------------------------------");
    for (int i=0;i<first.state.cardsFE;i++) {
      Card card = first.state.cards[i];
      Card card2 = second.state.cards[i];
      String pf1 = getLocationPrefix(card);
      String pf2 = getLocationPrefix(card);
      
      comparatorOutput(true, "card "+card.id, "", card.score, pf1, card2.score, pf2);
    }
  }

  private static String getLocationPrefix(Card card) {
    switch(card.location) {
    case GRAVEYARD:
      return " [☠]";
    case HIS_BOARD:
      return " [B]";
    case HIS_HAND:
      return " [H]";
    case MY_BOARD:
      return " [B]";
    case MY_HAND:
      return " [H]";
    default:
      return "?";
    }
  }

  private static void comparatorOutput(boolean bestIsBetter, String values, String prefix, double first, String pf1, double second, String pf2) {
    String sign = "=";
    if (first-second > EPSILON) sign = ">";
    if (second-first > EPSILON) sign = "<";
    
    System.err.println(String.format("%20s =>   %(10f%s   %s   %(10f%s   {%+2.3f}", 
        prefix + " "+values,
        first,
        pf1,
        sign,
        second,
        pf2,
        first-second
        
        ));
  }

  public Eval9 withHisHealthBonus(double coeff) {
    wHisHealthCoeff = coeff;
    return  this;
  }

}
