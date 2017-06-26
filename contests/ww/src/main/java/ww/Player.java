package ww;

import java.util.Scanner;

import ww.sim.Simulation;

public class Player {
  static GameState state = new GameState();
  static Simulation simulation = new Simulation();
  static Node backupState = new Node();
  
  static boolean locked[] = new boolean[4];
  static int x[] = new int[4];
  static int y[] = new int[4];

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
        System.err.println("Reachable for "+id+" "+state.getReachableCells(id));
      }

      /* Debug possible actions calculus*/
//      int totalAction = state.agents[0].getPossibleActions(state) +state.agents[1].getPossibleActions(state);
//      if (totalAction != state.legalActions) {
//        System.err.println("calculated actions : "+totalAction+" vs "+state.legalActions);
//        throw new RuntimeException("Difference in totalLegalAction on init round");
//      }

      
      // TODO get a good assessment
      if (round > 1) {
        assessOppPosition();
      }

      
      
      Node node = new Node();
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

  /*
    compare old state with new state and try to know new opp position
    if found with xxx% sure, modify current gamestate to counter *fog of war*
   */
  private static void assessOppPosition() {
    // restore position of locked agents
    for (int id=2;id<4;id++) {
      if (locked[id]) {
        //TODO remove debug !
        if (!state.agents[id].inFogOfWar() && state.agents[id].x != x[id]) throw new RuntimeException("LOCKED NOT GOOD !");
        if (!state.agents[id].inFogOfWar() && state.agents[id].y != y[id]) throw new RuntimeException("LOCKED NOT GOOD !");
        
        // restore position, we know agent wont have move
        state.agents[id].x = x[id];
        state.agents[id].y = y[id];
        System.err.println("Restoring values for "+id);
      } else if (!state.agents[id].inFogOfWar()  && state.getReachableCells(id) == 0) {
        locked[id] = true;
        x[id] = state.agents[id].x;
        y[id] = state.agents[id].y;
      }
    }    
  }
}