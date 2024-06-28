package fall2020;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import fall2020.ai.bs.BeamSearch;
import fall2020.astar.AStar;
import fall2020.astar.AStarResult;
import fall2020.astar.BasicScorer;
import fall2020.fast.FastReader;
import fall2020.optimizer.OptiGraph;


public class Player {
  public static final boolean DEBUG_INPUT = false;
  public static final boolean DEBUG_MY_RECIPES = false;
  public static final boolean DEBUG_OPP_RECIPES = false;
  public static final boolean DEBUG_BESTPATH = false;
  public static final Random random = ThreadLocalRandom.current(); //new Random(0); //ThreadLocalRandom.current();

  public static int turn = 0;
  public final boolean inGame;
  private String output;
  private BeamSearch ai = new BeamSearch();
  
  static State state= new State();
  public static int expectedEndTurn = 100;
  public static long start;
  public static double[][] recipesValueAtDepth = new double[6][150];
  public static final int[] oppDistanceToRecipes = new int[6];
  public static final int[] myDistanceToRecipes= new int[6];
  
  public static void main(String args[]) throws Exception {
    
    
    
    // FastReader reader = new FastReader();
    FastReader reader = new FastReader(System.in);
    
    Player player = new Player();
    player.init(reader);
    player.play(reader);
  }
  
  public Player() {
    inGame = true;
  }
  
  public void init(FastReader reader) {
  }
  
  public void play(FastReader  reader) throws Exception {
    while(true) {
      turn++;
      state.read(reader);
      
      
      debug();
      calculateOppDistanceToRecipes();
      
      BeamSearch.maxDepth = 100 - turn;
      if (state.agents[1].brewedRecipe == 5) {
        for (int i=0;i<5;i++) {
          Recipe recipe = state.recipes[i];
          System.err.println("Distance to recipe "+recipe+" : "+myDistanceToRecipes[i]+ " / "+oppDistanceToRecipes[i]+" => Multiplier is "+state.recipeScore[i]);
          if (state.agents[1].score + recipe.price < state.agents[0].score) {
            System.err.println("   Il peut pas me battre avec ça");
          } else {
            BeamSearch.maxDepth = Math.min(BeamSearch.maxDepth, oppDistanceToRecipes[i]);
          }
        }
      }
      System.err.println("Max depth is "+BeamSearch.maxDepth);
      output = think();
      System.out.println(output+ " ["+turn+"]+/"+(System.currentTimeMillis()-start)+"/");
      if (!inGame) break;
    }
  }

  private void debug() {
    if (turn != 1000) return;
    
    for (int i=0;i<OptiGraph.ospells.length;i++) {
      System.err.println("Did I have #"+i+" "+OptiGraph.ospells[i]+" ? => "+ state.agents[0].hasSpell(OptiGraph.ospells[i]));
    }
  }

  AStarResult result = new AStarResult();
  BasicScorer basicScorer = new BasicScorer()
    .withMaxDepth(5)
    .withAllowedDepthMargin((s, recipe) -> 0)
    .withAStarScoreFunction((depth, recipe, resultAfteBrew, castedSpells, knownSpells) -> -depth)
    .withRecipeScoreFunction(BasicScorer.BASIC_RECIPE_FUNCTION);

  private void calculateOppDistanceToRecipes() {
    
    if (DEBUG_OPP_RECIPES) {
      System.err.println("Estimation distance to recipes");
    }
    for (int r=0;r<5;r++) {
      Recipe recipe = state.recipes[r];
      if (DEBUG_OPP_RECIPES) {
        System.err.println("For "+recipe);
      }
//      AStarResult mines = new AStar().process(result, state, state.agents[0], recipe.recipe);
//      myDistanceToRecipes[r] = mines != null ? mines.fullActions.size() : 9999;
      
      AStarResult opp = new AStar().process(result, state, state.agents[1], recipe.recipe);
      oppDistanceToRecipes[r] = opp != null ? opp.fullActions.size() : 99;
      if (DEBUG_OPP_RECIPES) {
        System.err.println("    OPP(A*) : "+(opp!=null?opp.fullActions.size()+" -> "+opp.fullActions:"+Inf"));
      }
    }

    double minCoeff[] = new double[] { 1.0, 1.0, 1.0 ,1.0, 1.0 };
    for (int i=0;i<150;i++) {
      int count = 0;
      for (int r=0;r<5;r++) {
        if (oppDistanceToRecipes[r] <= i) count++;
      }
      
      for (int r=0;r<5;r++) {
        Recipe recipe = state.recipes[r];
        if (oppDistanceToRecipes[r] <= i)  {
          minCoeff[r] = Math.min(minCoeff[r], 1.0  * (count+10) / (count+11));
          Player.recipesValueAtDepth[r][i] = 1.0 * recipe.price * minCoeff[r] - 0.01*i;
        } else {
          Player.recipesValueAtDepth[r][i] = 1.0 * recipe.price - 0.01*i;
        }
      }
    }

    if (DEBUG_OPP_RECIPES) {
      for (int r=0;r<5;r++) {
        Recipe recipe = state.recipes[r];
        System.err.print("Price evaluation for "+recipe);
        for (int i=0;i<10;i++) {
          System.err.print(""+Player.recipesValueAtDepth[r][i]+"; ");
        }
        System.err.println();
      }
    }
  }

  private String think() {
    return ai.think(state);
  }

  public List<String> getOutputs() {
    return Arrays.asList(output);
  }
}
