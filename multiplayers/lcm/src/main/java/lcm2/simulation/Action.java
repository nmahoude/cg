package lcm2.simulation;

import java.io.PrintStream;

import lcm2.Agent;
import lcm2.CardType;
import lcm2.cards.Card;
import lcm2.cards.CardModel;

public class Action {
  private static final Action END_TURN = new Action() {
    {
      this.type = ActionType.PASS;
    }
  };

  static final Action summonActions[] = new Action[20];
  static final Action attackActions[] = new Action[21*20];
  static final Action useActions[] = new Action[21*20];
  static {
    for (int from=0;from<20;from++) {
      
      Action summon = new Action();
      summon.type = ActionType.SUMMON;
      summon.from = from;
      summon.target = -1;
      summonActions[from] = summon;
      
      for (int target=-1;target<20;target++) {
        Action use = new Action();
        use.type = ActionType.USE;
        use.from = from;
        use.target = target;        
        useActions[21 * from + (target+1)] = use;
        
        Action a = new Action();
        a.type = ActionType.ATTACK;
        a.from = from;
        a.target = target;
        attackActions[21 * from + (target+1)] = a;

      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }
  
  
  
  public ActionType type = ActionType.PASS;
  public int from = -1;
  public int target = -1;
  String message = "";
  
  public static Action pass() {
    return END_TURN;
  }
  public static Action endTurn() {
    return END_TURN;
  }

  public static Action pick(int index) {
    Action a = new Action();
    a.type = ActionType.PICK;
    a.from = index;
    return a;
  }
  
  public static Action summon(int from) {
    return summonActions[from];
  }
  
  public static Action use(int from, int target) {
    return useActions[21 * from + (target+1)];
  }
  
  public static Action use(int from) {
    return useActions[21 * from + 0];
  }

  public static final Action attack(final int from, final int target) {
    return attackActions[21 * from + (target+1)];
  }
  
  public void print(Agent player, Agent opp, PrintStream os) {
    switch(type) {
    case ATTACK:
      os.print("ATTACK " + player.boardCards[from].model.instanceId + " " +(target == -1 ? -1 : opp.boardCards[target].model.instanceId) + " " + message);
      break;
    case PASS:
      os.print("PASS " + message );
      break;
    case SUMMON:
      os.print("SUMMON " + player.handCards[from].model.instanceId +" " + message);
      break;
    case USE:
      CardModel myCardModel = player.handCards[from].model;
      CardModel targetCardModel;
      
      if (target == -1) {
        targetCardModel = null;
      } else if (myCardModel.type == CardType.ITEM_GREEN) {
        targetCardModel = player.boardCards[target].model;
      } else {
        targetCardModel = opp.boardCards[target].model;
      }
      
      os.print("USE " + myCardModel.instanceId +" " + (target == -1 ? -1 : targetCardModel.instanceId) + " " + message);
      break;
    case PICK:
      os.print("PICK " + from +" " + message);
      break;
    default:
      os.print("UNKOWN ACTION" + type +" "+message);
      break;
    }
  }

  public Action message(String msg) {
    this.message = msg;
    return this;
  }

  @Override
  public java.lang.String toString() {
    return type.name()+ "("+from+","+target+")";
  }
}
