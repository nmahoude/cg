package lcm2.simpleai;

import java.util.ArrayList;
import java.util.List;

import lcm2.Agent;
import lcm2.cards.Card;

/** 
 * 
 * Recherche la combinaison de cartes la moins "chere" pour tuer une carte
 *
 */
public class Ordonanceur {
  private Agent me;
  private Card target;

  List<Card> result = new ArrayList<>();
  double bestScore = Double.NEGATIVE_INFINITY;
  private List<Card> allCards;
  
  public List<Card> find(Agent me, List<Card> allCards, Card target) {
    this.me = me;
    this.allCards = allCards;
    this.target = target;
    
    find(new ArrayList<>(), new ArrayList<>(), 0, 0);
    
    return result;
  }


  private void find(List<Card> cards, List<Card> ignoredCards, double cs, int ta) {
    for (Card card : allCards) {
      if (cards.contains(card)) continue;
      if (ignoredCards.contains(card)) continue;
      
      // attack
      double currentScore = cs - 1 ; // each played card cost 1
      int totalAtt = ta;
      
      cards.add(card);
      totalAtt+= card.attack;
      
      if (target.attack >= card.defense || target.isLethal()) {
        currentScore -= card.defense;
        currentScore -= 2; // one card less
        if (card.isGuard()) currentScore -= 5;
      }
      
      if (card.isLethal() 
          || totalAtt >= target.defense 
          || (target.isGuard() && allCards.isEmpty())) {
        System.err.print("Found a solution with cards ");
        for (Card c : cards) {
          System.err.print(""+c.model.instanceId+", ");
        }
        System.err.println();
        
        System.err.println("Score : "+currentScore);
        if (currentScore > bestScore) {
          System.err.println("    best !");
          bestScore = currentScore;
          result.clear();
          result.addAll(cards);
        }
      } else {
        find(cards, ignoredCards, currentScore, totalAtt);
      }
      cards.remove(card);
      
      // don't attack with this card
//      ignoredCards.add(card);
//      find(cards, ignoredCards, cs, ta);
//      ignoredCards.remove(card);
    }
  }
  
}
