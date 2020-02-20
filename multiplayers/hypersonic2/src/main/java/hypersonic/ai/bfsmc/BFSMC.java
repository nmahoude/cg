package hypersonic.ai.bfsmc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ZobristLayer;
import hypersonic.ai.Score;
import hypersonic.entities.Bomberman;

/**
 * Do a BFS until there is no nodes available or no time, then do MC with the
 * remaining of time
 * 
 * @author nmahoude
 *
 */
public class BFSMC {
  static final int DEPTH = Player.DEPTH;
  static final int MAX_BRUTE_DEPTH = 6; // depth where we keep all nodes

  private static final long SURVAVIBILITY_TIMEOUT = 10;
  private static final long BFS_TIME_LIMIT = 40;
  private static final long TIME_LIMIT = 95;
  public static int collisions;
  public static int generatedNodes;
  static boolean dropEnnemyBombs = true;
  public static int nodesPerDepth[] = new int[Player.DEPTH];

  public static ZobristLayer zobrists[] = new ZobristLayer[DEPTH];
  static {
    for (int i=0;i<DEPTH;i++) {
      zobrists[i] = new ZobristLayer();
    }
  }
  int simu = 0;
  private String message = "TODO";
  private BFSMCNode root;

  static double bestScore;
  private BFSMCNode bestNode;
  private Move[] bestMoves = new Move[Player.DEPTH];
  static boolean checkForSurvivableSituation;
  private State model;

  public void reset() {
    BFSMCNodeCache.reset();
  }

  Move[] allMoves = new Move[Player.DEPTH];
  List<BFSMCNode> nodes = new ArrayList<>();
  public void think(State model) {
    this.model = model;
    simu = 0;
    dropEnnemyBombs = true;
    checkForSurvivableSituation = false;
    initSimulationFromModel(model);

    // 1st check for survability for xxx ms
    // TODO can be achieved by a simple BFS and good board state
    simu = 0;
    root.generateChildren(true);
    while(true) {
      simu++;
      // TODO we may start building the tree here ?
      double score = root.rollout(allMoves, 8);
      if (score > Score.DEAD_MALUS) {
        System.err.println("We survive!");
        System.err.println(Arrays.asList(allMoves));
        root.rolloutMoves(allMoves, 8);
        checkForSurvivableSituation = true;
        break;
      }
      if ((simu & 0b1111) == 0) {
        long duration = System.currentTimeMillis()-Player.startTime;
        if (duration > SURVAVIBILITY_TIMEOUT) {
          System.err.println("Break surva");
          break;
        }
      }
    }
    System.err.println("Stop survavibility ... @ " + (System.currentTimeMillis()-Player.startTime));
    if (!checkForSurvivableSituation) {
      System.err.println("we didn't find a survival path");
      dropEnnemyBombs = false; 
      initSimulationFromModel(model);
    }
    
    // build the tree (BFS + memory & timelimit)
    BFSMCNode current = null;
    initSimulationFromModel(model);
    simu = 0;
    while (!nodes.isEmpty()) {
      simu++;
      if ((simu & 0b1111) == 0) {
        long duration = System.currentTimeMillis() - Player.startTime;
        if (duration > BFS_TIME_LIMIT) {
          System.err.println("Break BFS at "+duration);
          break;
        }
      }
      current = nodes.remove(0);

      if (!current.generateChildren(dropEnnemyBombs)) {
        // no more spaces in cache, stop BFS
        System.err.println("Could not generate children, stop");
        break;
      }

      for (int i = 0; i < current.movesFE; i++) {
        BFSMCNode child = BFSMCNodeCache.nodes[current.firstChildIndex+i];
        nodesPerDepth[child.depth]++;
        if (child.state.players[Player.myId].isDead) continue;
        nodes.add(child);
      }
    }
    
    if(Player.DEBUG_AI) {
      if (nodes.isEmpty()) {
        System.err.println("No nodes to process");
      }
      System.err.println("Stop BFs at depth " + current.depth);
      System.err.println(" at"+ (System.currentTimeMillis() - Player.startTime));
      System.err.println(" nb simu : "+simu);
      for (int i=0;i<Player.DEPTH;i++) {
        System.err.println("Nodes @ depth "+i+" => "+nodesPerDepth[i]);
      }
    }
    
    // now, we rollout as many plies as possible
    while(true) {
      if (!checkTimeout(TIME_LIMIT)) {
        break;
      }
      simu++;
      
      double score = root.rollout(allMoves, Player.DEPTH);
      if (score > bestScore) {
        bestScore = score;
        // swap moves
        Move tmp[] = bestMoves;
        bestMoves = allMoves;
        allMoves = tmp;
//        if (Player.DEBUG_SCORE) {
//          root.rolloutMoves(allMoves, Player.DEPTH);
//        }
        if(Player.DEBUG_AI) {
          System.err.println("@simu :"+ simu);
          System.err.println("New best score : "+bestScore);
          System.err.println("best move : "+Arrays.asList(bestMoves));
          System.err.println("Drop bombs ? "+dropEnnemyBombs);
        }
      }
    }
    System.err.println("survive : " + dropEnnemyBombs);
  }

  private boolean checkTimeout(long limit) {
    if ((simu & 0b1111) == 0) {
      long duration = System.currentTimeMillis() - Player.startTime;
      if (duration > limit) {
        return false;
      }
    }
    return true;
  }

  private void initSimulationFromModel(State model) {
    bestScore = Double.NEGATIVE_INFINITY;
    BFSMCNodeCache.reset();
    resetZobrists();
    root = BFSMCNodeCache.pop(); // restart from fresh tree
    root.depth = 0;
    root.score = 0.0;
    root.state.copyFrom(model);
    root.state.hash = 0;
    nodes.clear();
    nodes.add(root);

    for (int i=0;i<Player.DEPTH;i++) {
      nodesPerDepth[i] = 0;
    }
  }
  
  private void resetZobrists() {
    collisions = 0;
    generatedNodes = 0;
    for (int d=0;d<DEPTH;d++) {
      zobrists[d].reset();
    }
  }
  
  public void ouput(State currentState) {
    final Move move = bestMoves[0];
    outputMove(currentState.players[Player.myId], move, message  );
  }
  private void outputMove(final Bomberman me, final Move move, String message) {
    int newX = me.position.x + move.dx;
    int newY = me.position.y + move.dy;
    if (move.dropBomb) {
      System.out.println("BOMB " + newX + " " + newY + " " + message);
    } else {
      System.out.println("MOVE " + newX + " " + newY + " " + message);
    }
  }

}
