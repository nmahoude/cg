package lcm.sim;

import java.io.PrintStream;
import java.util.List;

import lcm.State;
import lcm.cards.Card;

public class Action {
  private static final Action END_TURN = new Action() {
    {
      this.type = ActionType.PASS;
    }
    
    String debugString(State state) {
      return String.format("Action.pass();");
    }
  };

  static final Action summonActions[] = new Action[20];
  static final Action attackActions[] = new Action[21*20];
  static final Action useActions[] = new Action[21*20];
  static {
    int hash = 0;
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
  
  public static Action summon(Card card) {
    return summon(card.stateIndex);
  }
  public static Action summon(int from) {
    return summonActions[from];
  }
  
  public static Action use(Card from, Card target) {
    return use(from.stateIndex, target.stateIndex);
  }
  
  public static Action use(int from, int target) {
    return useActions[21 * from + (target+1)];
  }
  
  public static Action use(Card from) {
    return use(from.stateIndex);
  }
  public static Action use(int from) {
    return useActions[21 * from + 0];
  }

  public static Action attack(Card from, Card target) {
    return attack(from.stateIndex, target.stateIndex);
  }
  public static final Action attack(final int from, final int target) {
    return attackActions[21 * from + (target+1)];
  }
  
  public void print(State state, PrintStream os) {
    switch(type) {
    case ATTACK:
      os.print("ATTACK " + state.cards[from].id + " " +(target == -1 ? -1 : state.cards[target].id) + " " + message);
      break;
    case PASS:
      os.print("PASS " + message );
      break;
    case SUMMON:
      os.print("SUMMON " + state.cards[from].id +" " + message);
      break;
    case USE:
      os.print("USE " + state.cards[from].id +" " + (target == -1 ? -1 : state.cards[target].id) + " " + message);
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
  
  public java.lang.String toString(State state) {
    return type.name()+ "("+state.cards[from].id+","+(target==-1 ? -1 : state.cards[target].id)+")";
  }

  String debugString(State state) {
    String findex = "state.card("+state.cards[from].id+")";
    String tindex = target == -1 ? "Card.opponent" : "state.card("+state.cards[target].id+")";
    
    switch (type) {
    case ATTACK:
      return String.format("Action.attack(%s,%s)", findex, tindex);
    case SUMMON:
      return String.format("Action.summon(%s)", findex);
    case USE:
      return String.format("Action.use(%s,%s)", findex, tindex);
    case PASS:
    case PICK:
      return "";
    default:
      throw new RuntimeException("unknown type");
    }
  }

  public static void debugAllAction(State state, List<Action> bestActions) {
    System.err.println("    List<Action> actions = new ArrayList<>();");
    for (Action action : bestActions) {
      System.err.println("    actions.add("+action.debugString(state)+");");
    }
  }

}
