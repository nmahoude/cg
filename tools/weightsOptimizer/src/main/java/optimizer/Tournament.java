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
		List<Participant> roundDone = new ArrayList<>();
		for (Participant bot1 : bots) {
			for (Participant bot2 : bots) {
				if (bot1 == bot2) continue ; // no match against each other
				if (roundDone.contains(bot2)) continue;
				
				Battle battle = new Battle(resolver);
				if (this.withSwap) battle.withSwap();
				
				BattleResult result = battle.launch(count, bot1, bot2);
				bot1.append(bot2, result);
				bot2.append(bot1, result.inv());
				
			}
			roundDone.add(bot1);
		}
		
		printHeader();		
		printLine();		
		for (Participant bot1 : bots) {
			System.err.print(String.format("%10s |", bot1.name()));
			for (Participant bot2 : bots) {
				if (bot1 == bot2) {
					System.err.print("***** |");
					continue ; // no match against each other
				} else {
					BattleResult br = bot1.results.get(bot2);
					System.err.print(String.format("%2.3f |", br.ratio()));
				}
			}
			System.err.println();
		}
		printLine();		
		
		
		List<Participant> sortedBots = bots.stream()
										.sorted((b1, b2) -> Integer.compare(b2.globalResult().wins, b1.globalResult().wins))
										.collect(Collectors.toList());

		for (Participant bot : sortedBots) {
			System.err.println(bot.name()+" => "+bot.globalResult());
		}
	}

	private void printHeader() {
		System.err.print(String.format("%12s", "|"));
		for (Participant bot2 : bots) {
			System.err.print(String.format("%4s|", bot2.name()));
		}		
		System.err.println();
	}

	private void printLine() {
		System.err.print(String.format("%12s", "------------"));
		for (Participant bot2 : bots) {
			System.err.print(String.format("%4s-|", "-----"));
		}
		System.err.println();
	}

	public List<Participant> getLeaderboard() {
		return bots.stream().sorted((b1, b2) -> Integer.compare(b2.globalResult().wins, b1.globalResult().wins)).collect(Collectors.toList());
	}
	
	
}
