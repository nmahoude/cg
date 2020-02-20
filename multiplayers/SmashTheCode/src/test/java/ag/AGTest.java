package ag;

import org.junit.Test;

import stc2.GameTest;
import stc2.BitBoard;
import stc2.BitBoardTest;
import stc2.Game;

public class AGTest {
  @Test
  public void aGConvergence() throws Exception {
    Game game = new Game();
    GameTest.setNextBlocks(game,
        "44",
        "21",
        "53",
        "23",
        "53",
        "14",
        "51",
        "31"
        );
    
    BitBoard board = new BitBoard();
    BitBoardTest.prepareBoard(board ,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "1.....",
        "11.111");
    
    AG ag = new AG();
    AGSolution bestSolution;

    
    ag.simulate(game, board, 8, 0, null); // one turn
    bestSolution = ag.bestSolution;
    
    double total = 0;
    int count = 0;
    reportGeneration(0, ag, total, count);
    
    for (int i=1;i<100;i++) { 
      ag.doOneGeneration(game);
      reportGeneration(i, ag, total, count);
    }

  }

  private void reportGeneration(int generation, AG ag, double total, int count) {
    System.err.println("Generation "+generation);
    for (int i=0;i<AG.POPULATION_COUNT;i++) {
      if (ag.populations2[i].energy > -1_000_000) {
        total+=ag.populations2[i].energy;
        count++;
      }
    }
    System.err.println("count : "+count);
    System.err.println("energy mean : "+(total/count));
    System.err.println("champion: " +ag.bestSolution.energy);
  }
  
}
