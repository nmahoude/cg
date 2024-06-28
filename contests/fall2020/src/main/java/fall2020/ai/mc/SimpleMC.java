package fall2020.ai.mc;

import fall2020.Agent;
import fall2020.Player;
import fall2020.State;
import fall2020.optimizer.Action;

public class SimpleMC {
  static final int MAX_DEPTH = 20;

  public static State state;
  
  static Action lastBestActions[] = new Action[MAX_DEPTH];
  static MCNode nodes[] = new MCNode[MAX_DEPTH];
  static int nodesFE = 0; // how many nodes are used
  
  public SimpleMC() {
    for (int i=0;i<nodes.length;i++) {
      nodes[i] = new MCNode();
    }
  }
  
  public String think(State state) {
    SimpleMC.state = state;
    Agent agent = state.agents[0];

    Action bestAction = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    
    long start = System.currentTimeMillis();
    
    MCNode root = nodes[0];
    root.castables = state.agents[0].castedSpells; // TODO always do 0 = is castable, 1 = already cast
    root.inv = agent.inv;
    root.turn = Player.turn;
    root.score = state.agents[0].score;
    root.recipables = 0;
    for (int r=0;r<state.recipesFE;r++) {
      root.recipables |= state.recipes[r].recipe.mask;
    }
    root.knownSpells = state.agents[0].knownSpells;
    root.brews = state.agents[0].brewedRecipe;
    root.end = false;
    root.fitnesse = 0.0;
    
    
    
    root.action = null;
    
    int iter = 0;
    while (true) {
      iter++;
      if ((iter & 255) == 0) {
        if (System.currentTimeMillis()-Player.start > 45) {
          break;
        }
      }
      
      MCNode current = root;
      for (int d=0;d<MAX_DEPTH-1;d++) {
        if (current.turn == 100) break;
        
        current = current.expand(d);
        if (current.end) {
          break;
        }
      }
      
      // score current
      double score = current.fitnesse;
      
      if (score > bestScore) {
        bestScore = score;
        bestAction = nodes[1].action;
        debugActions(nodes, state);

        // copy lastBestActions for next turn
        for (int n=2;n<MAX_DEPTH;n++) {
          lastBestActions[n-2] = nodes[n].action;
        }
      }
    }
    
    long end = System.currentTimeMillis();
    System.err.println("MC Iter : "+iter);
    
    if (state.agents[0].spellsFE < 10 && state.tomesFE > 0) {
      int chosen=0;
      for (int t=0;t<Math.min(state.tomesFE, state.agents[0].inv.inv[0]+1);t++) {
        if (!state.tomes[t].spell.repeatable /* only + */ && state.tomes[t].spell.allTier1plus > 0) {
          chosen = t;
          break;
        }
      }
      return "LEARN " + state.tomes[chosen].gameId;
    }
    
    //System.err.println("MC in "+(end-start)+" ms");
    if (bestAction != null) {
//      System.err.println("Next best actions after "+bestAction);
//      for (int i=0;i<MAX_DEPTH;i++) {
//        System.err.print(lastBestActions[i]+" --> ");
//      }
//      System.err.println();
      return bestAction.output(state);
    } else {
      System.err.println("MC found nothing :(");
      return "WAIT";
    }
    
    
  }

  private void debugActions(MCNode[] nodes, State state) {
    //System.err.println("Preparing a new best : ");

//    for (int i=1;i<MAX_DEPTH;i++) {
//      if (nodes[i] == null) break;
//      System.err.print(nodes[i].action.toString()+" --> ");
//    }
//    System.err.println();
    
    int brewCount = 0;
    
    MCNode currentNode = null;
    for (int i=1;i<MAX_DEPTH;i++) {
      currentNode = nodes[i];
      if (currentNode == null) break;
      if (currentNode.action.type == Action.BREW) {
        brewCount ++;
      }
      System.err.print(currentNode.action.debug(state,state.agents[0])+"; ");
      if (currentNode.end) {
        break;
      }
    }
    System.err.println(";brews = "+brewCount + "; score is "+currentNode.score+"; fitnesse "+currentNode.fitnesse);
  }
}
