package coif;

import java.util.Scanner;

public class Agent {
  public Pos HQ;
  public int gold;
  public int income;
  public int unitsCount;
  
  Agent(Pos HQ) {
    this.HQ = HQ;
  }
  
  public void read(Scanner in) {
    unitsCount = 0;
    
    gold = in.nextInt();
    income = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.println(String.format("%d %d", gold, income));
    }
  }
}
