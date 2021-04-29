package sample;

import java.util.Random;

import com.codingame.gameengine.runner.simulate.GameResult;

import optimizer.Participant;
import optimizer.Tournament;

public class Battles {
	public static void main(String[] args) {

		
		Tournament tournament = new Tournament((seed, cmd1, cmd2) -> {
			GameResult result = new GameResult();
			int score0 = new Random().nextBoolean() ? 0 : 1;
			int score1 = 1 - score0;
			
			result.scores.put(0, score0);
			result.scores.put(1, score1);
			return result;
		});
		
		tournament.addParticipant(new Participant("BOT 1 ", "cmd"));
		tournament.addParticipant(new Participant("BOT 2 ", "cmd"));
		tournament.addParticipant(new Participant("BOT 3 ", "cmd"));
		
		tournament.withSwap().withGameCount(10).startTournament();
		
	}
}
