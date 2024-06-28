package fall2020.ai.mc;

import fall2020.Player;
import fall2020.Recipe;
import fall2020.Spell;
import fall2020.optimizer.Action;
import fall2020.optimizer.OInv;
import fall2020.optimizer.ORecipe;
import fall2020.optimizer.OSpell;
import fall2020.optimizer.OptiGraph;

public class MCNode {

  static final Action brewActions[] = new Action[6];
  static final Action learnActions[] = new Action[6];
  static {
    for (int i=0;i<6;i++) {
      learnActions[i] = new Action(null, null, null, null, Action.LEARN, i);
    }

    for (int i=0;i<5;i++) {
      brewActions[i] = new Action(null, null, null, null, Action.BREW, i);
    }
  }

  static double deval[] =new double[100];
  static {
    for (int i=0;i<deval.length;i++) {
      deval[i] = Math.pow(0.97, i);
    }
  }
  
  
  // temp values
  static Action actions[] = new Action[50];
  static int actionsFE = 0;
  
  
  int turn;
  double score;
  
  OInv inv;
  long castables;
  long knownSpells;
  int recipables;
  int brews = 0;
  Action action;
  public boolean end;
  double fitnesse;

  public void apply(MCNode predecessor, Action action, int depth) {
    this.inv= predecessor.inv;
    this.castables = predecessor.castables;
    this.recipables = predecessor.recipables;
    this.knownSpells = predecessor.knownSpells;
    this.turn = predecessor.turn+1;
    this.brews = predecessor.brews;
    this.score = predecessor.score;
    this.fitnesse = predecessor.fitnesse;
    this.end = false;
    this.action = action;
    
    if (action.type == Action.BREW) {
      Recipe originalRecipe = SimpleMC.state.recipes[action.times];
      
      this.recipables &=~originalRecipe.recipe.mask; // can't do recipe twice
      score +=  originalRecipe.price;
      this.inv = predecessor.inv.brew(originalRecipe.recipe);
      this.brews++;
      if (this.brews == 6) {
        this.end = true;
      }
    } else if (action.type == Action.CAST) {
      this.castables |=(1 << action.spell.id); // can't do spell twice
      this.inv = action.to;
    } else if (action.type == Action.REST) {
      this.castables = 0;
    } else if (action.type == Action.LEARN) {
      // TODO trouver un moyen de décaler les spells (juste chnager les couts ?)
      Spell spell = SimpleMC.state.tomes[action.times];
      this.knownSpells |= spell.spell.mask;
      int cost = action.times;
      OInv newInvAfterBuy= OptiGraph.getInventory(predecessor.inv.inv[0]-cost, predecessor.inv.inv[1], predecessor.inv.inv[2], predecessor.inv.inv[3]);
      int maxTax = Math.min(spell.taxCount, (10-newInvAfterBuy.allTierTotal));
      this.inv = OptiGraph.getInventory(newInvAfterBuy.inv[0]+maxTax, newInvAfterBuy.inv[1], newInvAfterBuy.inv[2], newInvAfterBuy.inv[3]);
    }
  }

  private void calculateFitnesse(int depth, Action action) {
    double localScore = 0.0;

    if (action.type == Action.BREW) {
      localScore += SimpleMC.state.recipes[action.times].price;
      localScore += (SimpleMC.MAX_DEPTH-depth) * 10;
      if (this.end) {
        // TODO regarder si on gagne quand meme (avec le delta score)
        if (this.score + inv.tier1plusTotal < SimpleMC.state.agents[1].score + SimpleMC.state.agents[1].inv.tier1plusTotal) {
          localScore -= 10_000_000; // perdu :/
        } else {
          localScore += 10_000_000; 
        }
      }
    }
    if (!this.end && depth == SimpleMC.MAX_DEPTH-1) {
      // add a fictive brew 
      int minRecipeDist = 100;
      int minRecipePrice = 0;
      for (int r=0;r<SimpleMC.state.recipesFE;r++) {
        Recipe recipe = SimpleMC.state.recipes[r];
        boolean isRecipeAvailable = (this.recipables & recipe.recipe.mask) != 0;
        if (!isRecipeAvailable) continue;
        
        int distToRecipe = this.inv.recipeDistance[recipe.recipe.id];
        if (distToRecipe < minRecipeDist) {
          minRecipeDist = distToRecipe;
          minRecipePrice = recipe.price;
        }
      }
      localScore += minRecipePrice;
    }
    this.fitnesse += deval[depth] * localScore;
    
//    localScore +=0.5*inv.allTierWeightedTotal;
//    
//    int minDistToRecipe = 1000;
//    for (int r=0;r<5;r++) {
//      if ((recipables & (1L << r)) == 0) continue;
//      minDistToRecipe = Math.min(minDistToRecipe, inv.recipeDistance[SimpleMC.state.recipes[r].recipe.id]);
//    }
//    localScore -= 0.1*minDistToRecipe;
//    if (this.end) {
//      localScore += 1_000_000;
//    }
//    
//    this.fitnesse += deval[depth] * localScore;
  }

  public MCNode expand(int depth) {
    actionsFE = 0;
    
    
    // all possible actions
    
    // brew
    for (int r=0;r<SimpleMC.state.recipesFE;r++) {
      ORecipe recipe = SimpleMC.state.recipes[r].recipe;
      
      if ((inv.brewableMask & recipe.mask) != 0) {
        boolean isRecipeAvailable = (this.recipables & recipe.mask) != 0;
        if (isRecipeAvailable) {
          actions[actionsFE++] = brewActions[r];
        }
      }
    }
    
    // spells
    for (Action action : this.inv.casts) {
      OSpell spell = action.spell;
      if ((this.knownSpells & spell.mask) == 0) /*unknwon spell*/ continue; 
      if ((this.castables & spell.mask) != 0) /* deja casté? */ continue;
      
      actions[actionsFE++] = action;
    }
    
    // rest 
    if (this.castables != 0) {
      actions[actionsFE++] = this.inv.rest();
    }
    
    // learns
    for (int t=0;t<SimpleMC.state.tomesFE;t++) {
      if (this.inv.inv[0] < t) {
        break;
      }
      
      Spell tome = SimpleMC.state.tomes[t];
      boolean spellAlreadyKnown = (this.knownSpells & tome.spell.mask) != 0;
      if (spellAlreadyKnown) {
        continue; // already known
      }
      actions[actionsFE++] = learnActions[t];
    }

    
    // TODO diminuer la rest ou des learns via des random.nextDouble() ??
    // chose one action 
    Action chosenAction;
    if (actionsFE == 0) {
      chosenAction = inv.rest();
    } else {
      int index = Player.random.nextInt(actionsFE);
      chosenAction = actions[index];
    }
    
    SimpleMC.nodes[depth+1].apply(this, chosenAction, depth);
    SimpleMC.nodes[depth+1].calculateFitnesse(depth, chosenAction);
    return SimpleMC.nodes[depth+1];
  }

  private void debugNodes(int depth) {
    for (int i=0;i<depth;i++) {
      System.err.print(SimpleMC.nodes[i]+" --> ");
    }
    actions[actionsFE++] = this.inv.rest();
  }

  @Override
  public String toString() {
    return ""+inv;
  }

}
