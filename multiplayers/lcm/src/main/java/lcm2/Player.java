package lcm2;

import java.util.Random;
import java.util.Scanner;

import lcm2.cards.Card;
import lcm2.mc.MC2;
import lcm2.simpleai.SimplePickerAI;
import lcm2.simulation.Action;

public class Player {
  public static Random random = new Random(0);
  public static double[] w = new double[100];
  
  public static long start;
  
  public static int turn = 0;
  Agent agents[] = new Agent[2];
  MC2 ai;
  SimplePickerAI picker;
  Player() {
    agents[0] = new Agent(0);
    agents[1] = new Agent(1);
    ai = new MC2();
    picker = new SimplePickerAI(agents[0]);
  }

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    String weights = System.getProperty("weights");
    if (weights  != null) {
      System.err.println("I got weights !  "+weights);
      String[] wAsString = weights.split(";");
      for (int i=0;i<wAsString.length;i++) {
        w[i] = Double.parseDouble(wAsString[i]);
      }
    } else {
      w[0] = 0.5;
      w[1] = 1.4;
      w[2] = 1.2;
      w[3] = 1.0;
      w[4] = 1.1;
      w[5] = 1.0;
      w[6] = 5.0;
      w[7] = 5.0;

      w[8] = 5.0;
      w[9] = 0.2;
      w[10] = 0.1;

      w[11] = 0.0; // mana

//      w = new double[] {
//          0.8850678497460907,
//          0.12877902715114797,
//          -0.873703588900931,
//          -0.907396638644653,
//          0.6394546620935149,
//          -0.09470634887669727,
//          -0.77940173304084,
//          0.05487658554178343,
//          0.144302071315221,
//          0.04293098765656156
//      };
    }
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
//        if (turn == 37) {
//          debug();
//        }
        runBattleTurn();
      }
    }
  }

  private void debug() {
    System.err.println("Debug choice ! ");
    System.err.println("---------------------------------------");
    Agent me = new Agent(0);
    Agent opp = new Agent(1);
    
    
    System.err.println("Attack 1 1");
    me.copyFrom(agents[0]);
    opp.copyFrom(agents[1]);
    MC2.sim.run(me, opp, Action.attack(1, 1));
    System.err.println("My board");
    me.debugBoardCards();
    System.err.println("opp board");
    opp.debugBoardCards();
    double score = MC2.scorer.score(me, opp);
    System.err.println("Score is "+score);
    
    
    System.err.println("Attack face");
    me.copyFrom(agents[0]);
    opp.copyFrom(agents[1]);
    MC2.sim.run(me, opp, Action.attack(1, 0));
    System.err.println("My board");
    me.debugBoardCards();
    System.err.println("opp board");
    opp.debugBoardCards();
    score = MC2.scorer.score(me, opp);
    System.err.println("Score is "+score);
    System.err.println("---------------------------------------");
    
    
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