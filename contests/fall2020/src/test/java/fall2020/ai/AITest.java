package fall2020.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fall2020.Player;
import fall2020.State;
import fall2020.astar.AStarResult;
import fall2020.astar.BasicScorer;
import fall2020.fast.FastReader;

public class AITest {
  private static final String EOF = "\n\r";
  private State state;
  private AI ai;
  
  @BeforeEach
  public void setup() throws IOException {
    state = createState();
    ai = new AI();
  }
  
  @Nested
  class ChooseScorer {
    @Nested
    class GivenItsMyLastRecipe {
      @BeforeEach
      public void setup() throws IOException {
        state.agents[0].brewedRecipe = 5;
        state.agents[1].brewedRecipe = 4;
        Player.turn = 60;
      }      
      @Test
      void GivenImFarAhead_doQuickest() throws Exception {
        state.agents[0].score = 100;
        state.agents[1].score = 80;
        
        BasicScorer scorer = ai.endGameScorer(state);
        
//        assertThat(scorer.allowedDepthMargin()).isEqualTo(0);
        assertThat(scorer.maxDepth()).isEqualTo(40);
        assertThat(scorer.getScorer()).isEqualTo(AI.SCORE_MAXIMIZE_TIER1PLUS);
        assertThat(scorer.getRecipeScorer()).isEqualTo(AI.RECIPE_QUICKEST);
      }
      
      @Test
      void GivenImFarBehind_doQuickest() throws Exception {
        state.agents[0].score = 80;
        state.agents[1].score = 100;
        
        BasicScorer scorer = ai.endGameScorer(state);
        
        assertThat(scorer.maxDepth()).isEqualTo(40);
//        assertThat(scorer.allowedDepthMargin()).isEqualTo(10);
        assertThat(scorer.getScorer()).isEqualTo(AI.SCORE_MAXIMIZE_TIER1PLUS);
        assertThat(scorer.getRecipeScorer()).isEqualTo(AI.RECIPE_QUICKEST);
      }
    }  
  }

  
  @Nested
  class Integration {
  	AStarResult result;
  	
  	@BeforeEach
  	public void setup() {
  		result = new AStarResult();
  	}
  	
  	@Test
  	public void testLibre() throws Exception {
  		String input = ""+
  				"29"+EOF+
  				" 57 BREW 0 0 -2 -2 17 3 2 0 0"+EOF+
  				" 51 BREW -2 0 -3 0 12 1 4 0 0"+EOF+
  				" 62 BREW 0 -2 0 -3 16 0 0 0 0"+EOF+
  				" 63 BREW 0 0 -3 -2 17 0 0 0 0"+EOF+
  				" 50 BREW -2 0 0 -2 10 0 0 0 0"+EOF+
  				"  0 LEARN -3  0  0  1  0  0  2  0  1"+EOF+
  				" 23 LEARN  1 -3  1  1  0  1  2  0  1"+EOF+
  				"  5 LEARN  2  3 -2  0  0  2  2  0  1"+EOF+
  				" 31 LEARN  0  3  2 -2  0  3  2  0  1"+EOF+
  				" 35 LEARN  0  0 -3  3  0  4  0  0  1"+EOF+
  				"  6 LEARN  2  1 -2  1  0  5  0  0  1"+EOF+
  				" 82 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
  				" 83 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
  				" 84 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
  				" 85 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
  				" 87 CAST  1  0  1  0  0 -1 -1  0  0"+EOF+
  				" 89 CAST  0  0  0  1  0 -1 -1  0  0"+EOF+
  				" 90 CAST -2  0  1  0  0 -1 -1  1  1"+EOF+
  				" 91 CAST  1  2 -1  0  0 -1 -1  1  1"+EOF+
  				" 92 CAST -1 -1  0  1  0 -1 -1  1  1"+EOF+
  				" 93 CAST  0  0  2 -1  0 -1 -1  1  1"+EOF+
  				" 78 OPPONENT_CAST  2  0  0  0  0 -1 -1  0  0"+EOF+
  				" 79 OPPONENT_CAST -1  1  0  0  0 -1 -1  0  0"+EOF+
  				" 80 OPPONENT_CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
  				" 81 OPPONENT_CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
  				" 86 OPPONENT_CAST  1  0  1  0  0 -1 -1  0  0"+EOF+
  				" 88 OPPONENT_CAST  0  0  0  1  0 -1 -1  1  0"+EOF+
  				" 94 OPPONENT_CAST  4  0  0  0  0 -1 -1  0  0"+EOF+
  				" 95 OPPONENT_CAST  0  2  0  0  0 -1 -1  1  0"+EOF+
  				"0 0 0 1 27"+EOF+
  				"0 0 1 0 8"+EOF+
  				"";

  		State state = new State();
  		state.read(new FastReader(input.getBytes()));

  		AI ai = new AI();
  		ai.think(state);

  	}
  }
  
  
  private State createState() throws IOException {
    String input = ""+
        "29"+EOF+
        " 57 BREW 0 0 -2 -2 17 3 2 0 0"+EOF+
        " 51 BREW -2 0 -3 0 12 1 4 0 0"+EOF+
        " 62 BREW 0 -2 0 -3 16 0 0 0 0"+EOF+
        " 63 BREW 0 0 -3 -2 17 0 0 0 0"+EOF+
        " 50 BREW -2 0 0 -2 10 0 0 0 0"+EOF+
        "  0 LEARN -3  0  0  1  0  0  2  0  1"+EOF+
        " 23 LEARN  1 -3  1  1  0  1  2  0  1"+EOF+
        "  5 LEARN  2  3 -2  0  0  2  2  0  1"+EOF+
        " 31 LEARN  0  3  2 -2  0  3  2  0  1"+EOF+
        " 35 LEARN  0  0 -3  3  0  4  0  0  1"+EOF+
        "  6 LEARN  2  1 -2  1  0  5  0  0  1"+EOF+
        " 82 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
        " 83 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
        " 84 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
        " 85 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
        " 87 CAST  1  0  1  0  0 -1 -1  0  0"+EOF+
        " 89 CAST  0  0  0  1  0 -1 -1  0  0"+EOF+
        " 90 CAST -2  0  1  0  0 -1 -1  1  1"+EOF+
        " 91 CAST  1  2 -1  0  0 -1 -1  1  1"+EOF+
        " 92 CAST -1 -1  0  1  0 -1 -1  1  1"+EOF+
        " 93 CAST  0  0  2 -1  0 -1 -1  1  1"+EOF+
        " 78 OPPONENT_CAST  2  0  0  0  0 -1 -1  0  0"+EOF+
        " 79 OPPONENT_CAST -1  1  0  0  0 -1 -1  0  0"+EOF+
        " 80 OPPONENT_CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
        " 81 OPPONENT_CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
        " 86 OPPONENT_CAST  1  0  1  0  0 -1 -1  0  0"+EOF+
        " 88 OPPONENT_CAST  0  0  0  1  0 -1 -1  1  0"+EOF+
        " 94 OPPONENT_CAST  4  0  0  0  0 -1 -1  0  0"+EOF+
        " 95 OPPONENT_CAST  0  2  0  0  0 -1 -1  1  0"+EOF+
        "0 0 0 1 27"+EOF+
        "0 0 1 0 8"+EOF+
        "";
    
    State state = new State();
    state.read(new FastReader(input.getBytes()));
    return state;
  }  
}
