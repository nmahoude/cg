package fall2022;

import java.util.ArrayList;
import java.util.List;

import fall2022.Ilot.Ilot;
import fall2022.sim.Sim;

public class Territory {
	public static int mySecureTerritory;
	public static int myTerritory;
	public static int oppSecureTerritory;
	public static int oppTerritory;
	public static int disputedTerritory;

	int blueDistances[] = new int [Pos.MAX_OFFSET];
	int redDistances[] = new int [Pos.MAX_OFFSET];
	public List<Pos> blueRobots = new ArrayList<>();
	public List<Pos> redRobots = new ArrayList<>();
	List<Pos> bluePos = new ArrayList<>();
	List<Pos> redPos = new ArrayList<>();

	public List<Pos> disputed = new ArrayList<>();
	public List<Pos> blueTerritory = new ArrayList<>();
	public List<Pos> redTerritory = new ArrayList<>();
	
	public double[] blueDangers = new double[Pos.MAX_OFFSET];
	
	
	public List<Pos> frontier = new ArrayList<>();
	
	public static void debugTerritories(State state) {
		State work = new State();
		work.copyFrom(state);
		Sim.tenTurn(work);
		
		TimeTraveler tt = new TimeTraveler();
		tt.init(work);
		
		
		List<Ilot> ilots = Ilot.build(work);
		
		mySecureTerritory = 0;
		oppSecureTerritory = 0;
		disputedTerritory = 0;
		myTerritory = 0;
		oppTerritory = 0;
		
		for (Ilot ilot : ilots) {
			if (ilot.ruler == O.ME) {
				mySecureTerritory += ilot.size();
				myTerritory += ilot.size();
			} else if (ilot.ruler == O.OPP) {
				oppSecureTerritory += ilot.size();
				oppTerritory += ilot.size();
			} else if (ilot.ruler == O.NEUTRAL){
			} else {
				
				Territory territory = new Territory();
				territory.calculateTerritories(ilot, tt);
				
				myTerritory+= territory.blueTerritory.size(); 
				oppTerritory+= territory.redTerritory.size(); 
				disputedTerritory += territory.disputed.size();
			}
			
		}
		Player.message = ""+myTerritory+"("+mySecureTerritory+") / "+oppTerritory+"("+oppSecureTerritory+")/"+disputedTerritory;
		Logger.info(Player.message);
	}

	
	
	public void calculateTerritories(Ilot ilot, TimeTraveler tt) {
		calculateTerritoriesA(ilot.p, ilot.pFE, tt);
		
		calculateDangerNew(ilot, tt);
		calculateTerritoriesA(ilot.p, ilot.pFE, tt); // oops need to recalculate them ! WHY ?
	}

	public void calculateTerritoriesA(Pos[] positions, int posFE, TimeTraveler tt) {
		
		State init = tt.sliceAt(0).state;
		init(positions, posFE, init);

		// calculate distances & save them
		BFS bfs = new BFS();
		bfs.calculate(null, blueRobots, bluePos, tt);
		System.arraycopy(bfs.distances, 0, blueDistances, 0, Pos.MAX_OFFSET);
		
		bfs.calculate(null, redRobots, redPos, tt);
		System.arraycopy(bfs.distances, 0, redDistances, 0, Pos.MAX_OFFSET);
		
		
		disputed.clear();
		blueTerritory.clear();
		redTerritory.clear();
		for (int i=0;i<posFE;i++) {
			Pos pos = positions[i];
			if (!init.canMove(pos) ) continue;
			if (blueDistances[pos.o] == Integer.MAX_VALUE && redDistances[pos.o] == Integer.MAX_VALUE) {
			} else if (blueDistances[pos.o] < redDistances[pos.o]) {
				if (!blueTerritory.contains(pos)) blueTerritory.add(pos);
			} else if (blueDistances[pos.o] > redDistances[pos.o]) {
				if (!redTerritory.contains(pos)) redTerritory.add(pos);
			} else {
				if (!disputed.contains(pos)) disputed.add(pos);
				
				if (init.o[pos.o]== O.ME) blueTerritory.add(pos);
				if (init.o[pos.o]== O.OPP) redTerritory.add(pos);
				
			}
		}		
		
		frontier.clear();
		for (Pos p : disputed) {
			boolean blue = init.o[p.o] == O.ME;
			boolean red = init.o[p.o] == O.OPP;

			for (Pos n : p.neighbors4dirs) {
				if (redTerritory.contains(n)) red = true;
				if (blueTerritory.contains(n)) blue = true;
			}
			if ((red && blue) || (red && !blue)) {
				if (!frontier.contains(p)) frontier.add(p);
			}
		}

		for (Pos p : blueTerritory) {
			for (Pos n : p.neighbors4dirs) {
				if (redTerritory.contains(n)) {
					if (!frontier.contains(p)) frontier.add(p);
				}
			}
		}
		
	}

	/*
	 * danger si on donne trop de cases à nous en laissant la case
	 * 
	 * malus si elles sont vide
	 */
	private void calculateDangerNew(Ilot ilot, TimeTraveler tt) {
		State state = tt.sliceAt(0).state;
		for (Pos p : Pos.allMapPositions) {
			blueDangers[p.o] = 0.0;
		}
		for (Pos current : Pos.allMapPositions) {
			double blueDanger = 0;
			int blueUnitsAround = 0;
			int redUnitsAround = 0;
			if (state.o[current.o] == O.NEUTRAL)
				blueDanger += 100;
			for (Pos n : current.neighbors4dirs) {
				if (!state.canMove(n))
					continue;
				if (state.o[n.o] == O.ME)
					blueUnitsAround += state.u[n.o];
				if (state.o[n.o] == O.OPP)
					redUnitsAround += state.u[n.o];
				if (blueTerritory.contains(n) || disputed.contains(n)) {
					blueDanger += 1.0;
					if (state.isNeutral(n))
						blueDanger += 15.0;
					else if (state.o[n.o] == O.ME && state.u[n.o] == 0)
						blueDanger += 5.0;
				}
			}
			double bonus = state.o[current.o] == O.NEUTRAL ? 1.0 : 0.0;
			if (state.o[current.o] == O.OPP) {
				redUnitsAround = 0;
				blueDanger = 1;
			}
			this.blueDangers[current.o] = bonus + blueDanger + 100 * (redUnitsAround > 0 ? 1 : 0);
		}
	}
	
	private void init(Pos[] positions, int posFE, State init) {
		bluePos.clear();
		redPos.clear();
		blueRobots.clear();
		redRobots.clear();

		for (int i=0;i<posFE;i++) {
			Pos pos = positions[i];
			blueDistances[pos.o] = Integer.MAX_VALUE;
			redDistances[pos.o] = Integer.MAX_VALUE;
			
			if (!init.canMove(pos)) continue;
			int unitsCount = init.u[pos.o];
			
			if (init.o[pos.o] == O.ME) {
				if (unitsCount > 0) {
					if (!blueRobots.contains(pos)) blueRobots.add(pos);
				} else {
					if (!bluePos.contains(pos)) bluePos.add(pos);
				}
			}
			if (init.o[pos.o] == O.OPP) {
				if (unitsCount > 0) {
					if (!redRobots.contains(pos)) redRobots.add(pos);
				} else {
					if (!redPos.contains(pos)) redPos.add(pos);
				}
			}
		}
	}

	public static List<Pos> getFullFrontier(TimeTraveler tt, List<Ilot> ilots) {
		List<Pos> frontier = new ArrayList<>();
		for (Ilot ilot : ilots) {
			if (ilot.ruler == Ilot.DISPUTED) {
				Territory t = new Territory();
				t.calculateTerritories(ilot, tt);
				frontier.addAll(t.frontier);
			}
		}
		return frontier;
	}

}
