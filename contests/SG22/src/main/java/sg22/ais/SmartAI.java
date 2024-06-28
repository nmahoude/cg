package sg22.ais;

import java.util.ArrayList;
import java.util.List;

import fast.array.FastArray;
import sg22.Application;
import sg22.Cards;
import sg22.GamePhase;
import sg22.Hand;
import sg22.Player;
import sg22.State;
import sg22.Actions.Action;
import sg22.nodes.Node;
import sg22.nodes.NodeCache;
import sg22.nodes.TranspositionTable;

public class SmartAI {

  
  private final static PlayCardAI playCardAI = new PlayCardAI();
  private final static MoveAI moveAI = new MoveAI();
  private final static GiveAI giveAI = new GiveAI();
  private final static ThrowAI throwAI = new ThrowAI();
  private final static Action action = new Action();
  private final static List<Node> listOfActions = new ArrayList<>();
  
  
  private static FastArray<Node> allChilds = new FastArray<>(Node.class, 80_000);
  private static FastArray<Node> futureAllChilds = new FastArray<>(Node.class, 80_000);
  private Node root;
  
  
  public Action think(State state) {
    NodeCache.reset();
    TranspositionTable.clear();
    
    Node bestRelease = null;
    double score;
    double bestScore = Double.NEGATIVE_INFINITY;
    
    root = Node.root(state);
    
    allChilds.clear();
    allChilds.add(root);
    
    long start = System.currentTimeMillis();
    
    int sims = 0;
    while (!allChilds.isEmpty()) {
      
      sims++; // somehow stop even if END is not reach because of timeouts 
      
      int previousNodeUse = NodeCache.getCurrentUse();
      futureAllChilds.clear();
      
      for (int i=0;i<allChilds.length;i++) {
        Node child = allChilds.get(i);
        if (child.s.phase == GamePhase.END) continue;
        if (sims != 1 && TranspositionTable.get(child) != null) continue; // already seen
        TranspositionTable.put(child);
        
        child.compute();
//        if (sims != 0 && Player.turn >= 0) {
//          System.err.println("   "+i+" [["+child.action +" "+child.s.hand+ "]] => "+child.children.length);
//          if (i <= 135) {
//            System.err.println("               DEBUG");
//            for (int k=0;k<child.children.length;k++) {
//              System.err.println("                   "+child.children.get(k));
//              
//            }
//          }
//        }
        
        futureAllChilds.addAll(child.children);
        
      }
      int newCurrentUse = NodeCache.getCurrentUse();
      System.err.println("Layer use nodes : "+(newCurrentUse - previousNodeUse)+ ", collisions : "+TranspositionTable.totalCollisions);
      
      for (int i=0;i<futureAllChilds.length;i++) {
        Node node = futureAllChilds.get(i);
        
        // check only for release
        if (! node.action.isRelease()) continue;
        score = fitnesse(node);
        if (score > bestScore) {
          bestScore = score;
          bestRelease = node;
        }
      }
      
      FastArray<Node> temp = futureAllChilds;
      futureAllChilds = allChilds;
      allChilds = temp;
      
      
      if (System.currentTimeMillis() - Player.start > 40) break; // stop the deepening 
    }    
    long end = System.currentTimeMillis();
    System.err.println("Compute smart ai in "+(end-start)+" ms");
    

    System.err.println("Possible actions : ");
    root.debug();

    if (bestRelease != null 
        && state.agents[0].score < 4
        && state.phase == GamePhase.PLAY 
        && state.hand.getHandCount(Cards.CI) > 0 
        && state.hand.getHandCount(Cards.BONUS) > 0) {

      System.err.println("Denying release  " + bestRelease+" to replace by CI 8 ? ");
      bestRelease = null;
    }
    
    
    
    
    int debtOccured = bestRelease != null ? bestRelease.s.hand.getHandCount(Cards.DEBT) - bestRelease.parent.s.hand.getHandCount(Cards.DEBT) : 1000;
    if (bestRelease != null && (
        (state.phase == GamePhase.RELEASE && debtOccured <= 1) 
        || (
          (Player.IMPlayerOne && Player.turn >= 12)
          ||
          (!Player.IMPlayerOne && Player.turn >= 12)
        )
      )) {
      System.err.println("found a release with score "+bestScore+"with debt : "+debtOccured+" ! ");
      listOfActions.clear();
      Node first = bestRelease;
      while (first != root) {
        listOfActions.add(0, first);
        first = first.parent;
      }
    
      System.err.println("Suite : ");
      for (Node n : listOfActions) {
        System.err.print(n+"  => ");
      }
      System.err.println();
      return listOfActions.get(0).action;
    } else {
      if (state.phase == GamePhase.GIVE) {
        return giveAI.think(root);
      } else if (state.phase == GamePhase.MOVE) {
        return moveAI.think(root);
      } else if (state.phase == GamePhase.PLAY) {
        return playCardAI.think(root);
      } else if (state.phase == GamePhase.THROW) {
        return throwAI.think(root);
      } else if (state.phase == GamePhase.RELEASE) {
        return action.doWait();
      } else {
        throw new RuntimeException("Don't know how tohandle phase "+state.phase);
      }
      
    }
  }


  private double fitnesse(Node node) {
    if (Player.DEBUG_RELEASES) {
      System.err.println("fitness ofrelease for #"+node.action.target);
    }
    double score = 100.0 * node.s.agents[0].score;
    
    int debtOccured = node.s.hand.getHandCount(Cards.DEBT) - node.parent.s.hand.getHandCount(Cards.DEBT);

    if ( debtOccured > 4 ) {
      return Double.NEGATIVE_INFINITY; // limit debtOccured TODO pas en dur !
    }

    int totalUsableCards = node.s.hand.getAliveCards() + node.s.hand.totalCount(Hand.LOC_AUTOMATED); 
    if (debtOccured > 0 
        && 1.0 * (totalUsableCards) / (totalUsableCards + node.s.hand.getDebtCards()) < 0.2) {
      return Double.NEGATIVE_INFINITY; // limit debtOccured TODO pas en dur !
    }

    score += 0.05 * node.s.hand.cardsToDraw;
    score -= 10 * node.s.hand.getHandCount(Cards.DEBT);
    
    score += 1 * node.s.hand.totalCount(Hand.LOC_AUTOMATED); // automated cards count is good
    score += 0.2 * node.s.hand.getAliveCards(); // total number of cards is good
    
    score += 0.2 * node.s.agents[0].permanentArchitectureStudyCards;
    score += 0.2 * node.s.agents[0].permanentDailyRoutineCards;
    
    // check the number of give & throw (not good) & CI (good)
    Node current = node;
    while (current.parent != null) {
      if (current.action.isGive()) score -= 50; 
      if (current.action.isThrow()) score -= 10;
      
      current = current.parent;
    }

    // the closer to pos 0 the best
    score += (8 - node.s.agents[0].location) * 4;
    
    score += 15 * Math.min(4, node.s.hand.get(Hand.LOC_AUTOMATED, Cards.BONUS));
    
    Application app = State.applicationById[node.action.target];
    score += fitnessOfApp(node, app);
    

    
    if (Player.DEBUG_RELEASES) {
      System.err.println("Found a release for #"+node.action.target+" with debt "+debtOccured+" and score "+score);
      node.debugActionList();
    }
    
    return score;
  }


  private double fitnessOfApp(Node node, Application app) {
    double score = 0.0;
    
    Cards total;
    int totalDiff;

    // Maximise remaining cards vs my Automated cards
    totalDiff = 0;
    for (Application a : State.applications) {
      if (a == app) continue;
      
      for (int i=0;i<8;i++) {
        int automatedCount = node.s.hand.get(Hand.LOC_AUTOMATED, i);
        if (a.needed[i] > 0 && automatedCount > 0) {
          totalDiff += automatedCount;
        }
      }
    }
    score += 1 * totalDiff;
    
    
    // Maximise the release where i have less cards (most hard for me)
    total = node.s.myTotal();
    totalDiff = 0;
    for (int i=0;i<8;i++) {
      if (app.needed[i] > 0 && total.count[i] > 0) {
        totalDiff+= total.count[i];
      }
    }
    score -= 0.05 * totalDiff;

    // Maximise the release where opp have most cards (most easy for him)
    total = node.s.oppTotal();
    totalDiff = 0;
    for (int i=0;i<8;i++) {
      if (app.needed[i] > 0 && total.count[i] > 0) {
        totalDiff+= total.count[i];
      }
    }
    score += 0.1 * totalDiff;
    return score;
  }
}
