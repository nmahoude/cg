package acoiaf._fx;

import static cgfx.CellBoard.HALF_CELL_SIZE;

import java.util.ArrayList;
import java.util.List;

import acoiaf.Action;
import acoiaf.DiffusionMap;
import acoiaf.DistanceDMap;
import acoiaf.Frontier;
import acoiaf.O;
import acoiaf.Pos;
import acoiaf.State;
import cgfx.Cell;
import cgfx.CellBoard;
import cgfx.Decal;
import cgfx.Inset;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import javafx.scene.paint.Color;

public class AOICGameViewer extends GameViewer {

	
	private static final Color MINE_COLOR = new Color(0.3,0.3,0.3,1.0);
	private GameOptionPane options;
	private CellBoard board = new CellBoard(this, 0.5, 12, 12, true);
	private AOICGameWrapper wrapper;
	private State state;
	
	List<Action> actions = new ArrayList<>();
	
	public AOICGameViewer(AOICGameWrapper wrapper, GameOptionPane options) {
		this.wrapper = wrapper;
		this.state = wrapper.state;
		
    this.setOptionsPane(options);
    options.register(this);

	}

	@Override
	public void setOptionsPane(GameOptionPane options) {
		this.options = options;
	}

	@Override
	protected void updateView() {
		drawBoard();
		drawUnits();
		
		if (options.isSelected(AOICGameOptionPane.MY_FLOODFILL)) drawMyFloodFill();
		if (options.isSelected(AOICGameOptionPane.OPP_FLOODFILL)) drawOppFloodFill();
		if (options.isSelected(AOICGameOptionPane.COMBINED_FLOODFILL)) drawCombinedFloodFill();
		if (options.isSelected(AOICGameOptionPane.DRAW_ACTIONS)) drawActions();
		if (options.isSelected(AOICGameOptionPane.DRAW_FRONTIER)) drawFrontier();
		
	}

	
	private void drawFrontier() {
		List<Pos>  myPos = new ArrayList<>();
		List<Pos>  oppPos = new ArrayList<>();
		for (Pos pos : Pos.allPositions) {
			if (state.owner[pos.offset] == O.OPP) {
				oppPos.add(pos);
			}
			if (state.owner[pos.offset] == O.ME) {
				myPos.add(pos);
			}
		}

		DistanceDMap myMap = new DistanceDMap();
		myMap.calculate(state, myPos);

		DistanceDMap oppMap = new DistanceDMap();
		oppMap.calculate(state, oppPos);

		Frontier frontier = new Frontier();
		frontier.calculate(state, myMap, oppMap);
		
		for (Pos pos : frontier.frontier()) {
			board.drawCellAt(Color.GREEN, cellAt(pos), Inset.of(0));
		}
		
	}

	public void drawActions() {
		for (Action a : actions) {
			if (a.type() == Action.MOVE) {
				Pos current = null;
				for (int i=0;i<144;i++) {
					if (state.unitId[i] == a.info()) {
						current = Pos.from(i);
						break;
					}
				}
				board.drawLine(Color.BLUE, cellPos(current).add(CellBoard.HALF_CELL_SIZE, HALF_CELL_SIZE), cellPos(a.pos()).add(HALF_CELL_SIZE, HALF_CELL_SIZE));
			}
			if (a.type() == Action.TRAIN) {
				board.drawCellText(Color.WHITE, cellAt(a.pos()), Decal.of(4, 40), "+"+a.info());
			}
			if (a.type() == Action.BUILD) {
				board.drawCellText(Color.WHITE, cellAt(a.pos()), Decal.of(4, 40), "B-"+a.info());
			}
		}
		
	}
	
	private cgfx.Pos pos(Pos pos) {
		return cgfx.Pos.from(pos.x, pos.y);
	}

	private cgfx.Pos cellPos(Pos from) {
		return board.getCellOrigin(Cell.at(from.x, from.y));
	}
	private void drawMyFloodFill() {
		List<Pos>  myPos = new ArrayList<>();
		for (Pos pos : Pos.allPositions) {
			if (state.owner[pos.offset] == O.ME) {
				myPos.add(pos);
			}
		}
		
		DistanceDMap map = new DistanceDMap();
		map.calculate(state, myPos);
		
		drawDiffuseMap(map);		
	}

	private void drawOppFloodFill() {
		List<Pos>  oppPos = new ArrayList<>();
		for (Pos pos : Pos.allPositions) {
			if (state.owner[pos.offset] == O.OPP) {
				oppPos.add(pos);
			}
		}
		
		DistanceDMap map = new DistanceDMap();
		map.calculate(state, oppPos);
		
		drawDiffuseMap(map);		
	}

	private void drawCombinedFloodFill() {
		List<Pos>  myPos = new ArrayList<>();
		List<Pos>  oppPos = new ArrayList<>();
		for (Pos pos : Pos.allPositions) {
			if (state.owner[pos.offset] == O.OPP) {
				oppPos.add(pos);
			}
			if (state.owner[pos.offset] == O.ME) {
				myPos.add(pos);
			}
		}

		DistanceDMap myMap = new DistanceDMap();
		myMap.calculate(state, myPos);

		DistanceDMap oppMap = new DistanceDMap();
		oppMap.calculate(state, oppPos);
		
		
		myMap.sub(oppMap);
		
		for (Pos pos : Pos.allPositions) {
			if (state.owner[pos.offset] == -99) continue;
			
			Color color;
			if (myMap.grid[pos.offset] < 0) {
				color = new Color(0.0,0.0,1.0,0.3);
			} else if (myMap.grid[pos.offset] > 0) {
				color = new Color(1.0,0.0,0.0,0.3);
			} else {
				color = new Color(0.0,1.0,0.0,0.3);
			}
			board.fillCellAt(color, cellAt(pos), Inset.of(4));
			board.drawCellText(Color.WHITE, cellAt(pos), Decal.of(4, 40), ""+myMap.grid[pos.offset]);
		}
	}

	private void drawDiffuseMap(DiffusionMap map) {
		for (Pos pos : Pos.allPositions) {
			if (state.owner[pos.offset] == O.VOID) continue;
			
			board.setFontSize(12);
			board.drawCellText(Color.WHITE, cellAt(pos), Decal.of(40, 40), ""+map.grid[pos.offset]);
			board.resetFont();
		}
	}

	private void drawBoard() {
		board.clear();
		
		for (int y=0;y<12;y++) {
			for (int x=0;x<12;x++) {
				Pos pos = Pos.get(x, y);
				
				if (state.owner[pos.offset] != O.VOID) {
					Color color = new Color(0.8, 0.8, 0.8, 1.0);
					if (state.owner[pos.offset] == 0) {
					  if (state.active[pos.offset]) {
					    color = new Color(0,0,0.7,1.0);
					  } else {
					    color = new Color(0.5,0.5,1.0,1.0);
					  }
					} else if (state.owner[pos.offset] == 1) {
            if (state.active[pos.offset]) {
              color = new Color(0.7,0,0.0,1.0);
            } else {
              color = new Color(1.0,0.5,0.5,1.0);
            }
					}
					
					board.fillCellAt(color, Cell.at(x, y), Inset.of(1));
				}
			}
		}
		
		for (int i=0;i<State.minesFE;i++) {
			board.drawCellCircle(MINE_COLOR, MINE_COLOR, cellAt(state.mines[i]), Inset.of(10));
		}
		
	}

	private void drawUnits() {
		for (int y=0;y<12;y++) {
			for (int x=0;x<12;x++) {
				Pos pos = Pos.get(x, y);
				Color color = colorOfOwner(pos);

				int buildingType = state.buildingType[pos.offset];
				if (buildingType == State.SOLDIER) {
					board.fillCellAt(color, Cell.at(x,y), Inset.of(40));
					board.drawCellText(Color.WHITE, Cell.at(x, y), Decal.of(24, 20), ""+state.unitId[pos.offset]);
				} else if (buildingType == State.MINE) {
					board.drawCellCircle(color, color, cellAt(pos), Inset.of(10));
				} else if (buildingType == State.TOWER) {
					board.fillCellAt(color, Cell.at(x,y), Inset.of(40));
					board.drawCellText(Color.WHITE, Cell.at(x, y), Decal.of(30, 28), ""+"T");
				} else if (buildingType == State.HQ) {
					board.fillCellAt(color, Cell.at(x,y), Inset.of(40));
					board.drawCellText(Color.WHITE, Cell.at(x, y), Decal.of(30, 28), ""+"H");
				}
			}
		}
	}
	
	
	private Color colorOfOwner(Pos pos) {
		Color color = Color.BLUE;
		if (state.owner[pos.offset] == 1) color = Color.RED;
		return color;
	}

	private Cell cellAt(Pos pos) {
		return Cell.at(pos.x, pos.y);
	}

	public void setActions(List<Action> actions) {
		this.actions.clear();
		this.actions.addAll(actions);
	}
	
	


}
