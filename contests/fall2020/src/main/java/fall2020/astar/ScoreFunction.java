package fall2020.astar;

import fall2020.optimizer.OInv;
import fall2020.optimizer.ORecipe;

@FunctionalInterface
public interface ScoreFunction {

  public double score(int depth, ORecipe recipe, OInv resultAfteBrew, long castedSpellsMask, long knownSpells);
}
