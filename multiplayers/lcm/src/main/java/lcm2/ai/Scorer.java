package lcm2.ai;

import lcm2.Agent;
import lcm2.Player;
import lcm2.cards.Card;

public class Scorer {
  
  public double score(Agent me, Agent opp) {
    
    if (me.face.defense <= 0) {
      return Double.NEGATIVE_INFINITY;
    }
    if (opp.face.defense <= 0) {
      return Double.POSITIVE_INFINITY;
    }
    
    if (me.guardsCount == 0 && opp.attackSum() >= me.face.defense) {
      return -1_000_000; // bad shape
    }
    
    
    return (Player.w[3] * score(me) - Player.w[4] * score(opp)) - Player.w[11] * me.mana;
  }

  private double score(Agent agent) {
    double score = 0.0;

    score += Player.w[0] * agent.boardCards[0].defense;
    
    for (int i=1;i<agent.boardCardsFE;i++) {
      Card card = agent.boardCards[i];
      if (card.isDead()) continue;
      
      score += Player.w[1] * card.attack;
      score += Player.w[2] * card.defense;
      score += Player.w[5];
      score += card.isGuard() ? Player.w[6] : 0;
      
      score += card.isLethal() ? Player.w[8] : 0;
      score += card.isWarded() ? Player.w[9] * (card.attack + card.defense) : 0;
      score += card.isBreakthrough() ? Player.w[10] : 0;

    }

    if (agent.boardCardsFE == 0) {
      score -= Player.w[7];
    }

    return score;
  }
}
