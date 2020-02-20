package lcm.ai.eval;

import lcm.State;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.CardType;
import lcm.cards.Location;

public class Eval6 implements IEval {
  private static final double EPSILON = 0.001;
  private static final double WIN = Double.POSITIVE_INFINITY;
  private static final double LOSE = Double.NEGATIVE_INFINITY;
  enum VALUES {
    HEALTH("health"),
    HAND_CARDS_COUNT("hand count"),
    BOARD_CARD_COUNT("board count"),
    BOARD_SCORE("board score"),
//    BOARD_CARD_COUNT_BONUS("wipe bonus"),
//    DECK_COUNT_SCORE("deck card"),
    USED_MANA("used mana"),
    HAND_SCORE("hand score"),
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
  
  double wMyBoardCoef = 0.6;
  double wHisBoardCoef = 0.8;
  
  public Eval6() {
  }
  
  public Eval6(double my, double his) {
    wMyBoardCoef = my;
    wHisBoardCoef = his;
  }
  
  @Override
  public double eval(State state, boolean debug) {
    this.state = state;
    initValues();
    
    double wMyHealth = 1.0;
    double wHisHealth = 1.0;
    double wHand = 0.6;
    double wMyBoard = wMyBoardCoef;
    double wHisBoard = wHisBoardCoef;
    
    // quick cutoff
    if (state.opp.health <= 0) return Double.POSITIVE_INFINITY;
    if (state.me.health <= 0) return Double.NEGATIVE_INFINITY;

    
    
    // TODO si j'ai plus de cartes, augmenter les dégats sur FACE 
    // (stopper les trades sauf vraiment bon : modif wMyBoard & wHisBoard ?)
    
    
    // TODO si il a bcp plus de dégats que ma def
    int bigCreaturesInHand = 0;
    double myDef = 0, hisDef = 0;
    double myAtk = 0, hisAtk = 0;
    double myGuardDef = 0, hisGuardDef = 0;
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_HAND) {
        if (card.type == CardType.CREATURE && card.attack>=3 && card.defense >= 3) {
          bigCreaturesInHand++;
        }
      }
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
    /**** Nouveau bonus malus à tester */
    // Ne pas jouer trop de cartes sur le board si c'est overfit
//    if (state.me.boardCardsCount - state.opp.boardCardsCount >= 3) {
//      myValues[VALUES.MISC_BONUS.ordinal()] += bigCreaturesInHand * 5;
//    }

    // big bonus for him if we let the board in a state where next turn is lethal
    if (state.opp.getBoardCardsCount() >= state.me.guardsCardsCount 
        && hisAtk-myGuardDef > state.me.health) {
      hisValues[VALUES.MISC_BONUS.ordinal()] += 100;
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
    
    // give value to mana (so less mana spend for the same result is ok)
    myValues[VALUES.USED_MANA.ordinal()] = -10 * state.me.mana;
    
    /** fin des bonus/malus */
    
    
    myValues[VALUES.HEALTH.ordinal()] = wMyHealth * healthScore[state.me.health];
    hisValues[VALUES.HEALTH.ordinal()] = wHisHealth * healthScore[state.opp.health];
    
    myValues[VALUES.BOARD_CARD_COUNT.ordinal()] = 10.0 * state.me.getBoardCardsCount();
    hisValues[VALUES.BOARD_CARD_COUNT.ordinal()] = 10.0 * state.opp.getBoardCardsCount();
    
    myValues[VALUES.HAND_CARDS_COUNT.ordinal()] = 15.0 * state.me.handCardsCount;
    hisValues[VALUES.HAND_CARDS_COUNT.ordinal()] = 15.0 * state.opp.handCardsCount;
    
    for (int c=0;c<state.cardsFE;c++) {
      Card card = state.cards[c];
      if (card.location == Location.MY_HAND) {
        double value = wHand * evaluateHandCardEGaetan(card);
        card.score = value;
        myValues[VALUES.HAND_SCORE.ordinal()] += value;
      } else if (card.location == Location.MY_BOARD) {
        double value = wMyBoard * evaluateCardEGaetan(card);
        card.score = value;
        myValues[VALUES.BOARD_SCORE.ordinal()] += value;
      } else if (card.location == Location.HIS_BOARD) {
        double value = wHisBoard * evaluateCardEGaetan(card);
        card.score = value;
        hisValues[VALUES.BOARD_SCORE.ordinal()] += value;
      }
    }
    
    total = 0.0;
    for (int i=0; i<VALUES.values().length;i++) {
      total += (myValues[i] - hisValues[i]);
    }    
    return total;
  }


  private double evaluateHandCardEGaetan(Card card) {
    double score = 0.0;
    
    if (card.isLethal() && card.hasCharge()) {
      score += 0.1 * evaluateCardEGaetan(card);
    }
    return score;
  }

  public double evaluateCardEGaetan(Card card) {
    //  1.3 att / 0.7 def / 0.1 drain / 0.05 guard / 2 lethal / 1 ward / 0.1 break
    double score = 0.0;

    score += 3.9 * Math.abs(card.attack);
    score += 2.1 * Math.abs(card.defense);
    score += ((card.abilities & Abilities.BREAKTHROUGH) != 0) ? 0.3 : 0;
    score += ((card.abilities & Abilities.LETHAL) != 0) ? 6 : 0;
    score += ((card.abilities & Abilities.GUARD) != 0) ? 0.15 : 0;
    score += ((card.abilities & Abilities.WARD) != 0) ? 3 : 0;
    score += ((card.abilities & Abilities.DRAIN) != 0) ? 0.03 : 0;

    return score;
  }

  public double evaluateCard(Card card) {
    // formula ? 
    // 4.22*atk + 2.34*def + 0.36*attack(D) + 0.23*attack(B) + 1.49(G)+3.78(L)+3.11(W)
    
    double score = 0.0;
    score += 4 * Math.abs(card.attack);
    score += 2 * Math.abs(card.defense);
    score += ((card.abilities & Abilities.BREAKTHROUGH) != 0) ? 0.25 : 0;
    score += ((card.abilities & Abilities.LETHAL) != 0) ? 4 : 0;
    score += ((card.abilities & Abilities.GUARD) != 0) ? 1.5 : 0;
    score += ((card.abilities & Abilities.WARD) != 0) ? 3 : 0;
    score += ((card.abilities & Abilities.DRAIN) != 0) ? 0.25 : 0;

    return score;
  }

  
  public static void comparator(Eval6 first, Eval6 second) {
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
