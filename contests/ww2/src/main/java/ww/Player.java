package ww;

import java.util.Scanner;

import ww.sim.Move;
import ww.sim.Simulation;

public class Player {
  static GameState state = new GameState();
  static Divination divination = null;
  static Simulation sim = new Simulation();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    
    state.readInit(in);
    divination = new Divination(state);
    
    int round = 0;
    // game loop
    while (true) {
      round++;
      
      state.readRound(in);
//      state.toTDD();

//      debugReachableCells();
//      debugPotentialActionsCount();
      
      if (round > 1) {
        divination.guessFrom(state);
        divination.debug();
        divination.apply(state);
      }

      Move bestMove = think(sim);
      
      long endTime = System.currentTimeMillis();
      System.err.println("Reflexion time : "+(endTime-state.startTime));
      
      if (bestMove.agent != null) {
        // just before the output, we replay our best move for the divination
        divination.updateInitialState(state);
        new Simulation().simulate(bestMove, true);
        divination.updatePrediction(state);
        
        System.out.println(bestMove.toPlayerOutput());
      } else {
        System.out.println("ACCEPT-DEFEAT GOOD FIGHT, WELL DONE");
      }
    }
  }

  private static void debugReachableCells() {
    for (int id=0;id<4;id++) {
      if (state.agents[id].inFogOfWar()) continue;
      System.err.println("Reachable for "+id+" "+AccessibleCellsCalculator.count(state, state.agents[id]));
    }
  }

  private static Move think(Simulation sim) {
    Move bestMove = new Move(null);
    double bestScore = Double.NEGATIVE_INFINITY;
    
    for (int i=0;i<2;i++) {
      Agent agent = state.agents[i];
      Move move = new Move(agent);

      for (Dir dir1 : Dir.values()) {
        for (Dir dir2 : Dir.values()) {
          move.dir1 = dir1;
          move.dir2 = dir2;
          sim.simulate(move, true);
          if (move.isValid()) {
            double score = AgentEvaluator.score(state);
//              System.err.println(""+move.toPlayerOutput()+" = "+score);
            if (score > bestScore) {
              bestScore = score;
              move.copyTo(bestMove);
            }
            state.restore();
          }
        }
      }
    }
    return bestMove;
  }

  private static void debugPotentialActionsCount() {
    /* Debug possible actions calculus*/
    int actionFor0 = Simulation.getPossibleActionsCount(state, state.agents[0]);
    int actionFor1 = Simulation.getPossibleActionsCount(state, state.agents[1]);
    int totalAction =  actionFor0 + actionFor1;
    if (totalAction != state.legalActions) {
      System.err.println("calculated actions : "+totalAction+" vs "+state.legalActions);
      System.err.println("for 0 :"+actionFor0 +" , for 1 : "+actionFor1);
      throw new RuntimeException("Difference in totalLegalAction on init round");
    }
  }

}
