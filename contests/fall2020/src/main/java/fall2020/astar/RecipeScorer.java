package fall2020.astar;

import java.util.List;

import fall2020.Recipe;
import fall2020.optimizer.Action;
import fall2020.optimizer.OInv;

@FunctionalInterface
public interface RecipeScorer {

  public double score(Recipe recipe, List<Action> actions, OInv invAfterBrew);
}
