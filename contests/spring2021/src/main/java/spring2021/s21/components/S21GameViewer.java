package spring2021.s21.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import cgfx.CellBoard;
import cgfx.Length;
import cgfx.Pos;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.GameWrapper;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import spring2021.Cell;
import spring2021.State;
import spring2021.SunMap;

public class S21GameViewer extends GameViewer {
	private S21GameWrapper gameWrapper;
	
	private CellBoard board = new CellBoard(this, 64, 7, 7);
	
	public boolean drawShadows = false;
	public boolean drawForbidenCells = false;
	public boolean displayIndexes = false;
	public boolean drawSunMap = false;

	private S21GameOptionPane options;
	
	
	public S21GameViewer(GameWrapper gameWrapper) {
		this.gameWrapper = (S21GameWrapper)gameWrapper;
		gameWrapper.addObserver(this);
	}

	public void setOptionsPane(GameOptionPane options) {
		this.options = (S21GameOptionPane)options;
		update(null, null);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (options != null) {
			drawShadows = options.drawShadows.isSelected();
			drawForbidenCells = options.drawForbidenCells.isSelected();
			displayIndexes = options.displayIndexes.isSelected();
			drawSunMap = options.drawSunMap.isSelected();
		}		
	
		super.update(o, arg);
	}

	public Node getBoard() {
		return board.getCanvas();
	}
	
	@Override
	protected void updateView() {
		draw(this.gameWrapper.player.state);
	}

  private void draw(State state) {
    board.clear();

		board.drawText(Color.BLACK, Pos.from(0, 400), "Day: "+state.day);
		board.drawText(Color.BLACK, Pos.from(0, 424), "Nutrients: "+state.nutrients);

		// changing values ...
		int allowedCellsCount = 0;
		for (int i=0;i<37;i++) {
			if (!State.forbidenSeedCells[i]) allowedCellsCount++;
		}
		board.drawText(Color.BLACK, Pos.from(320, 400), "AllowedCells: "+allowedCellsCount);
		board.drawText(Color.BLACK, Pos.from(320, 424), "-");

		
		board.drawText(Color.RED, Pos.from(0, 12), "Score: "+state.score[0]);
		board.drawText(Color.RED, Pos.from(0, 24), "Sun: "+state.sun[0]);
		
		board.drawText(Color.BLUE, Pos.from(360, 12), "Score: "+state.score[1]);
		board.drawText(Color.BLUE, Pos.from(360, 24), "Sun: "+state.sun[1]);
		
		Pos sunPosition = sunPos.get(state.day % 6);
		board.drawRect(Color.YELLOWGREEN, Color.YELLOW, sunPosition, Length.of(32,32) );
		
		
		for (int index=0;index<37;index++) {
			int size = state.trees[index];
			Pos center = centerOf(index);
			board.drawRect(Color.RED, Color.TRANSPARENT, center, Length.square(48));
			if (size >= 0) {
				Color team = state.isMine(index) ? Color.RED : Color.BLUE;
				if (state.isDormant(index)) {
					if (team == Color.RED) 
						team = Color.DARKGOLDENROD;
					else 
						team = Color.CORNFLOWERBLUE;
				}
				board.drawRect(team, center, Length.of(8*(size+1), 0));
			}
			if (drawForbidenCells ) {
				if (State.forbidenSeedCells[index]) {
					board.drawLine(Color.BLACK, Pos.from(center.x-24, center.y-24), Pos.from(center.x+24, center.y+24));
				}
			}
			if (displayIndexes ) {
				board.drawText(Color.BLACK,Pos.from(4+center.x, 20+center.y), ""+index);
			}
			if (drawSunMap ) {
				board.drawText(Color.BLACK, Pos.from(-14+center.x, 20+center.y), ""+String.format("%.2f",SunMap.theoricalSunMap[index]));
			}
			
			if (State.richness[index] == 0) {
				board.drawRect(Color.BLACK, center, Length.square(32));
			}
		}
		
		if (drawShadows ) {
			for (int index=0;index<37;index++) {
				int day = state.day % 6;
				int size = state.trees[index];
				
				for (int i=0;i<size;i++) {
					Cell next = Cell.cells[Cell.shadowIndexes[index][day][i]];
					if (next != Cell.WALL) {
						Pos center = centerOf(next.index);
						board.drawRect(Color.BLACK, center, Length.square(46));
					}
				}
			}			
		}
  }
	
	private Pos centerOf(int index) {
		Pos center = cellPos.get(index);
		int x = 24*center.x;
		int y = 32+48*center.y;

		return new Pos(x,y); 
	}
	
	List<Pos> sunPos = new ArrayList<>();
	{
		sunPos.add(new Pos(0,220)); // 0
		sunPos.add(new Pos(30,280)); 
		sunPos.add(new Pos(300,280)); 
		sunPos.add(new Pos(360,200)); 
		sunPos.add(new Pos(300,50)); 
		sunPos.add(new Pos(30,50)); 
	}
	
	List<Pos> cellPos = new ArrayList<>();
	{
		cellPos.add(new Pos(7,3)); // 0
		cellPos.add(new Pos(9,3));
		cellPos.add(new Pos(8,2));
		cellPos.add(new Pos(6,2));
		cellPos.add(new Pos(5,3));
		cellPos.add(new Pos(6,4));
		cellPos.add(new Pos(8,4));
		cellPos.add(new Pos(11,3));
		cellPos.add(new Pos(10,2));
		cellPos.add(new Pos(9,1));
		cellPos.add(new Pos(7,1)); // 10
		cellPos.add(new Pos(5,1));
		cellPos.add(new Pos(4,2));
		cellPos.add(new Pos(3,3));
		cellPos.add(new Pos(4,4));
		cellPos.add(new Pos(5,5));
		cellPos.add(new Pos(7,5));
		cellPos.add(new Pos(9,5));
		cellPos.add(new Pos(10,4));
		cellPos.add(new Pos(13,3));
		cellPos.add(new Pos(12,2)); // 20
		cellPos.add(new Pos(11,1));
		cellPos.add(new Pos(10,0));
		cellPos.add(new Pos(8,0));
		cellPos.add(new Pos(6,0));
		cellPos.add(new Pos(4,0));
		cellPos.add(new Pos(3,1));
		cellPos.add(new Pos(2,2));
		cellPos.add(new Pos(1,3));
		cellPos.add(new Pos(2,4));
		cellPos.add(new Pos(3,5)); // 30
		cellPos.add(new Pos(4,6));
		cellPos.add(new Pos(6,6));
		cellPos.add(new Pos(8,6));
		cellPos.add(new Pos(10,6));
		cellPos.add(new Pos(11,5));
		cellPos.add(new Pos(12,4));

	}
}
