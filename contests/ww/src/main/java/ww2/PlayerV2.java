package ww2;

import java.util.Scanner;

import ww.NodeV2;

public class PlayerV2 {
  static GameState state = new GameState();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    
    
    state.readInit(in);

    int round = 0;
    // game loop
    while (true) {
      round++;
      
      state.readRound(in);
      state.toTDD();
      for (int id=0;id<4;id++) {
        if (state.agents[id].inFogOfWar()) continue;
        System.err.println("Reachable for "+id+" "+new ReachableCellCalculator().getReachableCells(state, id));
      }

      /* Debug possible actions calculus*/
//      int totalAction = state.agents[0].getPossibleActions(state) +state.agents[1].getPossibleActions(state);
//      if (totalAction != state.legalActions) {
//        System.err.println("calculated actions : "+totalAction+" vs "+state.legalActions);
//        throw new RuntimeException("Difference in totalLegalAction on init round");
//      }

      
      // TODO get a good assessment
      if (round > 1) {
        //assessOppPosition();
      }

      
      
      NodeV2 node = new NodeV2();
      node.calculateChilds(0, state);
      

//      state.restore();
//      simulation.simulate(node.bestAction, state); // simulate the state after our action
//      backupState.save(state);

      long endTime = System.currentTimeMillis();
      System.err.println("Reflexion time : "+(endTime-state.startTime));
      
      if (node.bestAction != null) {
        System.out.println(node.bestAction.toPlayerOutput());
      } else {
        System.out.println("ACCEPT-DEFEAT GOOD FIGHT, WELL DONE");
      }
    }
  }
  
  
}
