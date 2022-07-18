package bender_ep4;

public class Node {
	State state;
	Pos currentPos;
	
	int currentDistance;

	/**
	 * Use for A* ordering
	 * @return
	 */
	public int evaluateTotalDistance() {
		return this.currentDistance + currentPos.manhattanDist(State.target);
	}
}
