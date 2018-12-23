package lcm;

import lcm.cards.Card;
import lcm.cards.Location;
import lcm.sim.Constants;

public class GameMetric {
  
  int manaAdvantage = 0;
  int boardManaAdvantage = 0;
  int boardAdvantage = 0;
  int drawAdvantage = 0;
  int handSizeAdvantage;
  
  public void updateMetrics(State state) {
    drawAdvantage += (30 - state.me.deck) - (30 - state.opp.deck) ;
    handSizeAdvantage += state.me.handCardsCount - state.opp.handCardsCount;
    
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      
      if (card.location == Location.MY_BOARD) {
        boardManaAdvantage+=card.cost;
        boardAdvantage++;
      }
      if (card.location == Location.HIS_BOARD) {
        boardManaAdvantage-=card.cost;
        boardAdvantage--;
      }
    }
  }
  
  public void predictWinner(State state) {
    int turn = Math.min(13, state.turn-Constants.DRAFT_TURNS);
    
    double boardManaAdvantageEfficiency[] = { 0, 0.025, 0.07, 0.12, 0.13, 0.14, 0.15, 0.16 ,0.17, 0.18, 0.21, 0.24, 0.24 ,0.23, 0.22};
    double drawAdvantageEffeciency[] = { 0, -0.01, -0.02, -0.02, -0.01, 0, 0.03, 0.06, 0.08, 0.1, 0.1, 0.1, 0.13, 0.15, 0.16};
    double boardAdvantageEffeciency[] = { 0, 0.025, 0.05, 0.06, 0.06, 0.05, 0.04, 0.03, 0, -0.01, -0.025, -0.06, -0.06, -0.06, -0.06};
    double handSizeAdvantageEffeciency[] = { 0, -0.03, -0.08, -0.1, -0.1, -0.07, -0.04, 0.03, 0.07 ,0.14, 0.17, 0.21, 0.22 ,0.22, 0.20};
    
    double score = boardManaAdvantageEfficiency[turn] * boardManaAdvantage
        + drawAdvantageEffeciency[turn] * drawAdvantage
        + boardAdvantageEffeciency[turn] * boardAdvantage
        + handSizeAdvantageEffeciency[turn] * handSizeAdvantage;
    
//    System.err.println("Current score (>0 is better)" + score);
  }
  
}
