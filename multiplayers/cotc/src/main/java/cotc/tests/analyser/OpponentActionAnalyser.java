package cotc.tests.analyser;

import cotc.GameState;
import cotc.Team;
import cotc.ai.ag.AG;
import cotc.ai.ag.AGAction;
import cotc.ai.ag.AGSolution;
import cotc.tests.analyser.reader.BattlesReader;
import cotc.tests.analyser.reader.Game;
import cotc.utils.Coord;

public class OpponentActionAnalyser {

  private static final int NUM_PLAYERS = 2;
  private static Game game;
  
  public static void main(String[] args) {
    // init cache
    Coord.get(0, 0);
    
    BattlesReader br = new BattlesReader();
    game = br.game;
    br.readOneBattle("230752328");
    
    analyse();
  }

  private static void analyse() {
    int compareId = 0;
    for (int i=0;i<game.frames.size()-1;i++) {
      System.err.println("Analyse opponent, turn "+(2*i));
      GameState state = game.frames.get(i).frameToState();
      GameState nextState = game.frames.get(i+1).frameToState();
      
      if (compareId == 1) {
        swapTeams(state);
      }
      
      AG ag = new AG();
      ag.setState(state);
      AGSolution bestSol = (AGSolution)ag.evolve(System.currentTimeMillis() + 5000);
 
      for (int s=0;s<3;s++) {
        AGAction agAction = bestSol.actions.elements[0 + s*AGSolution.DEPTH];
        if (agAction.action != nextState.teams[compareId].ships.get(s).action) {
          System.err.println("   "+s+" - Difference : his="+nextState.teams[compareId].ships.get(s).action+ " vs my calculated : "+agAction.action);
        } else {
          System.err.println("   "+s+" - Same       : his="+nextState.teams[compareId].ships.get(s).action+ " vs my calculated : "+agAction.action);
        }
      }
      
    }
  }

  private static void swapTeams(GameState state) {
    Team swap = state.teams[0];
    state.teams[0] = state.teams[1];
    state.teams[1] = swap;
  }
}
