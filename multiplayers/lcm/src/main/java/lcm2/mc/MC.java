package lcm2.mc;

import java.util.Arrays;
import java.util.List;

import lcm2.Agent;
import lcm2.CardType;
import lcm2.Player;
import lcm2.ai.Scorer;
import lcm2.cards.Card;
import lcm2.simulation.Action;
import lcm2.simulation.Simulator;

public class MC {
  private static Simulator sim = new Simulator();
  private static Scorer scorer = new Scorer();
  
  private Action[] bestActions = new Action[100];
  private int bestActionsFE = 0;

  private Action[] currentActions = new Action[100];
  private int currentActionsFE = 0;

  public Agent me = new Agent(0);
  public Agent opp = new Agent(1);

  private Action[] possibleActions = new Action[100];

  private int possibleActionsFE;
  
  
  public void think(Agent originalMe, Agent originalOpp) {
    double bestScore = Double.NEGATIVE_INFINITY;
    bestActions[0] = Action.pass();
    bestActionsFE = 1;
    
    
    int iter = 0;
    while(true) {
      iter++;
      me.copyFrom(originalMe);
      opp.copyFrom(originalOpp);
    
      currentActionsFE = 0;
      boolean stop = false;
      
      while(!stop) {
        if (Player.random.nextInt(100) > 95) {
          stop = true;
        } else {
          
          possibleActionsFE = 0;
          computeSummons(); // TODO possible de precompute tous les summon possible pour pick dedans sur le 1er tour
          computeAttacks();
          computeUse();
          
          if (possibleActionsFE == 0) {
            stop = true;
          } else {
            int rand = Player.random.nextInt(possibleActionsFE);
            Action chosen = possibleActions[rand];
            currentActions[currentActionsFE++] = chosen;
            sim.run(me, opp, chosen);
            
          }
        }
      }
      
      double score = scorer.score(me, opp);
      if (score > bestScore) {
        bestScore = score;
        bestActionsFE = currentActionsFE;
        Action[] tmp = currentActions;
        currentActions = bestActions;
        bestActions = tmp;
      }
      
      if (iter % 255 == 0) {
        if (System.currentTimeMillis() - Player.start > 40) {
          break;
        }
      }
    }
    
    System.err.println("Iterations = "+iter);
    if (bestActionsFE != 0) {
      me.copyFrom(originalMe);
      opp.copyFrom(originalOpp);
      for (int i = 0;i<bestActionsFE;i++) {
        System.err.println("Action = "+bestActions[i]);

        bestActions[i].print(me, opp, System.out);
        System.out.print(";");
        
        sim.run(me, opp, bestActions[i]);
      }
      
      // policy: attack with all remaining cards
      if (opp.guardsCount == 0) {
        for (int i=1;i<me.boardCardsFE;i++) {
          Card myCard = me.boardCards[i];
          if (!myCard.canAttack) continue;
          Action.attack(i, -1).print(me, opp, System.out);
        }
      }
      
      System.out.println();
    } else {
      System.err.println("No best node");
      System.out.println("PASS");
    }
    
    
  }
  
  private void computeSummons() {
    if (me.boardCardsFE == 9) return; // no space left
    
    for (int m=0;m<me.handCardsFE;m++) {
      Card card = me.handCards[m];
      if (card.isDead() || card.model.type != CardType.CREATURE) continue;
      if (card.model.cost > me.mana) continue;
      possibleActions[possibleActionsFE++] = Action.summon(m);
    }
  }

  private void computeUse() {
    for (int m=0;m<me.handCardsFE;m++) {
      Card card = me.handCards[m];

      if (card == Card.EMPTY || card.model.type == CardType.CREATURE) continue;
      if (card.model.cost > me.mana) continue;
      
      if (card.model.type == CardType.ITEM_RED) {
        for (int i=1;i<opp.boardCardsFE;i++) {
          Card oppCard = opp.boardCards[i];
          if (oppCard.isDead()) continue;
          possibleActions[possibleActionsFE++] = Action.use(m, i);
        }
      } else if (card.model.type == CardType.ITEM_GREEN) {
        for (int i=1;i<me.boardCardsFE;i++) {
          Card meCard = me.boardCards[i];
          if (meCard.isDead()) continue;
          possibleActions[possibleActionsFE++] = Action.use(m, i);
        }
      } else {
        possibleActions[possibleActionsFE++] = Action.use(m);
      }
    }
  }

  private void computeAttacks() {
    for (int m=1;m<me.boardCardsFE;m++) {
      if (me.boardCards[m].isDead()) continue;
      if (!me.boardCards[m].canAttack) continue;
      
      if (opp.guardsCount > 0) {
        // only attack guard cards
        for (int i=1;i<opp.boardCardsFE;i++) {
          if (!opp.boardCards[i].isGuard() || opp.boardCards[i].isDead()) continue;
          possibleActions[possibleActionsFE++] = Action.attack(m, i);
        }
      } else {
        // attack all cards
        for (int i=0;i<opp.boardCardsFE;i++) {
          if (opp.boardCards[i].isDead()) continue;
          possibleActions[possibleActionsFE++] = Action.attack(m, i);
        }
      }
    }
  }
}
