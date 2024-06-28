package fall2020.ai.heuristic;

import java.util.HashMap;
import java.util.Map;

import fall2020.Agent;
import fall2020.Recipe;
import fall2020.Spell;
import fall2020.State;
import fall2020.optimizer.Action;
import fall2020.optimizer.OInv;
import fall2020.optimizer.OSpell;

public class SimpleAI {

  public String think(State state) {

    if (state.agents[0].spellsFE < 10 && state.tomesFE > 0) {
      int inv0 = state.agents[0].getInv(0);
      System.err.println("Check tomes my inv0 = "+inv0);
      int bestTome = -1;
      double bestScore = Double.NEGATIVE_INFINITY;
      for (int i=0;i<Math.min(inv0+1, state.tomesFE);i++) {
        Spell spell = state.tomes[i];
        double score = spell.spell.delta[0]+spell.spell.delta[1]+spell.spell.delta[2]+spell.spell.delta[3] - 1.0 * i / 4.0;
        System.err.println("Tome "+i+" score="+score);
        if (score > bestScore) {
          bestScore = score;
          bestTome = i;
        }
      }
      if (bestTome != -1) {
        return "LEARN " + state.tomes[bestTome].gameId;
      }
    }

    int bestPrice = -10;
    Recipe bestRecipe = null;
    for (int i = 0;i<state.recipesFE;i++) {
      Recipe recipe = state.recipes[i];
      if (state.agents[0].canBrew(recipe)) {
        if (bestPrice < recipe.price) {
          bestPrice = recipe.price;
          bestRecipe = recipe;
        }
      }
    }
    if (bestRecipe != null) {
      return "BREW " + bestRecipe.gameId;
    }

    // check spells
    System.err.println("Cast a spell ? ");

    // calculate current distances
    Map<Recipe, Double> distances = new HashMap<>();
    for (int i = 0;i<state.recipesFE;i++) {
      Recipe recipe = state.recipes[i];
      distances.put(recipe, recipe.distance(state.agents[0].inv));
    }

    double bestScore = Double.NEGATIVE_INFINITY;
    OSpell bestSpell = null;
    Agent agent = state.agents[0];
    OInv source = agent.inv;
    
    for (int i = 0;i<state.recipesFE;i++) {
      Recipe recipe = state.recipes[i];

      System.err.println("Checking recipe "+recipe.debug() +" "+"Current score for this recipe is "+scoreRecipe(state, recipe, source));
    
      for (Action action : source.casts) {
        OSpell spell = action.spell;
        if (!agent.hasSpell(spell)) continue;
        if (!agent.hasInvToCast(spell)) continue;
        
        OInv target = source.cast(spell);
        String spellId = agent.findSpellId(spell);
        double score = scoreRecipe(state, recipe, target);
        if (agent.isExhausted(spell)) {
          score -= 200;
          System.err.println("    cannot cast this spell atm");
        }
        System.err.println("  From " + source + ", If I cast ("+spellId+")" + spell + " I will have " + target + " score = "+score);
        if (bestScore < score) {
          bestScore = score;
          bestSpell = spell;
          System.err.println("    > new best cast " + spellId + " for " + recipe.debug() + " with score " + score);
        }
      }
    }
    
    if (bestSpell != null) {
      if (!agent.isExhausted(bestSpell)) {
        int times = 0;
        OInv cInv= source;
        while (true) {
          cInv = cInv.cast(bestSpell);
          if (cInv == null) break;
          times++;
          if (!bestSpell.repeatable) break;
        }
        
        return "CAST " + agent.findSpellId(bestSpell) + " "+times;
      } else {
        return "REST ";
      }
    }

    // rest ?
    return "REST";
  }

  private double scoreRecipe(State state, Recipe recipe, OInv inv) {
    double currentDistance = recipe.distance(inv);
    double currentScore = 20 * recipe.price + 1000 - currentDistance;
    return currentScore;
  }
}
