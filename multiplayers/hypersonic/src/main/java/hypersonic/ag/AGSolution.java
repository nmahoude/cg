package hypersonic.ag;

import java.util.List;

import hypersonic.Move;
import hypersonic.Simulation;
import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import random.FastRand;

public class AGSolution {
  private static FastRand random = new FastRand(43);

  public static int MOVE_COUNT = 12;
  private static double[] pow;
  static {
    pow = new double[MOVE_COUNT];
    for (int i=0;i<MOVE_COUNT;i++) {
      pow[i] = Math.pow(0.9, i);
    }
  }
  
  
  public Move[] keys = new Move[MOVE_COUNT];
  public double energy = 0;
  private int mutateNextGenAt = 0;
  
  public void reset() {
    energy = 0;
    mutateNextGenAt = 0;
  }
  
  public void play(Simulation simulation) {
//    printMoves();
    energy = 0;
    
    for (int i=0;i<MOVE_COUNT;i++) {
      if (i >= mutateNextGenAt) {
        List<Move> possibleMoves = simulation.getPossibleMoves();
        keys[i] = possibleMoves.get(random.fastRandInt(possibleMoves.size()));
      } else {
        // keep old move
      }
      simulation.simulate(keys[i]);
      if (simulation.board.me.isDead) {
        energy = -1000.0*(MOVE_COUNT-i);
        return; // dead
      } else {
        energy += pow [i]*simulation.getScoreHeuristic();
      }
      if (i==0 && keys[i].ordinal() > 3) {
        putPotentialAdverserialBombs(simulation);
      }
    }
  }

  private void putPotentialAdverserialBombs(Simulation simulation) {
    for (Bomberman b : simulation.board.players) {
      if (b == simulation.board.me) continue;
      if (!b.isDead 
          && b.position.manhattanDistance(simulation.board.me.position) < 3
          && b.bombsLeft > 0
          ) {
        simulation.board.addBomb(Bomb.create(simulation.board, b.owner, b.position, 8, b.currentRange));
      }
    }    
  }

  private void printMoves() {
    System.err.println("Moves :");
    for (int i=0;i<MOVE_COUNT;i++) {
      System.err.print(keys[i]);
    }
    System.err.println("");
  }

  public void copyFrom(AGSolution agSolution) {
    for (int i=0;i<MOVE_COUNT;i++) {
      this.keys[i] = agSolution.keys[i];
    }
  }

  public static void mutate(AGSolution child, AGSolution parent) {
    child.mutateNextGenAt  = random.fastRandInt(MOVE_COUNT);
    child.energy = 0;
    for (int i=0;i<MOVE_COUNT;i++) {
      child.keys[i] = parent.keys[i];
    }
  }
  
  
}
