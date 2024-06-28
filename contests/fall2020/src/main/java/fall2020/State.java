package fall2020;

import java.io.IOException;

import fall2020.fast.FastReader;
import fall2020.optimizer.ORecipe;

public class State {
  public Recipe recipes[] = new Recipe[5];
  public double[] recipeScore = new double[5];
  public int recipesFE = 0;
  
  public Spell tomes[] = new Spell[6];
  public int tomesFE = 0;
  
  public Agent agents[] = new Agent[2];

  public State() {
  	for (int i=0;i<recipes.length;i++) {
  		recipes[i] = new Recipe();
  		recipeScore[i] = 1.0;
  	}
  	for (int i=0;i<tomes.length;i++) {
  		tomes[i] = new Spell();
  	}
    agents[0] = new Agent();
    agents[1] = new Agent();
  }

  public void reset() {
    
    recipesFE = 0;
    tomesFE = 0;
    agents[0].spellsFE = 0;
    agents[0].castedSpells= 0;
    agents[0].knownSpells= 0;
    agents[1].spellsFE = 0;
    agents[1].castedSpells= 0;
    agents[1].knownSpells= 0;
  }

  public void read(FastReader in) throws IOException {
    int actionCount = in.nextInt();
    System.err.println("READ turn "+Player.turn);
    if (Player.turn == 1) {
      Player.start = System.currentTimeMillis()+800;
    } else {
      Player.start = System.currentTimeMillis();
    }

    if (Player.DEBUG_INPUT) {
      System.err.println("\""+actionCount+"\"+EOF+");
    }
    reset();

    for (int i = 0; i < actionCount; i++) {
      int actionId = in.nextInt();
      byte actionType = in.next();

      if ('B'/* BREW */ == actionType) {
        Recipe recipe = recipes[recipesFE++];
        recipe.gameId = actionId;
        recipe.read(in);
      } else {

        if ('C'/* CAST */ == actionType) {
        	agents[0].readSpell(in, actionId, actionType);
        } else if ('O'/* OPPONENT_CAST */ == actionType) {
        	agents[1].readSpell(in, actionId, actionType);
        } else if ('L'/* "LEARN" */ == actionType) {
          Spell spell = tomes[tomesFE++];
          spell.gameId = actionId;
          spell.type = actionType;
					spell.read(in);
        }
      }
    }

    for (int i = 0; i < 2; i++) {
      agents[i].read(in);
    }
    
    if (isOppLastRecipe()) {
      System.err.println("/!\\/!\\/!\\ Last recipe");
    }
    
  }

  private boolean isOppLastRecipe() {
    return agents[1].isLastRecipe();
  }

  public String findRecipeId(ORecipe recipe) {
    for (int i=0;i<recipesFE;i++) {
      if (recipes[i].recipe == recipe) return ""+recipes[i].gameId;
    }
    System.err.println("/!\\ Can't find real recipe from template");
    return "-1";
  }

  public Recipe findRecipe(ORecipe recipe) {
    for (int i=0;i<recipesFE;i++) {
      if (recipes[i].recipe == recipe) return recipes[i];
    }
    return null;
  }
}
