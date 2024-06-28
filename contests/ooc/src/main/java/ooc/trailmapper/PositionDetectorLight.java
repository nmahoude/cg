package ooc.trailmapper;

import ooc.Direction;
import ooc.P;

public class PositionDetectorLight {

	private static final int RAW_MASK = 0b111111111111111;
	int pos[] = new int[15];
	
	public void move(Direction direction) {
		if (direction == Direction.EAST) {
			for (int i=0;i<15;i++) {
				pos[i] = (pos[i] >> 1) & RAW_MASK;
			}
		} else if (direction == Direction.WEST) {
			for (int i=0;i<15;i++) {
				pos[i] = (pos[i] << 1);
			}
		} else if (direction == Direction.NORTH) {
			for (int i=0;i<15-1;i++) {
				pos[i] = pos[i+1];
			}
			pos[14] = 0;
		}
	}

	public void setPositions(P p) {
		int offset = p.o;
		int raw = offset / 15;
		int col = 15 - (offset - 15 * raw);
		
		pos[raw] = pos[raw] | (1 << col);
	}

	public boolean hasPositions(P p) {
		int offset = p.o;

		int raw = offset / 15;
		int col = 15 - (offset - 15 * raw);
		
		return (pos[raw] & (1 << col)) != 0;
	}

	void debug() {
	}
}
