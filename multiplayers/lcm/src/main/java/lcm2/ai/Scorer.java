package lcm2.ai;

import lcm2.Agent;
import lcm2.cards.Card;

public class Scorer {
  double wa = 1.4;
  double wd = 1.2;

  
  public double score(Agent me, Agent opp) {
    
    if (me.face.defense <= 0) {
      return Double.NEGATIVE_INFINITY;
    }
    if (opp.face.defense <= 0) {
      return Double.POSITIVE_INFINITY;
    }
    
    return (score(me) - score(opp));
  }

  private double score(Agent agent) {
    double score = 0.0;

    for (int i=0;i<agent.boardCardsFE;i++) {
      Card card = agent.boardCards[i];
      if (card.isDead()) continue;
      
      score += wa * card.attack;
      score += wd * card.defense;
      score += 1;
    }

    if (agent.boardCardsFE == 0) {
      score -= 5;
    }

    return score;
  }
}
