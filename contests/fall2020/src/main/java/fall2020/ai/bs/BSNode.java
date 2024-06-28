package fall2020.ai.bs;

import java.util.ArrayList;
import java.util.List;

import fall2020.Player;
import fall2020.Spell;
import fall2020.State;
import fall2020.optimizer.Action;
import fall2020.optimizer.OInv;
import fall2020.optimizer.ORecipe;
import fall2020.optimizer.OSpell;
import fall2020.optimizer.OptiGraph;

public class BSNode implements Comparable {
  public static final int RECIPE_BASE_MASK = 48;

  static double deval[] =new double[100];
  static {
    for (int i=0;i<deval.length;i++) {
      deval[i] = Math.pow(0.95, i);
    }
  }

  static final int[] tomesMask = new int[6];
  static final int[] invTomesMask = new int[6];
  static final int[] allOverMask = new int[6];
  static final int[] allUnderMask = new int[6];
  static final int allTomesMask;
  static final int invAllTomesMask;

  
  static final long[] recipeMask = new long[6];
  static final long[] recipeInvMask = new long[6];

  static {
      tomesMask[0] = 0b1111;
      tomesMask[1] = 0b1111_0000;
      tomesMask[2] = 0b1111_0000_0000;
      tomesMask[3] = 0b1111_0000_0000_0000;      
      tomesMask[4] = 0b1111_0000_0000_0000_0000;
      tomesMask[5] = 0b1111_0000_0000_0000_0000_0000;

      invTomesMask[0] = 0b1111_1111_1111_1111_1111_0000;
      invTomesMask[1] = 0b1111_1111_1111_1111_0000_1111;
      invTomesMask[2] = 0b1111_1111_1111_0000_1111_1111;
      invTomesMask[3] = 0b1111_1111_0000_1111_1111_1111;
      invTomesMask[4] = 0b1111_0000_1111_1111_1111_1111;
      invTomesMask[5] = 0b0000_1111_1111_1111_1111_1111;
      
      allOverMask[0] = 0b1111_1111_1111_1111_1111_1111;
      allOverMask[1] = 0b1111_1111_1111_1111_1111_0000;
      allOverMask[2] = 0b1111_1111_1111_1111_0000_0000;
      allOverMask[3] = 0b1111_1111_1111_0000_0000_0000;
      allOverMask[4] = 0b1111_1111_0000_0000_0000_0000;
      allOverMask[5] = 0b1111_0000_0000_0000_0000_0000;

      allUnderMask[0] = 0b0000_0000_0000_0000_0000_0000;
      allUnderMask[1] = 0b0000_0000_0000_0000_0000_1111;
      allUnderMask[2] = 0b0000_0000_0000_0000_1111_1111;
      allUnderMask[3] = 0b0000_0000_0000_1111_1111_1111;
      allUnderMask[4] = 0b0000_0000_1111_1111_1111_1111;
      allUnderMask[5] = 0b0000_1111_1111_1111_1111_1111;

      
      
      
      allTomesMask = 0b1111_1111_1111_1111_1111_1111;
      invAllTomesMask = ~allTomesMask;
      
      
      for (int i=0;i<6;i++) {
        recipeMask[i] = 1L << (RECIPE_BASE_MASK+i); 
        recipeInvMask[i] = ~recipeMask[i];
      }
  };
  public final static long allSpellsMask=0b11111111111111111111111111111111111111111111111L;
  
  
  
  public BSNode parent;
  public OInv inv;
  public Action fromAction;
  long castables; /// 47-0 
  long bitSet1; /// recipables(<< 54 - 48) + knownSpells(<<47-0)
  public double score;
  public double fitnesse;

  public int brews;    /// <6
  public int spellsCost; /// 24-0
  
  private void copyFrom(BSNode parent) {
    this.parent = parent;
    
    this.inv = null;
    this.fromAction = null;
    this.score = parent.score;
    
    this.castables = parent.castables;
    this.bitSet1 = parent.bitSet1;
    this.brews = parent.brews;
    this.fitnesse = 0.0;
    this.spellsCost = parent.spellsCost;
  }
  
  
  public int expand(State state, BSNode[] childs, final int currentChildsFE) {
    if (this.brews == 6) {
      return currentChildsFE;
    }

    int childsFE = currentChildsFE;
    
    childsFE = calculateBrews(state, childs, childsFE);
    childsFE = calculateSpellsCasts(childs, childsFE);
    childsFE = addRest(childs, childsFE);
    childsFE = calculateLearns(state, childs, childsFE);
    
    return childsFE;
  }


  private int calculateLearns(State state, BSNode[] childs, int childsFE) {
    for (int t=0;t<state.tomesFE;t++) {
      int costOfTome = spellsCost >> 4*t & 0b1111; // récupérer le prix du spell
      
      if (this.inv.inv[0] < costOfTome) {
        continue;
      }
      
      Spell tome = state.tomes[t];
      
      BSNode child = childs[childsFE++];
      child.copyFrom(this);

      
      child.bitSet1 |= tome.spell.mask;

      if (t == 0) {
        child.score += 0.0000001 * (100-BeamSearch.currentDepth); // priorise l'apprentissage du 1er spell puisqu'il est gratuit et peut vite disparaitre
      }
      
      child.spellsCost= (spellsCost & BSNode.allOverMask[t]) << 4 & BSNode.allTomesMask// on décale tous les prix au dessus 
          | (spellsCost & BSNode.allUnderMask[t])  // on remet les prix du dessous inchangé
          | BSNode.tomesMask[t]; // on met le prix à 15 pour ne plus l'acheter
      
      
      OInv newInvAfterBuy= OptiGraph.getInventory(this.inv.inv[0]-costOfTome, this.inv.inv[1], this.inv.inv[2], this.inv.inv[3]);
      int maxTax = Math.min(tome.taxCount, (10-newInvAfterBuy.allTierTotal));
      child.inv = OptiGraph.getInventory(newInvAfterBuy.inv[0]+maxTax, newInvAfterBuy.inv[1], newInvAfterBuy.inv[2], newInvAfterBuy.inv[3]);
      
      child.fromAction = child.inv.learnActions[t];
      child.updateFitnesse();
    }
    return childsFE;
  }


  private int addRest(BSNode[] childs, int childsFE) {
    if (this.castables != 0) {
      BSNode child = childs[childsFE++];
      child.copyFrom(this);

      child.fromAction = inv.rest;
      child.inv = this.inv;
      child.castables = 0;
      child.updateFitnesse();
      
    }
    return childsFE;
  }


  private int calculateSpellsCasts(BSNode[] childs, int childsFE) {
    for (Action action : this.inv.casts) {
      OSpell spell = action.spell;
      if ((this.bitSet1 & spell.mask) == 0) /*unknwon spell*/ continue; 
      if ((this.castables & spell.mask) != 0) /* deja casté? */ continue;
      
      BSNode child = childs[childsFE++];
      child.copyFrom(this);

      child.fromAction = action;
      child.inv = action.to;
      child.castables |= action.spell.mask;
      child.updateFitnesse();
    }
    return childsFE;
  }


  private int calculateBrews(State state, BSNode[] childs, int childsFE) {
    for (int r=0;r<state.recipesFE;r++) {
      ORecipe recipe = state.recipes[r].recipe;
      
      Action brewAction = inv.recipeActions[recipe.id];
      
      if (brewAction != null) {
        boolean isRecipeAvailable = (this.bitSet1 & recipeMask[r]) != 0;
        if (isRecipeAvailable) {
          BSNode child = childs[childsFE++];
          child.copyFrom(this);
          
          child.fromAction = brewAction;
          child.inv = this.inv.brew(recipe);
          child.bitSet1 = this.bitSet1 & recipeInvMask[r];
          
          child.score += Player.recipesValueAtDepth[r][BeamSearch.currentDepth];
          child.brews++;
          if (child.brews == 6) {
            child.score += child.inv.tier1plusTotal;
          }
          child.updateFitnesse();
        }
      }
    }
    return childsFE;
  }


	public void updateFitnesse() {
	  /*
	  1 point for each score point
	  1 points for each tier 0 ingredient in my inventory
	  2 points for each tier 1 ingredient in my inventory
	  3 points for each tier 2 ingredient in my inventory
	  4 points for each tier 3 ingredient in my inventory
	  1.1 point for each crafted potion
	  0.5 point for each learned spells
	  */
	  
	  this.fitnesse = parent.fitnesse / 0.95 + 
	          0.0
  	      + 1* this.score 
  	      + 1* inv.allTierWeightedTotal
  	      + 1.1 * brews
  	      + 0.5 * Long.bitCount(bitSet1 & allSpellsMask & ~castables )
	      ;
	  
	  
	  // is it the end or should I brew another ?
	  if (this.brews == 6) {
	    this.fitnesse += 0.0 + inv.tier1plusTotal - BeamSearch.currentDepth * 100;
	    if (this.score < BeamSearch.state.agents[1].score + BeamSearch.state.agents[1].inv.tier1plusTotal) {
	      this.fitnesse += -1_000_000;
	    } else {
	      if (this.score < BeamSearch.state.agents[1].score + BeamSearch.state.agents[1].inv.tier1plusTotal + 2) {
	        this.fitnesse += 100_000;
	      } else {
	        this.fitnesse += 1_000_000;
	      }
	    }
    } else if (BeamSearch.currentDepth == BeamSearch.maxDepth) {
      // on ne fait pas le dernier brew, mais la game est terminée (surement que lui finit)
      this.fitnesse += 0.0 + 50 * inv.tier1plusTotal;
    } else {
//        int totalDist = 1;
//        int totalPrice = 0;
//        for (int r = 0; r < 5; r++) {
//            boolean isRecipeAvailable = (this.bitSet1 & recipeInvMask[r]) != 0;
//            if (isRecipeAvailable) {
//                int distance = inv.recipeDistance[BeamSearch.state.recipes[r].recipe.id];
//                if (distance < 100) {
//                    totalDist += distance;
//                    totalPrice += Player.recipesValueAtDepth[r][BeamSearch.currentDepth+distance];
//                }
//            }
//        }
//        this.fitnesse += 0.0 + 1.0 * totalPrice / totalDist;
    }
		if (fitnesse > BeamSearch.bestFitnesse) {
		  BeamSearch.bestFitnesse = fitnesse;
		  BeamSearch.bestNode = this;

		  // debugNode();
		}
	}

	private void debugNode() {
    BSNode best = this;
    List<Action> actions = new ArrayList<>();
    while (best.parent != BeamSearch.root) {
      actions.add(0, best.fromAction);
      best = best.parent;
    }
    actions.add(0, best.fromAction);
    for (Action action : actions) {
      System.err.println(action.debug(BeamSearch.state, BeamSearch.state.agents[0]));
    }
    
    System.err.println("new best :"+fitnesse+"(depth="+BeamSearch.currentDepth+") => "+actions);
	}
	
	@Override
	public String toString() {
	  return fromAction.toString()+" => "+fitnesse;
	}


  @Override
  public int compareTo(Object o) {
    if (((BSNode)o).fitnesse < fitnesse)
      return -1;      
    else if (((BSNode)o).fitnesse > fitnesse)
      return 1;  
    else 
      return 0;
  }
}
