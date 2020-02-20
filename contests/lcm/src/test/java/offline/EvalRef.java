package offline;

import lcm.State;
import lcm.ai.eval.IEval;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.CardType;
import lcm.cards.Location;

public class EvalRef implements IEval{
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
    ATK_SUM("sum atk (b)"),
    DEF_SUM("sum def (b)"),
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
  
  public EvalRef() {
  }
  
  public EvalRef(double my, double his) {
    wMyBoardCoef = my;
    wHisBoardCoef = his;
  }
  @Override
  public double eval(State state, boolean debug) {
    this.state = state;
    initValues();
    
    double wMyHealth = wMyHealthCoeff;
    double wHisHealth = wHisHealthCoeff;
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

    // big bonus for me if He lets the board in a state where next turn is lethal
    if (state.me.getBoardCardsCount() >= state.opp.guardsCardsCount 
        && myAtk-hisGuardDef > state.opp.health) {
      myValues[VALUES.MISC_BONUS.ordinal()] += 100;
    }

    int tempo = state.me.getBoardCardsCount() - state.opp.getBoardCardsCount();
    double myClockToHisFaceScore = 0, hisClockToMyFaceScore = 0;
    if (myAtk-hisGuardDef > 0  && state.me.getBoardCardsCount() > state.opp.guardsCardsCount) {
      myClockToHisFaceScore = 1.0 * (myAtk-hisGuardDef) / state.opp.health;
    }
    if (hisAtk-myGuardDef > 0 && state.opp.getBoardCardsCount() > state.me.guardsCardsCount) {
      hisClockToMyFaceScore = 1.0 * (hisAtk - myGuardDef) / state.me.health;
    }
    
    if (debug) {
      System.err.println("tempo : " + tempo);
      System.err.println("myClockToHisFaceScore : "+myClockToHisFaceScore);
      System.err.println("hisClockToMyFaceScore : "+hisClockToMyFaceScore);
    }
    
    myValues[VALUES.USED_MANA.ordinal()] = -10.0 * state.me.mana;
    // myValues[VALUES.CLOCK.ordinal()] = 5.0 * myClockToHisFaceScore;
    // hisValues[VALUES.CLOCK.ordinal()] = 5.0 * hisClockToMyFaceScore;
    
    myValues[VALUES.HEALTH.ordinal()] = wMyHealth * healthScore[state.me.health];
    hisValues[VALUES.HEALTH.ordinal()] = wHisHealth * healthScore[state.opp.health];
    
    myValues[VALUES.BOARD_CARD_COUNT.ordinal()] = 10.0 * state.me.getBoardCardsCount();
    hisValues[VALUES.BOARD_CARD_COUNT.ordinal()] = 10.0 * state.opp.getBoardCardsCount();
    
    myValues[VALUES.HAND_CARDS_COUNT.ordinal()] = 5.0 * (state.me.handCardsCount );
    hisValues[VALUES.HAND_CARDS_COUNT.ordinal()] = 5.0 * (state.opp.handCardsCount );

    for (int c=0;c<state.cardsFE;c++) {
      Card card = state.cards[c];
      if (card.location == Location.MY_HAND) {
        double value = wHand * evaluateHandCardEGaetan(card);
        card.score = value;
        myValues[VALUES.HAND_SCORE.ordinal()] += value;
      } else if (card.location == Location.MY_BOARD) {
        double value = wMyBoard * evaluateCard(card);
        card.score = value;
        myValues[VALUES.BOARD_SCORE.ordinal()] += value;
      } else if (card.location == Location.HIS_BOARD) {
        double value = wHisBoard * evaluateCard(card);
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
    
    if (card.instanceId == 151) {
      // DECIMATE equivalent à 6/6 ?
      //return evaluateCard(Card.card6);
    }
    if (card.isLethal() && card.hasCharge()) {
      score += 0.1 * evaluateCard(card);
    }
    
    if (card.type == CardType.ITEM_GREEN) {
      if (state.turn < 30 + 8) {
        return 10;
      }
    }

    return score;
  }

  public double evaluateCard(Card card) {
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
}
