package calm.ai.astarai;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import calm.Player;
import calm.actions.Action;
import calm.state.Agent;
import calm.state.State;

public class AStarAI {

  public void think(State currentState) {
    AStarCache.reset();
    
    PriorityQueue<AStarNode> openList= new PriorityQueue<>(new Comparator<AStarNode>() {
      @Override
      public int compare(AStarNode o1, AStarNode o2) {
        return Integer.compare(o1.totalCost, o2.totalCost);
      }
    });
    
    AStarNode root = AStarCache.pop();
    root.state.copyFrom(currentState);
    root.provenCost = currentState.turn; // number of turns
    root.totalCost = 0;
    root.parent = null;
    root.action = null;
    root.player1ToPlay = true;
    
    int bestCost = 210;
    AStarNode best = null;
    int bestDepth = 0;
    
    openList.add(root);
    int simulation = 0;
    while (!openList.isEmpty()) {
      AStarNode current= openList.poll();
      if (current.provenCost > bestCost) {
        break; // the best in the openList is worst than our solution, skip all
      }
      if ((simulation & 0b1111) == 0) {
        // check for time limits
        long end= System.currentTimeMillis();
        if (end - Player.start > Player.TIME_LIMIT) {
          break;
        }
      }
      if (Player.DEBUG_PLANNER) {
        System.err.println("Opening : " +current.toString());
      }
      
      List<Action> allActions;
      Agent mainPlayer;
      if (current.player1ToPlay) {
        allActions = current.state.getPossibleActionsForPlayer1();
        mainPlayer = current.state.agent1;
      } else {
        mainPlayer = current.state.agent2;
        allActions = current.state.getPossibleActionsForPlayer2();
      }
      for (Action action : allActions) {
        if (action.prerequisites(current.state, mainPlayer)) {
          AStarNode child = AStarCache.pop();
          child.parent = current;
          child.state.copyFrom(current.state);
          Agent currentAgent;
          if (current.player1ToPlay) {
            currentAgent = child.state.agent1;
          } else {
            currentAgent = child.state.agent2;
          }
          
          child.action = action;
          child.player1ToPlay = !current.player1ToPlay;
          action.applyEffect(child.state, currentAgent);
          simulation++;
          child.provenCost = current.provenCost+1;
          child.totalCost = child.provenCost + Player.random.nextInt(3); //estimateTurnToGoal(child.state); // TODO real cost here
          
          if (child.provenCost > bestDepth) {
            bestDepth = child.provenCost;
          }
          if (child.totalCost < bestCost) {
            bestCost = child.totalCost;
            best = child;
            System.err.println("Best total cost for now : "+ child.totalCost +"!  " + child.toString());
          } 
          
          if (child.provenCost < bestCost && child.provenCost < 200) {
            openList.add(child);
          }
        }
      }
    }
    
    System.err.println("Best depth " + (bestDepth - currentState.turn));
    System.err.println("End of thinking at " + (System.currentTimeMillis()-Player.start));
    System.err.println("Nb of Simulations : "+simulation);
    if (best != null) {
      System.err.println("Best score is " + best.provenCost + " / "+ best.totalCost);
      System.err.println("Best node is "+best.toString());
    }
  }
  
  private int estimateTurnToGoal(State state) {
    return 10;
  }

  private boolean isTerminated(State state) {
    // TODO Auto-generated method stub
    return false;
  }

  public void output(State currentState) {
    System.out.println("WAIT");
  }
}
