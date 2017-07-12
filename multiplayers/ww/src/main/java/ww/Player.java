package ww;

import java.util.Comparator;
import java.util.Scanner;

import ww.paths.AccessibleCellsCalculator;
import ww.prediction.Oracle;
import ww.sim.Move;
import ww.sim.Simulation;
import ww.think.NodePOC;
import ww.think.Think;

public class Player {
  public static GameState state = new GameState();
  static Oracle oracle = null;
  static Simulation sim = new Simulation(state);
  static final boolean DEBUG_SCORING = false;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    
    state.readInit(in);
    
    int round = 0;
    // game loop
    while (true) {
      round++;
      
      state.readRound(in);
//      state.toTDD();
//      debugReachableCells();
//      debugPotentialActionsCount();
      
      if (round > 1) {
        oracle.guessFrom(state);
//        oracle.debug(state);
        oracle.apply(state);
      } else {
        // init the oracle
        oracle = new Oracle(state);
        //oracle.setDebug(true);
      }

      Move bestMove = new Move(null); //think(sim);
      // deepening
      int deepening = 1; // odd deepening only
      Move move = null;
      int maxDeepening = 1;
      
      int enemyInSight = state.getEnemyInSight();
      switch(enemyInSight) {
        case 0:  maxDeepening = 10; break;
        case 1:  maxDeepening = 10; break;
        case 2:  maxDeepening = 10; break;
      }
      do {
        move = new Think(state).think(deepening);
        
        if (move != null && move.agent != null) {
          move.copyTo(state, bestMove);
          System.err.println("AB @ "+deepening+" found bestMove :"+bestMove);
        } else {
          System.err.println("AB curoff at "+deepening);
        }
        if (deepening == 1) {
          // order moves
          state.legalActionDepth0NodeCache.sort(new Comparator<NodePOC>() {
            @Override
            public int compare(NodePOC o1, NodePOC o2) {
              return Double.compare(o2.score, o1.score);
            }
          });
//          for (Node node : state.legalActionDepth0NodeCache) {
//            System.err.println(""+node.move+" -> "+node.score);
//          }
        }
        deepening+=2;
      } while (move.agent != null && deepening < maxDeepening);
      
      long endTime = System.currentTimeMillis();
      int depth = deepening / 2;
      // System.err.println("Reflexion time : "+(endTime-state.startTime)+" depth reached : "+ depth);
      
      if (bestMove !=null && bestMove.agent != null) {
        // just before the output, we replay our best move for the divination
        oracle.updatePreviousState(state);
        sim.simulate(bestMove);
        // System.err.println("State after last simulation for prediction for move "+bestMove);
        //state.toTDD();
        oracle.updateSimulated(state, bestMove);
        if (DEBUG_SCORING ) {
          System.err.println("Evaluation for move : "+bestMove);
          AgentEvaluator.score(state);
        }
        
        System.out.println(bestMove.toPlayerOutput()); //+" "+depth+" in "+(endTime-GameState.startTime));
        
      } else {
        System.out.println("ACCEPT-DEFEAT GOOD FIGHT, GG");
      }
    }
  }

  private static void debugReachableCells() {
    for (int id=0;id<4;id++) {
      if (state.agents[id].inFogOfWar()) continue;
      System.err.println("Reachable for "+id+" "+AccessibleCellsCalculator.count(state, state.agents[id]));
    }
  }

  public static Move think(Simulation sim) {
    Move bestMove = new Move(null);
    double bestScore = Double.NEGATIVE_INFINITY;
    
    for (int i=0;i<2;i++) {
      Agent agent = state.agents[i];
      Move move = new Move(agent);

      for (Dir dir1 : Dir.getValues()) {
        for (Dir dir2 : Dir.getValues()) {
          move.dir1 = dir1;
          move.dir2 = dir2;
          sim.simulate(move);
          if (move.isValid()) {
            double score = AgentEvaluator.score(state);
            System.err.println(""+move.toPlayerOutput()+" = "+score);
            if (score > bestScore) {
              bestScore = score;
              move.copyTo(state,bestMove);
            }
            sim.undo(move);
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
