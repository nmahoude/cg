package cotc.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import cotc.GameState;
import cotc.ai.AI;
import cotc.ai.DummyAI;

public class Controller {
  private static int rounds;
  public Referee referee;
  public GameState state1;
  public GameState state2;
  public AI ai1;
  public AI ai2;
  private int round;

  public static void main(String[] args) throws Exception {
    Random rand = new Random(System.currentTimeMillis());

    AI ai1 = new DummyAI();
    AI ai2 = new DummyAI();
    List<AI> ais = Arrays.asList(ai1, ai2);
    
    final int matchPerEvaluation = 1000;
    int totalMatches = matchPerEvaluation * factoriel(ais.size()-1);
    int matches = 0;
    
    int scoreMatrix[][] = new int[ais.size()][ais.size()];
    for (int team1 = 0;team1<ais.size()-1;team1++) {
      for (int team2 = team1+1;team2<ais.size();team2++) {
        int score1 = 0, score2 = 0;
        
        for (int i=0;i<matchPerEvaluation;i++) {
          try {
            if (i % 2 == 0) {
              oneGame(rand.nextInt(), ais.get(team1), ais.get(team2));
            } else {
              oneGame(rand.nextInt(), ais.get(team2), ais.get(team1));
            }
          } catch(GameFinished rf) {
            if (rf.teamId == 0) {if (i % 2 == 0) score1++; else score2++;}
            if (rf.teamId == 1) {if (i % 2 == 0) score2++; else score1++;}
          } catch(Exception e) {
          }
          matches++;
          System.out.println("match "+matches+" ("+rounds+" rounds)");
        }
        scoreMatrix[team1][team2] = score1;
        scoreMatrix[team2][team1] = score2;
      }      
    }
    System.out.println("Score matrix : ");
    for (int team1 = 0;team1<ais.size();team1++) {
      System.out.print("team"+team1+": ");
      for (int team2 = 0;team2<ais.size();team2++) {
        if (team1 == team2) {
          System.out.print("-  ");
        } else {
          System.out.print(scoreMatrix[team1][team2]+" ");
        }
      }
      System.out.println();
    }
  }

  private static int factoriel(int fac) {
    if (fac <= 1) {
      return 1;
    } else {
      return fac * factoriel(fac-1);
    }
  }

  private static void oneGame(int seed, AI ai1, AI ai2) throws Exception {
    Controller controller = new Controller();
    controller.init(seed);
    controller.setAI1(ai1);
    controller.setAI2(ai2);
    rounds = 0;
    while(true) {
      try { 
        controller.playOneTurn();
        rounds++;
      } catch (Exception e) {
        throw new GameFinished(controller.referee.winner());
      }
    }
  }

  public void setAI2(AI ai2) {
    this.ai2 = ai2;
    ai2.setState(state2);
  }

  public void setAI1(AI ai1) {
    this.ai1 = ai1;
    ai1.setState(state1);
  }

  public void init(int seed) throws Exception {
    referee = new Referee();
    referee.initReferee(seed, 2, new Properties());
    
    state1 = new GameState();
    refereeToState1();
    
    state2 = new GameState();
    refereeToState2();
    
    round = 0;
  }

  public void playOneTurn() throws Exception {
    String[] output1 = ai1.evolve().output();
    String[] output2 = ai2.evolve().output();
    
    referee.handlePlayerOutput(0, round, 0, output1);
    referee.handlePlayerOutput(0, round, 1, output2);
    
    referee.updateGame(round);
    
    refereeToState1();
    refereeToState2();
    round++;
  }
  
  private void refereeToState1() {
    state1.shipCount = referee.shipsPerPlayer;
    state1.backup();
  }

  private void refereeToState2() {
    state1.shipCount = referee.shipsPerPlayer;
    state2.backup();
  }
}
