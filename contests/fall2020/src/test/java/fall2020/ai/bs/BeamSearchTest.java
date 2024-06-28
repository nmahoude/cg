package fall2020.ai.bs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fall2020.Player;
import fall2020.State;
import fall2020.fast.FastReader;

public class BeamSearchTest {
	 private static final String EOF = "\n\r";
	  
	 
	 @BeforeEach
	 public void setup() {
	   BeamSearch.maxDepth = 100;
	 }
	 
	  @Test
	  void IntegrationTest() throws Exception {
	    String input = ""+
	        "23"+EOF+
	        " 51 BREW -2 0 -3 0 14 3 4 0 0"+EOF+
	        " 75 BREW -1 -3 -1 -1 17 1 4 0 0"+EOF+
	        " 63 BREW 0 0 -3 -2 17 0 0 0 0"+EOF+
	        " 70 BREW -2 -2 0 -2 15 0 0 0 0"+EOF+
	        " 57 BREW 0 0 -2 -2 14 0 0 0 0"+EOF+
	        "  0 LEARN -3  0  0  1  0  0  0  0  1"+EOF+
	        " 26 LEARN  1  1  1 -1  0  1  0  0  1"+EOF+
	        "  6 LEARN  2  1 -2  1  0  2  0  0  1"+EOF+
	        "  4 LEARN  3  0  0  0  0  3  0  0  0"+EOF+
	        " 25 LEARN  0 -3  0  2  0  4  0  0  1"+EOF+
	        " 30 LEARN -4  0  1  1  0  5  0  0  1"+EOF+
	        " 78 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
	        " 79 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
	        " 80 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
	        " 81 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
	        " 86 CAST  3 -1  0  0  0 -1 -1  1  1"+EOF+
	        " 88 CAST -5  0  0  2  0 -1 -1  1  1"+EOF+
	        " 82 OPPONENT_CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
	        " 83 OPPONENT_CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
	        " 84 OPPONENT_CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
	        " 85 OPPONENT_CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
	        " 87 OPPONENT_CAST  0  2 -2  1  0 -1 -1  1  1"+EOF+
	        " 89 OPPONENT_CAST -5  0  0  2  0 -1 -1  1  1"+EOF+
	        "4 0 0 0 0"+EOF+
	        "3 0 0 0 0"+EOF+
	        "";
	    
	    
	    State state = new State();
	    state.read(new FastReader(input.getBytes()));
	    
	    BeamSearch bs = new BeamSearch();
	    Player.start = System.currentTimeMillis() + 800;
	    String output = bs.think(state);

	    System.err.println("BS output "+output);
	  }
	  
	  //PERF 
	  public static void main(String[] args) throws Exception  {
	    String input = ""+
	        "31"+EOF+
	        " 51 BREW -2 0 -3 0 14 3 4 0 0"+EOF+
	        " 52 BREW -3 0 0 -2 12 1 3 0 0"+EOF+
	        " 46 BREW -2 -3 0 0 8 0 0 0 0"+EOF+
	        " 54 BREW 0 -2 0 -2 12 0 0 0 0"+EOF+
	        " 43 BREW -3 -2 0 0 7 0 0 0 0"+EOF+
	        " 24 LEARN  0  3  0 -1  0  0  7  0  1"+EOF+
	        "  7 LEARN  3  0  1 -1  0  1  3  0  1"+EOF+
	        "  5 LEARN  2  3 -2  0  0  2  1  0  1"+EOF+
	        "  4 LEARN  3  0  0  0  0  3  1  0  0"+EOF+
	        " 41 LEARN  0  0  2 -1  0  4  0  0  1"+EOF+
	        " 39 LEARN  0  0 -2  2  0  5  0  0  1"+EOF+
	        " 78 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
	        " 79 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
	        " 80 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
	        " 81 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
	        " 86 CAST  1 -3  1  1  0 -1 -1  1  1"+EOF+
	        " 87 CAST -4  0  2  0  0 -1 -1  1  1"+EOF+
	        " 88 CAST  3 -1  0  0  0 -1 -1  1  1"+EOF+
	        " 89 CAST  4  0  0  0  0 -1 -1  1  0"+EOF+
	        " 90 CAST -4  0  1  1  0 -1 -1  1  1"+EOF+
	        " 91 CAST  0  3  2 -2  0 -1 -1  1  1"+EOF+
	        " 92 CAST  0 -3  3  0  0 -1 -1  1  1"+EOF+
	        " 93 CAST -3  3  0  0  0 -1 -1  1  1"+EOF+
	        " 95 CAST  1  2 -1  0  0 -1 -1  0  1"+EOF+
	        " 96 CAST -5  0  3  0  0 -1 -1  1  1"+EOF+
	        " 97 CAST  1  1  1 -1  0 -1 -1  1  1"+EOF+
	        " 82 OPPONENT_CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
	        " 83 OPPONENT_CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
	        " 84 OPPONENT_CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
	        " 85 OPPONENT_CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
	        " 94 OPPONENT_CAST  1  0  1  0  0 -1 -1  1  0"+EOF+
	        "7 2 0 0 0"+EOF+
	        "1 0 1 0 15"+EOF+
	        "";
	        
	        
	        State state = new State();
	        state.read(new FastReader(input.getBytes()));
	        BeamSearch.maxDepth = 100;
	        
	        BeamSearch mc = new BeamSearch();
	        String output = mc.think(state);

	        
	        for (int i=0;i<1_000;i++) {
	          Player.start = System.currentTimeMillis();
	          output = mc.think(state);
	        }
	        
	        
	        System.err.println("BS  output "+output);
	  }
	  
	  @Test
    void learnAtFirstRound() throws Exception {
      String input = ""+
          "19"+EOF+
	    " 53 BREW 0 0 -4 0 15 3 4 0 0"+EOF+
	    " 61 BREW 0 0 0 -4 17 1 4 0 0"+EOF+
	    " 54 BREW 0 -2 0 -2 12 0 0 0 0"+EOF+
	    " 51 BREW -2 0 -3 0 11 0 0 0 0"+EOF+
	    " 48 BREW 0 -2 -2 0 10 0 0 0 0"+EOF+
	    " 35 LEARN  0  0 -3  3  0  0  0  0  1"+EOF+
	    " 11 LEARN -4  0  2  0  0  1  0  0  1"+EOF+
	    " 34 LEARN -2  0 -1  2  0  2  0  0  1"+EOF+
	    " 40 LEARN  0 -2  2  0  0  3  0  0  1"+EOF+
	    " 31 LEARN  0  3  2 -2  0  4  0  0  1"+EOF+
	    " 38 LEARN -2  2  0  0  0  5  0  0  1"+EOF+
	    " 78 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
	    " 79 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
	    " 80 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
	    " 81 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
	    " 82 OPPONENT_CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
	    " 83 OPPONENT_CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
	    " 84 OPPONENT_CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
	    " 85 OPPONENT_CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
	    "3 0 0 0 0"+EOF+
	    "3 0 0 0 0"+EOF+
	    "";
      
      State state = new State();
      BeamSearch.maxDepth = 5;

      state.read(new FastReader(input.getBytes()));
      Player.start = System.currentTimeMillis() +80000;
      
      BeamSearch mc = new BeamSearch();
      String output = mc.think(state);
      
    }
	  @Test
	  void integration() throws Exception {
	    String input = ""+
	        "36"+EOF+
	        " 47 BREW -3 0 -2 0 10 1 2 0 0"+EOF+
	        " 63 BREW 0 0 -3 -2 17 0 0 0 0"+EOF+
	        " 49 BREW 0 -5 0 0 10 0 0 0 0"+EOF+
	        " 52 BREW -3 0 0 -2 11 0 0 0 0"+EOF+
	        " 45 BREW -2 0 -2 0 8 0 0 0 0"+EOF+
	        " 21 LEARN -3  1  1  0  0  0  0  0  1"+EOF+
	        " 14 LEARN  0  0  0  1  0  1  0  0  0"+EOF+
	        " 27 LEARN  1  2 -1  0  0  2  0  0  1"+EOF+
	        " 10 LEARN  2  2  0 -1  0  3  0  0  1"+EOF+
	        " 15 LEARN  0  2  0  0  0  4  0  0  0"+EOF+
	        "  1 LEARN  3 -1  0  0  0  5  0  0  1"+EOF+
	        " 78 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
	        " 79 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
	        " 80 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
	        " 81 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
	        " 86 CAST  4  1 -1  0  0 -1 -1  1  1"+EOF+
	        " 88 CAST -4  0  2  0  0 -1 -1  1  1"+EOF+
	        " 90 CAST  3  0  1 -1  0 -1 -1  1  1"+EOF+
	        " 92 CAST -2  0  1  0  0 -1 -1  1  1"+EOF+
	        " 94 CAST  3  0  0  0  0 -1 -1  1  0"+EOF+
	        " 96 CAST  0  3  0 -1  0 -1 -1  0  1"+EOF+
	        " 98 CAST  0 -3  3  0  0 -1 -1  1  1"+EOF+
	        " 99 CAST  0 -2  2  0  0 -1 -1  1  1"+EOF+
	        "100 CAST  0  0 -2  2  0 -1 -1  0  1"+EOF+
	        " 82 OPPONENT_CAST  2  0  0  0  0 -1 -1  0  0"+EOF+
	        " 83 OPPONENT_CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
	        " 84 OPPONENT_CAST  0 -1  1  0  0 -1 -1  0  0"+EOF+
	        " 85 OPPONENT_CAST  0  0 -1  1  0 -1 -1  0  0"+EOF+
	        " 87 OPPONENT_CAST  4  1 -1  0  0 -1 -1  0  1"+EOF+
	        " 89 OPPONENT_CAST -4  0  2  0  0 -1 -1  0  1"+EOF+
	        " 91 OPPONENT_CAST  3  0  1 -1  0 -1 -1  1  1"+EOF+
	        " 93 OPPONENT_CAST -2  0  1  0  0 -1 -1  0  1"+EOF+
	        " 95 OPPONENT_CAST  3  0  0  0  0 -1 -1  1  0"+EOF+
	        " 97 OPPONENT_CAST  0  3  0 -1  0 -1 -1  0  1"+EOF+
	        "101 OPPONENT_CAST  0  0 -2  2  0 -1 -1  0  1"+EOF+
	        "102 OPPONENT_CAST -5  0  3  0  0 -1 -1  0  1"+EOF+
	        "0 3 1 3 75"+EOF+
	        "0 8 1 1 67"+EOF+
	        "";
	    
	    State state = new State();
	    BeamSearch.maxDepth = 5;
	    
	    state.read(new FastReader(input.getBytes()));
	    state.agents[0].brewedRecipe = 5;
	    Player.start = System.currentTimeMillis() +80000;
	    
	    BeamSearch mc = new BeamSearch();
	    String output = mc.think(state);
	    
	  }
	  
	  @Test
	  public void learnRecipeToFindGoal() throws Exception {
	    String input = ""+
	        "7"+EOF+
	        " 72 BREW 0 -2 -2 -2 0 3 4 0 0"+EOF+
	        " 55 BREW 0 -3 -2 0 100 1 4 0 0"+EOF+
	        " 64 BREW 0 0 -2 -3 0 0 0 0 0"+EOF+
	        " 58 BREW 0 -3 0 -2 0 0 0 0 0"+EOF+
	        " 70 BREW -2 -2 0 -2 0 0 0 0 0"+EOF+
	        " 23 LEARN  -2 2  0  0  0  1  0  0  1"+EOF+
	        " 83 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
	        "7 0 2 0 0"+EOF+
	        "0 9 0 0 7"+EOF+
	        "";
	    
	    State state = new State();
	    
	    state.read(new FastReader(input.getBytes()));
	    Player.start = System.currentTimeMillis() +80000;
	    
	    BeamSearch mc = new BeamSearch();
      String output = mc.think(state);
	    
	    // assertThat(pathToBrew).isEqualTo(3);
	  }
}
