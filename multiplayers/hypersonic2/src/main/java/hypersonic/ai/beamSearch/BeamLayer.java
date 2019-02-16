package hypersonic.ai.beamSearch;

import java.util.Arrays;
import java.util.Comparator;

import hypersonic.Cache;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ai.Score;
import hypersonic.entities.Bomberman;
import hypersonic.simulation.MoveGenerator;
import hypersonic.simulation.Simulation;

public class BeamLayer {
  private static Simulation sim = new Simulation(null);
  private static MoveGenerator gen = new MoveGenerator(null);
  private static State tmpState = new State();
  
  static Move moves[] = new Move[10]; // TODO use a cache of possible moves ? 2^10 = 1024 only ?
  static int movesFE = 0;
  
  static final int WIDTH = 1000;
  BeamNode nodes[];
  int nodesFE = 0;
  int depth;

  
  public static final Comparator<BeamNode> sorter = new Comparator<BeamNode>() {
    @Override
    public int compare(BeamNode o1, BeamNode o2) {
      return Double.compare(o2.cumulativeScore, o1.cumulativeScore);
    }
  };

  public BeamLayer() {
    this(WIDTH);
  }
  public BeamLayer(int size) {
    nodes = new BeamNode[size];
    for (int i=0;i<size;i++) {
      nodes[i] = new BeamNode();
    }
  }

  public void buildFromParent(BeamLayer parentLayer) {
    nodesFE = 0;
    this.depth = parentLayer.depth+1;
    
    int width = Math.min(WIDTH, parentLayer.nodesFE);
    for (int i=0;i<width;i++) {
      BeamNode parent = parentLayer.nodes[i];
      if (parent.state.players[Player.myId].isDead) {
        continue; // we're dead, don't treat this parent node
      }
      gen.state = parent.state;
      movesFE = gen.getPossibleMoves(moves);
      
      for (int m=0;m<movesFE;m++) {
        BeamNode node = nodes[nodesFE++];
        Move move = moves[m];
        
        node.parent = parent;
        node.state.copyFrom(parent.state);
        node.moveToHere = move;
        if (depth == 0 && BeamSearch.dropEnnemyBombs) {
          dropEnnemyBombs(node.state);
        }
        sim.state = node.state;
        sim.simulate(move);
        node.cumulativeScore = parent.cumulativeScore + Score.score(node.state, depth, move);
        
        // for 10 turns in future to know bombs explosions
        tmpState.copyFrom(node.state);
        sim.state = tmpState;
        tmpState.turn+=10;
        sim.simulate(Move.STAY);
        tmpState.players[Player.myId].isDead = false;
        node.cumulativeScore += Score.score(tmpState, depth+10, Move.STAY);
      }
    }
    Arrays.sort(nodes, 0, nodesFE, sorter);
    System.err.println("End of layer :");
    System.err.println("nodes in this layer : " + nodesFE);
    if (nodesFE != 0) {
      System.err.println("Best score : "+nodes[0].cumulativeScore+" worse  : "+nodes[nodesFE-1].cumulativeScore);
    } else {
      System.err.println("All nodes are death");
    }
  }
  
  private static void dropEnnemyBombs(State state) {
    // for all players different than me and who can, drop a bomb at first one
    for (int i=0;i<4;i++) {
      if (i == Player.myId) continue;
      Bomberman b = state.players[i];
      if (b.isDead || b.bombsLeft == 0) continue;
      state.addBomb(Cache.popBomb(i, b.position, 8, b.currentRange));
    }
  }
}
