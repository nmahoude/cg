package fall2022.sim;

import fall2022.O;
import fall2022.Pos;
import fall2022.State;

public class Sim {
	
  public static void oneTurn(State state) {
  	int[] minedByMe = new int[Pos.MAX_OFFSET];
  	int[] minedByOpp = new int[Pos.MAX_OFFSET];
  	
    for( Pos current : Pos.allMapPositions) {
      if (state.rec[current.o] == 0) continue;
      

      if (state.o[current.o] == O.ME) {
      	for (Pos n : current.meAndNeighbors4dirs) {
      		minedByMe[n.o]++;
      	}
      } else {
      	for (Pos n : current.meAndNeighbors4dirs) {
      		minedByOpp[n.o]++;
      	}
      }
    }
    
    // recycle & clean recyclers
    for( Pos current : Pos.allMapPositions) {
      if (state.s[current.o] > 0) {
      	if (minedByMe[current.o] > 0) state.myMatter++;
      	if (minedByOpp[current.o] > 0) state.oppMatter++;
      	if (minedByMe[current.o] > 0 || minedByOpp[current.o] > 0) state.s[current.o]--;
      } 
      
      if (state.s[current.o] == 0) {
    		state.rec[current.o] = 0;
    		state.u[current.o] = 0;
    		state.mu[current.o] = 0;
      }
      
    }
    
    // add 10 matters
    state.myMatter+=10;
    state.oppMatter+=10;
  }

	public static void tenTurn(State state) {
		for (int i=0;i<10;i++) {
			oneTurn(state);
		}
	}
    
}
