package cotc.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import cotc.GameState;
import cotc.ai.AI;
import cotc.ai.ag.AG;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class Controller {
  private static Coord coord = Coord.get(0, 0); // force Coord caches initialisation
  static Random rand = new Random(System.currentTimeMillis());
  private static int rounds;

  public Referee referee;
  public GameState state1;
  public GameState state2;
  public AI ai1;
  public AI ai2;
  private int round;

  public static void main(String[] args) throws Exception {
    

    //AI ai1 = new cotc.ai.ag.ref2.AG();
    AI ai1 = new cotc.ai.ag.ref2.AG();
    AI ai2 = new AG();
    List<AI> ais = Arrays.asList(ai1, ai2);
    
    final int matchPerEvaluation = 500;
    int totalMatches = matchPerEvaluation * factoriel(ais.size()-1);
    int matches = 0;
    
    int scoreMatrix[][] = new int[ais.size()][ais.size()];
    for (int team1 = 0;team1<ais.size()-1;team1++) {
      for (int team2 = team1+1;team2<ais.size();team2++) {

        int scores[] = doMatches(matchPerEvaluation, ais.get(team1), ais.get(team2));
        System.out.println("match "+(++matches)+" ("+rounds+" rounds)");

        scoreMatrix[team1][team2] = scores[0];
        scoreMatrix[team2][team1] = scores[1];
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

  public static int[] doMatches(int matchCount, AI ai1, AI ai2) {
    int scores[] = new int[2];
    for (int i=0;i<matchCount;i++) {
      try {
        if (i % 2 == 0) {
          oneGame(rand.nextInt(), ai1, ai2);
        } else {
          oneGame(rand.nextInt(), ai2, ai1);
        }
      } catch(GameFinished rf) {
        if (rf.teamId == 0) {if (i % 2 == 0) scores[0]++; else scores[1]++;}
        if (rf.teamId == 1) {if (i % 2 == 0) scores[1]++; else scores[0]++;}
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    return scores;
  }
  public static int factoriel(int fac) {
    if (fac <= 1) {
      return 1;
    } else {
      return fac * factoriel(fac-1);
    }
  }

  public static void oneGame(int seed, AI ai1, AI ai2) throws Exception {
    Controller controller = new Controller();
    controller.init(seed);
    controller.setAI1(ai1);
    controller.setAI2(ai2);
    rounds = 0;
    while(true) {
      try { 
        controller.playOneTurn();
        rounds++;
      } catch (GameOverException goe) {
        throw new GameFinished(controller.referee.winner());
      } catch (Exception e) {
        e.printStackTrace();
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
    initState(0, state1);
    
    state2 = new GameState();
    initState(1, state2);
    
    round = 0;
  }

  public void playOneTurn() throws Exception {
    String[] output1 = ai1.evolve().output();
    String[] output2 = ai2.evolve().output();
    
    referee.handlePlayerOutput(0, round, 0, output1);
    referee.handlePlayerOutput(0, round, 1, output2);
    
    referee.updateGame(round);

    refereeToState(0, state1);
    refereeToState(1, state2);
    round++;
  }
  
  private void initState(int playerIdx, GameState state) {
    state.shipCount = referee.shipsPerPlayer;
    state.teams[0] = referee.state.teams[playerIdx];
    state.teams[1] = referee.state.teams[(playerIdx + 1) % 2];
    refereeToState(playerIdx, state);
  }
  private void refereeToState(int playerIdx, GameState state) {
    {
      state.ships.clear();
      // Player's ships first
      for (int s=0;s<referee.state.teams[playerIdx].shipsAlive.length;s++) {
        Ship ship = referee.state.teams[playerIdx].shipsAlive.elements[s];
        state.ships.add(ship);
      }
      // Opponent's ships
      for (int s = 0; s < referee.state.teams[(playerIdx + 1) % 2].shipsAlive.length; s++) {
        Ship ship = referee.state.teams[(playerIdx + 1) % 2].shipsAlive.elements[s];
        state.ships.add(ship);
      }
    }

    // Visible mines
    state.mines.clear(); //TODO handle fog of war
    for (int i=0;i<referee.state.mines.length;i++) {
      Mine mine = referee.state.mines.get(i);
        boolean visible = false;
        
        for (int s=0;s<referee.state.teams[playerIdx].shipsAlive.length;s++) {
          Ship ship = referee.state.teams[playerIdx].shipsAlive.elements[s];
          if (ship.position.distanceTo(mine.position) <= Simulation.MINE_VISIBILITY_RANGE) {
              visible = true;
              break;
          }
        }
        if (visible) {
          state.mines.add(mine);
        }
    }

    state.cannonballs.clear();
    for (int i=0;i<referee.state.cannonballs.length;i++) {
      state.cannonballs.add(referee.state.cannonballs.get(i));
    }

    state.barrels.clear();
    for (int i=0;i<referee.state.barrels.length;i++) {
      state.barrels.add(referee.state.barrels.get(i));
    }

    state.backup();
  }
}
