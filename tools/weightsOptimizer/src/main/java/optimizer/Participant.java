package optimizer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Participant {
	String id;
	String cmd;

	Map<Participant, BattleResult> results = new HashMap<>();
	private String name;
	
	public Participant(String name, String cmd) {
		id = UUID.randomUUID().toString();
		this.name = name;
		this.cmd = cmd;
	}

	public String getGameCmd() {
		return cmd;
	}
	
	public BattleResult append(Participant opponent, BattleResult result) {
		BattleResult current = results.get(opponent);
		if (current == null) {
			current = new BattleResult();
			results.put(opponent, result);
		}
		current.append(result);
		return current;
	}
	
	public BattleResult globalResult() {
		BattleResult result = new BattleResult();
		results.forEach((id, r) -> result.append(r));
		return result;
	}

	public String getId() {
		return id;
	}

	public String name() {
		return name;
	}

	public boolean hasMatchesAgainst(Participant bot2) {
		return results.containsKey(bot2);
	}

	public void removeResult(Participant bot) {
		results.remove(bot);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}