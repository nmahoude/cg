package optimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tournament {

	private GameResolver resolver;
	private List<Participant> bots = new ArrayList<>();
	private boolean withSwap;
	private int count;
	
	public Tournament(GameResolver resolver) {
		this.resolver = resolver;
	}
	
	public void addParticipant(Participant bot) {
		bots.add(bot);
	}
	
	public Tournament withSwap() {
		this.withSwap = true;
		return this;
	}
	public Tournament withGameCount(int count) {
		this.count = count;
		return this;
	}
	
	public void startTournament() {
		if (bots.size() < 2) {
			throw new RuntimeException("Not enough participants in tournament");
		}
		for (Participant bot1 : bots) {
			for (Participant bot2 : bots) {
				if (bot1 == bot2) continue ; // no match against each other
				
				Battle battle = new Battle(resolver);
				if (withSwap) battle.withSwap();
				
				BattleResult result = battle.launch(count, bot1, bot2);
				bot1.append(bot2, result);
				bot2.append(bot1, result.inv());
				
			}
		}
		
		
		int i=0;
		for (Participant bot : bots) {
			System.err.println(""+i+" => "+bot.globalResult());
			i++;
		}
	}

	public List<Participant> getLeaderboard() {
		return bots.stream().sorted((b1, b2) -> Integer.compare(b2.globalResult().wins, b1.globalResult().wins)).collect(Collectors.toList());
	}
	
	
}
