package pac.simpleai;

import pac.agents.Pacman;
import pac.map.Pos;
import pac.sim.Action;

public class Order {

  private static final int WAIT   = 0;
  private static final int MOVE   = 1;
  private static final int SPEED  = 2;
  private static final int SWITCH_PAPER = 3;
  private static final int SWITCH_ROCK = 4;
  private static final int SWITCH_SCISSORS = 5;
  
  int action;
  Pos pos;
  String message; 
  public Order() {
  }
  
  public String toOuput(Pacman pacman) {
    switch(action) {
    case 0:
      return "MOVE " + pacman.index + " " + pacman.pos.x + " " + pacman.pos.y + " ?";
    case 1:
      return "MOVE " + pacman.index + " " + pos.x + " " + pos.y + " " + pos + " " + message;
    case 2:
      return "SPEED " + pacman.index + " SPD " + message;
    case 3:
      return "SWITCH " + pacman.index + " PAPER " + message;
    case 4:
      return "SWITCH " + pacman.index + " ROCK " + message;
    case 5:
      return "SWITCH " + pacman.index + " SCISSORS " + message;
    default:
      return "UNKNOWN ORDER " + action;
    }
  }
  
  public void speed() {
    speed("");
  }
  public void doWait() {
    doWait("");
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public void move(Pos p, String message) {
    action = MOVE;
    pos = p;
    setMessage(message);
  }
  public void speed(String message) {
    action = SPEED;
    pos = Pos.INVALID;
    setMessage(message);
  }
  public void doWait(String message) {
    action = WAIT;
    pos = Pos.INVALID;
    setMessage(message);
  }

  public void doSwitch(Action action, String message) {
    switch(action) {
    case SWITCH_PAPER: this.action = SWITCH_PAPER; break;
    case SWITCH_ROCK: this.action = SWITCH_ROCK; break;
    case SWITCH_SCISSOR: this.action = SWITCH_SCISSORS; break;
    }
    setMessage(message);
  }

  public void doSwitch(Action action) {
    doSwitch(action, "");
  }

  public void move(Pos pos) {
    move(pos, "");
  }
}
