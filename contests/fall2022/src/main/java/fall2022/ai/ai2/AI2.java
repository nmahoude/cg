package fall2022.ai.ai2;

import java.util.ArrayList;
import java.util.List;

import fall2022.Action;
import fall2022.DistanceMap;
import fall2022.O;
import fall2022.Pos;
import fall2022.SpawnMap;
import fall2022.State;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;
import fall2022.ai.AI;
import fall2022.ai.ai1.WinGameHeuristic;

public class AI2 implements AI {
	private State state = new State();
	public static DistanceMap myDistances = new DistanceMap(O.ME);
	public static SpawnMap mySpawnDistances = new SpawnMap(O.ME);
	
	public static DistanceMap oppDistances = new DistanceMap(O.OPP);
	public static SpawnMap oppSpawnDistances = new SpawnMap(O.OPP);
	public static List<Pos> fullFrontier;
	private final TimeTraveler tt = new TimeTraveler();
	private final List<Ilot> ilots = new ArrayList<>();
	
	
	
	@Override
	public List<Action> think(State originalStateReadOnly) {
		Territory.debugTerritories(originalStateReadOnly);
		
		List<Action> actions = new ArrayList<>();

		updateInformations(originalStateReadOnly, actions, true);
		
		List<Action> winningConditionsActions = new WinGameHeuristic().think(state, ilots, tt);
		actions.addAll(winningConditionsActions);
		if (!winningConditionsActions.isEmpty()) {
			updateInformations(originalStateReadOnly, actions, false);
		}
		
		
		double canBuildRecyclerScore = checkCostOfRecyclers(state, ilots); // check the cost of recyclers => 1.0 == shouldBeRecyclers !, 0.0 avoid at all costs
		
		fullFrontier = Territory.getFullFrontier(tt, ilots);
		int frontierSize = fullFrontier.size();
		
		
		boolean allIlotsDecided = true;
		for (Ilot ilot : ilots) {
			if (ilot.isDisputed()) {
				allIlotsDecided = false;
				actions.addAll(new Defense().think(state, tt, ilots, ilot, myDistances, oppDistances));
			}
		}
		updateInformations(originalStateReadOnly, actions, false); // defense could have build recycler

		actions.addAll(Finisher.cover(state, ilots, tt)); // only cover when stall & one unit found
		// calculate ratio after defense to take potential build into account
		LowRecyclers.prepareRatio(originalStateReadOnly);

		boolean earlyGame = fullFrontier.size() > 0;
		for (Pos f : fullFrontier) {
			if (myDistances.grid[f.o] < 2 || mySpawnDistances.grid[f.o] < 2) {
				earlyGame = false;
				break;
			}
		}
		
		if (!earlyGame) {
			System.err.println("NOT Early game");
			List<Action> recyclers = new RecyclerMatterUpgrader().think(state, ilots, tt, actions);
			actions.addAll(recyclers);
			if (hasBuild(recyclers)) {
				updateInformations(originalStateReadOnly, actions, false); // could build recycler
			}
		} else {
			System.err.println("Early game");
			actions.addAll(new LowRecyclers().think(state));
			actions.addAll(new RecyclerMatterUpgrader().think(state, ilots, tt, actions));
			updateInformations(originalStateReadOnly, actions, false); // could build recycler
		}

		for (Ilot ilot : ilots) {
			if (ilot.ruler == O.ME) {
				if (ilot.isFullCoverByMe()) {
				// do nothing!
				} else {
					if (allIlotsDecided) {
						actions.addAll(Spreader.spreadQuicklyWithSpawn(state, ilot, tt));
					} else {
						actions.addAll(Spreader.spreadQuickly(state, ilot, tt));

					}
				}
			} else if (ilot.ruler == O.OPP || ilot.ruler == O.NEUTRAL) {
			// nothing to do anymore :(
			} else {
				actions.addAll(new Spreader().think(state, actions, ilot, tt));
				actions.addAll(new Attack().think(state, tt, ilot, myDistances, oppDistances, frontierSize == 1));
				if (!State.isStall()) {
					actions.addAll(Spreader.spreadQuickly(state, ilot, tt));
				}
				actions.addAll(new UnitSaver().think(state, ilot, tt));
				//actions.addAll(new FreeRecyclers().think(state, tt, ilot));
			}
		}


//		for (Ilot ilot : ilots) {
//			if (ilot.isDisputed()) {
//				actions.addAll(new DisputedProtector().think(state, ilot));
//			}
//		}

		actions.addAll(new Neutralizer().think(state, tt, actions));
		actions.addAll(Finisher.think(state, ilots, tt));

		return actions;
	}

	private void updateInformations(State originalStateReadOnly, List<Action> actions, boolean first) {
		// check if build happens or first time
		if (hasBuild(actions) || first) {
			// on met à jour les territoires et les map seulement sur la base des builds
			state.copyFrom(originalStateReadOnly);
			for (Action a : actions) {
				if (a.type() == Action.BUILD) {
					state.apply(a);
				}
			}
			
			myDistances.calculate(state);
			mySpawnDistances.calculate(state);
			
			oppDistances.calculate(state);
			oppSpawnDistances.calculate(state);
		}
		
		// on réapplique toutes les actions pour être quand meme dans le bon état
		state.copyFrom(originalStateReadOnly);
		for (Action a : actions) {
			state.apply(a);
		}
		ilots.clear();
		ilots.addAll(Ilot.build(state));
		tt.init(state);
		
	}

	private boolean hasBuild(List<Action> actions) {
		for (Action a : actions) {
			if (a.type() == Action.BUILD) return true;
		}
		return false;
	}

	private double checkCostOfRecyclers(State work, List<Ilot> ilots) {
		return 0.5;
	}
}
