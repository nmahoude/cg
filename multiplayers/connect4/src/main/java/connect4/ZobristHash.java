package connect4;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ZobristHash {
	static long[] player0Hashes = createHashes(101L);
	static long[] player1Hashes = createHashes(101010L);
	
	static Map<Long, Position> zobrist = new HashMap<>();
	
	private static long[] createHashes(long seed) {
		Random random = new Random(seed);
		
		long[] hashes = new long[9 * 7];
		
		for (int i=0;i<hashes.length;i++) {
			hashes[i] = random.nextLong();
		}
		
		return hashes;
	}
	
	public static long get(boolean player, int col, int row) {
		if (player) {
			return player0Hashes[col + 9*row];
		} else {
			return player1Hashes[col +  9*row];
		}
	}

	public static Position contains(long hash) {
		return zobrist.get(hash);
	}

	public static void add(long hash, long mine, long opp, double score) {
		Position position = Position.getFromCache();
		position.mine = mine;
		position.opp = opp;
		position.score = score;
				
		zobrist.put(hash, position);
	}

	public static void clear() {
		zobrist.clear();
		Position.resetCache();
	}
}
