package xmashrush2.ai;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.traversal.TreeWalker;

import xmashrush2.Agent;
import xmashrush2.BFS;
import xmashrush2.Direction;
import xmashrush2.Item;
import xmashrush2.Pos;
import xmashrush2.PushAction;
import xmashrush2.State;

public class MoveAI {
	
	static class Route {
		State state = new State();
		List<Pos> route = new ArrayList<>();
		boolean explored = false;
		int questItemsFound = 0;
		
		public List<Route> findRouteToAnyQuestItem() {
			ArrayList<Route> routes = new ArrayList<>();
			
			BFS bfs = new BFS();
			Agent agent = state.agents[0];
			bfs.process(state, agent.pos, MAX_MOVE - route.size());
			
			for (int i = 0;i<49;i++) {
				if (bfs.gScore[i] == Integer.MAX_VALUE) continue; // not reachable
				
				if (agent.needs(state.items[i]) ) {
					Route newRoute = this.createFrom(bfs.reconstructPathTo(i));
					newRoute.state.agents[0].pos = newRoute.route.get(newRoute.route.size()-1);
					newRoute.state.agents[0].getQuestItem(newRoute.state.items[i]);
					newRoute.state.items[i] = -1;
					newRoute.questItemsFound++;
					
					routes.add(newRoute);
				}
			}
			
			return routes;
		}

		public Route goToBestSpotForNextPush() {
			
			int reachableQuestItemsByPos[] = new int[49];
			
			BFS bfs = new BFS();
			Agent agent = state.agents[0];
			bfs.process(state, agent.pos, MAX_MOVE - route.size());

			// get the current reachable positions
			List<Pos> reachablePositions = new ArrayList<>();
			for (int i = 0;i<49;i++) {
				if (bfs.gScore[i] == Integer.MAX_VALUE) continue; // not reachable
				reachablePositions.add(Pos.from(i));
			}
			System.err.println("Reachable positions : "+reachablePositions);
			
			State tmpState = new State(); // TODO dangerous, state is still in scope and can be used instead of tmpState
			List<Pos> movedReachablePositions = new ArrayList<>();
			for (PushAction action : PushAction.actions) {
				tmpState.copyFrom(state);
				tmpState.apply(action, null);
				
				if (action == PushAction.actions(6, Direction.DOWN))  {
					System.err.println("push myself to the other side ....");
					tmpState.debugGrid();
				}
				// moves reachable positions accordingly 
				movedReachablePositions.clear();
				for (Pos pos : reachablePositions) {
					movedReachablePositions.add(pos.applyPushOnPos(action));
				}
//				System.err.println("Moved Reachable positions : "+movedReachablePositions);
				
				// for each quest item, check if we can reach it
				for (int i = 0;i<49;i++) {
					// if (!tmpState.agents[0].needs(tmpState.items[i])) continue; // TODO check for all our items (not only in quest list ?)
					if (tmpState.items[i] < 0 || tmpState.items[i] > 99) continue; // not my items
					
					
					double baseScore = 0;
					if (!tmpState.agents[0].fullOfQuestItems()) {
						baseScore = 1;
					}
					if (tmpState.agents[0].needs(tmpState.items[i])) {
						baseScore = 100000; // need items are way better
					}
					
//					System.err.println("For action "+action+" can reach "+Item.name(tmpState.items[i])+" ?");
					
					bfs.process(tmpState, Pos.from(i), MAX_MOVE); // quick BFS from the item perspective
					
					// check if we can reach a original reachable position (which is moved by the push) from the item
					for (int r = 0;r<49;r++) {
						if (bfs.gScore[r] == Integer.MAX_VALUE) continue; // not reachable
						int index = movedReachablePositions.indexOf(Pos.from(r));
						if (index != -1) {
							reachableQuestItemsByPos[reachablePositions.get(index).offset]+=baseScore; //
							if (baseScore == 100000) {
								System.err.println("Can reach QuestItem target "+Pos.from(i)+"from "+Pos.from(r)+" with this action "+action);
							}
//							System.err.print("From "+reachablePositions.get(index)+" , ");
						}
						
					}
//					System.err.println();
					
				}
			}
			
			System.err.println("QuestItems reachable by positions : ");
			double bestScore = Double.NEGATIVE_INFINITY;
			Pos best = Pos.WALL;
			for (int i=0;i<49;i++) {
				if (reachableQuestItemsByPos[i] == 0) {
					continue; // no way to reach it
				}
				System.err.println("Can reach item @"+Pos.from(i)+" with score "+reachableQuestItemsByPos[i]);
				
				double score = reachableQuestItemsByPos[i] - 0.01 * (49 - state.agents[0].pos.manhattan(Pos.from(i)));
				if (!state.agents[0].fullOfQuestItems() && Item.p0Item(state.items[i])) score+=2; // bonus to land on an item
				if (score > bestScore) {
					bestScore = score;
					best = Pos.from(i);
				}
			}

			if (best != Pos.WALL) {
				if (bestScore > 100) {
					System.err.println("Found a next pos with a next push to quest items  : "+best);
				} else {
					System.err.println("Didn't found quest items, but only items");
				}
				bfs.process(state, agent.pos, MAX_MOVE - route.size());
				Route newRoute = this.createFrom(bfs.reconstructPathTo(best.offset));
				newRoute.state.agents[0].pos = newRoute.route.get(newRoute.route.size()-1);
				return newRoute;
			} else {
				System.err.println("no pos with a item on next push found, defaulting to bestSpot overrall");
				return goToBestSpot();
			}
		}

		public Route goToBestSpot() {
			System.err.println("Trying depth 2 lookup of pushes");
			PushTreeAI pushTreeAI = new PushTreeAI(2);
			List<PushTreeNode> solutions = pushTreeAI.findSolution(state);
			for (PushTreeNode solution : solutions) {
				boolean debug = false;
				if (debug 
					&& solution.parent.actionFromParent == PushAction.actions(1, Direction.DOWN)
					&& solution.actionFromParent == PushAction.actions(1, Direction.DOWN)
						) {
					System.err.println("down down 1 -> ");
					System.err.println(solution.parent.toString());
					System.err.println(solution.toString());
					System.err.println("quest items :"+solution.reachableQuestItems);
					System.err.println("reachable positions : "+solution.potentialPos);
				}
					
				if (solution.reachableQuestItems.size() > 0) {
					String soluce = solution.parent.actionFromParent + " -> "  + solution.actionFromParent;
					System.err.println("Can reach items after "+soluce);
					System.err.println("Items are at "+solution.reachableQuestItems);
					if (solution.parent.reachableQuestItems.isEmpty()) {
						System.err.println("And just one push didn't find any !");
						System.err.println("Rollback move & push to find initial positions");
						
						BFS bfs = new BFS();
						bfs.process(solution.state, solution.reachableQuestItems.get(0));
						List<Pos> reachablePosToQuestItem = new ArrayList<>();
						for (int i=0;i<49;i++) {
							if (bfs.gScore[i] == Integer.MAX_VALUE) continue;
							if (solution.potentialPos.contains(Pos.from(i))) {
								reachablePosToQuestItem.add(Pos.from(i));
							}
						}
						System.err.println("Reachable pos to get to quest items "+reachablePosToQuestItem);
						List<Pos> parentPos = new ArrayList<>();
						for (Pos pos : reachablePosToQuestItem) {
							
							Pos positionBefore = pos.unapplyPushOnPos(solution.actionFromParent);
							System.err.println("Pos b4 : "+positionBefore);
							if (solution.parent.potentialPos.contains(positionBefore)) {
								System.err.println("   is in parent potential pos");
								parentPos.add(positionBefore);
							} else {
								System.err.println("   is not in parent potential pos");
							}
						}
						System.err.println("Can reach before action "+solution.actionFromParent+" from "+parentPos);
						
						
						List<Pos> positionsFromStep2 = solution.findStartingPosFromParentToReach(solution.reachableQuestItems.get(0), 20);
						List<Pos> positionsFromStep1 = solution.parent.findStartingPosFromParentToReach(positionsFromStep2, 20);
						System.err.println("New result from step 2 : "+positionsFromStep2);
						System.err.println("which give from step 1 : "+positionsFromStep1);
						
						Pos target = positionsFromStep1.get(0);
						System.err.println("Choosing starting point : "+target+" in possible : "+positionsFromStep1);
						// create a route to one of the positions
						// TODO there should be better one to choose ...
						Agent agent = state.agents[0];
						bfs.process(state, agent.pos, MAX_MOVE - route.size());
						Route newRoute = this.createFrom(bfs.reconstructPathTo(target.offset));
						newRoute.state.agents[0].pos = newRoute.route.get(newRoute.route.size()-1);
						return newRoute;
					}
				}
			}
			
			
			BFS bfs = new BFS();
			Agent agent = state.agents[0];
			bfs.process(state, agent.pos, MAX_MOVE - route.size());
			
			double bestScore = Double.NEGATIVE_INFINITY;
			Pos bestTarget = agent.pos;
			
			for (int i = 0;i<49;i++) {
				if (bfs.gScore[i] == Integer.MAX_VALUE) continue; // not reachable

				double score = 0.0
							-Pos.from(i).manhattan(Pos.CENTER)
							+10 * Cell.exits(state.cells[i]);
							;
				if (score > bestScore) {
					bestScore = score;
					bestTarget = Pos.from(i);
				}
			}
			
			int index = bestTarget.offset;
			Route newRoute = this.createFrom(bfs.reconstructPathTo(index));
			newRoute.state.agents[0].pos = newRoute.route.get(newRoute.route.size()-1);

			
			return newRoute;
		}
		
		
		
		private Route createFrom(List<Pos> reconstructedPath) {
			Route newRoute = new Route();
			newRoute.state.copyFrom(this.state);
			newRoute.questItemsFound = this.questItemsFound;
			newRoute.route.addAll(this.route);
			newRoute.route.addAll(reconstructedPath);
			return newRoute;
		}

		public String debug() {
			StringBuilder sb = new StringBuilder();
			for (Pos p : route) {
				sb.append(p);
			}
			return sb.toString();
		}

	}
	
	
	private static final int MAX_MOVE = 20;
	private static final String PASS = "PASS";
	private State state = new State();
	private String actions;
	
	public void output() {
		if (actions == PASS) {
			System.out.println("PASS");
		} else {
			System.out.println("MOVE "+actions.trim());
		}
	}

	public void think(State currentState) {
		
		state.copyFrom(currentState);
		
		Agent agent = state.agents[0];
		BFS bfs = new BFS();
		bfs.process(state, agent.pos, MAX_MOVE);
		

		Route emptyRoute = new Route();
		emptyRoute.state.copyFrom(currentState);
		List<Route> allRoutes = new ArrayList<>();
		allRoutes.add(emptyRoute);
		
		boolean foundNewRoutes = true;
		while (foundNewRoutes) {
			List<Route> newRoutesToCheck = new ArrayList<>();
			foundNewRoutes = false;
			
			System.err.println("Total routes : "+allRoutes.size());
			for (Route route : allRoutes) {
				if (route.explored) continue;
				route.explored = true;
				
				List<Route> newRoutes = route.findRouteToAnyQuestItem();
				System.err.println("Checking route : "+route.debug());
				System.err.println("  => found "+newRoutes.size()+" new routes to questItems");
				if (!newRoutes.isEmpty()) {
					foundNewRoutes = true;
					newRoutesToCheck.addAll(newRoutes);
				}
			}
			allRoutes.addAll(newRoutesToCheck);
		}
		
		// now find the best route ...
		double bestScore = Double.NEGATIVE_INFINITY;
		Route best = null;
		for (Route route : allRoutes) {
			double score = route.questItemsFound * 100;
			if (score > bestScore) {
				bestScore = score;
				best = route;
			}
		}
		
		if (best != null && !best.route.isEmpty()) {
			best.state.applyMoves(best.route); // remove objects 
			
			
			best = best.goToBestSpotForNextPush();
			List<Pos> route = best.route;
			actions = rebuildFromRoute(route);
			return;
		} else {
			best = emptyRoute.goToBestSpotForNextPush();
			List<Pos> route = best.route;
			if (route.size() > 1) {
				actions = rebuildFromRoute(route);
			} else {
				actions = PASS;
			}
			return;
		}
	}

	private String rebuildFromRoute(List<Pos> route) {
		String actions = "";
		Pos currentPos = null;
		for (Pos pos : route) {
			if (currentPos != null && currentPos != pos) {
				actions+=" "+pathBetween(currentPos, pos);
			}
			currentPos = pos;
		}
		return actions;
	}

	public List<List<Pos>> findRouteToAnyQuestItem(Agent agent, Pos start, int depth) {
		List<List<Pos>> routes = new ArrayList<>();
		
		BFS bfs = new BFS();
		bfs.process(state, start, depth);
		
		for (int i = 0;i<49;i++) {
			if (bfs.gScore[i] == Integer.MAX_VALUE) continue; // not reachable
			
			if (agent.needs(state.items[i]) ) {
				routes.add(bfs.reconstructPathTo(i));
			}
		}
		
		return routes;
	}
	
	
	private String rebuildMoveActions(Pos target, BFS bfs) {
		
		List<Pos> positions = bfs.reconstructPathTo(target.offset);
		rebuildFromRoute(positions);
		
		return actions;
	}

	private String pathBetween(Pos from, Pos to) {
		return to.dirFrom(from);
	  }
}
