package fall2020.optimizer;

import fall2020.Agent;
import fall2020.State;

public class Action {
  public static final int WAIT = 0;
  public static final int REST = 1;
  public static final int CAST = 2;
  public static final int BREW = 3;
  public static final int LEARN = 4;
  
  public final OInv from;
  public final OInv to;
  public final OSpell spell;
  public final ORecipe recipe;
  public final int type; // TODO enum
  public final int times;
  
  public Action(OInv from, OInv to, OSpell spell, ORecipe recipe, int type, int times) {
    super();
    this.from = from;
    this.to = to;
    this.spell = spell;
    this.recipe = recipe;
    this.type = type;
    this.times = times;
  }
  
  public String debug(State state, Agent agent) {
    switch(type) {
    case BREW:
      if (recipe == null) {
        return "B#"+state.recipes[times];
      } else {
        return "B#"+state.findRecipeId(recipe);
      }
    case CAST:
      
      String spellId = agent.findSpellId(spell);
      if ("-1".equals(spellId)) {
        spellId = "T#"+spell.id;
      }
      return "C#"+spellId +"*"+times;
    case REST:
      return "Rest";
    default:
      return "LEARN "+state.tomes[times];
    }
  }
  
  public String output(State state) {
    switch(type) {
    case BREW:
      if (recipe == null) {
        return "BREW "+state.recipes[times].gameId;
      } else {
        return "BREW "+state.findRecipeId(recipe);
      }
    case CAST:
      return "CAST "+state.agents[0].findSpellId(spell)+" "+times+ (times > 1 ? " *! " : "");
    case LEARN:
      return "LEARN "+state.tomes[times].gameId;
    case REST:
      return "REST";
    case WAIT:
      return "WAIT REALLY?";
    default:
      return "LEARN "+state.tomes[times].gameId;
    }
  }
  
  @Override
  public String toString() {
    switch(type) {
    case BREW:
      if (recipe == null) {
        return "BREW #"+times;
      } else {
        return ""+recipe;
      }
    case CAST:
      return ""+spell +"*"+times;
    case REST:
      return "Rest";
    case LEARN:
      return "Learn #"+times;
    default:
      return "Wait ";
    }
  }
}
