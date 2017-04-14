package cotc.tests;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cotc.Team;
import cotc.game.PhysicsEngine;

public class Referee {
  public static boolean debugoutput = false;
  
  private static final Pattern PLAYER_INPUT_MOVE_PATTERN = Pattern
      .compile("(?<x>-?[0-9]{1,8})\\s+(?<y>-?[0-9]{1,8})\\s+(?<thrust>([0-9]{1,8}))", Pattern.CASE_INSENSITIVE);

  static Random random = new Random();

  public static boolean collisionOn = true;
  public PhysicsEngine physics;
  public Team teams[] = new Team[2];
  
  public int playerCount;
  
  public void initReferee(int seed, int playerCount) throws Exception {
    this.playerCount = playerCount;
    random = new Random(seed);
  
    physics = new PhysicsEngine();
  }
  
  protected String[] getInputForPlayer(int round, int playerIdx) {
    return null;
  }
  
  public void handlePlayerOutput(int frame, int round, int playerIdx, String action) {
    // output in the form : X Y thurst
    if (debugoutput) {
      if (playerIdx == 0) {
        System.err.println("----");
        System.err.println("Player1");
      }
      if (playerIdx == 2) {
        System.err.println("Player2");
      }
      System.err.println(action);
    }
    Matcher matchMove = PLAYER_INPUT_MOVE_PATTERN.matcher(action);
    if (matchMove.matches()) {
      int x = Integer.parseInt(matchMove.group("x"));
      int y = Integer.parseInt(matchMove.group("y"));
    }
  }
  
  public void updateGame(int round) throws Exception {
    physics.simulate();
  }
}
