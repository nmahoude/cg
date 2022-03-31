package searchrace;

import java.util.Scanner;

public class SimplePlayer {
  public static long start;
  public static int turn;
  
  MC ai = new MC();
  State state = new State();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    new SimplePlayer().play(in);
  }

  private void play(Scanner in) {

    State.readInit(in);

    while (true) {
      turn++;
      
      state.read(in);
      
      updateTimer();
      
      ai.think(state);
      
      output();
    }
  }

  private void output() {
    System.out.println("EXPERT "+ai.bestAngle+" "+ai.bestThrust);
  }

  private void updateTimer() {
    start = System.currentTimeMillis();
    if (turn == 1) {
      start -= 500;
    }
  }
}
