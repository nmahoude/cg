package lcm2;

import java.util.Random;
import java.util.Scanner;

import lcm2.cards.Card;
import lcm2.mc.MC;
import lcm2.simpleai.SimplePickerAI;

public class Player {
  public static Random random = new Random(0);

  public static long start;
  
  int turn = 0;
  Agent agents[] = new Agent[2];
  MC ai;
  SimplePickerAI picker;
  Player() {
    agents[0] = new Agent(0);
    agents[1] = new Agent(1);
    ai = new MC();
    picker = new SimplePickerAI(agents[0]);
  }

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    
    new Player().play(in);
 }

  private void play(Scanner in) {
    // game loop
    while (true) {
      turn ++;
      
      for (int i = 0; i < 2; i++) {
        agents[i].clearCards();
        agents[i].read(in);
      }
      Player.start = System.currentTimeMillis();
      
      
      agents[1].readOpponentActions(in);
      
      int cardCount = in.nextInt();
      for (int i = 0; i < cardCount; i++) {
        int cardNumber = in.nextInt();
        int instanceId = in.nextInt();
        Location location = Location.fromValue(in.nextInt());
        
        int cardType = in.nextInt();
        int cost = in.nextInt();
        int attack = in.nextInt();
        int defense = in.nextInt();
        String abilities = in.next();
        int myHealthChange = in.nextInt();
        int opponentHealthChange = in.nextInt();
        int cardDraw = in.nextInt();
        Card card = CardDeck.get(cardNumber, instanceId, cardType, cost, attack, defense ,abilities, myHealthChange, opponentHealthChange, cardDraw);
        switch(location) {
        case HIS_BOARD:
          agents[1].addBoardCard(instanceId, card);
          break;
        case MY_BOARD:
          agents[0].addBoardCard(instanceId, card);
          break;
        case MY_HAND:
          agents[0].addHandCard(instanceId, card);
          break;
        default:
          break;
        }
      }

      System.err.println("Turn " + turn);
      if (turn <= 30) {
        runPickTurn();
      } else {
        runBattleTurn();
      }
    }
  }

  private void runBattleTurn() {
    System.err.println("Playing cards ...");
    System.err.println("My hand");
    agents[0].debugHandCards();
    System.err.println("My board");
    agents[0].debugBoardCards();
    System.err.println("His board");
    agents[1].debugBoardCards();

    
    
    ai.think(agents[0], agents[1]);
    
  }

  private void runPickTurn() {
    System.err.println("Choosing cards ....");
    System.err.println("My hand");
    agents[0].debugHandCards();
    System.err.println("My board");
    agents[0].debugBoardCards();
    
    picker.run();
  }
}