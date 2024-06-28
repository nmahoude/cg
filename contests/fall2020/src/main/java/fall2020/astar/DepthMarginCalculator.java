package fall2020.astar;

import fall2020.State;
import fall2020.optimizer.ORecipe;

@FunctionalInterface
public interface DepthMarginCalculator {
  int depthMargin(State state, ORecipe recipe);
}
