package fall2020.optimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fall2020.Agent;
import fall2020.State;
import fall2020.astar.AStar;
import fall2020.astar.AStarResult;

public class OptiGraph {
  public static int OINV_ID = 0;
  public static int OSPELL_ID = 0;
  public static int ORECIPE_ID = 0;
  
  public static OSpell ospells[] = new OSpell[] {
      //   always
      new OSpell(2, 0, 0, 0, false),
      new OSpell(-1, 1, 0, 0, false),
      new OSpell(0, -1, 1, 0, false),
      new OSpell(0, 0, -1, 1, false),
      
      //   to learn
      new OSpell(-3, 0, 0, 1), // 0
      new OSpell(3, -1, 0, 0),
      new OSpell(1, 1, 0, 0),
      new OSpell(0, 0, 1, 0),
      new OSpell(3, 0, 0, 0),
      new OSpell(2, 3, -2, 0),
      new OSpell(2, 1, -2, 1),
      new OSpell(3, 0, 1, -1),
      new OSpell(3, -2, 1, 0),
      new OSpell(2, -3, 2, 0),
      new OSpell(2, 2, 0, -1),//10
      new OSpell(-4, 0, 2, 0),
      new OSpell(2, 1, 0, 0),
      new OSpell(4, 0, 0, 0),
      new OSpell(0, 0, 0, 1),
      new OSpell(0, 2, 0, 0),
      new OSpell(1, 0, 1, 0),
      new OSpell(-2, 0, 1, 0),
      new OSpell(-1, -1, 0, 1),
      new OSpell(0, 2, -1, 0),
      new OSpell(2, -2, 0, 1),//20
      new OSpell(-3, 1, 1, 0),
      new OSpell(0, 2, -2, 1),
      new OSpell(1, -3, 1, 1),
      new OSpell(0, 3, 0, -1),
      new OSpell(0, -3, 0, 2),
      new OSpell(1, 1, 1, -1),
      new OSpell(1, 2, -1, 0),
      new OSpell(4, 1, -1, 0),
      new OSpell(-5, 0, 0, 2),
      new OSpell(-4, 0, 1, 1),//30
      new OSpell(0, 3, 2, -2),
      new OSpell(1, 1, 3, -2),
      new OSpell(-5, 0, 3, 0),
      new OSpell(-2, 0, -1, 2),
      new OSpell(0, 0, -3, 3),
      new OSpell(0, -3, 3, 0),
      new OSpell(-3, 3, 0, 0),
      new OSpell(-2, 2, 0, 0),
      new OSpell(0, 0, -2, 2),
      new OSpell(0, -2, 2, 0),//40
      new OSpell(0, 0, 2, -1),
  };
  
  public static ORecipe orecipes[] = new ORecipe[] {
      new ORecipe(-2, -2, 0, 0, 6),// 42
      new ORecipe(-3, -2, 0, 0, 7),
      new ORecipe(0, -4, 0, 0, 8),
      new ORecipe(-2, 0, -2, 0, 8),
      new ORecipe(-2, -3, 0, 0, 8),
      new ORecipe(-3, 0, -2, 0, 9),
      new ORecipe(0, -2, -2, 0, 10),
      new ORecipe(0, -5, 0, 0, 10),// 49
      new ORecipe(-2, 0, 0, -2, 10),// 50
      new ORecipe(-2, 0, -3, 0, 11),
      new ORecipe(-3, 0, 0, -2, 11),
      new ORecipe(0, 0, -4, 0, 12),
      new ORecipe(0, -2, 0, -2, 12),
      new ORecipe(0, -3, -2, 0, 12),
      new ORecipe(0, -2, -3, 0, 13),
      new ORecipe(0, 0, -2, -2, 14),
      new ORecipe(0, -3, 0, -2, 14),
      new ORecipe(-2, 0, 0, -3, 14),
      new ORecipe(0, 0, -5, 0, 15 ),//60
      new ORecipe(0, 0, 0, -4, 16),
      new ORecipe(0, -2, 0, -3, 16),
      new ORecipe(0, 0, -3, -2, 17),
      new ORecipe(0, 0, -2, -3, 18),
      new ORecipe(0, 0, 0, -5, 20),
      new ORecipe(-2, -1, 0, -1, 9),
      new ORecipe(0, -2, -1, -1, 12),
      new ORecipe(-1, 0, -2, -1, 12),
      new ORecipe(-2, -2, -2, 0, 13),
      new ORecipe(-2, -2, 0, -2, 15),//70
      new ORecipe(-2, 0, -2, -2, 17),
      new ORecipe(0, -2, -2, -2, 19),
      new ORecipe(-1, -1, -1, -1, 12),
      new ORecipe(-3, -1, -1, -1, 14),
      new ORecipe(-1, -3, -1, -1, 16),
      new ORecipe(-1, -1, -3, -1, 18),
      new ORecipe(-1, -1, -1, -3, 20),//77
  };
  
  
		
	public static OInv inventories[] = new OInv[11*11*11*11];
	public static Map<ORecipe, Set<OInv>> recipeEndpoints = new HashMap<>();
	public static OInv invs[] = new OInv[1001];
	
	static {
	  newInit();
	}
	
	static void newInit() {
    System.err.println("Init by visiting");
    long start = System.currentTimeMillis();
    List<OInv> seen = new ArrayList<>();
    List<OInv> toVisit = new ArrayList<>();

    // initialiser les recipes endpoints
    for (ORecipe recipe : orecipes) {
      recipeEndpoints.put(recipe, new HashSet<>());
    }
    
    
    // creer un OInv de démarrage
    OInv inv = getOrCreateInv(0, 0, 0, 0);
    
    toVisit.add(inv);
    
    while (!toVisit.isEmpty()) {
      OInv current = toVisit.remove(0);
      seen.add(current);
  
      for (OSpell spell : ospells) {
        int ii0 = current.inv[0];
        int ii1 = current.inv[1];
        int ii2 = current.inv[2];
        int ii3 = current.inv[3];
        
        
        int times = 1;
        do {
          ii0 = ii0 + spell.delta[0];
          ii1 = ii1 + spell.delta[1];
          ii2 = ii2 + spell.delta[2];
          ii3 = ii3 + spell.delta[3];
          
          
          if (   ii0 >= 0 && ii1 >=0 && ii2 >=0 && ii3>=0 
              && ii0+ii1+ii2+ii3<=10) {
            
            OInv newInv = getOrCreateInv(ii0, ii1, ii2, ii3);
            if (!seen.contains(newInv) && !toVisit.contains(newInv)) {
              toVisit.add(newInv);
            }
            Action action = new Action(current, newInv, spell, null, Action.CAST, times++);
            current.casts.add(action);
            newInv.reverseCasts.add(action);
          } else {
            break;
          }
          if (!spell.repeatable) break;
        } while(true);           
        
      }

      for (ORecipe recipe : orecipes) {
        if (current.inv[0] < -recipe.delta[0]) continue;
        if (current.inv[1] < -recipe.delta[1]) continue;
        if (current.inv[2] < -recipe.delta[2]) continue;
        if (current.inv[3] < -recipe.delta[3]) continue;

        
        
        int ii0 = current.inv[0] + recipe.delta[0];
        int ii1 = current.inv[1] + recipe.delta[1];
        int ii2 = current.inv[2] + recipe.delta[2];
        int ii3 = current.inv[3] + recipe.delta[3];
        
        
        if (   ii0 >= 0 && ii1 >=0 && ii2 >=0 && ii3>=0) {
          OInv newInv = getOrCreateInv(ii0, ii1, ii2, ii3);
          if (!seen.contains(newInv) && !toVisit.contains(newInv)) {
            toVisit.add(newInv);
          }
          Action action = new Action(current, newInv, null, recipe, Action.BREW, 0);
          current.brews.add(action);
          current.brewableMask |=recipe.mask;
          current.recipeActions[recipe.id] = action;
          recipeEndpoints.get(recipe).add(current);
        }
      }
    }
    
    calculateDistances();
    calculateSpellScore();
    
    
    System.err.println("SIZE is "+seen.size());
    System.err.println("OptiGraph: init in "+(System.currentTimeMillis()-start));
	}
	
	
	private static void calculateDistances() {
	  for(ORecipe recipe : orecipes) {
	    for (OInv inv : invs) {
	      inv.missingIngredients[recipe.id]= 
	          Math.max(-recipe.delta[0] - inv.inv[0], 0)
            + Math.max(-recipe.delta[1] - inv.inv[1], 0)
            + Math.max(-recipe.delta[2] - inv.inv[2], 0)
            + Math.max(-recipe.delta[3] - inv.inv[3], 0)
	          ;  
	    }
	    
	    
	    
	    Set<OInv> toVisit = new HashSet<>();
      Set<OInv> toVisitFuture = new HashSet<>();
      Set<OInv> temp;
      
	    toVisit.addAll(recipeEndpoints.get(recipe));
	    int depth = 0;

	    while (!toVisit.isEmpty()) {
  	    for (OInv visit : toVisit) {
  	      if (visit.recipeDistance[recipe.id] <= depth) continue;
  	      
  	      visit.recipeDistance[recipe.id] = depth;
  	      for (Action neighbors : visit.reverseCasts) {
  	        toVisitFuture.add(neighbors.from);
  	      }
  	    }
  	    temp = toVisit;
  	    toVisit = toVisitFuture;
  	    toVisitFuture = temp;
  	    toVisitFuture.clear();
  	    depth++;
	    }
	  }
  }


  private static OInv getOrCreateInv(int ii0, int ii1, int ii2, int ii3) {
	  int newIndex = ii0 + ii1*11+ ii2*11*11 +ii3*11*11*11;
	  if (inventories[newIndex] != null) return inventories[newIndex];
	  
	  OInv inv = new OInv(newIndex, ii0, ii1, ii2, ii3);
	  inventories[newIndex] = inv;
	  invs[inv.id]= inv;
    return inv;
  }



	public static int getSpellOptiIndex(int[] inv) {
		for (int s=0;s<ospells.length;s++) {
			OSpell spell = ospells[s];
      if (inv[0] == spell.delta[0] && inv[1] == spell.delta[1] && inv[2] == spell.delta[2] && inv[3] == spell.delta[3]) {
				return s;
			}
		}
		System.err.println("/!\\ Spell inconnu "+ Arrays.toString(inv));
		return -1;
	}
	public static int getRecipeOptiIndex(int[] inv) {
		for (int s=0;s<orecipes.length;s++) {
		  ORecipe recipe = orecipes[s];
			if (inv[0] == recipe.delta[0] && inv[1] == recipe.delta[1] && inv[2] == recipe.delta[2] && inv[3] == recipe.delta[3]) {
				return s;
			}
		}
    System.err.println("/!\\ Recipe inconnu "+ Arrays.toString(inv));
    return -1;
	}

  public static ORecipe getRecipe(int a, int b, int c, int d) {
    for (int s=0;s<orecipes.length;s++) {
      ORecipe recipe = orecipes[s];
      if (a == recipe.delta[0] && b == recipe.delta[1] && c == recipe.delta[2] && d == recipe.delta[3]) {
        return recipe;
      }
    }
    System.err.println("/!\\ Recipe inconnu "+ a+" "+b+" "+c+" "+d);
    return null;
  }

  public static OSpell getSpell(int a, int b, int c, int d) {
    for (int s=0;s<ospells.length;s++) {
      OSpell spell = ospells[s];
      if (a == spell.delta[0] && b == spell.delta[1] && c == spell.delta[2] && d == spell.delta[3]) {
        return spell;
      }
    }
    System.err.println("/!\\ Recipe inconnu "+ a+" "+b+" "+c+" "+d);
    return null;
  }

	static void testIt() {
		System.out.println("It works !");
	}
	
	public static void main(String[] args) {
		OptiGraph.testIt();
	}
  
  public static int getInventoryIndex(int[] inv) {
    return inv[0] + inv[1]*11+ inv[2]*11*11 +inv[3]*11*11*11;
  }
  public static int[] indexToInv(int index) {
    int[] inv = new int[4];
    inv[3] = index / (11*11*11);
    inv[2] = (index - inv[3]*11*11*11) / (11*11);
    inv[1] = (index - inv[3]*11*11*11 - inv[2]*11*11) / 11;
    inv[0] = (index - inv[3]*11*11*11 - inv[2]*11*11 - inv[1]*11);
    return inv;
  }
  public static OInv getInventory(int[] inv) {
    return inventories[getInventoryIndex(inv)];
  }
  public static OInv getInventory(int invIndex) {
    return inventories[invIndex];
  }
  public static OInv getInventory(int a, int b, int c, int d) {
    return inventories[a + b*11+ c*11*11 + d*11*11*11];
  }


  // TODO not working, comment descendre le gradient de distance sans connaitre tous les sorts possibles ? A* non ???
  public static int findReadDistanceTo(OInv inv, long spellsMask, ORecipe recipe) {
    OInv current = inv;

    int depth = 0;
    int currentDistance = 100;
    while ((currentDistance = current.recipeDistance[recipe.id]) != 0) {
      boolean found = false;
      for (Action action : current.casts) {
        if ((action.spell.mask & spellsMask) == 0) continue; // je l'ai pas
        if (action.to.recipeDistance[recipe.id] < currentDistance) {
          current = action.to;
          depth++;
          found = true;
          break;
        }
      }
      if (!found) break;
    }

    if (current.recipeDistance[recipe.id] == 0) {
      return depth;
    } else {
      return Integer.MAX_VALUE;
    }
  }


  private static void calculateSpellScore() {
    int scores[] = new int[ospells.length];
    
    for (ORecipe recipe : orecipes) {
      for (OInv endpoint : recipeEndpoints.get(recipe)) {
        for (Action action : endpoint.reverseCasts) {
          scores[action.spell.id]++;
        }
      }
    }
    
    for (OSpell spell : ospells) {
      spell.score = scores[spell.id];
    }    
  }
  
  static void calculateSpellScore2() {
    // pour chaque recette
    // pour chaque inv
    // prendre en compte les spells pour arriver à la recette en partant d'inv

    AStar astar = new AStar();
    Agent agent = new Agent();
    agent.castedSpells = 0;
    agent.knownSpells = 0b1111111111111111111111111111111111111111111111111111111111111111L;// all spells
    
    int spellUsed[] = new int[ospells.length];
    
    for (ORecipe recipe : orecipes) {
      System.err.println("Calculating recipe "+recipe);
      for (OInv inv : invs) {
        System.err.println("Starting @ "+inv);
        agent.inv = inv;

        AStarResult result = new AStarResult();
        astar.process(result, new State(), agent, recipe);
        if (result.fullActions == null) {
          System.err.println("Can't go from "+inv+" to "+recipe);
        } else {
          for (Action action : result.fullActions) {
            if (action.spell == null) continue;
            spellUsed[action.spell.id]++;
          }
        }
      }
    }    
    
    System.err.println("Use in shorted path : ");
    for (OSpell spell: ospells) {
      System.err.println(""+spell+" => "+spellUsed[spell.id]);
    }
    
  }
  
  public static void showSpellScores() {

    for (ORecipe recipe : orecipes) {
      System.err.println("Recette : "+recipe+" a "+recipeEndpoints.get(recipe).size()+" endpoints");
    }
    System.err.println("Nombre de spell décisifs / spell pour faire une recette");
    for (OSpell spell : ospells) {
      System.err.println(""+spell+" => "+spell.score);
    }
  }
}
