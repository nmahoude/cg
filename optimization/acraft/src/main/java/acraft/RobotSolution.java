package acraft;

public class RobotSolution {
	State state = new State();
	
	Pos[] solution = new Pos[1000];
	int solutionFE = 0;
	
	public void clear() {
		solutionFE = 0;
	}

	public void copyFrom(Pos[] candidate, int candidateFE) {
		System.arraycopy(candidate, 0, solution, 0, candidateFE);
		solutionFE = candidateFE;
	}

	public void print() {
		for (int i=0;i<solutionFE;i++) {
			System.out.print(""+solution[i].x +" "+solution[i].y+" "+solution[i].dirToLetter()+" ");
		}
		System.out.println();
	}

	public void copyFrom(State initState) {
		this.state.copyFrom(initState);
		solutionFE = 0;
	}

	public boolean apply(Pos pos) {
		if (state.cells[pos.x][pos.y] != State.EMPTY) return false; // not applied

		solution[solutionFE++] = pos;
		state.applyArrow(pos);
		return true;
	}

	public void unapply(Pos pos) {
		solutionFE--;
		this.state.removeArrow(pos);
	}

	
	public int calculateScore() {
		return state.calculateScore();
	}

	public void backupSolution(RobotSolution model) {
		System.arraycopy(model.solution, 0, this.solution, 0, model.solutionFE);
		this.solutionFE = model.solutionFE;
	}

	public void reset() {
		state.resetRobots();
	}
	
	public void debug() {
		for (int i=0;i<solutionFE;i++) {
			System.err.print(solution[i]);
			System.err.print(" ");
		}
		System.err.println();
	}

}
