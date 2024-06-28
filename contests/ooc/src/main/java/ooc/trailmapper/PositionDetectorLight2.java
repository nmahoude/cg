package ooc.trailmapper;

import ooc.Direction;
import ooc.OOCMap;
import ooc.P;

public class PositionDetectorLight2 {
	P pos[] = new P[OOCMap.S2];
	int posFE = 0;
	
	OOCMap map;
	
	public PositionDetectorLight2(OOCMap map) {
		this.map = map;
	}

	public void move(Direction direction) {
		int oldPosFE = posFE;
		posFE = 0;
		
		for (int i=0;i<oldPosFE;i++) {
			P c = pos[i].neighbors[direction.direction];
			if (!map.isIsland(c)) {
				pos[posFE++] = c;
			}
		}
	}

	
	public void silence(Direction direction, int length) {
		for (int i=0;i<length;i++) {
			move(direction); // TODO optimize ?
		}
	}	
	
	public void surface(int sector) {
		int oldPosFE = posFE;
		posFE = 0;
		
		for (int i=0;i<oldPosFE;i++) {
			if (pos[i].sector == sector) {
				pos[posFE++] = pos[i];
			}
		}
	}
	
	public void addPositions(P p) {
		pos[posFE++] = p;
	}

	public boolean hasPositions(P p) {
		for (int i=0;i<posFE;i++) {
			if (pos[i] == p) return true;
		}
		return false;
	}

	public int count() {
		return posFE;
	}
	
	public void reset() {
		posFE=0;
	}
	
	void debug() {
	}
}
