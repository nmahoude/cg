package spring2022._cgfx;

import static spring2022._cgfx.Mapper.map;

import javafx.scene.paint.Color;
import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022._cgfx.components.S22GameViewer;

public class ActionDrawer {

  
  public static void drawAction(S22GameViewer viewer, Hero hero, Action action, Pos targetPos) {
//  action.type = Action.TYPE_MOVE;
//  action.target.copyFrom(100, 100);
//  action.targetEntity = 150;
  if (targetPos == null) targetPos = Pos.VOID;
  
  if (action.type == Action.TYPE_MOVE) {
    viewer.board().drawLine(Color.GREEN, 3, map(hero.pos) , map(action.target));
    viewer.board().drawCircle(Color.GREEN, map(action.target), State.MONSTER_TARGET_KILL);
    
  } else if (action.type == Action.TYPE_WIND) {
    viewer.board().drawLine(Color.GREEN, 1, map(hero.pos) , map(action.target));
  } else if (action.type == Action.TYPE_CONTROL) {
    viewer.board().drawLine(Color.GREEN, 1, map(targetPos) , map(action.target));
  } else if (action.type == Action.TYPE_SHIELD) {
      viewer.board().drawCircle(Color.GREEN, map(targetPos), 150);
      viewer.board().drawCircle(Color.GREEN, map(targetPos), 170);
      viewer.board().drawCircle(Color.GREEN, map(targetPos), 190);
  }

  }
}
