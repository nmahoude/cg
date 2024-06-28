package spring2021;

/** for each cell, calculate the minimum sunshine for 24 day */
public class SunMap {

	public static double[] theoricalSunMap = new double[37];
	public static double[][] actualSunMap = new double[37][4]; // per cell for each size

	static public void calculateActualSunMap(State state) {
		// TODO calculer précisement pour les fins de parties
		int startDay = 0;
		int endDay = 6;

		for (int i = 0; i < 37; i++) {
			for (int t=0;t<3;t++) {
				actualSunMap[i][t] = 0;
			}
		}
		for (int i = 0; i < 37; i++) {
			for (int forTreeSize=1;forTreeSize<4;forTreeSize++) {
				for (int day = startDay; day < endDay; day++) {
					int invShadow = (day + 3) % 6;
					// check shadow
					boolean shadow = false;
					for (int s=0;s<3;s++) {
						int index = Cell.shadowIndexes[i][invShadow][s];
						int treeSize = state.trees[index];
						
						if (treeSize > s && treeSize >= forTreeSize) {
							shadow = true;
							break;
						}
					}
					
					if (!shadow) {
						actualSunMap[i][forTreeSize]+=forTreeSize;
					}
				}
			}
		}

	}
	
	static public void precalculate() {
		State state = new State();

		// grow big trees where possible
		for (int i = 0; i < 37; i++) {
			if (State.richness[i] > 0) {
				state.trees[i] = 3;
				state.setMine(i);
			}
		}

		for (int i = 0; i < 37; i++) {
			for (int day = 0; day < 6; day++) {
				int shadows = 0;
				for (int s = 0; s < 3; s++) {
					int index = Cell.shadowIndexes[i][day][s];
					int treeSize = state.trees[index];
					if (treeSize > 0) {
						shadows +=1;
					}
				}
				theoricalSunMap[i] += 1.0 / (6.0+shadows);
			}
		}

	}

}
