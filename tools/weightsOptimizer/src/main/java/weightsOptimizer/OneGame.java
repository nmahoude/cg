package weightsOptimizer;

import java.util.Properties;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.codingame.game.engine.Constants;
import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.dto.GameResult;

public class OneGame {
  static Random random = new Random(System.currentTimeMillis());
  
  public static void main(String[] args) throws ParseException {
    Options options = new Options();
    options.addOption("p1", true, "commandLine for player 1 bot")
           .addOption("p2", true, "commandLine for player 2 bot")
           .addOption("seed", false, "seed of the game to play");
    
    CommandLine cmd = new DefaultParser().parse(options, args);
    
    String agent1 = cmd.getOptionValue("p1");
    String agent2 = cmd.getOptionValue("p2");
    String seedStr = cmd.getOptionValue("seed"); 
    
    long seed;
    try {
      seed = Long.parseLong(seedStr);
    } catch(Exception e) {
      seed = random.nextLong();
    }
    
    
    String result = new OneGame().doOneGame(agent1, agent2, seed);
    System.out.println(result);
  }

  public String doOneGame(String agent1, String agent2, long seed) {
    agent1 = agent1.replace("'", "\"");
    agent2 = agent2.replace("'", "\"");

    MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
    
    Properties gameParameters = new Properties();
    gameRunner.setGameParameters(gameParameters);
    gameRunner.setSeed(seed);
    
    gameRunner.addAgent(agent1);
    gameRunner.addAgent(agent2);
  
    Constants.VERBOSE_LEVEL = 0;
    System.setProperty("league.level", "6");
    
    GameResult result = gameRunner.simulate();
    
    return result.scores.get(0)+" "+result.scores.get(1);
  }
}
