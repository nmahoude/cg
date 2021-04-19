package optimizer;

import java.util.concurrent.ThreadLocalRandom;

import com.codingame.gameengine.runner.dto.GameResult;

/** launch n games between 2 bots */
public class Battle {
	static private int globalBattleId = 0;
	
  boolean swap = false;
	BattleResult result = new BattleResult();

	private GameResolver resolver;
	
	public Battle(GameResolver resolver) {
		this.resolver = resolver;
	}
	
	
	public BattleResult launch(int gameCount, Participant bot1, Participant bot2) {
		String cmd1 = bot1.getGameCmd();
		String cmd2 = bot2.getGameCmd();
		
		for (int i=0;i<gameCount;i++) {
			long seed = ThreadLocalRandom.current().nextLong();
			BattleResult matchResult = doOneMatch(seed, cmd1, cmd2);
			result.append(matchResult);
			
			if (this.swap) {
				matchResult = doOneMatch(seed, cmd2, cmd1);
				result.append(matchResult.inv());
			}
			
		}
		return result;
	}

	private BattleResult doOneMatch(long seed, String cmd1, String cmd2) {
		globalBattleId++;

    long start = System.currentTimeMillis();
    GameResult result = null;
    int tries = 3;
    do {
    	result = resolver.resolveGame(seed, cmd1, cmd2);
    	if (result != null) {
    		break;
    	}
    	System.err.println("Retrying ....");
    	tries--;
    } while(tries > 0);
    long end = System.currentTimeMillis();

    BattleResult r = new BattleResult();
    if (result == null) {
    	return r;
    }
    
    result.summaries.stream().filter(s -> s.contains("$0 has not provided")).findAny().ifPresent(s -> r.timeouts[0]++);
    result.summaries.stream().filter(s -> s.contains("$1 has not provided")).findAny().ifPresent(s -> r.timeouts[1]++);
    
//    System.err.println(result.summaries);
    
    r.gameCount++;
    int score0 = result.scores.get(0);
		int score1 = result.scores.get(1);
		if (score0 > score1) {
    	r.wins++;
    } else if (score1 > score0) {
    	r.loses++;
    } else {
    	r.draws++;
    }
		
		System.err.println("("+globalBattleId+")"+" Time = "+ (end-start)+" => "+r);
		
    
    
//    System.err.println(result.scores);
//    if (result.failCause != null) {
//    	System.err.println("Failed : ");
//    	System.err.println(result.failCause);
//    	System.err.println(result.metadata);
//    }

		return r;
	}
	
	public Battle withSwap() {
		this.swap = true;
		return this;
	}
	
}
