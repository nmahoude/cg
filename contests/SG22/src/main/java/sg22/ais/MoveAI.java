package sg22.ais;

import sg22.Application;
import sg22.Cards;
import sg22.Hand;
import sg22.Player;
import sg22.State;
import sg22.Actions.Action;
import sg22.nodes.Node;

public class MoveAI {
  private final Action action = new Action();
  final int[] totalNeeded = new int[9];
  
  int cardValues[] = new int[] { 1, 2, 3, 3, 5, 10, 3, 3, 5, 0, 0};
  public Action think(Node root) {
    State state = root.s;
    System.err.println("MoveAI think ... ");
    
    calculateNeeded(state);
    
    if (Player.turn == 1) {
      if (!state.isOppNear(Cards.DAILY_ROUTINE)) {
        return action.moveTo(Cards.DAILY_ROUTINE);
      } else if (!state.isOppNear(Cards.CI)) {
        return action.moveTo(Cards.CI);
      } 
    }
    
    
    
    double bestScore = Double.NEGATIVE_INFINITY;
    Node bestNode = null;
    for (int i=0;i<root.children.length;i++) {
      Node child = root.children.get(i);
      System.err.println("Scoring "+child.action+" ...");
      double score = fitnesse(child.action, child.parent.s, child.s);
      System.err.println("Scoring "+child.action+" => "+score);
      if (score > bestScore) {
        bestScore = score;
        bestNode = child;
      }
    }
    return bestNode.action;
  }

  private double fitnesse(Action action, State before, State current) {
    double score = 0.0;

    
    if (before.agents[0].location == 6 && before.agents[0].score == 4) {
      // on peut, peut-etre sauter le 7 pour donner 2 cartes pourries ou recup 2 debts
      
      int cardsICanThrow = 0;
      for (int i=0;i<8;i++) {
        if (before.hand.get(Hand.LOC_HAND, i) == 0) continue;
        
        if (totalNeeded[i] == 0) {
          cardsICanThrow += before.hand.get(Hand.LOC_HAND, i);
        }
        
        
      }
      if (cardsICanThrow >=2 || before.hand.getAliveCards(Hand.LOC_HAND) == cardsICanThrow) {
        score += 100; // contrebalance le go through admin
      }
    }

    
    // position (current vs previous)
    int delta = current.agents[0].location - before.agents[0].location;
    if (delta < 0 ) {
      delta += 8;
      score -= 100.0; // go through admin
    }
    score -= 10 * delta;
    
    // cardless zone target
    if (before.cardsOnDesks.count[action.target2] == 0 ) {
      score -= 100_000; // to reimplement last algo, but TODO change it somehow
    }
    
    // small bonus for cards i need most to finish applications
    score += 0.05 * totalNeeded[action.target2];
    score += 0.01 * cardValues[action.target2];
    
    // quand P2, fin de boucle (6 ou 7), qu'on a pas choppé de DR, que l'autre n'est pas à coté, on jump sur 2 plutot que 0 ou 1
    if (!Player.IMPlayerOne
        && Player.turn < 12
        && before.agents[0].location >= 6
        && before.agents[0].permanentDailyRoutineCards == 0
        && !before.isOppNear(Cards.DAILY_ROUTINE)
        && action.target2 == Cards.DAILY_ROUTINE
        ) {
      score += 10_000;
    }
      
    
    
    
    
    if (before.cardsOnDesks.count[Cards.CI] > 0 
        && Math.abs(delta) <= 1 
        // && Player.turn < 50 
        && before.hand.totalFromCard(Hand.CI) < 5 
        // && totalNeeded[Cards.CI] > 0 // il faut que j'en ai besoin 
        && !before.isOppNear(action.target) // don't give cards
        
        ) {
      if (action.target2 == Cards.CI) score += 50_000;
    }
    
    
    // Décourager le fait de donner des cartes à l'adversaire ...
    if (current.giveCount > 0) {
      score -= 200;
    }
    
    // Prendre des cartes CI à fond
    if (Player.turn <= 6 && action.target2 == Cards.CI && current.giveCount == 0) {
      score += 1000;
    }

    // '3' est un peu une case maudite
    
    // sauter sur CI si il est sur 3
    if (current.agents[1].location == Cards.TASK_PRIORIZATION) {
      // let's go to CI si il reste des cartes à prendre
      if (before.cardsOnDesks.count[Cards.CI] > 0 && action.target == Cards.CI) {
        score += 1000;
      }
    }

    // décourager la case 3 quand on passe par admin car il peut me re-sauter au dessus et bloquer CI 
    if (action.target == Cards.TASK_PRIORIZATION && current.agents[1].location != Cards.CI) {
      score -= 1000;
    }

    
    return score;
  }

  private void calculateNeeded(State state) {
    // calculate how many cards of each are needed for applications
    for (int i=0;i<8;i++) {
      totalNeeded[i]=0; 
    }
    
    for (Application a : State.applications) {
      for (int i=0;i<8;i++) {
        if (a.needed[i]> 0) totalNeeded[i]+=2; 
      }
    }
    totalNeeded[Cards.BONUS] = state.applications.size(); // weird TODO better ?
  }
}
