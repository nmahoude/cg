package connect4;

import java.util.Random;

public class ZobristHash {
	static long[] player0Hashes = createHashes(101L);
	static long[] player1Hashes = createHashes(101010L);
	
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

}
