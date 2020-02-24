package lcm2;

import java.util.ArrayList;
import java.util.List;

import lcm2.cards.Card;
import lcm2.simulation.Action;
import lcm2.simulation.Simulator;

public class SimpleAI2 {
  List<String> actions = new ArrayList<>();
  private final Agent me;
  private final Agent opp;
  private Simulator sim;
  
  public SimpleAI2(Agent me, Agent opp) {
    this.me = me;
    this.opp = opp;
  }

  public void run() {
    think();
    output();
  }

  private void think() {
  	System.err.println("----------- think ---------------");
    actions.clear();
    sim = new Simulator();
    
    // play cards from board
    for (int i=0+1;i<me.boardCardsFE;i++) {
      Card current = me.boardCards[i];

    	if (current.attack == 0) {
    	  continue;
    	}

    	// check if we can go for the face kill
      if (canFaceKillAndDoIt(i, current)) continue;
      
    	Card bestCard = null;
    	int bestIndex = 0;
    	double bestScore = Double.NEGATIVE_INFINITY;
    	for (int o=0;o<opp.boardCardsFE;o++) {
    	  Card oppCard = opp.boardCards[o];

    	  double score = scoreCard(current, oppCard);
    	  System.err.println("Score of "+current.model.instanceId + " vs "+oppCard.model.instanceId +" = "+score);
    	  
    	  if (score > bestScore) {
    	    bestScore = score;
    	    bestCard = oppCard;
    	    bestIndex = o;
    	  }
    	}
    	if (bestCard != null) {
        Action action = Action.attack(i, bestIndex);
        sim.run(me, opp, action);

        actions.add("ATTACK "+current.model.instanceId+ " " + bestCard.model.instanceId);
        
    	}
    }    
    
    summonCards();
    
    actions.add("PASS");
  }

  private void summonCards() {
    boolean cardChosen = false;

    System.err.println("Summon cards ...");
    for (int i=0;i<me.handCardsFE;i++) {
      Card current = me.handCards[i];
      current.debug();
    }
    
    do {
      cardChosen = false;
      double bestScore = Double.NEGATIVE_INFINITY;
      int bestIndex = -1;
      
      for (int i=0;i<me.handCardsFE;i++) {
        Card current = me.handCards[i];
        if (me.boardCardsFE < 9 && current.model.type == CardType.CREATURE && current.model.cost  <= me.mana) {
          double score = 0.0;
          score += 1.0 * (current.attack + current.defense) / current.model.cost;
          if (current.isGuard()) score += 5;
          
          if (score > bestScore) {
            bestScore = score;
            bestIndex = i;
          }
        }
      }
      if (bestIndex != -1) {
        Card card = me.handCards[bestIndex];

        Action action = Action.summon(bestIndex);
        sim.run(me, opp, action);
        
        actions.add("SUMMON "+card.model.instanceId);
        cardChosen = true;
      }
    } while (cardChosen);
      
  }

  private boolean canFaceKillAndDoIt(int ourCardIndex, Card current) {
    if (opp.guardsCount == 0) {
      int attackSum = 0;
      for (int r = ourCardIndex;r<me.boardCardsFE;r++) {
        Card remaining = me.boardCards[r];
        attackSum += remaining.attack;
      }
      if (attackSum >= opp.face.defense) {
        // go for face
        Action action = Action.attack(ourCardIndex, 0);
        sim.run(me, opp, action);

        System.err.println("Go for the kill "+current.model.instanceId);
        actions.add("ATTACK "+me.boardCards[ourCardIndex].model.instanceId+ " " + opp.boardCards[0].model.instanceId);
        return true;
      }
    }
    return false;
  }

  private double scoreCard(Card current, Card oppCard) {
    double score = 0;
    if (oppCard.isDead()) { return Double.NEGATIVE_INFINITY; }
    if (opp.guardsCount > 0 && !oppCard.isGuard()) { return Double.NEGATIVE_INFINITY; }

    boolean iDie = !current.isWarded() && (oppCard.attack >= current.defense || oppCard.isLethal());
    boolean heDies = (current.attack >= oppCard.defense || current.isLethal());

    
    if (heDies) {
      score += oppCard.attack + oppCard.defense;
      if (oppCard.isLethal()) score += 10;
    }
    if (iDie) {
      score -=  (current.attack + current.defense);
      if (current.isGuard()) {
        score -= 1000;
      }
    }
    
    // face
    if (oppCard.model.cardNumber < 0) {
      score += 1;
    }
    return score;
  }

  private int getIndexOfGuard(Agent agent) {
    for (int i=0;i<agent.boardCardsFE;i++) {
      Card card = agent.boardCards[i];
      if (!card.isDead() && card.isGuard()) {
        return i;
      }
    }
    return -1;
  }

  private void output() {
    for (String action : actions) {
      System.out.print(action+";");
    }
    System.out.println();
  }

}
