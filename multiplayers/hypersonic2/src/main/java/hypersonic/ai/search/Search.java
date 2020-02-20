package hypersonic.ai.search;

import java.util.Arrays;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ZobristLayer;
import hypersonic.ai.Optimizer;
import hypersonic.entities.Bomberman;

public class Search {
  static final int DEPTH = Player.DEPTH;
  static final int MAX_BRUTE_DEPTH = 6; // depth where we keep all nodes

  private static final long TIME_LIMIT = 95;
  private static final long DROP_BOMB_TIME_LIMIT = 10;

  public static ZobristLayer zobrists[] = new ZobristLayer[DEPTH];
  static {
    for (int i=0;i<DEPTH;i++) {
      zobrists[i] = new ZobristLayer();
    }
  }
  private SNode root;
  private static Move[] nextTurnMoves = new Move[DEPTH];
  public static Move[] bestMoves = new Move[DEPTH];
  public static Move allMoves[] = new Move[DEPTH];
  private double bestScore;
  
  
  static boolean dropEnnemyBombs = true;
  static boolean checkForSurvivableSituation = false;
  public static int collisions;
  public static int generatedNodes;
  private String message = "";
  
  public void reset() {
    SNodeCache.reset();
  }
  public void think(State model) {
    simu = 0;
    this.bestScore = Double.NEGATIVE_INFINITY;
    
    initSimulationFromModel(model);
    
//    if (nextTurnMoves[0] != null) {
//      bestScore = root.recalculate(nextTurnMoves);
//    }
    
    
    dropEnnemyBombs = true;
    checkForSurvivableSituation = true;
    while (true) {
      simu++;
      if ((simu & 0b1111) == 0 ) {
        long duration = System.currentTimeMillis() - Player.startTime;
        if (duration > TIME_LIMIT) {
          break;
        } else if (checkForSurvivableSituation && duration > DROP_BOMB_TIME_LIMIT) {
          // we did'nt find any safe routes until now, 
          // so stop dropping ennemy bombs to try to find a safe route now
          checkForSurvivableSituation = false;
          dropEnnemyBombs = false;
          initSimulationFromModel(model);
        }
      }
      
      doOnePly();
    }

    if (Player.DEBUG_AI) {
      root.debug();
    }
    
    bestScore = Optimizer.optimizeBombs(bestMoves, bestScore, DEPTH, model, dropEnnemyBombs);
    bestScore = Optimizer.optimizeMoves(bestMoves, bestScore, DEPTH, model, dropEnnemyBombs);
    
    message  = bestMoves[0]+" "+(simu/1000)+"k" 
              //+ " / "+(System.currentTimeMillis()-Player.startTime)
              +(dropEnnemyBombs?"":"(☢☠)")
              +" (c:"+collisions+")";
    
    if (Player.DEBUG_AI) {
      System.err.println("Generated nodes : "+generatedNodes+", collisions : "+collisions);
      System.err.println("Simulations : " + simu);
      System.err.println("Still drop bombs? : "+dropEnnemyBombs);
      System.err.println("Best: "+(dropEnnemyBombs?"(☠)":"")+Arrays.asList(bestMoves)+ " => "+bestScore);
    }

  }
  private void initSimulationFromModel(State model) {
    SNodeCache.reset();
    resetZobrists();
    root = SNodeCache.pop(); // restart from fresh tree
    root.state.copyFrom(model);
    root.state.hash = 0;
  }

  private void resetZobrists() {
    collisions = 0;
    generatedNodes = 0;
    for (int d=0;d<DEPTH;d++) {
      zobrists[d].reset();
    }
  }

  SNode tmpNode = new SNode();
  double intermediateScores[] = new double[DEPTH];
  private int simu;
  private void doOnePly() {
    // reset state
    for (int i=0;i<DEPTH;i++) {
      allMoves[i] = null;
    }
    
    double score = root.choose(0, dropEnnemyBombs);
    
    if (score > bestScore) {
      bestScore = score;
      Move tmp[] = bestMoves;
      bestMoves = allMoves;
      allMoves = tmp;
      if(Player.DEBUG_AI) {
        System.err.println("@simu :"+ simu);
        System.err.println("IntScores : ");
        for (int i=0;i<DEPTH;i++) {
          System.err.print(intermediateScores[i]+" , ");
        }
        System.err.println("New best score : "+bestScore);
        System.err.println("best move : "+Arrays.asList(bestMoves));
        System.err.println("Status pos = ("+SNode.tmpState.players[Player.myId].position);
        System.err.println("Status dead = "+SNode.tmpState.players[Player.myId].isDead);
        System.err.println("Drop bombs ? "+dropEnnemyBombs);
      }
    }
  }
  
  public void ouput(State currentState) {
    Move move = bestMoves[0];
    //prepareForNextTurn();
    if (move == null) {
      move = Move.STAY;
    }
    outputMove(currentState.players[Player.myId], move, message);
  }
  
  private void prepareForNextTurn() {
    for (int i=0;i<DEPTH-1;i++) {
      nextTurnMoves [i] = bestMoves[i+1];
    }
    nextTurnMoves[DEPTH-1] = Move.STAY;
  }
  private void outputMove(final Bomberman me, final Move move, String message) {
    int newX = me.position.x + move.dx;
    int newY = me.position.y + move.dy;
    if (move.dropBomb) {
      System.out.println("BOMB "+newX+" "+newY+ " "+message);
    } else {
      System.out.println("MOVE "+newX+" "+newY+ " "+message);
    }
  }

  
}
