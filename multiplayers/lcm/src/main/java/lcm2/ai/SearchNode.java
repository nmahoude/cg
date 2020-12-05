package lcm2.ai;

import java.util.ArrayList;
import java.util.List;

import lcm2.Agent;
import lcm2.CardType;
import lcm2.cards.Card;
import lcm2.simulation.Action;
import lcm2.simulation.Simulator;

public class SearchNode {
  private static Simulator sim = new Simulator();
  private static Scorer scorer = new Scorer();
  
  private SearchNode parent;
  
  public Action action = null;
  public Agent me = new Agent(0);
  public Agent opp = new Agent(1);
  
  List<Action> possibleActions = new ArrayList<>();
  
  List<SearchNode> childs = new ArrayList<>();
  
  double score = 0.0;
  int victoryCount;
  int totalCount;
  public int depth;
  
  
  public SearchNode bestNode(SearchNode currentBest) {
    if (action == Action.pass() && (currentBest == null || currentBest.score < this.score)) {
      currentBest = this;
    }

    for (SearchNode node : childs) {
      currentBest = node.bestNode(currentBest);
    }
    
    return currentBest;
  }
  
  public List<Action> actions() {
    List<Action> actions = new ArrayList<>();
    SearchNode current = this;
    while (current.action != null) {
      actions.add(0, current.action);
      current = current.parent;
    }
    return actions;
  }
  
  public void init() {
    if (depth == 300) return;

    childs.clear();
    computePossibleActions();
    //debugPossibleActions();
    
    
    for (Action action : possibleActions) {
      SearchNode child = new SearchNode();
      child.parent = this;
      child.action = action;
      child.me.copyFrom(this.me);
      child.opp.copyFrom(this.opp);
      child.depth = this.depth+1;
      childs.add(child);
      
      if (action != Action.pass()) {
        sim.run(child.me, child.opp, action);
        child.score = scorer.score(child.me, child.opp);
        child.init();
      }
    }
  }
  
  
  private void debugPossibleActions() {
    String decal = "";
    for (int i=0;i<depth;i++) {
      decal+= "  ";
    }
    System.err.println(decal+"Action = "+action);
    System.err.println(decal+">Score = "+score);
    System.err.println(decal+"Possible actions:");
    for (Action action : possibleActions) {
      System.err.println(decal+"-"+action);
    }
  }


  private void computePossibleActions() {
    clearPossibleActions();
    
    computeAttacks();
    computeSummons();
    computeUse();
    addPassAction();
  }




  private void clearPossibleActions() {
    possibleActions.clear();
  }


  private void computeSummons() {
    if (me.boardCardsFE == 9) return; // no space left
    
    for (int m=0;m<me.handCardsFE;m++) {
      Card card = me.handCards[m];
      if (card.isDead() || card.model.type != CardType.CREATURE) continue;
      if (card.model.cost > me.mana) continue;
      possibleActions.add(Action.summon(m));
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
          possibleActions.add(Action.use(m, i));
        }
      } else if (card.model.type == CardType.ITEM_GREEN) {
        for (int i=1;i<me.boardCardsFE;i++) {
          Card meCard = me.boardCards[i];
          if (meCard.isDead()) continue;
          possibleActions.add(Action.use(m, i));
        }
      } else {
        possibleActions.add(Action.use(m));
      }
    }
  }

  private void addPassAction() {
    possibleActions.add(Action.pass());
  }


  private void computeAttacks() {
    for (int m=1;m<me.boardCardsFE;m++) {
      if (me.boardCards[m].isDead()) continue;
      if (!me.boardCards[m].canAttack) continue;
      
      if (opp.guardsCount > 0) {
        // only attack guard cards
        for (int i=1;i<opp.boardCardsFE;i++) {
          if (!opp.boardCards[i].isGuard() || opp.boardCards[i].isDead()) continue;
          possibleActions.add(Action.attack(m, i));
        }
      } else {
        // attack all cards
        for (int i=0;i<opp.boardCardsFE;i++) {
          if (opp.boardCards[i].isDead()) continue;
          possibleActions.add(Action.attack(m, i));
        }
      }
    }
  }
}
