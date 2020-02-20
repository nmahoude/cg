package calm;

import java.util.Scanner;

public class Player {
  State state = new State();
  Agent me = new Agent();
  Agent him = new Agent();
  private int turnsRemaining;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    Player player = new Player();
    player.play(in);
  }
  
  public void play(Scanner in) {
    state.readInit(in);

    while (true) {
      readTurn(in);

      System.out.println("WAIT");
    }
  }

  private void readTurn(Scanner in) {
    turnsRemaining = in.nextInt();
    me.read(in);
    him.read(in);
    
    state.read(in);
  }
}
