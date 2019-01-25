package hypersonic.ai.search;

import java.util.Arrays;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ai.BombOptimizer;
import hypersonic.entities.Bomberman;

public class Search {
  public static final int DEPTH = 20;
  private static final int MAX_BRUTE_DEPTH = 5; // depth where we keep all nodes

  private static final long TIME_LIMIT = 50;
  private static final long DROP_BOMB_TIME_LIMIT = 10;

  
  private SNode root;
  public static Move[] bestMoves = new Move[DEPTH];
  private static Move allMoves[] = new Move[DEPTH];
  private double bestScore;
  
  
  private boolean dropEnnemyBombs = true;
  private boolean survivableSituation = false;
  private String message = "";
  
  public void think(State model) {
    simu = 0;
    this.bestScore = Double.NEGATIVE_INFINITY;
    
    SNodeCache.reset();
    
    root = SNodeCache.pop();
    root.state.copyFrom(model);
    
    dropEnnemyBombs = true;
    survivableSituation = false;
    while (true) {
      simu++;
      if ((simu & 0b11111) == 0 ) {
        long duration = System.currentTimeMillis() - Player.startTime;
        if (duration > TIME_LIMIT) {
          break;
        } else if (duration > DROP_BOMB_TIME_LIMIT) {
          if (!survivableSituation) {
            // we did'nt find any safe routes until now, 
            // so stop dropping ennemy bombs to try to find a safe route now
            dropEnnemyBombs = false;
            SNodeCache.reset();
            root = SNodeCache.pop(); // restart from fresh tree
            root.state.copyFrom(model);
          }
        }
      }
      
      doOnePly();
    }
    
    message  = ""+simu + " / "+(System.currentTimeMillis()-Player.startTime)+ " db:"+dropEnnemyBombs;
    
    BombOptimizer.optimizeBombs(bestMoves, bestScore, DEPTH, model, dropEnnemyBombs);
    if (Player.DEBUG_AI) {
      System.err.println("Simulations : " + simu);
      System.err.println("Still drop bombs? : "+dropEnnemyBombs);
    }
    System.err.println("Best (db:"+dropEnnemyBombs+"): "+Arrays.asList(bestMoves)+ " => "+bestScore);

  }

  SNode tmpNode = new SNode();
  double intermediateScores[] = new double[DEPTH];
  private int simu;
  private void doOnePly() {
    // reset state
    int depth = 0;
    double score = 0.0;
    
    SNode node = root;
    while (depth < DEPTH) {
      if (depth < MAX_BRUTE_DEPTH) {
        node = node.chooseChild(depth, dropEnnemyBombs);
      } else if (depth == MAX_BRUTE_DEPTH){
        node = node.expand1stMC(depth, dropEnnemyBombs);
      } else {
        node.expandMC(depth, dropEnnemyBombs);
      }

      allMoves[depth] = node.moveToHere;
      intermediateScores[depth] = node.score;
      score += node.score;
      if (node.state.players[Player.myId].isDead) {
        break; // stop here
      }
      depth++;
    }
    
    if (score > bestScore) {
      bestScore = score;
      Move tmp[] = bestMoves;
      bestMoves = allMoves;
      allMoves = tmp;
      survivableSituation |= !node.state.players[Player.myId].isDead;
      if(Player.DEBUG_AI) {
        System.err.println("@simu :"+ simu);
        System.err.println("IntScores : ");
        for (int i=0;i<DEPTH;i++) {
          System.err.print(intermediateScores[i]+" , ");
        }
        System.err.println("New best score : "+bestScore);
        System.err.println("best move : "+Arrays.asList(bestMoves));
        System.err.println("Status pos = "+node.state.players[Player.myId].position);
        System.err.println("Status dead = "+node.state.players[Player.myId].isDead);
        System.err.println("Drop bombs ? "+dropEnnemyBombs);
      }
    }
  }
  
  public void ouput(State currentState) {
    final Move move = bestMoves[0];
    outputMove(currentState.players[Player.myId], move, message);
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
