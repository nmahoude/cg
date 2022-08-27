package tests;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import csb.GameState;
import csb.ai.AI;
import csb.ai.DummyAI;
import csb.ai.ag.AGAI;
import csb.entities.CheckPoint;
import csb.entities.Pod;
import tests.RaceFinished.TeamType;

public class Controller {
  public static Referee referee;
  public static GameState state1;
  public static GameState state2;
  public AI ai1;
  public AI ai2;
  private int round;

  public static void main(String[] args) throws Exception {
    Random rand = new Random(System.currentTimeMillis());

    AI ai1 = new AGAI();
    AI ai2 = new DummyAI();
    List<AI> ais = Arrays.asList(ai1, ai2);
    
    final int matchPerEvaluation = 2;
    int totalMatches = matchPerEvaluation * factoriel(ais.size()-1);
    int matches = 0;
    
    int scoreMatrix[][] = new int[ais.size()][ais.size()];
    for (int team1 = 0;team1<ais.size()-1;team1++) {
      for (int team2 = team1+1;team2<ais.size();team2++) {
        int score1 = 0, score2 = 0;
        
        for (int i=0;i<matchPerEvaluation;i++) {
          try {
            if (i % 2 == 0) {
              oneRace(rand.nextInt(), ais.get(team1), ais.get(team2));
            } else {
              oneRace(rand.nextInt(), ais.get(team2), ais.get(team1));
            }
          } catch(RaceFinished rf) {
            if (rf.team == TeamType.TEAM_1) {if (i % 2 == 0) score1++; else score2++;}
            if (rf.team == TeamType.TEAM_2) {if (i % 2 == 0) score2++; else score1++;}
          } catch(Exception e) {
          }
          matches++;
          System.out.println("match "+matches);
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

  private static void oneRace(int seed, AI ai1, AI ai2) throws Exception {
    Controller controller = new Controller();
    controller.init(seed);
    controller.setAI1(ai1);
    controller.setAI2(ai2);
    while(true) {
      controller.playOneTurn();
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
    referee.initReferee(seed, 4);
    
    state1 = new GameState();
    state1.setCheckPoints(referee.checkPoints);
    refereeToState1();
    
    state2 = new GameState();
    state2.setCheckPoints(referee.checkPoints);
    refereeToState2();
    
    round = 0;
  }

  public void playOneTurn() throws Exception {
    String[] output1 = ai1.evolve(System.currentTimeMillis()+100).output();
    String[] output2 = ai2.evolve(System.currentTimeMillis()+100).output();
    
    referee.handlePlayerOutput(0, round, 0, output1[0]);
    referee.handlePlayerOutput(0, round, 1, output1[1]);
    referee.handlePlayerOutput(0, round, 2, output2[0]);
    referee.handlePlayerOutput(0, round, 3, output2[1]);
    
    referee.updateGame(round);
    
    refereeToState1();
    refereeToState2();
    round++;
  }
  
  private static void refereeToState1() {
    referee.pods[0].copyTo(state1.pods[0]);
    referee.pods[1].copyTo(state1.pods[1]);
    referee.pods[2].copyTo(state1.pods[2]);
    referee.pods[3].copyTo(state1.pods[3]);
    state1.backup();
  }

  private static void refereeToState2() {
    referee.pods[2].copyTo(state2.pods[0]); // inverse pods team
    referee.pods[3].copyTo(state2.pods[1]);
    referee.pods[0].copyTo(state2.pods[2]);
    referee.pods[1].copyTo(state2.pods[3]);
    state2.backup();
  }

  public Pod[] getPods() {
    return referee.pods;
  }

  public CheckPoint[] getCheckPoints() {
    return referee.checkPoints;
  }
}
