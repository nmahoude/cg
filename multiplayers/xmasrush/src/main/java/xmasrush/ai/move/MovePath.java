package xmasrush.ai.move;

import java.util.ArrayList;
import java.util.List;

import xmasrush.Cell;
import xmasrush.Pos;

public class MovePath {
  Cell currentCell;
  int collectedItems;
  int steps;
  
  List<Cell> path = new ArrayList<>();
  List<Pos> questItems = new ArrayList<>();

  
  public static MovePath createFrom(MovePath oldMp, Pos questItemsPicked, List<Cell> path) {
    MovePath mp = new MovePath();
    mp.currentCell = path.get(path.size() - 1);
    mp.path.clear();
    mp.path.addAll(oldMp.path);
    if (mp.path.size() > 1) {
      mp.path.remove(mp.path.size()-1); // remove last entry as it will be the same as new path
    }
    mp.path.addAll(path);
    mp.collectedItems = oldMp.collectedItems+1;
    mp.steps = oldMp.steps + path.size();
    mp.questItems.clear();
    mp.questItems.addAll(oldMp.questItems);
    mp.questItems.remove(questItemsPicked);
    
    return mp;
  }
  
  @Override
  public String toString() {
    return "collect "+collectedItems+" in "+steps+" steps "+path;
  }
}
