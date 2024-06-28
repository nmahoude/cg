package fall2020.ai;

import java.util.ArrayList;
import java.util.List;

import fall2020.Player;
import fall2020.Recipe;
import fall2020.State;
import fall2020.astar.AStar;
import fall2020.astar.AStarResult;
import fall2020.astar.BasicScorer;
import fall2020.astar.RecipeScorer;
import fall2020.astar.ScoreFunction;
import fall2020.optimizer.Action;

public class AI {
  private AStar astar = new AStar();

  
  static final ScoreFunction SCORE_MAXIMIZE_TIER1PLUS = (depth, recipe, brew, castedSpells, possibleSpells) -> brew.tier1plusTotal;

  
  
  static final RecipeScorer RECIPE_QUICKEST = (recipe, actions, invAfterBrew) -> 100-actions.size();
  static final RecipeScorer RECIPE_BESTPRICE_PLUS_TIER1 = (recipe, actions, invAfterBrew) -> {
                                                                                    return recipe.price + invAfterBrew.tier1plusTotal;
                                                                                  };

  private String output;
  private State state;
  private int makingPotion;

  private BasicScorer scorer = new BasicScorer();
  
  public String think(State state) {

    this.state = state;

    // default scorer
    scorer
      .withMaxDepth(100 - Player.turn)
      .withAllowedDepthMargin((s,r) -> 2)
      .withAStarScoreFunction((depth, recipe, resultAfteBrew, castedSpells, knownSpells) -> {
        return -depth - Long.bitCount(castedSpells) + 0.5 * Long.bitCount(knownSpells);
      })
      .withRecipeScoreFunction((recipe, actions, invAfterBrew) -> {
        double score =  1.0 * recipe.price + 0.1*invAfterBrew.allTierTotal - 2* actions.size();
        return score;
      });

    
    //endGameScorer(state);
    
    output = null;
    if (findViaAstar()) {
      return output + " "+state.recipes[makingPotion];
    }

    // rest ?
    return "REST";
  }

  BasicScorer endGameScorer(State state) {
    
    if (state.agents[0].isLastRecipe() && state.agents[1].isLastRecipe()) {
      System.err.println("Both can do last recipe");
      if (state.agents[0].score < state.agents[1].score) {
        System.err.println("  I'm ahead ");
      } else {
        System.err.println("  I'm behind");
      }
    } else if (state.agents[0].isLastRecipe()) {
      System.err.println("I Can do last recipe, but not him");
      if (state.agents[0].score < state.agents[1].score) {
        System.err.println("But I'm late");
        System.err.println("    so make the greatest recipe with keeping max inv");
        System.err.println("    I have time");
        scorer
          .withMaxDepth(100-Player.turn)
          .withAllowedDepthMargin((s,r) -> 10)  
          .withAStarScoreFunction(SCORE_MAXIMIZE_TIER1PLUS) // still maximise tier1
          .withRecipeScoreFunction(RECIPE_BESTPRICE_PLUS_TIER1); // quickiest
        
      } else {
        System.err.println("I'm ahead can he beats me with inv ?");
        System.err.println(" My score : "+state.agents[0].score);
        System.err.println(" His score : "+state.agents[1].score);
        int delta = state.agents[0].score - state.agents[1].score;
        System.err.println(" Delta = "+delta);
        if (delta > 10) {
          System.err.println("I'm far ahead, do quickest recipe");
          scorer
            .withMaxDepth(100-Player.turn) // take your time
            .withAllowedDepthMargin((s, r) -> 0)  // no margin
            .withAStarScoreFunction(SCORE_MAXIMIZE_TIER1PLUS) // still maximise tier1
            .withRecipeScoreFunction(RECIPE_QUICKEST); // quickiest
          
        } else {
          System.err.println("I'm not enough far ahead, brew enough points");
          scorer
          .withMaxDepth(100-Player.turn) // take your time
          .withAllowedDepthMargin((s,r) -> 0)  // no margin
          .withAStarScoreFunction((depth, recipe, brew, castedSpell, possibleSpells) -> brew.tier1plusTotal) // still maximise tier1
          .withRecipeScoreFunction((recipe, actions, invAfterBrew) -> {
            if (recipe.price + delta < 10) {
              return recipe.price + invAfterBrew.tier1plusTotal; // not enough to be sure TODO il ne pourra pas forcement faire 10 !
            }
            return 100-actions.size(); // price is enough do this one
          });
        }
      }
    } else if (state.agents[1].isLastRecipe()){
      System.err.println("He can do Last recipe !");
      int hisBest = 100;
      int myBest = 100;
      for (int i=0;i<5;i++) {
        if (hisBest > Player.oppDistanceToRecipes[i]) hisBest = Player.oppDistanceToRecipes[i];
        if (myBest > Player.myDistanceToRecipes[i]) myBest = Player.myDistanceToRecipes[i];
      }
      System.err.println("My best recipe in "+myBest);
      System.err.println("Vs his best in "+hisBest);

      boolean timeToBrew = false;
      for (int i=0;i<5;i++) {
        if (hisBest >= Player.myDistanceToRecipes[i]) {
          timeToBrew = true;
          System.err.println("I have time to do "+state.recipes[i]+" but I need to maximize");
        }
      }      
        
      
      
      if (timeToBrew) {
        System.err.println("I can do another recipe first, so maximize it too ");
        final int hisMargin = hisBest;
        scorer
          .withMaxDepth(hisBest)
          .withAllowedDepthMargin((s,r) -> hisMargin)
          .withAStarScoreFunction((depth, recipe, brew, castedSpell, possibleSpells) -> brew.tier1plusTotal)
          .withRecipeScoreFunction((recipe, actions, invAfterBrew) -> recipe.price);
      } else {
        System.err.println("I need to maximize tier1+ to try to be ahead");
      }
    }
    
    return scorer;
  }

  AStarResult result = new AStarResult();
  AStarResult result2 = new AStarResult();

  private boolean findViaAstar() {
    long start = System.currentTimeMillis();

    List<Action> bestNodes = new ArrayList<>();
    double bestScore = Double.NEGATIVE_INFINITY;
    Recipe bestRecipe= null;
    
    List<Action> firstActions = new ArrayList<>();
    double[] scores= new double[6];
    for (int i=0;i<state.recipesFE;i++) {
      if (Player.DEBUG_MY_RECIPES) {
        System.err.println("Pass 1 : Looking for recipe : "+state.recipes[i].debug());
        System.err.println("---------------------------");
      }
      Recipe recipe = state.recipes[i];
      final int myMarginForThisRecipe = Math.max(0, Player.oppDistanceToRecipes[i] - Player.myDistanceToRecipes[i]);
      if (Player.DEBUG_MY_RECIPES) {
        System.err.println("   My margin is "+myMarginForThisRecipe);
      }
      
      scorer.withAllowedDepthMargin((s,r) -> myMarginForThisRecipe);
      
      
      AStarResult result = astar.process(new AStarResult(), state, state.agents[0], recipe.recipe, scorer);
      
      if (result != null && result.fullActions != null) {
        if (Player.DEBUG_MY_RECIPES) {
          System.err.println("Calculating score : ");
          System.err.println(" length = "+result.fullActions.size());
          System.err.println(" inv after best brew "+result.invAfterBrewed);
          System.err.println(" path is "+result.fullActions);
        }        
        double score = scorer.scoreRecipe(recipe, result.fullActions, result.invAfterBrewed);
        
        if (Player.DEBUG_MY_RECIPES) {
          System.err.println("  > Found a solution in path length "+result.fullActions.size()+" >score = "+score +" reevulated score :"+(score * state.recipeScore[i]));
          System.err.println("  > "+result.fullActions);
        }
        score *= state.recipeScore[i];
        
//        if (state.agents[0].brewedRecipe+1 < 6) {
//          double bestScore2 = Double.NEGATIVE_INFINITY;
//          for (int r2=0;r2<state.recipesFE;r2++) {
//            if (r2 == i) continue;
//            Recipe recipe2 = state.recipes[r2];
//            
//            astar.process(result2, state, 
//                  result.invAfterBrewed,result.castedSpellsAfterBrew, result.knownSpellsAfterBrew, 
//                  recipe2.recipe, scorer);
//            double score2 = scorer.scoreRecipe(recipe2, result2.fullActions, result2.invAfterBrewed);
//
//            if (Player.DEBUG_MY_RECIPES) {
//              System.err.println("     > Recipe 2 : "+recipe2+
//                                        " length="+ result2.fullActions.size()+
//                                        ">score = "+score2+
//                                        " totalScore = "+(score+score2));
//              //System.err.println("     > "+result2.fullActions);
//            }
//            if (score2 > bestScore2) {
//              bestScore2 = score2;
//            }
//          }
//          score+=bestScore2;
//        }        
        
        int index = firstActions.indexOf(result.fullActions.get(0));
        if (index == -1) {
          firstActions.add(result.fullActions.get(0));
          index= firstActions.indexOf(result.fullActions.get(0));
          scores[index] = score;
        } else {
          scores[index]=Math.max(scores[index] , score);
        }
      }
    }

    bestScore = Double.NEGATIVE_INFINITY;
    Action bestAction = null;
    for (int s=0;s<firstActions.size();s++) {
      double score = scores[s]; 
      if (score > bestScore) {
        bestScore = score;
        bestAction = firstActions.get(s);
      }
    }

    
    
    if (Player.DEBUG_MY_RECIPES) {
      System.err.println("processed astar in " + (System.currentTimeMillis() - start) + " ms");
    }

    if (state.agents[0].spellsFE < 10 && state.tomesFE > 0 && bestAction.type != Action.LEARN) {
      output = "LEARN " + state.tomes[0].gameId + " OUTL";
      return true;
    }
    if (bestAction != null) {
      output = bestAction.output(state);
    } else {
      output = "BREW "+bestRecipe.gameId;
    }
    return true;
    
//    if (bestScore != Double.NEGATIVE_INFINITY) {
//      if (Player.DEBUG_MY_RECIPES) {
//        System.err.println("Best recipe to do "+bestRecipe.debug()+" with path ");
//        System.err.println("  > "+bestNodes);
//      }
//      if (bestNodes.size()> 0) {
//        // check if I need to rest ?
//        OAction nextAction = bestNodes.get(0);
//        
//        // Learn a book TODO obviously needs to be redone
//        if (state.agents[0].spellsFE < 10 && state.tomesFE > 0 && nextAction.type != OAction.LEARN) {
//          output = "LEARN " + state.tomes[0].gameId + " OUTL";
//        }
//        else if (false && state.agents[0].spellsFE < 10 && state.tomesFE > 0 && nextAction.type != OAction.LEARN) {
//          int chosen=0;
//          for (int t=0;t<Math.min(state.tomesFE, state.agents[0].inv.inv[0]+1);t++) {
//            if (!state.tomes[t].spell.repeatable /* only + */ && state.tomes[t].spell.allTier1plus > 0) {
//              chosen = t;
//              break;
//            }
//          }
//          output = "LEARN " + state.tomes[chosen].gameId + " OUTL" + (chosen != 0 ? "WOW" : "");
//        } else {
//          output = nextAction.output(state);
//        }
//      } else {
//        output = "BREW "+bestRecipe.gameId;
//      }
//      return true;
//    } else {
//      return false;
//    }
  }
  
}
