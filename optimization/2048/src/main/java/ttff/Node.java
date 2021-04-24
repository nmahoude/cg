package ttff;

public class Node {
	static int table_left[] = new int[1 << 16];
	static int score_left[] = new int[1 << 16];
	
	static int table_right[] = new int[1 << 16];
	static int score_right[] = new int[1 << 16];

	static long table_up[] = new long[1 << 16];
	static int score_up[] = new int[1 << 16];

	static long table_down[] = new long[1 << 16];
	static int score_down[] = new int[1 << 16];

	static {
		int values[] = new int[4];
		for (int bb3 = 0; bb3 < 16; bb3++) {
			for (int bb2 = 0; bb2 < 16; bb2++) {
				for (int bb1 = 0; bb1 < 16; bb1++) {
					for (int bb0 = 0; bb0 < 16; bb0++) {
						values[0] = bb3;
						values[1] = bb2;
						values[2] = bb1;
						values[3] = bb0;
						int mask = bb3 * 0b1_0000_0000_0000 + bb2 * 0b1_0000_0000 + bb1 * 0b1_0000 + bb0 * 0b1;
						int scoreMask = 0;

						int index = 0;
						int lastValueSeen = -1;
						for (int i = 0; i < 4; i++) {
							if (values[i] == 0) {
								// nothing
							} else {
								if (lastValueSeen == values[i]) {
									values[index - 1]++;
									scoreMask += (int) (Math.pow(2, lastValueSeen + 1));
									lastValueSeen = -1;
								} else {
									values[index++] = values[i];
									lastValueSeen = values[i];
								}
							}
						}
						for (; index < 4; index++) {
							values[index] = 0;
						}

						table_left[mask] = values[0] * 0b1_0000_0000_0000 + values[1] * 0b1_0000_0000 + values[2] * 0b1_0000	+ values[3] * 0b1;
						score_left[mask] = scoreMask;
						
						table_right[horizontalMirror(mask)] = horizontalMirror(table_left[mask]);
						score_right[horizontalMirror(mask)] = score_left[mask];
						
						table_up[mask] = values[0] * (1L << 48) + values[1] * (1L << 32) + values[2] * (1L << 16)	+ values[3] ;
						score_up[mask] = score_left[mask];


						// down
						index = 3;
						lastValueSeen = -1;
						for (int i = 3; i >= 0; i--) {
							if (values[i] == 0) {
								// nothing
							} else {
								if (lastValueSeen == values[i]) {
									values[index + 1]++;
									scoreMask += (int) (Math.pow(2, lastValueSeen + 1));
									lastValueSeen = -1;
								} else {
									values[index--] = values[i];
									lastValueSeen = values[i];
								}
							}
						}
						for (; index >=0; index--) {
							values[index] = 0;
						}

						
						
						table_down[mask] = values[0] * (1L << 48) + values[1] * (1L << 32) + values[2] * (1L << 16)	+ values[3] ;
						score_down[mask] = scoreMask;

					}
				}
			}
		}
	}

	long seed;
	long bits; // 64 bits for 16*4bits of data  15..0
	int score;
	char dirFromParent;
	
	public long nextSeed() {
		return seed * seed % 50515093L;
	}

	public void left(Node parent) {
		this.dirFromParent = 'L';
		this.score = parent.score;
		this.seed = parent.nextSeed();

		long result = 0;
		for (int i = 0; i < 4; i++) {
			int row = (int) (parent.bits >> (i * 16) & 0b1111_1111_1111_1111);
			result += (long)table_left[row] << (i * 16);
			score += score_left[row];
		}

		bits = result;
	}

	public void right(Node parent) {
		this.dirFromParent = 'R';
		this.score = parent.score;
		this.seed = parent.nextSeed();
		
		long result = 0;
		for (int i = 0; i < 4; i++) {
			int row = (int) (parent.bits >> (i * 16) & 0b1111_1111_1111_1111);
			result += (long)table_right[row] << (i * 16);
			score += score_right[row];
		}

		bits = result;
	}

	public void up(Node parent) {
		this.dirFromParent = 'U';

		this.score = parent.score;
		this.seed = parent.nextSeed();
		
		long result = 0;
		long tstate = transpose(parent.bits);
		for (int i = 0; i < 4; i++) {
			int row = (int) (tstate >> (i * 16) & 0b1111_1111_1111_1111);
			result += table_up[row] << (i * 4);
			score += score_up[row];
		}

		bits = result;
		
	}

	public void down(Node parent) {
		this.dirFromParent = 'D';
		this.score = parent.score;
		this.seed = parent.nextSeed();

		long result = 0;
		long tstate = transpose(parent.bits);
		for (int i = 0; i < 4; i++) {
			int row = (int) (tstate >> (i * 16) & 0b1111_1111_1111_1111);
			result += table_down[row] << (i * 4);
			score += score_down[row];
		}

		bits = result;
		
	}
	
	public void predict(Node parent) {
		// TODO auto calculate ?
		long freeCells = 0;
		long mask = 0b1111;
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				if ((bits & mask) != 0) freeCells++;
				mask = mask << 4;
			}
		}
		System.err.println("Current freecells = "+freeCells);
		
		int spawnIndex = (int) (parent.seed % freeCells); // TODO quick find of the real location
		int value = (parent.seed & 0x10) == 0 ? 2 : 4;
		System.err.println("Previsions ");
		System.err.println("It will be a " + value + " @ empty cell " + spawnIndex);
		System.err.println("next seed will be " + this.seed);

	}
	
	
	protected long transpose(long state) {
		long result = 0;

		result += extract(state, 0, 0);
		result += extract(state, 1, 4);
		result += extract(state, 2, 8);
		result += extract(state, 3, 12);

		result += extract(state, 4, 1);
		result += extract(state, 5, 5);
		result += extract(state, 6, 9);
		result += extract(state, 7, 13);
		
		result += extract(state, 8, 2);
		result += extract(state, 9, 6);
		result += extract(state, 10, 10);
		result += extract(state, 11, 14);
		
		result += extract(state, 12, 3);
		result += extract(state, 13, 7);
		result += extract(state, 14, 11);
		result += extract(state, 15, 15);

		return result;
	}

	private long extract(long state, int from, int to) {
		if (from <= to) {
			return (state & (0b1111L << 4*from)) << 4*(to-from);
		} else {
			return (state & (0b1111L << 4*from)) >> 4*(from-to);
		}
	}

	private static int horizontalMirror(int prepareRow) {
//		System.err.println("   1st bits: " + Integer.toBinaryString(prepareRow >> 12));
//		System.err.println("   2nd bits: " + Integer.toBinaryString((prepareRow & 0b0000_1111_0000_0000)));
//		System.err.println("   3rd bits: " + Integer.toBinaryString((prepareRow & 0b0000_0000_1111_0000)));
//		System.err.println("   4th bits: " + Integer.toBinaryString((prepareRow & 0b0000_0000_0000_1111)));
		return (prepareRow >> 12)
						 + ((prepareRow & 0b0000_1111_0000_0000) >> 4)
						 + ((prepareRow & 0b0000_0000_1111_0000) << 4)
						 + ((prepareRow & 0b0000_0000_0000_1111) << 12)
						 ;
	}

	
	public int valueAt(int x, int y) {
		int decal = 4*((3-x)+4*(3-y));
		return (int)((bits >>> decal) & 0b1111);
	}
	
	public void debug() {
		System.err.println("Grid from node bits");
		for (int y=0;y<4;y++) {
			for (int x=0;x<4;x++) {
				System.err.print(" " + valueAt(x,y));
			}
			System.err.println();
		}
	}

	public void dir(int dir, Node node) {
		switch(dir) {
		case 0: down(node); break;
		case 1: left(node); break;
		case 2: right(node); break;
		case 3: up(node); break;
		}
	}


}
