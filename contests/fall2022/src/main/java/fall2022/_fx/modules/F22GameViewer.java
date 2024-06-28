package fall2022._fx.modules;

import static fall2022._fx.modules.F22GameOptionPane.BFS;
import static fall2022._fx.modules.F22GameOptionPane.ILOTS;
import static fall2022._fx.modules.F22GameOptionPane.ILOTS_10;
import static fall2022._fx.modules.F22GameOptionPane.OWNERSHIP;
import static fall2022._fx.modules.F22GameOptionPane.SCRAPS;
import static fall2022._fx.modules.F22GameOptionPane.UNITS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import cgfx.Cell;
import cgfx.CellBoard;
import cgfx.Decal;
import cgfx.Inset;
import cgfx.Length;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import fall2022.Action;
import fall2022.BFS;
import fall2022.DiffusionMap;
import fall2022.DistanceMap;
import fall2022.O;
import fall2022.Pos;
import fall2022.SpawnMap;
import fall2022.State;
import fall2022.TTSlice;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Voronoi;
import fall2022.Ilot.Ilot;
import fall2022.ai.ai2.AI2;
import fall2022.ai.ai2.LowRecyclers;
import fall2022.ai.ai2.RecyclerMatterUpgrader;
import fall2022.ai.ai2.Spreader;
import fall2022.sim.Sim;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class F22GameViewer extends GameViewer {
	private static final int CELL_SIZE = 64;
	private static final int DECALX = 16;
	private static final int DECALY = 48;
	private static final Image redRecycler = new Image(F22GameOptionPane.class.getResourceAsStream("/red-recycler.png"));
	private static final Image blueRecycler = new Image(F22GameOptionPane.class.getResourceAsStream("/blue-recycler.png"));

	
	
	private static final Color GRASS = Color.TRANSPARENT; // new Color(0.0, 0.4, 0.0, 0.3);
	private F22GameWrapper wrapper;
	private F22GameOptionPane options;

	private CellBoard board;
  State state;
	private List<Action> actions = new ArrayList<>();

	public F22GameViewer(F22GameWrapper wrapper) {
		this.wrapper = wrapper;
		this.state = wrapper.state;
		board = new CellBoard(this, CELL_SIZE, Pos.WIDTH, Pos.HEIGHT, true, Decal.of(DECALX, DECALY));
	}

	
	@Override
	public void setOptionsPane(GameOptionPane options) {
		this.options = (F22GameOptionPane) options;
	}

	@Override
	protected void updateView() {
		
		drawBoard();
		drawInfos();
		
		drawOwnership(state);
		if (options.isSelected(F22GameOptionPane.ENNEMY_HEATMAP)) drawEnnemyHeatMap();
		if (options.isSelected(F22GameOptionPane.MY_HEATMAP)) drawMyHeatMap();
		if (options.isSelected(F22GameOptionPane.DIFF_HEATMAP)) drawDiffMap();
		if (options.isSelected(F22GameOptionPane.MY_SPAWN_MAP)) drawMySpawnHeatMap();
		
		if (options.isSelected(F22GameOptionPane.MY_SPREADER_MAP)) drawMySpreaderMap();
		if (options.isSelected(F22GameOptionPane.MY_SPREADER_SPAWN_MAP)) drawMySpreaderSpawnMap();
		
		if (options.isSelected(BFS)) drawBfsGrid();
		if (options.isSelected(F22GameOptionPane.REACHABLE_POSITIONS)) drawReachablePositions();
		if (options.isSelected(ILOTS)) drawIlots(0);
		if (options.isSelected(ILOTS_10)) drawIlots(10);
		if (options.isSelected(F22GameOptionPane.VORONOI)) drawVoronoi(state, true);
		if (options.isSelected(F22GameOptionPane.VORONOI_P)) drawVoronoi(state, false);

		if (options.isSelected(SCRAPS)) drawScraps(state);
		if (options.isSelected(UNITS)) drawUnits(state);
		if (options.isSelected(F22GameOptionPane.RECYCLER_MAP)) drawRecyclersCost(state);
		if (options.isSelected(F22GameOptionPane.MRU_SCORES_MAP)) drawMru(state);

		if (options.isSelected(F22GameOptionPane.TERRITORY)) drawTerritory(state, false);
		if (options.isSelected(F22GameOptionPane.LOCK_MAP)) drawLockMap(state);
		if (options.isSelected(F22GameOptionPane.ACTIONS)) drawActions(state);
		// clear all so keep last
		if (options.isSelected(F22GameOptionPane.POS_AT_10) || options.sliderValue("POS_AT") > 0) drawPosBoardInTime(state);
	}

	private void drawMru(State state) {
		State work = new State();
		work.copyFrom(state);
		
		List<Ilot> ilots = Ilot.build(work);
		TimeTraveler tt = new TimeTraveler();
		tt.init(work);
		Territory territory = new Territory();
		Ilot ilot = ilots.get(0);
		territory.calculateTerritories(ilot, tt);
		AI2.fullFrontier = Territory.getFullFrontier(tt, ilots);
		
		LowRecyclers.prepareRatio(work);
		RecyclerMatterUpgrader mru = new RecyclerMatterUpgrader();
		mru.think(work, ilots, tt, Collections.emptyList());
		
		for (Pos p : Pos.allMapPositions) {
			if (state.s[p.o] == 0) continue;
			if (state.rec[p.o] != 0) continue;
			
			Color color = Color.BLACK;
			
			double score = mru.scores[p.o];
			if (score >= 1) {
				color = Color.GREEN; 
			} else if (score < 0) {
				color = Color.RED;
			}
			board.drawCellText(color, Cell.at(p.x, p.y), Decal.of(5, 45), ""+String.format("%.1f", score));

		}
	}


	private void drawRecyclersCost(State state) {
		
		State temp = new State();
		temp.copyFrom(state);
		for (Pos p : Pos.allMapPositions) {
			temp.o[p.o] = O.ME;
			temp.oo[p.o] = O.ME;
			
		}
		LowRecyclers.prepareRatio(temp);
		
		for (Pos p : Pos.allMapPositions) {
			if (state.s[p.o] == 0) continue;
			if (state.rec[p.o] != 0) continue;
			
			Color color = Color.BLACK;
			
			double ratio = LowRecyclers.ratio[p.o];
			if (ratio >= 1) {
				color = Color.GREEN; 
			} else if (ratio < 0) {
				color = Color.RED;
			}
			board.drawCellText(color, Cell.at(p.x, p.y), Decal.of(5, 45), ""+String.format("%.1f", LowRecyclers.ratio[p.o]));

		}
	}


	private void drawMySpreaderMap() {
		List<Ilot> ilots = Ilot.build(state);
		TimeTraveler tt = new TimeTraveler();
		tt.init(state);
		Territory territory = new Territory();
		Ilot ilot = ilots.get(0);
		territory.calculateTerritories(ilot, tt);

		
		List<Pos> disputedFrontier = Spreader.calculateForbiddenCells(state, ilot, tt, territory);
		DiffusionMap map = new DistanceMap(O.ME);
		map.calculate(state, disputedFrontier);
		
		drawDiffusion(p -> Math.min(50, map.grid[p.o]));

		
	}

	private void drawMySpreaderSpawnMap() {
		List<Ilot> ilots = Ilot.build(state);
		TimeTraveler tt = new TimeTraveler();
		tt.init(state);
		Territory territory = new Territory();
		Ilot ilot = ilots.get(0);
		territory.calculateTerritories(ilot, tt);
		List<Pos> disputedFrontier = Spreader.calculateForbiddenCells(state, ilot, tt, territory);

		DiffusionMap map = new SpawnMap(O.ME);
		map.calculate(state, disputedFrontier);
		
		drawDiffusion(p -> Math.min(50, map.grid[p.o]));
		
	}

	private void drawLockMap(State state) {

		for (Pos p : Pos.allMapPositions) {
			if (state.lm[p.o] == 0) continue;
			
			Color color;
			if (state.isOpp(p)) color = Color.RED;
			else if (state.isMine(p)) color = Color.BLUE;
			else color = Color.BLACK;
			
			board.drawCellText(color, Cell.at(p.x, p.y), Decal.of(5, 45), ""+state.lm[p.o]);

		}
	}


	private void drawTerritory(State state, boolean b) {
		Territory t = new Territory();
		TimeTraveler tt = new TimeTraveler();
		tt.init(state);
		
		
		int myPotentielCells = 0;
		int oppPotentielCells = 0;
		
		
		List<Ilot> ilots = Ilot.build(state);
		for (Ilot ilot : ilots) {
			t.calculateTerritories(ilot	, tt);
			myPotentielCells += t.blueTerritory.size();
			oppPotentielCells+= t.redTerritory.size();
			
			
			for (int i=0;i<ilot.pFE;i++) {
				Pos p = ilot.p[i];
				Color color;
				if (t.blueTerritory.contains(p)) color = Color.BLUE;
				else if (t.redTerritory.contains(p)) color = Color.RED;
				else if (t.disputed.contains(p)) color = Color.GREEN;
				else color = Color.BLACK;
				
				board.drawCellAt(color, Cell.at(p.x, p.y), Inset.of(1));
				board.drawCellAt(color, Cell.at(p.x, p.y), Inset.of(3));
				
				if (t.frontier.contains(p)) {
					board.drawCellCircle(Color.GREEN, Color.GREEN, Cell.at(p.x, p.y), Inset.of(4), 8);
				}
				
				if (t.blueDangers[p.o] > 0 && t.frontier.contains(p)) {
					board.drawText(Color.BLUE, cellPos(p).add(CELL_SIZE-40,  CELL_SIZE-4), String.format("☣%2.0f", t.blueDangers[p.o]));
				}
				
			}
			
			board.drawText(Color.BLACK, cgfx.Pos.from(600, 16), "territory:");
			board.drawText(Color.BLACK, cgfx.Pos.from(700, 16), "Me:" + state.myCellCount+"(p "+myPotentielCells+" )");
			board.drawText(Color.BLACK, cgfx.Pos.from(800, 16), "Opp: " + state.oppCellCount+"(p/ "+oppPotentielCells+" )");

		}
		
	}


	private void drawReachablePositions() {
		List<Ilot> ilots = Ilot.build(state);
		for (Ilot ilot : ilots) {
			
			Set<Pos> reachable = ilot.oppReachablePositions(state);
			for (Pos p : reachable) {
				board.fillCellAt(Color.RED, Cell.at(p.x, p.y), Inset.of(4));
			}
			
		}
	}


	private void drawActions(State state2) {
		for (Pos p : Pos.allMapPositions) {
			int spawnHere = 0;
			for (Action a : actions) {
				if (a.type() == Action.SPAWN && a.to() == p) {
					spawnHere += a.amount();
					board.drawLine(Color.BLUE, 1, cellPos(a.to()).add(CELL_SIZE / 2+2, CELL_SIZE / 2+2), cellPos(a.realTarget).add(CELL_SIZE / 2+2, CELL_SIZE / 2+2));
				}
			}
			if (spawnHere > 0) {
				board.drawLine(Color.BLUE, cellPos(p).add(5,CELL_SIZE-12), cellPos(p).add(5,CELL_SIZE-4) );
				board.drawLine(Color.BLUE, cellPos(p).add(1,CELL_SIZE-8), cellPos(p).add(9,CELL_SIZE-8) );
				board.drawText(Color.BLUE, cellPos(p).add(10,CELL_SIZE-4), ""+spawnHere);
			}
		}
		
		for (Action a : actions) {
			if (a.type() == Action.BUILD) {
				board.fillCellCircle(Color.GREEN, Cell.at(a.to().x, a.to().y), Inset.of(5));
			}
			if (a.type() == Action.MOVE) {
				board.drawLine(Color.GREEN, 3, cellPos(a.from()).add(CELL_SIZE / 2, CELL_SIZE / 2), cellPos(a.to()).add(CELL_SIZE / 2, CELL_SIZE / 2));
				board.drawLine(Color.RED, 1, cellPos(a.from()).add(CELL_SIZE / 2+2, CELL_SIZE / 2+2), cellPos(a.realTarget).add(CELL_SIZE / 2+2, CELL_SIZE / 2+2));
			}
		}
	}


	private cgfx.Pos pos(Pos from) {
		return cgfx.Pos.from(from.x, from.y);
	}

	private cgfx.Pos cellPos(Pos from) {
		return board.getCellOrigin(Cell.at(from.x, from.y));
	}

	private void drawBoard() {
		board.clear();
	}


	private void drawPosBoardInTime(State state2) {
		State work = new State();
		work.copyFrom(state2);

		
		int timeTravelerValue = options.sliderValue("POS_AT");
		TimeTraveler tt = new TimeTraveler();
		tt.init(work);
		tt.bfsInTime(Pos.from(1, 0));

		
		
		drawBoard();
		TTSlice slice = tt.sliceAt(timeTravelerValue);
		drawOwnership(slice.state);
		drawUnits(slice.state);
		
		drawScraps(slice.state);
	}


	private void drawVoronoi(State state, boolean byUnit) {
		Voronoi v = new Voronoi();
		v.calculate(state);
		
		int iter = 0;
		for (Entry<Pos, List<Pos>> e : v.map.entrySet()) {
			Color color;
			if (byUnit) {
				color = colorIndexes[iter % colorIndexes.length];
			} else {
				if (state.o[e.getKey().o] == O.ME) color = Color.LIGHTBLUE;
				else if (state.o[e.getKey().o] == O.OPP) color = Color.CORAL;
				else color = Color.GREEN;

			}
			
			for (Pos p : e.getValue()) {
				board.fillCellAt(color, Cell.at(p.x, p.y), Inset.of(4));
			}
			iter++;
		}
	}

	private void drawInfos() {
		board.drawText(Color.BLACK, cgfx.Pos.from(0, 16), "Materials:");
		board.drawText(Color.BLACK, cgfx.Pos.from(80, 16), "Me:" + state.myMatter);
		board.drawText(Color.BLACK, cgfx.Pos.from(160, 16), "Opp: " + state.oppMatter);
		
		board.drawText(Color.BLACK, cgfx.Pos.from(300, 16), "Units:");
		board.drawText(Color.BLACK, cgfx.Pos.from(370, 16), "Me:" + state.myUnitCount);
		board.drawText(Color.BLACK, cgfx.Pos.from(440, 16), "Opp: " + state.oppUnitCount);
	}


	private void drawDiffusion(Function<Pos, Double> valueFunction) {
		DiffusionMap map = new DistanceMap(O.ME);
		DiffusionMap oppMap = new DistanceMap(O.OPP);
		map.calculate(state);
		oppMap.calculate(state);
		
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				Pos p = Pos.from(x, y);
				if (state.canMove(p) ) {
					double value = valueFunction.apply(p);
					min = Math.min(min, value);
					max = Math.max(max, value);
				}
			}
		}

		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				fall2022.Pos p = fall2022.Pos.from(x, y);
				if (state.canMove(p) ) {
					double value = valueFunction.apply(p);
					double hue = Color.BLUE.getHue() + (Color.RED.getHue() - Color.BLUE.getHue()) * (value - min) / (max - min) ;
					Color color = Color.hsb(hue, 1.0, 0.5);
					board.fillCellAt(color, Cell.at(p.x, p.y), Inset.of(1));
					board.drawCellText(Color.RED, Cell.at(p.x, p.y), Decal.of(2, 45), String.format("%.2f", value));
				} else {
				}
			}
		}
	}
	
	
	private void drawDiffMap() {
		DiffusionMap map = new DistanceMap(O.ME);
		map.calculate(state);
		DiffusionMap oppMap = new DistanceMap(O.OPP);
		oppMap.calculate(state);

		
		drawDiffusion(p -> map.grid[p.o] - oppMap.grid[p.o]);
	}

	
	private void drawMyHeatMap() {
		DiffusionMap map = new DistanceMap(O.ME);
		map.calculate(state);
		drawDiffusion(p -> map.grid[p.o]);
	}
	
	private void drawMySpawnHeatMap() {
		DiffusionMap map = new SpawnMap(O.ME);
		map.calculate(state);
		drawDiffusion(p -> map.grid[p.o]);
	}
	
	private void drawEnnemyHeatMap() {
		DiffusionMap map = new DistanceMap(O.OPP);
		map.calculate(state);
		
		drawDiffusion(p -> map.grid[p.o]);
	}


	private void drawBfsGrid() {
		TimeTraveler tt = new TimeTraveler();
		tt.init(state);
		
		BFS bfs = new BFS();
		Pos from = Pos.from(8,1);
		board.drawCellCircle(Color.GREEN, Cell.at(from.x, from.y));
		
		
		bfs.calculate(wrapper.state, from, tt);
		
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				fall2022.Pos p = fall2022.Pos.from(x, y);
				if (bfs.distances[p.o] != Integer.MAX_VALUE) {
					board.drawCellText(Color.RED, Cell.at(p.x, p.y), Decal.of(45, 45), ""+bfs.distances[p.o]);
				}
			}
		}
	}

	private void drawScraps(State state) {
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				fall2022.Pos p = fall2022.Pos.from(x, y);
				if (state.s[p.o] > 0) {
					board.drawCellText(Color.BLACK, Cell.at(p.x, p.y), Decal.of(2, 0), ""+state.s[p.o]);
				}
			}
		}
	}

	private void drawUnits(State state) {
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				fall2022.Pos p = fall2022.Pos.from(x, y);
				
				Color color = Color.LIGHTGREY;
				if (state.o[p.o] == O.ME) {
					color = Color.BLUE;
				} else if (state.o[p.o] == O.OPP) {
					color = Color.RED;
				}

				if (state.u[p.o] > 0) {
					board.fillCellAt(Color.BLACK, color, Cell.at(p.x, p.y), Decal.of(15,15), Length.of(34,34));
					board.drawCellText(Color.WHITE, Cell.at(p.x, p.y), Decal.of(20, 20), ""+state.u[p.o]);
				}
				
				
				if (state.rec[p.o] != 0) {
					board.fillCellCircle(color, Cell.at(p.x, p.y), Inset.of(8));
//					cgfx.Pos pos = board.getCellOrigin(Cell.at(p.x, p.y));
//					Image recycler = state.o[p.o] == O.ME ? blueRecycler : redRecycler; 
//					board.getGc().drawImage(recycler, pos.x+6, pos.y+6, CELL_SIZE-12, CELL_SIZE-12);
				}
				
			}
		}
	}

	private void drawOwnership(State state) {
		
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				fall2022.Pos p = fall2022.Pos.from(x, y);
				if (state.s[p.o] == 0) {
					board.fillCellAt(GRASS, Cell.at(p.x, p.y), Inset.of(1));
				} else {
					Color color = Color.LIGHTGREY;
					if (options.isSelected(OWNERSHIP)  && state.o[p.o] == O.ME) {
						color = Color.LIGHTBLUE;
					} else if (options.isSelected(OWNERSHIP)  && state.o[p.o] == O.OPP) {
						color = Color.LIGHTSALMON;
					}
					
					board.fillCellAt(color, Cell.at(p.x, p.y), Inset.of(1));
					
				}
			}
		}
	}

	
	static Color colorIndexes[] = new Color[10];
	static {
		colorIndexes = new Color[]{ Color.AQUA, Color.BEIGE, Color.BLUEVIOLET, Color.CORAL, Color.AQUAMARINE, Color.CRIMSON, Color.DARKSLATEBLUE, Color.BROWN, Color.CADETBLUE};
	}
	private void drawIlots(int steps) {
		State work = new State();
		Sim sim = new Sim();
		work.copyFrom(state);
		for (int i=0;i<steps;i++) {
			sim.oneTurn(work);
		}
		
		List<Ilot> ilots = Ilot.build(work);
		int colorIndex = +1;
		for (Ilot ilot : ilots) {
			colorIndex = (colorIndex+1) % colorIndexes.length;
			for (int i=0;i<ilot.pFE;i++) {
				Pos p = ilot.p[i];
				board.fillCellAt(colorIndexes[colorIndex], Cell.at(p.x, p.y), Inset.of(1));
				
				board.drawCellText(Color.RED, Cell.at(p.x, p.y), Decal.of(45, 45), ""+ilot.ruler);
			}
		}
	}


	public void setActions(List<Action> actions) {
		// there is a cache of actions, so need to copy them :/
		this.actions.clear();
		for (Action action : actions) {
			Action a = Action.copyOf(action);
			this.actions.add(a);
		}
	}
}
