package fall2020.optimizer;

import java.util.ArrayList;
import java.util.List;

public class OInv {
  public Action rest = new Action(this, this, null, null, Action.REST, 0);
  public Action wait = new Action(this, this, null, null, Action.WAIT, 0);
  public final int id;
  
  public final int optiIndex;
  public final int[] inv = new int[4];
  public final int allTierTotal;
  public final double allTierWeightedTotal;
  public final int tier1plusTotal;
  
  public List<Action> casts = new ArrayList<>();
  public List<Action> reverseCasts = new ArrayList<>();
  
  public List<Action> brews = new ArrayList<>();
  public int[] recipeDistance = new int[OptiGraph.orecipes.length];
  public Action[] recipeActions = new Action[OptiGraph.orecipes.length];
  public int[] missingIngredients = new int[OptiGraph.orecipes.length];
  public long brewableMask = 0;
  
  public Action[] learnActions = new Action[6];
  
  
  public OInv(int optiIndex, int a, int b, int c, int d) {
    super();
    this.id = OptiGraph.OINV_ID++;
    this.optiIndex = optiIndex;
    this.inv[0] = a;
    this.inv[1] = b;
    this.inv[2] = c;
    this.inv[3] = d;
    this.tier1plusTotal = inv[1]+inv[2]+inv[3];
    this.allTierTotal = inv[0] + tier1plusTotal;
    this.allTierWeightedTotal = a/2.0+1*b+2*c+3*d;
    
    for (int i=0;i<recipeDistance.length;i++) {
      recipeDistance[i] = Integer.MAX_VALUE;
    }
    for (int i=0;i<6;i++) {
      learnActions[i] = new Action(this, this, null, null, Action.LEARN, i);
    }
  }
  
  public boolean canCast(OSpell spell) {
    for (Action action : casts) {
      if (action.spell == spell) return true;
    }
    return false;
  }

  public OInv cast(OSpell spell) {
    for (Action action : casts) {
      if (action.spell == spell && action.times == 1) return action.to;
    }
    
    return null;
  }

  public final boolean canBrew(final ORecipe recipe) {
    return (brewableMask & recipe.mask) != 0;
  }

  public OInv brew(ORecipe targetRecipe) {
    for (Action action : brews) {
      if (action.recipe == targetRecipe) return action.to;
    }
    System.err.println("Can't find recipe with  "+targetRecipe+" from "+this);
    return null;
  }

  @Override
  public String toString() {
    return String.format("I#%d[%d %d %d %d]", id, inv[0], inv[1], inv[2], inv[3]);
  }
  
  public final Action rest() {
    return rest;
  }
  public Action doWait() {
    return wait;
  }

  public int minDistanceTo(ORecipe recipe) {
    return recipeDistance[recipe.id];
  }
}
