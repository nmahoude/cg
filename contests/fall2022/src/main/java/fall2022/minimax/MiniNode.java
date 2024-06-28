package fall2022.minimax;

import fall2022.O;

public class MiniNode {
	int units[] = new int[5];
	int owner[] = new int[5];
	int matter[] = new int[2];
	int mm01;
	int mm10;
	int mm12;
	int ms;
	int mm21;
	int mm23;
	int mm32;
	int mm34;
	int mm43;
	int os2;
	int os3;
	
	public MiniNode(int mm01, int mm10, int mm12, int ms) {
		this.mm01 = mm01;
		this.mm10 = mm10;
		this.mm12 = mm12;
		this.ms = ms;
	}

	public MiniNode() {
		// TODO Auto-generated constructor stub
	}

	public void copyFrom(MiniNode model) {
		for (int i=0;i<5;i++) {
			units[i] = model.units[i];
			owner[i] = model.owner[i];
		}
		
		matter[0] = model.matter[0];
		matter[1] = model.matter[1];
		
		this.mm01 = model.mm01;
		this.mm10 = model.mm10;
		this.mm12 = model.mm12;
		this.ms = model.ms;
		
		// opp actions
		this.mm21 = model.mm21;
		this.mm23 = model.mm23;
		this.mm32 = model.mm32;
		this.mm34 = model.mm34;
		this.mm43 = model.mm43;
		this.os2 = model.os2;
		this.os3 = model.os3;
	}

	public void applyActions(int mm01, int mm10, int mm12, int ms, int mm21, int mm23, int mm32, int mm34, int mm43, int os2, int os3) {
		// my actions
		this.mm01 = mm01;
		this.mm10 = mm10;
		this.mm12 = mm12;
		this.ms = ms;
		
		// opp actions
		this.mm21 = mm21;
		this.mm23 = mm23;
		this.mm32 = mm32;
		this.mm34 = mm34;
		this.mm43 = mm43;
		this.os2 = os2;
		this.os3 = os3;
		
		
		// TODO Auto-generated method stub
		// reduce matter
		matter[0] -= ms * O.COST;
		matter[1] -= (os2 + os3) * O.COST;
		
		units[0] -= mm01;
		units[1] += (mm01 +ms - mm10 - mm12);
		int myUnit2 = mm12;
		
		// move & spawn from origin
		units[4] -= mm43;
		units[3] += mm43 + os3 - mm34 - mm32;

		int oppUnit2 = units[2] + mm32 + os2 - mm21 - mm23;

		if (myUnit2 == oppUnit2) {
			units[2] = 0;
		} else if (myUnit2 > oppUnit2) {
			units[2] = myUnit2 - oppUnit2;
			owner[2] = O.ME;
		} else {
			units[2] = oppUnit2 - myUnit2;
			owner[2] = O.OPP;
		}
		
		// check 1 to !
		if (units[1] >= mm21) {
			units[1] -= mm21;
		} else  {
			units[1] = mm21 - units[1];
			owner[1] = O.OPP;
		}
	}
	
	public void debug() {
		for (int i=0;i<5;i++) {
			System.out.print(
					(owner[i] == O.ME ? "Me  " : (owner[i] == O.OPP ? "Opp " : "Neu "))+" " + units[i]	+ " | ");
			
		}
		System.out.println();
	}

	public double score(boolean allowMyRecycler, boolean allowOppRecycler) {
		double score = 0.0;
		

		
		if (owner[1] == O.OPP) score -= 100_000;
		if (owner[2] == O.OPP) score -= 50_000;
		if (owner[2] == O.ME) score += 1_000;
		
		if (allowMyRecycler && owner[1] == O.ME && units[1] == 0 && owner[2] == O.OPP) score += 1000.0; // on pourra poser un reycler
		if (!allowOppRecycler && owner[2] == O.OPP && units[2] == 0 && owner[1] == O.ME) score -= 1000.0; // il pourra reposer un recycler
		
		if (owner[1] == O.ME && owner[2] == O.OPP) {
			score += 1.0 * (units[1] - units[2]);
		}
		
		return score;
	}

	
}
