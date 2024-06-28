package fall2020.astar;

import java.util.List;

import fall2020.Player;
import fall2020.Recipe;
import fall2020.State;
import fall2020.optimizer.Action;
import fall2020.optimizer.OInv;
import fall2020.optimizer.ORecipe;

public class BasicScorer {
  public static final ScoreFunction BASIC_SCORE_FUNCTION = (depth, recipe, resultAfteBrew, castedSpells, knownSpells) -> {
    if (Player.DEBUG_BESTPATH) {
      System.err.println("Depth of sol is "+depth + " result is "+resultAfteBrew+" with weight total "+resultAfteBrew.allTierWeightedTotal);
    }

    return (100-depth) + 0.5 * resultAfteBrew.allTierWeightedTotal;
  };
  public static final RecipeScorer BASIC_RECIPE_FUNCTION = (recipe, actions, brew) -> recipe.price;


  
  
  int maxDepth;
  DepthMarginCalculator depthMarginCalculator = (state, recipe)  -> 0;
  ScoreFunction function = BASIC_SCORE_FUNCTION;
  RecipeScorer recipeScorer = BASIC_RECIPE_FUNCTION;
  
  
  public BasicScorer withMaxDepth(int depth) {
    this.maxDepth = depth;
    return this;
  }

  public BasicScorer withAllowedDepthMargin(DepthMarginCalculator calculator) {
    this.depthMarginCalculator = calculator;
    return this;
  }
  
  public BasicScorer withRecipeScoreFunction(RecipeScorer recipeScorer) {
    this.recipeScorer = recipeScorer;
    return this;
  }

  public BasicScorer withAStarScoreFunction(ScoreFunction function) {
    this.function = function;
    return this;
  }

  public int maxDepth() {
    return maxDepth;
  }

  public int allowedDepthMargin(State state, ORecipe recipe) {
    return depthMarginCalculator.depthMargin(state, recipe);
  }

  public double score(int depth, ORecipe recipe, OInv resultAfteBrew, long castedSpells, long knownSpells) {
    return function.score(depth, recipe, resultAfteBrew, castedSpells, knownSpells);
  }


  public double scoreRecipe(Recipe recipe, List<Action> actions, OInv invAfterBrew) {
    return recipeScorer.score(recipe, actions, invAfterBrew);
  }
  
  public ScoreFunction getScorer() {
    return function;
  }
  
  public RecipeScorer getRecipeScorer() {
    return recipeScorer;
  }
}