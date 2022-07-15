package csb.ai;

import java.util.concurrent.ThreadLocalRandom;

import csb.Player;
import csb.State;

public class AG {
	private static final ThreadLocalRandom random = ThreadLocalRandom.current();
	
  public static final int POPULATION_SIZE = 30;
  public static final int SURVIVOR_SIZE = 4;
  
  public AGSolution solutions[] = new AGSolution[POPULATION_SIZE];
  
  final AGSolution bestSolution = new AGSolution();
  private int currentPly;
  private double bestScore;
	private int bestPlyaAt;
  
  public AG() {
    for (int i=0;i<POPULATION_SIZE;i++) {
      solutions[i] = new AGSolution();
    }
  }
  

	public void think(State state) {
		resetAG();
		int iter = 0;
		int counter = 0;
		
		
		initFullRandomPopulation(state);
		
		while(true) {
      iter++;
      counter++;
      if (counter >= 4) {
        if (System.currentTimeMillis() - Player.start > 70) {
          break;
        } else {
          counter = 0;
        }
      }
      
  		doOnePly(state);
    }

		
		System.err.println("iterations :"+iter);
		bestSolution.output(state);
	}

  
	public void doOnePly(State state) {
		currentPly++;
		
    for (int i=SURVIVOR_SIZE;i<POPULATION_SIZE;i+=2) {
      AGSolution.merge(solutions[random.nextInt(SURVIVOR_SIZE)], 
      								 solutions[random.nextInt(SURVIVOR_SIZE)], 
                       solutions[i], 
                       solutions[i+1] );
      
      
      
      solutions[i].apply(state);

      solutions[i+1].apply(state);
    }

    sortPopulationAndUpdateBest();
  }



  public void resetAG() {
  	currentPly = 0;
    bestPlyaAt = 0;

    bestScore = Double.NEGATIVE_INFINITY;
    for (int i=0;i<POPULATION_SIZE;i++) {
      solutions[i].score = Double.NEGATIVE_INFINITY;
    }    
  }


  

  public void initFullRandomPopulation(State state) {
  	int start = 0;
  	for (int ev = -3;ev<=-3;ev++) {
			cp3v(start++, state, ev, 0.2);
			cp3v(start++, state, ev, 0.8);
			cp3v(start++, state, ev, 1.0);
  	}
  	
//  	cp3v(start++, state, -3, 1.0);
//  	while (start < POPULATION_SIZE) {
//  		cp3v(start++, state, 0, 0.0);
//  	}
    for (int i=start;i<POPULATION_SIZE;i++) {
      solutions[i].fullRandom();
      solutions[i].apply(state);
    }

    sortPopulationAndUpdateBest();
  }

  
  private void cp3v(int index, State state, int ev, double thrust) {
  	
    solutions[index].directBot(state, ev, thrust);
    solutions[index].apply(state);
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
