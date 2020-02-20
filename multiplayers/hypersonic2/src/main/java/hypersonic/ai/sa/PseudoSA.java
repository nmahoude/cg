package hypersonic.ai.sa;

import java.util.Arrays;
import java.util.Comparator;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.entities.Bomberman;
import hypersonic.simulation.MoveGenerator;
import hypersonic.simulation.Simulation;

/**
 * AI is a pseudo simulated annealing
 * reducing the number of random pieces with time
 * @author nmahoude
 *
 */
public class PseudoSA {
  private static final int HALF_POP = 50;
  private static final int FULL_POP = 2 * HALF_POP;

  static final int DEPTH = 20;
  static boolean survivableSituation = false;
  static public State state = new State();
  static Simulation simulator = new Simulation(state);
  static MoveGenerator gen = new MoveGenerator(state);

  SANode nodes[] = new SANode[FULL_POP];
  String message = "";
  
  public PseudoSA() {
    for (int i=0;i<FULL_POP;i++) {
      nodes[i] = new SANode();
    }
  }

  public void think(State model) {
    PseudoSA.state.copyFrom(model);
    survivableSituation = false;
    
    init1stPopulation(model);
    
    double bestScore = Double.NEGATIVE_INFINITY;
    boolean dropEnnemyBombs = false;
    int invT = 0;
    int simu = 0;
    while(true) {
      simu++;
      if ((simu & 0b11) == 0 ) { // tous les X * FULL_POP
        long duration = System.currentTimeMillis() - Player.startTime;
        if (duration > 95) {
          break;
        } else if (duration > 20) {
          if (!survivableSituation) {
            // we did'nt find any safe routes until now, 
            // so stop dropping ennemy bombs to try to find a safe route now
            invT = 0;
            dropEnnemyBombs = false;
          }
        }
      }
      
      for (int i=HALF_POP;i<FULL_POP;i++) {
        PseudoSA.state.copyFrom(model);
        nodes[i].copyFrom(nodes[Player.rand.nextInt(HALF_POP)]);
        nodes[i].build(dropEnnemyBombs, PseudoSA.state, invT);
      }
      sortPopulation();

//      invT++;
//      if (invT > DEPTH - 5) {
//        invT = 0;
//      }
    }

    message = ""+(simu/1000)+"k";
  }

  public void ouput(State currentState) {
    final Move move = nodes[0].moves[0];
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
  
  private void init1stPopulation(State model) {
    for (int i=0;i<FULL_POP;i++) {
      PseudoSA.state.copyFrom(model);
      nodes[i].build(true, PseudoSA.state, 0);
    }    
    
    sortPopulation();
  }

  private void sortPopulation() {
    Arrays.sort(nodes, new Comparator<SANode>() {

      @Override
      public int compare(SANode o1, SANode o2) {
        return Double.compare(o2.accumulatedScore, o1.accumulatedScore);
      }
    });
  }

  public void reset() {
    // no cache to clean ?
  }

}
