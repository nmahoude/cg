package sg22.ais;

import java.util.ArrayList;
import java.util.List;

import sg22.State;
import sg22.Actions.Action;

public class HardCodedAI {

  public List<Action> actions = new ArrayList<>();
  public HardCodedAI() {
    
    actions.add(Action.from("MOVE 5"));
    actions.add(Action.from("CONTINUOUS_INTEGRATION 8"));
    actions.add(Action.from("MOVE 6"));
    actions.add(Action.from("CODE_REVIEW"));
    actions.add(Action.from("RELEASE 13"));
    actions.add(Action.from("MOVE 0"));
    actions.add(Action.from("TRAINING"));
    actions.add(Action.from("MOVE 2"));
    actions.add(Action.from("DAILY_ROUTINE"));
    actions.add(Action.from("MOVE 4 5"));
    actions.add(Action.from("CONTINUOUS_INTEGRATION 8"));
    actions.add(Action.from("RELEASE 12"));
    actions.add(Action.from("MOVE 5"));
    actions.add(Action.from("TRAINING"));
    actions.add(Action.from("CONTINUOUS_INTEGRATION 5"));
    actions.add(Action.from("RELEASE 19"));
    actions.add(Action.from("MOVE 2"));
    actions.add(Action.from("DAILY_ROUTINE"));
    actions.add(Action.from("WAIT"));
    actions.add(Action.from("MOVE 6 5"));
    actions.add(Action.from("CONTINUOUS_INTEGRATION 8"));
    actions.add(Action.from("RELEASE 14"));
    actions.add(Action.from("MOVE 0"));
    actions.add(Action.from("TRAINING"));
    actions.add(Action.from("DAILY_ROUTINE"));
    actions.add(Action.from("MOVE 6 5"));
    actions.add(Action.from("MOVE 7 6"));
    actions.add(Action.from("GIVE 4"));
    actions.add(Action.from("WAIT"));
    actions.add(Action.from("RELEASE 15"));




  }
  
  
  public Action think(State state) {
    Action action = actions.remove(0);
    return action;
  }
}
