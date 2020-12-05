package lcm2.simpleai;

import java.util.ArrayList;
import java.util.List;

import lcm2.Agent;
import lcm2.CardType;
import lcm2.cards.Card;
import lcm2.simulation.Action;
import lcm2.simulation.Simulator;

public class SimpleAI3 {
  List<String> actions = new ArrayList<>();
  private final Agent me;
  private final Agent opp;
  private Simulator sim;
  
  public SimpleAI3(Agent me, Agent opp) {
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
    
    
    List<Card> oppOrder = sortOppCards(opp);
    List<Card> myCards = myPlayingCardsAsList();
    for (Card oppCard : oppOrder) {
      if (!oppCard.isGuard() && opp.guardsCount > 0) continue;
      
      List<Card> cardsToPlay = new Ordonanceur().find(me, myCards, oppCard);
      System.err.println("Attacking "+oppCard.model.instanceId+" with ... ");
      for (Card play : cardsToPlay) {
        if (play.attack == 0) continue;
        play.debug();
        int index = indexFromCard(me, play);
        Action action = Action.attack(index, indexFromCard(opp, oppCard));
        sim.run(me, opp, action);

        actions.add("ATTACK "+play.model.instanceId+ " " + oppCard.model.instanceId);
        myCards.remove(play);
      }
    }
    
    // FACE
    if (myCards.size() > 0 && opp.guardsCount == 0) {
      for (Card c : myCards) {
        Action action = Action.attack(indexFromCard(me, c), 0);
        sim.run(me, opp, action);

        actions.add("ATTACK "+c.model.instanceId+ " -1");
        
      }
    }
    
    summonCards();
    
    actions.add("PASS");
  }

  private ArrayList<Card> myPlayingCardsAsList() {
    ArrayList<Card> cards = new ArrayList<>();
    for (int i=1;i<me.boardCardsFE;i++) {
      cards.add(me.boardCards[i]);
    }
    for (int i=0;i<me.handCardsFE;i++) {
      Card card = me.handCards[i];
      if (card.isCharge() && card.model.type.isAttackItem()) {
        cards.add(card);
      }
    }
    return cards;
    
  }

  private int indexFromCard(Agent agent, Card bestCard) {
    for (int i=0;i<agent.boardCardsFE;i++) {
      if (agent.boardCards[i].model.instanceId == bestCard.model.instanceId) return i;
    }
    return -666;
  }

  private List<Card> sortOppCards(Agent opp) {
    List<Card> order = new ArrayList<>();
    for (int i=0;i<opp.boardCardsFE;i++) {
      order.add(opp.boardCards[i]);
    }
    order.sort((c1, c2) -> Integer.compare(assesCard(c2), assesCard(c1)));
    return order;
  }

  private int assesCard(Card card) {
    int score = 0;
    if (card.isGuard()) {
      score += 1000;
    }
    score += 4 * card.attack + card.defense;
    return score;
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
          if (current.isGuard() && me.guardsCount < 3) score += 5;
          
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
