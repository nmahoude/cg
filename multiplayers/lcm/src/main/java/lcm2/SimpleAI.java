package lcm2;

import java.util.ArrayList;
import java.util.List;

import lcm2.cards.Card;
import lcm2.simulation.Action;
import lcm2.simulation.Simulator;

public class SimpleAI {
  List<String> actions = new ArrayList<>();
  private final Agent me;
  private final Agent opp;
  
  public SimpleAI(Agent me, Agent opp) {
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
    Simulator sim = new Simulator();
    
    // play cards from board
    for (int i=0+1;i<me.boardCardsFE;i++) {
      Card current = me.boardCards[i];
    	boolean attack = false;

    	// kill guards
    	if (opp.guardsCount > 0) {
      	System.err.println("Opp number of guards : "+opp.guardsCount);
        int guardIndex = getIndexOfGuard(opp);
        Card guard = opp.boardCards[guardIndex];
        
        Action action = Action.attack(i, guardIndex);
        sim.run(me, opp, action);
        
        System.err.println("his guard after attack ... dead? "+guard.isDead());
        opp.boardCards[guardIndex].debug();
      	System.err.println("Opp number of guards : "+opp.guardsCount);
        
        actions.add("ATTACK "+current.model.instanceId+ " " + guard.model.instanceId);
        attack = true;
    	}

    	// kill without being killed => go for it
    	if (!attack) {
      	for (int o=1;o<opp.boardCardsFE;o++) {
      		Card oppCard = opp.boardCards[o];
      		if (oppCard.isDead()) continue;
      		if (oppCard.defense <= current.attack && oppCard.attack < current.defense) {
            Action action = Action.attack(i, o);
            sim.run(me, opp, action);
            
            System.err.println("Kill without being killed, go for it");
      			
            actions.add("ATTACK "+current.model.instanceId+ " " + oppCard.model.instanceId);
            attack = true;
            break;
      		}
      	}
    	}
      	
    	if (!attack) {
        // go for faces
        Action action = Action.attack(i, 0);
        sim.run(me, opp, action);

        actions.add("ATTACK "+me.boardCards[i].model.instanceId+ " " + opp.boardCards[0].model.instanceId);
    	}
    }    
    
    // play cards from hand
    for (int i=0;i<me.handCardsFE;i++) {
      Card current = me.handCards[i];
      current.debug();
      if (me.boardCardsFE < 9 && current.model.type == CardType.CREATURE && current.model.cost  <= me.mana) {
      	Action action = Action.summon(i);
        sim.run(me, opp, action);
        actions.add("SUMMON "+current.model.instanceId);
      }
    }
    actions.add("PASS");
  }

  private int getIndexOfGuard(Agent agent) {
    for (int i=0;i<agent.boardCardsFE;i++) {
      Card card = agent.boardCards[i];
      if (card != Card.EMPTY && card.isGuard()) {
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
