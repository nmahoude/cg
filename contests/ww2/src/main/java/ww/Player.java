package ww;

import java.util.Scanner;

import ww.sim.Move;
import ww.sim.Simulation;
import ww.think.Think;

public class Player {
  public static GameState state = new GameState();
  static Divination divination = null;
  static Simulation sim = new Simulation();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    
    state.readInit(in);
    divination = new Divination(state);
    //divination.setDebug(true);
    
    int round = 0;
    // game loop
    while (true) {
      round++;
      
      state.readRound(in);
      //state.toTDD();

//      debugReachableCells();
//      debugPotentialActionsCount();
      
      if (round > 1) {
        divination.guessFrom(state);
        //divination.debug();
        divination.apply(state);
      }

      Move bestMove = new Move(null); //think(sim);
      // deepening
      int deepening = 1; // odd deepening only
      Move move = null;
      do {
        move = new Think(state).think(deepening);
        if (System.currentTimeMillis() > GameState.startTime + GameState.MAX_TIME) {
          move = null;
        }
        if (move != null && move.agent != null) {
          move.copyTo(bestMove);
          System.err.println("AB @ "+deepening+" found bestMove :"+bestMove);
        }
        deepening+=2;
      } while (move != null && deepening < 20);
      
      long endTime = System.currentTimeMillis();
      int depth = deepening / 2;
      System.err.println("Reflexion time : "+(endTime-state.startTime)+" depth reached : "+ depth);
      
      if (bestMove !=null && bestMove.agent != null) {
        // just before the output, we replay our best move for the divination
        divination.updateInitialState(state);
        sim.simulate(bestMove, true);
        // System.err.println("State after last simulation for prediction for move "+bestMove);
        //state.toTDD();
        divination.updatePrediction(state);
        
        System.out.println(bestMove.toPlayerOutput()+" "+depth+" in "+(endTime-GameState.startTime));
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
          sim.simulate(move, true);
          if (move.isValid()) {
            double score = AgentEvaluator.score(state);
            System.err.println(""+move.toPlayerOutput()+" = "+score);
            if (score > bestScore) {
              bestScore = score;
              move.copyTo(bestMove);
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
