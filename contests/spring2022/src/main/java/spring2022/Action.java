package spring2022;

import spring2022.ag.AGSolution;

public class Action {
  public static final int TYPE_WAIT = 0;
  public static final int TYPE_MOVE = 1;
  public static final int TYPE_WIND = 2;
  public static final int TYPE_SHIELD = 3;
  public static final int TYPE_CONTROL = 4;
  public static final Action WAIT = new Action(TYPE_WAIT, Pos.VOID, -1);
  
  public int type;
  public final Pos target = new Pos();
  public int targetEntity;
  
  public Action() {
    type= TYPE_WAIT;
  }

  public Action(int type, Pos target, int id) {
    this.type= type;
    this.target.copyFrom(target);
    this.targetEntity = id;
  }

  public Action(int type, int x, int y, int id) {
    this.type= type;
    this.target.x = x;
    this.target.y = y;
    this.targetEntity = id;
  }

  @Override
  public String toString() {
    switch(type) {
    case TYPE_WAIT : return "WAIT";
    case TYPE_MOVE : return "MOVE "+target.output();
    case TYPE_WIND : return "SPELL WIND "+target.output();
    case TYPE_SHIELD : return "SPELL SHIELD "+targetEntity;
    case TYPE_CONTROL : return "SPELL CONTROL "+targetEntity + " "+target.output();
      
    default: return "NOT_IMPLEMENTED";
    }
    
  }


  public void moveTo(Pos target) {
    this.type = Action.TYPE_MOVE;
    this.target.copyFrom(target);
  }


  public void moveTo(int x, int y) {
    this.type = Action.TYPE_MOVE;
    this.target.x = x;
    this.target.y = y;
  }


  public void doWait() {
    this.type = TYPE_WAIT;
  }
  
  public void wind(Pos toward) {
    type = TYPE_WIND;
    target.copyFrom(toward);
  }

  public void control(int id, Pos toward) {
    type = TYPE_CONTROL;
    target.copyFrom(toward);
    targetEntity = id;
  }
  
  public void copyFrom(Action model) {
    this.type = model.type;
    this.target.copyFrom(model.target);
    this.targetEntity = model.targetEntity;
  }

  public void updateFromAGValues(Hero hero, int angle, int speed) {
    if (speed == 0) {
      this.copyFrom(WAIT);
    } else if (speed > 0) {
      this.type = TYPE_MOVE;
      this.target.x = Math.max(0, Math.min(State.WIDTH, hero.pos.x + (int)(speed * AGSolution.cos[angle])));
      this.target.y = Math.max(0, Math.min(State.HEIGHT, hero.pos.y + (int)(speed * AGSolution.sin[angle])));
    } else if (speed == -1) {
      this.type = TYPE_WIND;
      if (angle == -1) {
        this.target.x = State.oppBase.x;
        this.target.y = State.oppBase.y;
      } else {
        this.target.x = hero.pos.x + (int)(1600 * AGSolution.cos[angle]);
        this.target.y = hero.pos.y + (int)(1600 * AGSolution.sin[angle]);
      }
    } else if (speed == -666) {
      this.type = TYPE_CONTROL;
      this.targetEntity = angle;
      this.target.copyFrom(State.oppBase);
    } else if (speed == -9000) {
      this.type = TYPE_SHIELD; // autoshield
      this.targetEntity = hero.id;
    }
    
  }

  public static Action doMove(Pos pos) {
    return newAction(TYPE_MOVE, pos, -1);
  }

  public static Action doMove(int x, int y) {
    return newAction(TYPE_MOVE, new Pos(x,y), -1);
  }
  private static Action newAction(int type, Pos pos, int id) {
    Action action = ActionPool.get();
    action.type = type;
    action.target.copyFrom(pos);
    action.targetEntity = id;
    return action;
  }

  private static Action newAction(int type, int x, int y, int id) {
    Action action = ActionPool.get();
    action.type = type;
    action.target.x = x;
    action.target.y = y;
    action.targetEntity = id;
    return action;
  }

  public static Action doWind(Pos pos) {
    return newAction(TYPE_WIND, pos, -1);
  }

  public static Action doWind(int x, int y) {
    return newAction(TYPE_WIND, x, y, -1);
  }

  public static Action doShield(int id) {
    return newAction(TYPE_SHIELD, Pos.VOID, id);
  }

  public boolean isSpell() {
    return type == TYPE_WIND || type == TYPE_SHIELD || type == TYPE_CONTROL;
  }

  public static Action doControl(int id, Pos pos) {
    return newAction(TYPE_CONTROL, pos, id);
  }

}
