package fall2022.minimax;

import java.util.List;

import fall2022.Logger;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;

public class Minimax {
	/**
	 * 	fight pour la case du milieu
	 * 
	 *      _____
	 *      |   | 
	 *      | 0 | 
	 *	_____________ 
	 *  |   |   |   |
	 *  | 3 | 4 | 1 |
	 *  _____________
	 *      |   | 
	 *      | 2 | 
	 *      _____
	 *  
	 *  
	 */
	
	int owner[] = new int[5];
	int blockedUnits[] = new int[5];
	int movableUnits[] = new int[5];
	
	
	double[] cellsScore = new double[5]; 
	
	// current result
	int unitsOnCenter[] = new int[2];
	int spawnOnCenter = 0;
	int centerOwner = O.NEUTRAL;
	
	public static void main(String[] args) {
		new Minimax().process(null, null);
	}
	
	
	void process(State state, List<Pos> frontier) {
		
		owner[0] = O.OPP;
		blockedUnits[0] = 0;
		movableUnits[0] = 1;
		cellsScore[0] = 0;
		
		owner[1] = O.ME;
		blockedUnits[1] = 0;
		movableUnits[1] = 0;
		cellsScore[1] = 0;

		owner[2] = O.ME;
		blockedUnits[2] = 0;
		movableUnits[2] = 0;
		cellsScore[2] = 0;

		owner[3] = O.NEUTRAL;
		blockedUnits[3] = 0;
		movableUnits[3] = 0;
		cellsScore[3] = 57;

		owner[4] = O.ME;
		blockedUnits[4] = 0;
		movableUnits[4] = 2;
		cellsScore[4] = 120;

		spawnOnCenter = 0;
		
		unitsOnCenter[owner[4]] = movableUnits[4]+blockedUnits[4];
		centerOwner = owner[4];
		
		resolve();
	}


	public void resolve() {
		Logger.error("J'ai oublié le recycler si la case est à moi + 0 unit dessus");

		for (int i=0;i<6;i++) {
			currentMoves[i] = 0;
			bestMoves[i] = -1;
		}
		minScore = Double.POSITIVE_INFINITY;
		maxScore = Double.NEGATIVE_INFINITY;
		
		doMove(0, O.ME);

	}

	public void init(State state, Pos p, double[] cellCost) {
		this.owner[4] = state.o[p.o];
		this.blockedUnits[4] = state.u[p.o] - state.mu[p.o];
		this.movableUnits[4] = state.mu[p.o];
		this.cellsScore[4] = cellCost[p.o];

		this.spawnOnCenter = state.o[p.o] == O.ME ? state.myMatter / O.COST : (state.o[p.o] == O.OPP ? state.oppMatter / O.COST : 0);

		int index = 0;
		for (Pos n : p.neighbors4dirs) {
			if (!state.canMove(n)) continue;

			this.owner[index] = state.o[n.o];
			this.blockedUnits[index] = state.u[n.o] - state.mu[n.o];
			this.movableUnits[index] = state.mu[n.o];
			this.cellsScore[index] = cellCost[n.o];
			
			index++;
		}
		// finish cleaning from index
		for (int i=index;i<4;i++) {
			this.owner[i] = O.NEUTRAL;
			this.blockedUnits[i] = 0;
			this.movableUnits[i] = 0;
			this.cellsScore[i] = 0;
		}
		
		centerOwner = owner[4];
		unitsOnCenter[0] = 0;
		unitsOnCenter[1] = 0;
		if (centerOwner != O.NEUTRAL) {
			unitsOnCenter[owner[4]] = movableUnits[4]+blockedUnits[4];
		}
		
	}
	
	
	
	int[] currentMoves = new int[6];
	double minScore = Double.POSITIVE_INFINITY;
	public double maxScore = Double.NEGATIVE_INFINITY;
	public int[] bestMoves = new int[6];

	private void doMove(int index, int currentOwner) {
		if (index == 6) {
			if (currentOwner == O.ME) {
				// now do opp actions!

				minScore = Double.POSITIVE_INFINITY;
				doMove(0, O.OPP);

				if (minScore > maxScore) {
					System.arraycopy(currentMoves, 0, bestMoves, 0, 6);
					maxScore = minScore;
				}
				return;
			} else {

				double score = evaluate();
				minScore = Math.min(minScore, score);

				return;
			}
		}

		int owner = index == 5 ? this.owner[4] : this.owner[index];
		if (owner != currentOwner) {
			doMove(index + 1, currentOwner);
			return;
		}

		if (index < 4) {
			for (int i = 0; i <= movableUnits[index]; i++) {
				// move n units towards center
				movableUnits[index] -= i;
				unitsOnCenter[owner] += i;
				currentMoves[index] = i;
				doMove(index + 1, currentOwner);
				
				movableUnits[index] += i;
				unitsOnCenter[owner] -= i;
				currentMoves[index] = 0;
			}

		} else if (index == 4) {
			for (int i = 0; i <= movableUnits[index]; i++) {
				// block n units
				blockedUnits[index] += i;
				movableUnits[index] -= i;
				currentMoves[index]  = i; // indicate how many blocked units

				doMove(index + 1, currentOwner);
				blockedUnits[index] -= i;
				movableUnits[index] += i;
				currentMoves[index]  = 0; // indicate how many blocked units
			}

			// remove n units
			for (int i = 0; i <= movableUnits[index]; i++) {
				// block n units
				movableUnits[4] -= i;
				unitsOnCenter[owner]-= i;
				currentMoves[4]  = -i; // negative indicate how many removes units

				doMove(index + 1, currentOwner);
				
				movableUnits[4] += i;
				unitsOnCenter[owner]+= i;
				currentMoves[4]  = 0; // indicate how many blocked units
			}
			
		} else if (index == 5 /* spawn phase*/) {
			if (this.owner[4] != currentOwner) {
				doMove(index+1, currentOwner);
				return;
			}
			// spawn
			for (int s = 0; s <= spawnOnCenter; s++) {
				unitsOnCenter[owner] += s;
				currentMoves[5] = s;

				doMove(index + 1, currentOwner);

				unitsOnCenter[owner] -= s;
				currentMoves[5] = 0;
			}
		}
	}


	private double evaluate() {
		int owner = this.owner[4];
		double score = 0.0;

		if (unitsOnCenter[O.ME] > unitsOnCenter[O.OPP]) {
			owner = O.ME;
		}
		if (unitsOnCenter[O.ME] < unitsOnCenter[O.OPP]) {
			owner = O.OPP;
		}

		if (owner == O.ME) score += 1_000_000;
		else if (owner == O.OPP) score -= 1_000_000;
		
		
		for (int i = 0; i < 4; i++) {
			if (this.owner[i] == O.ME) {
				score += 0.1 * movableUnits[i] * cellsScore[i];
			}
			
		}
		if (currentMoves[4] < 0) {
			score += 100 * (-currentMoves[4]);
		}
		score -= 0.1* currentMoves[5]; // the less spawn the better

		

		return score;
	}
	
}
