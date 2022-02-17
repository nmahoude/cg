package connect4;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ZobristHash {
	static long[] player0Hashes = createHashes();
	static long[] player1Hashes = createHashes();
	
	static Set<Long> zobrist = new HashSet<>();
	
	private static long[] createHashes() {
		Random random = new Random(0);
		
		long[] hashes = new long[9 * 7];
		
		for (int i=0;i<hashes.length;i++) {
			hashes[i] = random.nextLong();
		}
		
		return hashes;
	}
	
	public static long get(boolean player, int col, int row) {
		if (player) {
			return player0Hashes[col + 9 * row];
		} else {
			return player1Hashes[col + 9 * row];
		}
	}

	public static boolean contains(long hash) {
		return zobrist.contains(hash);
	}

	public static void add(long hash) {
		zobrist.add(hash);
	}

	public static void clear() {
		zobrist.clear();
	}
}
