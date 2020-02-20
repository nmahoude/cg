package tron;

public class Action {
  Agent agent;
  int moveIndex;

  public String toOutput() {
    switch (moveIndex) {
    case 0:
      return "RIGHT";
    case 1:
      return "DOWN";
    case 2:
      return "LEFT";
    case 3:
      return "UP";
    default:
      return "Damn!";
    }
  }
}
