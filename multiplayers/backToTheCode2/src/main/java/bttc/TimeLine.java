package bttc;

public class TimeLine {
  private static final int MAX_TURNS = 350;
  public TimeLine() {
    for (int i = 0; i < 350; i++) {
      states[i] = new State();
    }
  }

  State currentState() {
    return states[round];
  }

  State states[] = new State[MAX_TURNS];
  int round; /* starts at 0, contrarily to input */
}

