package fall2020.astar;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import fall2020.Agent;
import fall2020.Player;
import fall2020.Spell;
import fall2020.State;
import fall2020.optimizer.Action;
import fall2020.optimizer.OInv;
import fall2020.optimizer.ORecipe;
import fall2020.optimizer.OSpell;
import fall2020.optimizer.OptiGraph;

public class AStar {
  static class AStarNode {
  	OInv inv;
  	int id;
  	
  	public AStarNode(OInv inv) {
  		this.inv = inv;
  		this.id = inv.id;
		}
  	
  	public AStarNode(int id) {
  		this.id = id;
		}
  }

  static BasicScorer basicScorer = new BasicScorer();
  
  long castablesAtStart;
  AStarNode[] nodes = new AStarNode[1010];
  
  private List<Action> actions = new ArrayList<>();
  private static List<Action> fullActions = new ArrayList<>();

  
  AStarNode[] cameFrom = new AStarNode[1020];
  int[] gScore = new int[1020];
  int[] fScore = new int[1020];
  long[] castables = new long[1020];
  long[] possibleSpells = new long[1020];
  Action learnActions[] = new Action[6];
  
  PriorityQueue<AStarNode> openSet = new PriorityQueue<>((o1, o2) -> Integer.compare(fScore[o1.id], fScore[o2.id]));

  private double bestScore;

  private AStarResult result;

  private ORecipe goal;


  public AStar() {
  	for (int i=0;i<1001;i++) {
  		nodes[i] = new AStarNode(OptiGraph.invs[i]);
  	}
  	nodes[1001] = new AStarNode(1001); // learns
  	nodes[1002] = new AStarNode(1002); // learns
  	nodes[1003] = new AStarNode(1003); // learns
  	nodes[1004] = new AStarNode(1004); // learns
  	nodes[1005] = new AStarNode(1005); // learns
  	nodes[1006] = new AStarNode(1006); // learns
  	nodes[1007] = new AStarNode(1007); // learns
  	
  	for (int i=0;i<6;i++) {
  		learnActions[i] = new Action(null, null, null, null, Action.LEARN, i);
  	}
  }
  
  public AStarResult process(AStarResult result, State state, Agent agent, ORecipe goal) {
    basicScorer
      .withMaxDepth(100 - Player.turn)
      .withAllowedDepthMargin((s, recipe) -> 0)
      .withAStarScoreFunction(BasicScorer.BASIC_SCORE_FUNCTION)
      .withRecipeScoreFunction(BasicScorer.BASIC_RECIPE_FUNCTION);
    
  	return process(result, state, agent, goal, basicScorer);
  }
  
  public AStarResult process(AStarResult result, State state, Agent agent, ORecipe goal, BasicScorer scorer) {
    init(agent.inv, agent.castedSpells, agent.knownSpells, goal);
    return process(result, state, goal, scorer);
  }
  
  public AStarResult process(AStarResult result, State state, OInv inv, long castedSpells, long knownSpells, ORecipe goal, BasicScorer scorer) {
    init(inv, castedSpells, knownSpells, goal);
    return process(result, state, goal, scorer);
  }

  private AStarResult process(AStarResult result, State state, ORecipe goal, BasicScorer scorer) {
    this.result = result; // hold temporary
    this.goal = goal; // hold temporary
    
    int maxDepth = scorer.maxDepth();
  	bestScore = Double.NEGATIVE_INFINITY;
    
    while(!openSet.isEmpty()) {
      AStarNode current = openSet.poll();
      if (fScore[current.id] > maxDepth ) {
      	return solutions();
      }
      
      if (current.inv.canBrew(goal)) {
      	OInv brewed = current.inv.brew(goal);
      	double score = scorer.score(fScore[current.id]/*depth*/, goal, brewed, castables[current.id], possibleSpells[current.id]);
      	if (Player.DEBUG_BESTPATH) {
  				System.err.println("Score is "+score);
        	System.err.println("  "+reconstructPath(current));
      	}
      	maxDepth = Math.min(maxDepth, fScore[current.id] + scorer.allowedDepthMargin(state, goal)); // allow some margin
      	if (score > bestScore) {
      		bestScore = score;
      		reconstructPath(current);
      		copyActualPathToBestPath(current.inv, brewed);
      	}
        //return reconstructPath(current);
      }
      
    	for (int t=0;t< state.tomesFE;t++) {
    		Spell tome =  state.tomes[t];
    		if ((possibleSpells[current.id] & tome.spell.mask) != 0) {
    			continue; // already known
    		}
    		if (t > current.inv.inv[0]) {
    			// can't buy it
    			continue;
    		}
    		int nextId = 1001+t;

    		//On change d'inv potentiellement parce qu'on doit payer !!!
    		//et qu'on peut aussi recuperer des tier0 !!!!
    		int cost =t;
    		OInv newInvAfterBuy= OptiGraph.getInventory(current.inv.inv[0]-cost, current.inv.inv[1], current.inv.inv[2], current.inv.inv[3]);
    		int maxTax = Math.min(tome.taxCount, (10-newInvAfterBuy.allTierTotal));
    		OInv newInvAfterTaxCount = OptiGraph.getInventory(newInvAfterBuy.inv[0]+maxTax, newInvAfterBuy.inv[1], newInvAfterBuy.inv[2], newInvAfterBuy.inv[3]);
    		
    		int tentativeGScore = gScore[current.id] + 1;
    		if (tentativeGScore < gScore[nextId]) {
      		castables[nextId] = castables[current.id];
      		possibleSpells[nextId] = possibleSpells[current.id] | tome.spell.mask;
      		gScore[nextId] = tentativeGScore;
          fScore[nextId] = gScore[nextId] + /* h*/ newInvAfterTaxCount.recipeDistance[goal.id];
      		
      		
      		// cost 1, node(inv) don't change
					cameFrom[nextId] = current;
					nodes[nextId].inv = newInvAfterTaxCount;
					
          // force reinsertion with correct order
					openSet.remove(nodes[nextId]);
					openSet.add(nodes[nextId]);
    		}
    	}

      
      for (Action action : current.inv.casts) {
        OSpell spell = action.spell;
        
        if ((spell.mask & possibleSpells[current.id]) == 0) {
        	// pas le spell, check if we can learn it  !
        	continue;
        } else {
        	// on a le spell
	        int tentativeGScore;
	        if ((castables[current.id] & spell.mask) != 0) {
	          // déjà casté, il faut prévoir le rest !
	          tentativeGScore = gScore[current.id] + 2; // need a rest !
	        } else {
	          tentativeGScore = gScore[current.id] + 1;
	        }
	        
	        int neighborId = action.to.id;
	        if (tentativeGScore < gScore[neighborId]) {
	          // c'est mieux !
	          
	
	          if ((castables[current.id] & spell.mask) != 0) {
	            castables[neighborId] = spell.mask; // need a rest so set it as casts, but remove all
	          } else {
	            castables[neighborId] = castables[current.id] | spell.mask; // set it as casts, keep others
	          }
	          
	          cameFrom[neighborId] = current;
	          possibleSpells[neighborId] = possibleSpells[current.id]; // ecraser les spells connus
	          gScore[neighborId] = tentativeGScore;
	          fScore[neighborId] = gScore[neighborId] + /* h*/ action.to.recipeDistance[goal.id];
	          
	          // force reinsertion with correct order
	          openSet.remove(nodes[action.to.id]);
						openSet.add(nodes[action.to.id]);
	        }
        }
      }
    }
    
    if (Player.DEBUG_BESTPATH) {
      System.err.println("Solutions best score is "+bestScore);
    }
    return solutions();
  }

	private AStarResult solutions() {
    if (bestScore != Double.NEGATIVE_INFINITY) {
      result.reconstructFullPath(castablesAtStart, goal);
      if (Player.DEBUG_BESTPATH) {
        System.err.println("--------------------------------");
      	System.err.println("Best solution : "+bestScore);
      	System.err.println(result.fullActions);
      }
			return result;
    } else {
    	return null; // not found
    }
	}

  private void copyActualPathToBestPath(OInv current, OInv invAfterBrewed) {
  	result.tempActions.clear();
  	result.tempActions.addAll(actions);
  	result.invAfterBrewed = invAfterBrewed;
    result.castedSpellsAfterBrew = castables[current.id];
    result.knownSpellsAfterBrew = possibleSpells[current.id];
  }

	private void init(OInv startingInv, long startingCastables, long startingSpells, ORecipe recipe) {
    openSet.clear();
    openSet.add(nodes[startingInv.id]);
    
    
    for (int i=0;i<cameFrom.length;i++) {
      gScore[i] = Integer.MAX_VALUE;
      fScore[i] = Integer.MAX_VALUE;
    }

    cameFrom[startingInv.id] = null;
    castablesAtStart = startingCastables;
    castables[startingInv.id] = startingCastables;
    possibleSpells[startingInv.id] = startingSpells;
    gScore[startingInv.id] = 0;
    fScore[startingInv.id] = startingInv.recipeDistance[recipe.id];
  }

	private List<Action> reconstructPath(AStarNode current) {
		AStarNode last = current;
    
    AStarNode prev;
    actions.clear();
    while(cameFrom[current.id]!= null) {
      prev = cameFrom[current.id];
      if (current.id >= 1001) {
      	// check the learn !
      	actions.add(0, learnActions[current.id-1001]);
      } else {
	      for (Action action : current.inv.reverseCasts) {
	        if (action.from == prev.inv && (possibleSpells[current.id] & action.spell.mask) != 0) {
	          actions.add(0, action);
	          break;
	        }
	      }
      }
      current = prev;
    }
    return actions;
	}
}
