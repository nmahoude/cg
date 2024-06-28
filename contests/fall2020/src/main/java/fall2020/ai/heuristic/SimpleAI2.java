package fall2020.ai.heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fall2020.Agent;
import fall2020.Player;
import fall2020.Recipe;
import fall2020.Spell;
import fall2020.State;
import fall2020.astar.AStar;
import fall2020.astar.AStarResult;
import fall2020.optimizer.Action;
import fall2020.optimizer.OInv;
import fall2020.optimizer.OSpell;

public class SimpleAI2 {
  private AStar astar = new AStar();

  
  private String output;
  private State state;

  public String think(State state) {

    this.state = state;

    
    
    // Learn a book TODO obviously
    if (state.agents[0].spellsFE < 10 && state.tomesFE > 0) {
      return "LEARN " + state.tomes[0].gameId;
    }

    output = null;
    if (findViaAstar()) {
      if (Player.DEBUG_MY_RECIPES) {
        System.err.println("Found via astar ! ");
      }
      return output;
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

  private double scoreSpell(int index, Spell spell) {
    
    OSpell oSpell = spell.spell;
    
    double lowtier = 0.0;
    if (oSpell.delta[0]> 0) { lowtier += 0.5*oSpell.delta[0]; }; 
    if (oSpell.delta[1]> 0) { lowtier += 2*oSpell.delta[1]; };
    
    int totalIngredients = oSpell.delta[0]+oSpell.delta[1]+oSpell.delta[2]+oSpell.delta[3];
    double balance = 0.5*oSpell.delta[0]+2*oSpell.delta[1]+3*oSpell.delta[2]+4*oSpell.delta[3];
    double cost = 1.0 * index;
    
    return  0.0
        + balance 
        +lowtier
        - cost
        - totalIngredients
        ;
  }

  private boolean findViaAstar() {
    long start = System.currentTimeMillis();

    List<Action> bestNodes = new ArrayList<>();
    double bestScore = Double.NEGATIVE_INFINITY;
    Recipe bestRecipe= null;
    
    for (int i=0;i<state.recipesFE;i++) {
      if (Player.DEBUG_MY_RECIPES) {
        System.err.println("Looking for recipe "+state.recipes[i].debug());
        System.err.println("---------------------------");
      }
      AStarResult result = astar.process(new AStarResult(), state, state.agents[0], state.recipes[i].recipe);
      if (result.fullActions != null) {
        double score = state.recipes[i].price + 50 - 2* result.fullActions.size();
        if (Player.DEBUG_MY_RECIPES) {
          System.err.println("  > Found a solution in size "+result.fullActions.size()+" >score = "+score);
          System.err.println("  > "+result.fullActions);
        }
        if (score > bestScore) {
          bestScore = score;
          bestNodes.clear();
          bestNodes.addAll(result.fullActions);
          bestRecipe = state.recipes[i];
        }
      }
    }
    if (Player.DEBUG_MY_RECIPES) {
      System.err.println("processed astar in " + (System.currentTimeMillis() - start) + " ms");
    }
    
    if (bestScore != Double.NEGATIVE_INFINITY) {
      if (Player.DEBUG_MY_RECIPES) {
        System.err.println("Best recipe to do "+bestRecipe.debug()+" with path ");
        System.err.println("  > "+bestNodes);
      }
      if (bestNodes.size()> 0) {
        // check if I need to rest ?
        Action nextAction = bestNodes.get(0);
        output = nextAction.output(state);
      } else {
        output = "BREW "+bestRecipe.gameId;
      }
      return true;
    } else {
      return false;
    }
  }
  
  private double scoreRecipe(State state, Recipe recipe, OInv inv) {
    double currentDistance = recipe.distance(inv);
    double currentScore = 20 * recipe.price + 1000 - currentDistance;
    return currentScore;
  }
}
