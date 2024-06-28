package fall2020.astar;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fall2020.State;
import fall2020.fast.FastReader;

public class AStarTest {
  private static final String EOF = "\n\r";
  private AStarResult result;

  @BeforeEach
  public void setup() {
    result = new AStarResult();
  }
  
  @Test
  public void oneStepAStar() throws Exception {
    String input = ""+
        "30"+EOF+
        " 66 BREW -2 -1 0 -1 12 3 1 0 0"+EOF+
        " 42 BREW -2 -2 0 0 7 1 3 0 0"+EOF+
        " 53 BREW 0 0 -4 0 12 0 0 0 0"+EOF+
        " 67 BREW 0 -2 -1 -1 12 0 0 0 0"+EOF+
        " 50 BREW -2 0 0 -2 10 0 0 0 0"+EOF+
        "  9 LEARN  2 -3  2  0  0  0  1  0  1"+EOF+
        " 40 LEARN  0 -2  2  0  0  1  1  0  1"+EOF+
        " 11 LEARN -4  0  2  0  0  2  1  0  1"+EOF+
        " 20 LEARN  2 -2  0  1  0  3  0  0  1"+EOF+
        " 25 LEARN  0 -3  0  2  0  4  0  0  1"+EOF+
        "  8 LEARN  3 -2  1  0  0  5  0  0  1"+EOF+
        " 78 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
        " 79 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
        " 80 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
        " 81 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
        " 86 CAST -5  0  0  2  0 -1 -1  0  1"+EOF+
        " 87 CAST  3 -1  0  0  0 -1 -1  1  1"+EOF+
        " 88 CAST  1  0  1  0  0 -1 -1  0  0"+EOF+
        " 89 CAST  4  1 -1  0  0 -1 -1  1  1"+EOF+
        " 91 CAST  3  0  1 -1  0 -1 -1  1  1"+EOF+
        " 92 CAST  0  0  1  0  0 -1 -1  1  0"+EOF+
        " 95 CAST  1  1  1 -1  0 -1 -1  1  1"+EOF+
        " 96 CAST  0  0 -3  3  0 -1 -1  1  1"+EOF+
        " 82 OPPONENT_CAST  2  0  0  0  0 -1 -1  0  0"+EOF+
        " 83 OPPONENT_CAST -1  1  0  0  0 -1 -1  0  0"+EOF+
        " 84 OPPONENT_CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
        " 85 OPPONENT_CAST  0  0 -1  1  0 -1 -1  0  0"+EOF+
        " 90 OPPONENT_CAST  2  1  0  0  0 -1 -1  1  0"+EOF+
        " 93 OPPONENT_CAST  0  0  1  0  0 -1 -1  1  0"+EOF+
        " 94 OPPONENT_CAST  3  0  0  0  0 -1 -1  1  0"+EOF+
        "1 1 2 2 75"+EOF+
        "4 2 0 0 37"+EOF+
        "";
    
    State state = new State();
    
    state.read(new FastReader(input.getBytes()));

    AStar astar = new AStar();
    long start = System.currentTimeMillis();
    for (int i=0;i<5;i++) {
      System.err.println("For recipe "+state.recipes[i]);
      AStarResult path = astar.process(result, state, state.agents[1], state.recipes[i].recipe);
      System.err.println("AStar found ? : "+path);
      System.err.println("process in " + (System.currentTimeMillis() - start) + " ms");
    }
  }
  
  @Test
  public void learnRecipeToFindGoal() throws Exception {
    String input = ""+
        "16"+EOF+
        " 72 BREW 0 -2 -2 -2 22 3 4 0 0"+EOF+
        " 55 BREW 0 -3 -2 0 13 1 4 0 0"+EOF+
        " 64 BREW 0 0 -2 -3 18 0 0 0 0"+EOF+
        " 58 BREW 0 -3 0 -2 14 0 0 0 0"+EOF+
        " 70 BREW -2 -2 0 -2 15 0 0 0 0"+EOF+
        " 23 LEARN  1 -3  1  1  0  1  0  0  1"+EOF+
        " 82 CAST  2  0  0  0  0 -1 -1  0  0"+EOF+
        " 83 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
        " 84 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
        " 85 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
        " 86 CAST -4  0  2  0  0 -1 -1  1  1"+EOF+
        " 87 CAST -2  2  0  0  0 -1 -1  1  1"+EOF+
        " 88 CAST -3  1  1  0  0 -1 -1  1  1"+EOF+
        " 89 CAST  0  0 -3  3  0 -1 -1  1  1"+EOF+
        " 90 CAST  0 -3  0  2  0 -1 -1  1  1"+EOF+
        " 91 CAST  0  0  2 -1  0 -1 -1  1  1"+EOF+
        "7 0 0 0 0"+EOF+
        "0 9 0 0 7"+EOF+
        "";
    
    State state = new State();
    
    state.read(new FastReader(input.getBytes()));

    AStar astar = new AStar();
    long start = System.currentTimeMillis();
    for (int i=0;i<5;i++) {
      if (i != 2) continue;
      System.err.println("For recipe "+state.recipes[i]);
      AStarResult path = astar.process(result, state, state.agents[1], state.recipes[i].recipe);
      System.err.println("AStar found ? : "+path);
      System.err.println("process in " + (System.currentTimeMillis() - start) + " ms");
    }
    
//    assertThat(astar.process(state, state.agents[1], state.recipes[0].recipe).size()).isEqualTo(2);
    assertThat(astar.process(result, state, state.agents[1], state.recipes[1].recipe).fullActions.size()).isEqualTo(3);
//    assertThat(astar.process(state, state.agents[1], state.recipes[2].recipe).size()).isEqualTo(2);
//    assertThat(astar.process(state, state.agents[1], state.recipes[3].recipe).size()).isEqualTo(2);
//    assertThat(astar.process(state, state.agents[1], state.recipes[4].recipe).size()).isEqualTo(2);
  }
  
  @Test
  public void nonRegressionAStar() throws Exception {
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

    AStar astar = new AStar();
    long start = System.currentTimeMillis();
    for (int i=0;i<5;i++) {
      if (i != 2) continue;
      System.err.println("For recipe "+state.recipes[i]);
      AStarResult path = astar.process(result, state, state.agents[1], state.recipes[i].recipe);
      System.err.println("AStar found ? : "+path);
      System.err.println("process in " + (System.currentTimeMillis() - start) + " ms");
    }
    
    assertThat(astar.process(result, state, state.agents[1], state.recipes[0].recipe).fullActions.size()).isEqualTo(5);
    assertThat(astar.process(result, state, state.agents[1], state.recipes[1].recipe).fullActions.size()).isEqualTo(5);
    assertThat(astar.process(result, state, state.agents[1], state.recipes[2].recipe).fullActions.size()).isEqualTo(6);
    assertThat(astar.process(result, state, state.agents[1], state.recipes[3].recipe).fullActions.size()).isEqualTo(7);
    assertThat(astar.process(result, state, state.agents[1], state.recipes[4].recipe).fullActions.size()).isEqualTo(4);
  }
  
  @Test
  public void canLimitAstarToACertainDepth() throws Exception {
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

    AStar astar = new AStar();
    // standard AStar is in 6 steps
    assertThat(astar.process(result, state, state.agents[1], state.recipes[3].recipe).fullActions.size()).isEqualTo(7);
    
    // limited Astar
    assertThat(astar.process(result, state, state.agents[1], state.recipes[3].recipe, new BasicScorer().withMaxDepth(6))).isNotNull();
    assertThat(astar.process(result, state, state.agents[1], state.recipes[3].recipe, new BasicScorer().withMaxDepth(5))).isNull();
    
  }
  
  @Test
  public void canFoundOtherSolutionsDepth() throws Exception {
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

    AStar astar = new AStar();
    // standard AStar is in 6 steps
    assertThat(astar.process(result, state, state.agents[1], state.recipes[3].recipe).fullActions.size()).isEqualTo(7);
    
  }
}
