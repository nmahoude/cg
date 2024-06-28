package fall2020.astar;

import java.util.ArrayList;
import java.util.List;

import fall2020.optimizer.Action;
import fall2020.optimizer.OInv;
import fall2020.optimizer.ORecipe;

public class AStarResult {
  List<Action> tempActions = new ArrayList<>();
  public OInv invAfterBrewed;
  public List<Action> fullActions = new ArrayList<>();
  public long castedSpellsAfterBrew;
  public long knownSpellsAfterBrew;
  
  
  public void reconstructFullPath(long castablesAtStart, ORecipe goal) {
    fullActions.clear();
    long currentCastables = castablesAtStart;
    for (Action action : tempActions) {
      if (action.spell == null/*learn*/) {
        fullActions.add(action);
      } else if ((currentCastables & action.spell.mask) != 0) {
        fullActions.add(action.from.rest());
        fullActions.add(action);
        currentCastables = action.spell.mask;
      } else {
        currentCastables |= action.spell.mask;
        fullActions.add(action);
      }
    }
    fullActions.add(new Action(null, null, null, goal, Action.BREW, 0));
  }

}
