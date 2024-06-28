package fall2020;

import java.util.HashMap;
import java.util.Map;

import fall2020.optimizer.ORecipe;

public class Oracle {

  static class History {
    int[][] distances = new int[2][100];
    
    boolean oppIsDoing = false;
  }
  
  Map<ORecipe, History> history = new HashMap<>();
  
  // try to detect the recipes he is doing
  public void think(State state) {
    System.err.println("------------- ORACLE ----------------------");
    int currentTurn = Player.turn;
    for (int i=0;i<5;i++) {
      Recipe recipe = state.recipes[i];
      
      History recipeHistory = history.get(recipe.recipe);
      if (recipeHistory == null) {
        recipeHistory = new History();
        history.put(recipe.recipe, recipeHistory);
      }
      
      recipeHistory.distances[0][currentTurn] = Player.myDistanceToRecipes[i];
      recipeHistory.distances[1][currentTurn] = Player.oppDistanceToRecipes[i];
      
      if (currentTurn == 0 || recipeHistory.distances[1][currentTurn-1] == 0) {
        System.err.println("No prior info on recipe "+recipe);
        continue;
      }
      if (recipeHistory.distances[1][currentTurn-1] > recipeHistory.distances[1][currentTurn]) {
        // he may be doing it !
        recipeHistory.oppIsDoing = true;
        System.err.println("HE may be doing "+recipe);
      } else {
        System.err.println("not doing "+recipe+" ("+recipeHistory.distances[1][currentTurn-1] +" " + recipeHistory.distances[1][currentTurn]+")");
        recipeHistory.oppIsDoing = false;
      }
      
    }
    
    
  }
}
