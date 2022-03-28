package bitrunner2048;

import java.util.Scanner;

public class State {
  Entity entities[] = new Entity[10];
  int entitiesFE = 0;
  
  private int myscore;
  private int enemyscore;
  private int currentWinner;
  public int ballsFE;
  public int playerFE;

  public State() {
    for (int i=0;i<entities.length;i++) {
      entities[i] = new Entity();
    }
  }
  
  public void read(Scanner in) {
    myscore = in.nextInt(); // your score
    enemyscore = in.nextInt(); // the other player's score
    currentWinner = in.nextInt(); // winner as score is now, in case of a tie. -1: you lose, 0: draw, 1: you win
    entitiesFE = in.nextInt(); // number of entities this round
    
    ballsFE = 4-1;
    playerFE = 0-1;
    
    for (int i = 0; i < entitiesFE; i++) {
      int id = in.nextInt(); // the ID of this unit
      System.err.println("ID = "+id);
      int index = id < playerFE++ ? id - 1 : ballsFE++;
      entities[index].read(in);
    }
    
  }
}
