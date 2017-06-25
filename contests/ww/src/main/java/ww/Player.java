package ww;

import java.util.Scanner;

public class Player {
  static GameState state = new GameState();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    state.readInit(in);

    int round = 0;
    int s = 0;
    // game loop
    while (true) {
      state.readRound(in);
      state.toTDD();
      round++;

      Node node = new Node();
      node.calculateChilds(state);
      
      System.out.println(node.bestAction);
      s++;
    }
  }
}