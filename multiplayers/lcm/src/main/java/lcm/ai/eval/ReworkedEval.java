package lcm.ai.eval;

import lcm.State;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.Location;

public class ReworkedEval implements IEval {
  private static final double EPSILON = 0.001;
  private static final double WIN = Double.POSITIVE_INFINITY;
  private static final double LOSE = Double.NEGATIVE_INFINITY;
  enum VALUES {
    HEALTH("health"),
    HAND_CARDS_COUNT("hand count"),
//    BOARD_CARD_COUNT("board count"),
    BOARD_SCORE("board score"),
//    BOARD_CARD_COUNT_BONUS("wipe bonus"),
//    DECK_COUNT_SCORE("deck card"),
    USED_MANA("used mana"),
    HAND_SCORE("hand score"),
    MISC("misc"),
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

  
  private double[] healthScore = new double[128];
  private State state;
  {
    for (int i=0;i<healthScore.length;i++) {
      healthScore[i] = 10 * Math.pow(i, 0.3);
    }
  }
  
  @Override
  public double eval(State state, boolean debug) {
    this.state = state;
    initValues();
    
    double wMyHealth = 1.0;
    double wHisHealth = 1.0;
    double wHand = 0.5;
    double wMyBoard = 0.4;
    double wHisBoard = 0.45;
    
    // quick cutoff
    if (state.opp.health <= 0) return Double.POSITIVE_INFINITY;
    if (state.me.health <= 0) return Double.NEGATIVE_INFINITY;

    // TODO si j'ai plus de cartes, augmenter les dégats sur FACE 
    // (stopper les trades sauf vraiment bon : modif wMyBoard & wHisBoard ?)
    
    // TODO si il a bcp plus de dégats que ma def
    double myDef = 0, hisDef = 0;
    double myAtk = 0, hisAtk = 0;
    double myGuardDef = 0, hisGuardDef = 0;
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_BOARD) {
        myAtk += card.attack;
        myDef += card.defense;
        if (card.isGuard()) myGuardDef += card.defense;
      }
      if (card.location == Location.HIS_BOARD) {
        hisAtk += card.attack;
        hisDef += card.defense;
        if (card.isGuard()) hisGuardDef += card.defense;
      }
    }
    
    for (int c=0;c<state.cardsFE;c++) {
      Card card = state.cards[c];
      if (card.location == Location.MY_HAND) {
        double value = wHand * evaluateHandCard(card);
        card.score = value;
        myValues[VALUES.HAND_SCORE.ordinal()] += value;
      } else if (card.location == Location.MY_BOARD) {
        double value = wMyBoard * evaluateCard(card);
        card.score = value;
        myValues[VALUES.BOARD_SCORE.ordinal()] += value;
      } else if (card.location == Location.HIS_BOARD) {
        double value = wHisBoard *evaluateCard(card);
        card.score = value;
        hisValues[VALUES.BOARD_SCORE.ordinal()] += value;
      }
    }
    
    if (state.opp.getBoardCardsCount() >= state.me.guardsCardsCount 
        && hisAtk-myGuardDef > state.me.health) {
      hisValues[VALUES.MISC.ordinal()] += 100;
    }

    int tempo = state.me.getBoardCardsCount() - state.opp.getBoardCardsCount();
    int myClockToHisFace = Integer.MAX_VALUE, hisClockToMyFace = Integer.MAX_VALUE;
    if (myAtk-hisGuardDef > 0  && state.me.getBoardCardsCount() > state.opp.guardsCardsCount) {
      myClockToHisFace = (int)(state.opp.health / (myAtk-hisGuardDef));
    }
    if (hisAtk-myGuardDef > 0 && state.opp.getBoardCardsCount() > state.me.guardsCardsCount) {
      hisClockToMyFace = (int)(state.me.health / (hisAtk - myGuardDef));
    }
    
    if (debug) {
      System.err.println("tempo : " + tempo);
      System.err.println("myClockToHisFace : "+myClockToHisFace);
      System.err.println("hisClockToHisFace : "+hisClockToMyFace);
    }
    // TODO add some strategic perspective 
    if (myClockToHisFace+1 < hisClockToMyFace && state.me.getBoardCardsCount() > state.opp.getBoardCardsCount()) {
//      wHisHealth *=5.0; // go face if we can 
    }
    myValues[VALUES.HEALTH.ordinal()] = wMyHealth * healthScore[state.me.health];
    hisValues[VALUES.HEALTH.ordinal()] = wHisHealth * healthScore[state.opp.health];
    
    myValues[VALUES.USED_MANA.ordinal()] = - 2 * state.me.mana;


    
    total = 0.0;
    for (int i=0; i<VALUES.values().length;i++) {
      total += (myValues[i] - hisValues[i]);
    }    
    return total;
  }

  private double evaluateHandCard(Card card) {
    double score = card.cost * 2 + 1;
    return score;
  }

  private double evaluateCard(Card card) {
    // formula ? 
    // 4.22*atk + 2.34*def + 0.36*attack(D) + 0.23*attack(B) + 1.49(G)+3.78(L)+3.11(W)
    
    double score = 0.0;
    score += 4 * card.attack;
    score += 2 * card.defense;
    score += ((card.abilities & Abilities.BREAKTHROUGH) != 0) ? 0.25 : 0;
    score += ((card.abilities & Abilities.LETHAL) != 0) ? 4 : 0;
    score += ((card.abilities & Abilities.GUARD) != 0) ? 1.5 : 0;
    score += ((card.abilities & Abilities.WARD) != 0) ? 3 : 0;
    score += ((card.abilities & Abilities.DRAIN) != 0) ? 0.25 : 0;

    return score;
  }

  
  public static void comparator(ReworkedEval first, ReworkedEval second) {
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

}
