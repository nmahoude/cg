package sample;

import java.util.List;
import java.util.Random;

import com.codingame.gameengine.runner.simulate.GameResult;

import optimizer.Participant;
import optimizer.Tournament;

public class AGBattles {
	public static void main(String[] args) {

		
		Tournament tournament = new Tournament((seed, cmd1, cmd2) -> {
			GameResult result = new GameResult();
			int score0 = new Random().nextInt(100) > 1 ? 1 : 0;
			int score1 = 1 - score0;
			
			result.scores.put(0, score0);
			result.scores.put(1, score1);
			return result;
		});
		
		tournament.addParticipant(new Participant("BOT 1 ", "cmd"));
		tournament.addParticipant(new Participant("BOT 2 ", "cmd"));
		tournament.addParticipant(new Participant("BOT 3 ", "cmd"));
		tournament.addParticipant(new Participant("BOT 4 ", "cmd"));
		tournament.addParticipant(new Participant("BOT 5 ", "cmd"));
		tournament.addParticipant(new Participant("BOT 6 ", "cmd"));
		tournament.addParticipant(new Participant("BOT 7 ", "cmd"));
		tournament.addParticipant(new Participant("BOT 8 ", "cmd"));
		
		tournament.withGameCount(10).startTournament();
		
		int i=10;
		for (int j=0;j<10;j++) {
			List<Participant> leaders = tournament.getLeaderboard();
			Participant last = leaders.get(leaders.size()-1);
			tournament.removeParticipant(last);
			System.err.println("Removing the last : "+last);
			Participant newOne = new Participant("Bot "+(i++), "cmd");
			System.err.println("Adding a new one : "+newOne);
			tournament.addParticipant(newOne);
			
			tournament.withGameCount(10).startTournament();
		}
		
		
	}
}
