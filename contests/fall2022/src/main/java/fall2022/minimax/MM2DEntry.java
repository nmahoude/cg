package fall2022.minimax;

public class MM2DEntry {
	int blocked;
	int movable;
	int reinforcement;
	
	public int total() {
		return blocked + movable + reinforcement;
	}

	public static MM2DEntry blocked(int b) {
		MM2DEntry mm2dEntry = new MM2DEntry();
		mm2dEntry.blocked = b;
		return mm2dEntry;
	}

	public MM2DEntry movable(int m) {
		this.movable = m;
		return this;
	}

	public MM2DEntry reinforcement(int r) {
		this.reinforcement = r;
		return this;
	}
}
