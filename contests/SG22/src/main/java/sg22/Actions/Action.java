package sg22.Actions;

import sg22.Application;
import sg22.Cards;

public class Action {
  private static final int ROOT = -1;

  public static final int WAIT = 0;
  public static final int MOVE = 1;
  public static final int GIVE = 2;
  public static final int RELEASE = 4;
  public static final int THROW = 5;
  public static final int MOVE_AND_GET = 6;

  public static final int PLAY_TRAINING = 1_0;
  public static final int PLAY_CODING = 1_1;
  public static final int PLAY_DAILY = 1_2;
  public static final int PLAY_TASK_PRIORIZATION = 1_3;
  public static final int PLAY_ARCHI = 1_4;
  public static final int PLAY_CI = 1_5;
  public static final int PLAY_CODE_REVIEW = 1_6;
  public static final int PLAY_REFACTORING = 1_7;

  public static final int RANDOM = 99;

      
  
  public int type;
  public int target;
  public int target2;
  
  public Action moveTo(int zoneId) {
    type = MOVE;
    this.target = zoneId;
    this.target2 = zoneId;
    return this;
  }
  
  public Action moveToAndGet(int zoneId, int cardIndex) {
    type = MOVE_AND_GET;
    this.target = zoneId;
    this.target2 = cardIndex;
    return this;
  }
  

  public void root() {
    type = ROOT;
  }

  public Action give(int cardIndex) {
    type = GIVE;
    target = cardIndex;
    return this;
  }

  public Action doWait() {
    type = WAIT;
    return this;
  }

  public Action release(Application a) {
    type = RELEASE;
    target = a.id;
    return this;
  }

  public Action release(int id) {
    type = RELEASE;
    target = id;
    return this;
  }

  @Override
  public String toString() {
    switch(type) {
    case WAIT : return "WAIT";
    case MOVE : return "MOVE "+target;
    case MOVE_AND_GET : return "MOVE "+target+" "+target2;
    case GIVE : return "GIVE "+target+" ("+Cards.toName(target)+")";
    case PLAY_TRAINING: return "TRAINING";
    case PLAY_CODING: return "CODING";
    case PLAY_DAILY: return "DAILY_ROUTINE";
    case PLAY_TASK_PRIORIZATION : return "TASK_PRIORITIZATION "+target+" "+target2+" ("+Cards.toName(target)+")";
    case PLAY_ARCHI : return "ARCHITECTURE_STUDY";
    case PLAY_CI : return "CONTINUOUS_INTEGRATION "+target+" ("+Cards.toName(target)+")";
    case PLAY_CODE_REVIEW : return "CODE_REVIEW";
    case PLAY_REFACTORING : return "REFACTORING";
    case RELEASE : return "RELEASE "+target;
    case THROW : return "THROW "+target;
    case RANDOM : return "RANDOM";
    }
    
    return "??? " + type;
  }

  public Action throwCard(int cardIndex) {
    type = THROW;
    target = cardIndex;
    return this;
  }
  
  public Action training() {
    type = PLAY_TRAINING;
    return this;
  }

  public Action coding() {
    type = PLAY_CODING;
    return this;
  }
  
  public Action daily() {
    type = PLAY_DAILY;
    return this;
  }
  
  public Action taskPriorization(int discard, int get) {
    type = PLAY_TASK_PRIORIZATION;
    target = discard;
    target2 = get;
    return this;
  }

  public Action archi() {
    type = PLAY_ARCHI;
    return this;
  }

  public Action ci(int cardIndexToAutomate) {
    type = PLAY_CI;
    target = cardIndexToAutomate;
    return this;
  }
  
  public Action codeReview() {
    type = PLAY_CODE_REVIEW;
    return this;
  }
  
  public Action refactoring() {
    type = PLAY_REFACTORING;
    return this;
  }

  public Action random() {
    type = RANDOM;
    return this;
  }

  public boolean isRandom() {
    return type == RANDOM;
  }

  public boolean isWait() {
    return type == WAIT;
  }

  public boolean isRelease() {
    return type == RELEASE;
  }

  public boolean isGive() {
    return type == GIVE;
  }

  public boolean isThrow() {
    return type == THROW;
  }

  public boolean isPlay() {
    return type >= PLAY_TRAINING && type <= PLAY_REFACTORING;
  }

  public boolean isTraining() {
    return type == PLAY_TRAINING;
  }

  public boolean isCoding() {
    return type == PLAY_CODING;
  }

  public boolean isCI() {
    return type == PLAY_CI;
  }
  
  
  public static Action from(String input) {
    String[] split = input.split(" ");
    int param1;
    if (split.length >= 2) {
      try {
        param1 = Integer.parseInt(split[1]);
      } catch(Exception e) {
        param1 = -1;
      }
    } else {
      param1 = -1;
    }
    int param2;
    if( split.length >= 3 ) {
      try {
        param2 = Integer.parseInt(split[2]);
      } catch(Exception e) {
        param2 = -1;
      }
    } else {
      param2 = param1;
    }
    
    
    Action action = new Action();
    switch (split[0].trim()) {
    case "MOVE" : 
      action.moveToAndGet(param1, param2);
      break;
    case "WAIT":
      action.doWait();
      break;
    case "RELEASE":
      action.release(param1);
      break;
    case "GIVE":
      action.give(param1);
      break;
    case "TRAINING":
      action.training();
      break;
    case "CODING":
      action.coding();
      break;
    case "DAILY_ROUTINE":
      action.daily();
      break;
    case "TASK_PRIORITIZATION":
      action.taskPriorization(param1, param2);
      break;
    case "ARCHITECTURE_STUDY":
      action.archi();
      break;
    case "CONTINUOUS_INTEGRATION":
      action.ci(param1);
      break;
    case "CODE_REVIEW":
      action.codeReview();
      break;

    case "REFACTORING":
      action.refactoring();
      break;

    case "THROW":
      action.throwCard(param1);
      break;
    case "RANDOM":
      action.random();
      break;
    }

    return action;
  }
}
