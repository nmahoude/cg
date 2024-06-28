package spring2022.ag;

import java.util.concurrent.ThreadLocalRandom;

import spring2022.Player;
import spring2022.State;
import spring2022.ai.TriAction;

public class AG {
  private static final ThreadLocalRandom random = ThreadLocalRandom.current();

  
  public static final int POPULATION_SIZE = 70;
  public static final int SURVIVOR_SIZE = 8;

  public static final int MAX_DEPTH = 10;
  public static int DEPTH = 10;
  
  public AGInformation information = new AGInformation();
  
  public final AGSolution[] solutions = new AGSolution[POPULATION_SIZE];
  {
    for (int i=0;i<POPULATION_SIZE;i++) {
      solutions[i] = new AGSolution();
    }
  }
  
  final AGSolution bestSolution = new AGSolution();
  private double bestScore;
  private final LightState originalState = new LightState();
  private final TriAction oppHeroesAction = new TriAction();

  
  private final LightState state = new LightState();

  final TriAction bestTriaction = new TriAction();


  private int bestPlyaAt;


  private int currentPly;

  public TriAction think(State originalRealState) {
    DEPTH = Math.min(MAX_DEPTH, 1 + 220 - Player.turn);
    
    System.err.println("Starting AG @ " + (System.currentTimeMillis() - Player.start) + "for depth "+DEPTH);
    information.update(originalRealState);
    originalState.createFrom(originalRealState);
    oppHeroesAction.reset();
    
    resetAG();
    

    // TODO implements other pecalculated populations
    System.err.println("TODO : implements other pecalculated populations");
    
    initPopulationFromLastTurn(originalState); // always first !
    // or
    //initFullRandomPopulation(originalState); // always first !
    
//    initRandomPopulation(originalState);
//    initSpeedStraight(originalState, State.HERO_MAX_MOVE / 2);
//    initSpeedStraight(originalState, State.HERO_MAX_MOVE / 4);
//    initSpeedStraightAndRandom(originalState, 800);
    // initWindAllDirection(originalState);
    
    while (true) {
      currentPly++;
      if ((currentPly & 8-1) == 0 && System.currentTimeMillis() - Player.start > 40) {
        break;
      }
      
      doOnePly(originalState);
    }
    
    System.err.println("AG "+currentPly+" plies in "+ (System.currentTimeMillis() - Player.start)+" ms");
    System.err.println("Best ply @ "+bestPlyaAt+" / " + currentPly+" with score : "+bestScore);
    
    bestTriaction.actions[0].updateFromAGValues(originalState.hero[0], bestSolution.c[0][0].angle, bestSolution.c[0][0].speed);
    bestTriaction.actions[1].updateFromAGValues(originalState.hero[1], bestSolution.c[0][1].angle, bestSolution.c[0][1].speed);
    
    return bestTriaction;
  }

  public void resetAG() {
    currentPly = 0;
    bestPlyaAt = 0;

    bestScore = Double.NEGATIVE_INFINITY;
    for (int i=0;i<POPULATION_SIZE;i++) {
      solutions[i].score = Double.NEGATIVE_INFINITY;
    }
    
    
  }

  public void initWindAllDirection(LightState originalState) {
    for (int i=SURVIVOR_SIZE;i<POPULATION_SIZE;i++) {
      solutions[i].random();
      if (random.nextDouble() > 0.5) {
        solutions[i].c[0][0].angle = random.nextInt(360);
        solutions[i].c[0][0].speed = -1;
      }
      if (random.nextDouble() > 0.5) {
        solutions[i].c[0][1].angle = random.nextInt(360);
        solutions[i].c[0][1].speed = -1;
      }
      
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }
    
    sortPopulationAndUpdateBest();
  }

  public void doOnePly(LightState originalState) {
    for (int i=SURVIVOR_SIZE;i<POPULATION_SIZE;i+=2) {
      AGSolution.merge(solutions[random.nextInt(SURVIVOR_SIZE)], solutions[random.nextInt(SURVIVOR_SIZE)], 
                       solutions[i], solutions[i+1] );
      
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);

      state.copyFrom(originalState);
      solutions[i+1].applyOn(state, oppHeroesAction);
    }

    sortPopulationAndUpdateBest();
  }

  /**
   * SURVIVOR = same as last turn, decaled by one turn
   * remaing = informedRandom
   * @param originalState
   */
  public void initPopulationFromLastTurn(LightState originalState) {
    for (int i=0;i<SURVIVOR_SIZE;i++) {
      solutions[i].decalFromLastTurn();
      
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }

    for (int i=SURVIVOR_SIZE;i<POPULATION_SIZE;i++) {
      solutions[i].informedRandom(information);
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }
    
    
    sortPopulationAndUpdateBest();

  }

  public void initSpeedStraight(LightState originalState, int speed) {
    int steps = (int)Math.sqrt(POPULATION_SIZE - SURVIVOR_SIZE);
    
    int i = SURVIVOR_SIZE;
    for (int a0=0;a0<steps;a0++) {
      solutions[i].dispatch(0, a0, steps, speed);

      for (int a1=0;a1<steps;a1++) {
        solutions[i].dispatch(1, a1, steps, speed);

        state.copyFrom(originalState);
        solutions[i].applyOn(state, oppHeroesAction);
        
        i++;
      }
    }
    
    sortPopulationAndUpdateBest();
  }

  public void initSpeedStraightAndRandom(LightState originalState, int speed) {
    int steps = (int)Math.sqrt(POPULATION_SIZE - SURVIVOR_SIZE);
    
    int i = SURVIVOR_SIZE;
    for (int a0=0;a0<steps;a0++) {

      for (int a1=0;a1<steps;a1++) {
        solutions[i].dispatchOne(0, a0, steps, speed);
        solutions[i].dispatchOne(1, a1, steps, speed);
        state.copyFrom(originalState);
        solutions[i].applyOn(state, oppHeroesAction);
        
        i++;
      }
    }
    
    sortPopulationAndUpdateBest();
  }

  public void initRandomPopulation(LightState originalState) {
    for (int i=SURVIVOR_SIZE;i<POPULATION_SIZE;i++) {
      solutions[i].informedRandom(information);
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }
    sortPopulationAndUpdateBest();
  }
  
  public void initFullRandomPopulation(LightState originalState) {
    for (int i=0;i<POPULATION_SIZE;i++) {
      solutions[i].informedRandom(information);
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }
    sortPopulationAndUpdateBest();
  }
  
  private void sortPopulationAndUpdateBest() {
    // java sort create object on the heap ==>  timeout ?
    //private static final Comparator<? super AGSolution> solComparator = (s1, s2) -> Double.compare(s2.score, s1.score);
    //Arrays.sort(solutions, solComparator);

    // should be enough (famous last words :) )
    for (int i=0;i<POPULATION_SIZE;i++) {
      int max = i;
      double maxScore = solutions[i].score;
      
      for (int j=i+1;j<POPULATION_SIZE;j++) {
        if (solutions[j].score > maxScore ) {
          maxScore = solutions[j].score;
          max = j;
        }
      }
      if (max == i) continue;
      
      // swap
      AGSolution tmp = solutions[i];
      solutions[i] = solutions[max];
      solutions[max] = tmp;
    }
    
    if (solutions[0].score > bestScore) {
      bestScore = solutions[0].score;
      bestSolution.copyFrom(solutions[0]);
      bestPlyaAt  = currentPly;
    }
  }

  
}
