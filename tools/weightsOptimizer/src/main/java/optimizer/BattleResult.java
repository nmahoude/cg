package optimizer;

public class BattleResult {
	public int wins = 0;
	public int loses = 0;
	public int draws = 0;
	
	public int timeouts[]= new int[] { 0, 0 };
	public int gameCount = 0;
	
	public void append(BattleResult r) {
		wins += r.wins;
		loses += r.loses;
		draws += r.draws;
		timeouts[0] += r.timeouts[0];
		timeouts[1] += r.timeouts[1];
		gameCount += r.gameCount;
	}


	public BattleResult inv() {
		BattleResult inv = new BattleResult();
		inv.wins = loses;
		inv.loses = wins;
		inv.draws = draws;
		inv.timeouts[0] = timeouts[1];
		inv.timeouts[1] = timeouts[0];
		inv.gameCount = gameCount;
		return inv;
	}

	public void debug() {
		System.err.println(this);
	}

	@Override
	public String toString() {
		return "w("+wins+") - d["+draws+"] - l("+loses+")"+ " timeouts: "+timeouts[0]+"/"+timeouts[1] +" count="+gameCount;
	}


	public double ratio() {
		return 1.0 * wins / (wins+loses+draws);
	}
}