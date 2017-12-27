package meanmax.ai.ag;

import java.util.Arrays;

import meanmax.Game;
import meanmax.Player;
import meanmax.ai.eval.EvalV2;
import trigo.Position;
import trigo.Speed;

public class AG {
  public static final int POPULATION = 150;
  private static final int SURVIVOR_POP_SIZE = 30;
  private static final int POPULATION_RANDOM_START = 110;
  public EvalV2 eval = new EvalV2();

  public int MAX_TIME = 30_000_000;
      
  public AGSolution bestSolution = new AGSolution(eval); // not used directly, will be swapped in place
  public AGSolution solutions[] = new AGSolution[POPULATION];
  {
    for (int i=0;i<solutions.length;i++) {
      solutions[i] = new AGSolution(eval);
    }
  }

  public void think(Player player) {
    setup();
    while (System.nanoTime() - Game.start < MAX_TIME) {
      nextGeneration();
    }
  }

  public void setup() {
    bestSolution.energy = Double.NEGATIVE_INFINITY;
    eval.chooseEnemies();

    initGeneration0();
    bestSolution.copyFrom(solutions[0]);
  }

  public void nextGeneration() {
    int pop=SURVIVOR_POP_SIZE;
    AGSolution localBest = bestSolution;
    while(pop < POPULATION_RANDOM_START && System.nanoTime() - Game.start < MAX_TIME) {
      int firstIndex = findIndex(solutions, SURVIVOR_POP_SIZE, -1);
      int secondIndex = findIndex(solutions, SURVIVOR_POP_SIZE, firstIndex);
      AGSolution solution1 = solutions[pop++];
      AGSolution solution2 = solutions[pop++];
      AGSolution.crossOverAndMutate(solution1, solution2, solutions[firstIndex], solutions[secondIndex]);
      
      // AGSolution.crossOverAndMutateAlternative(solution1, solution2, solutions[firstIndex], solutions[secondIndex]);
      solution1.evaluate();
      if (solution1.energy > localBest.energy) localBest = solution1;
      solution2.evaluate();
      if (solution2.energy > localBest.energy) localBest = solution2;
    }
    if (localBest.energy > bestSolution.energy) {
      bestSolution.copyFrom(localBest);
    }
    while(pop < POPULATION && System.nanoTime() - Game.start < MAX_TIME) {
      AGSolution solution1 = solutions[pop++];
      solution1.random();
      solution1.evaluate();
      if (solution1.energy > localBest.energy) localBest = solution1;
    }
    if (localBest.energy > bestSolution.energy) {
      bestSolution.copyFrom(localBest);
    }
    Arrays.sort(solutions);
  }

  private static int findIndex(AGSolution[] pool, int max, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = Game.random.nextInt(max);
    } while (aIndex == otherThanIndex);

    do {
      bIndex = Game.random.nextInt(max);
    } while (bIndex == aIndex || bIndex == otherThanIndex);

    return pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
  }

  private void initGeneration0() {
    int start = 0;
    if (Game.turn > 1) {
      start = 1;
      AGSolution solution = solutions[0]; // last best (that we output last turn)
      solution.shiftAndCopy(solution);
      solution.evaluate();

      for (int i=1;i<start;i++) {
        solution = solutions[i]; // last best (that we output last turn)
        solution.shiftAndCopy(solutions[0]);
        solution.evaluate();
      }
    }
    for (int i=start;i<POPULATION;i++) {
      AGSolution solution = solutions[i];
      solution.random();
      solution.evaluate();
    }

    Arrays.sort(solutions);
  }

  public void output() {
    //System.err.println("ai "+simulations+" simulations in "+ (end - Game.start) + " ms");
    if (Game.DEBUG_AI) {
      bestSolution.debug();
    }
    bestSolution.output();
  }

  static Position exP[] = new Position[9];
  static Speed    exS[] = new Speed[9];
  static {
    for (int i=0;i<9;i++) {
      exP[i] = new Position();
      exS[i] = new Speed();
    }
  }

  String debugOutput[] = new String[9];
  int exRage[] = new int[3];
  int exRageDiff[] = new int[3];
  AGSolution backupSolution = new AGSolution(eval);
  int oiledGood = 0, oiledBad = 0;
  
  public void saveExpected() {
    backupSolution.copyFrom(bestSolution);
    backupSolution.reevaluate();
    
    for (int i=0;i<3;i++) {
      exRage[i] = (int)Game.players[i].rage;
    }
    for (int i=0;i<9;i++) {
      exP[i].x = Game.entities[i].position.x;
      exP[i].y = Game.entities[i].position.y;
      exS[i].vx = Game.entities[i].speed.vx;
      exS[i].vy = Game.entities[i].speed.vy;
      debugOutput[i] = bestSolution.actions[0][i].toString();
    }    
  }
  
  public void compareExpected() {
    boolean diff = false;
    for (int i=0;i<3;i++) {
      boolean localdiff = false;
      if (Math.abs(exRage[i] - (int)Game.players[i].rage) >= 30) {
        exRageDiff[i]++;
        localdiff = true;
        diff = true;
      }
      if (i != 0) { // not for player 0
        if ( backupSolution.actions[0][3*i+2].thrust < 0) {
          if (!localdiff) {
            System.err.println("GOT OILED IT RIGHT");
            oiledGood++;
          } else {
            oiledBad++;
          }
        }
      }
    }
    System.err.println("oiled good/bad : "+oiledGood + " / "+ oiledBad);
    if (diff) {
      System.err.println("Diff in rage");
      System.err.println(String.format("expected: %3d %3d %3d", exRage[0], exRage[1], exRage[2]));
      System.err.println(String.format("got     : %3d %3d %3d", (int)Game.players[0].rage,(int)Game.players[1].rage,(int)Game.players[2].rage));
    }
  }
}
