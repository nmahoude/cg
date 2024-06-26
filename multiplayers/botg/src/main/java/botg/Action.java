package botg;

import botg.units.Hero;
import trigonometry.Point;

public class Action {
  public static final Action CHOOSE_HULK = new Action("HULK");
  public static final Action WAIT = new Action("WAIT");
  public static final Action ATTACK_NEAREST_HERO = new Action("ATTACK_NEAREST HERO");
  public static final Action ATTACK_NEAREST_UNIT = new Action("ATTACK_NEAREST UNIT");
  public static final Action CHOOSE_IRONMAN = new Action("IRONMAN");
  public static final Action CHOOSE_STRANGE = new Action("DOCTOR_STRANGE");
  
  private final String actionStr;
  private final String message;

  public Action(String actionStr) {
    this(actionStr, "");
  }

  public Action(String actionStr, String message) {
    this.actionStr = actionStr;
    this.message = message;
  }

  @Override
  public String toString() {
    if (!"".equals(message.trim())) {
      return actionStr+";"+message;
    } else {
      return actionStr;
    }
  }

  public static Action attackNearest(String unitType) {
    return new Action("ATTACK_NEAREST "+unitType);
  }

  public static Action attack(int unitId) {
    return new Action("ATTACK "+unitId);
  }

  public static Action moveTo(Point pos) {
    return new Action("MOVE "+State.trans(pos.x)+" "+pos.y);
  }

  public static Action on(String actionName, Point target) {
    return new Action(actionName+" "+State.trans(target.x)+" "+target.y);
  }

  public static Action on(String actionName, int unitId) {
    return new Action(actionName+" "+unitId);
  }

  public static Action sell(State state, Hero hero, Item item) {
    state.gold += item.cost / 2;
    if (!item.isPotion) {
      hero.items.remove(item);
    }
    return new Action("SELL "+item.name);
  }

  public static Action moveAndAttack(int unitId, Point pos) {
    return new Action("MOVE_ATTACK "+State.trans(pos.x)+" "+pos.y+" "+unitId);
  }
  
  public static Action buy(State state, Hero hero, Item item) {
    state.gold -= item.cost;
    if (!item.isPotion) {
      hero.addItem(item);
    }

    return new Action("BUY " + item.name, "buy item !");
  }
  
}
