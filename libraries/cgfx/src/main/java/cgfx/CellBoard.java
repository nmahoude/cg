package cgfx;

import javafx.scene.Group;
import javafx.scene.paint.Color;

public class CellBoard extends Board {

  private int cellSize;
	private Decal boardDecal;
	private boolean showCoordinates;
	
	public CellBoard(Group parent, int cellSize, int width, int height) {
		this(parent, cellSize, width, height, false);
	}

	public CellBoard(Group parent, int cellSize, int width, int height, boolean showCoordinates) {
		this(parent, cellSize, width, height, showCoordinates, Decal.NO);
	}
	public CellBoard(Group parent, int cellSize, int width, int height, boolean showCoordinates, Decal decal) {
		super(parent, cellSize * width + (showCoordinates ? 16+decal.dx : 0), cellSize * height + (showCoordinates ? 16+decal.dy : 0));
		this.showCoordinates = showCoordinates;
		
		this.boardDecal = showCoordinates ? Decal.of(16, 16).add(decal) : decal; 
		this.cellSize = cellSize;
	}
	
	
	@Override
	public void clear() {
		super.clear();
		
		if (showCoordinates) {
			for (int x=0;x<width;x++) {
				this.drawText(Color.BLACK, cgfx.Pos.from(boardDecal.dx + cellSize/2 + cellSize * x, boardDecal.dy-2), ""+x);
			}
			for (int y=0;y<height;y++) {
				this.drawText(Color.BLACK, cgfx.Pos.from(0, boardDecal.dy + cellSize / 2 + cellSize * y), ""+y);
			}
		}
	}
	
  public void drawCellAt(Color color, int x, int y, Inset inset) {
    drawCellAt(color, Cell.at(x, y), inset);
  }

  public void drawCellAt(Color color, Cell cell, Inset inset) {
    drawRect(color, Pos.from(boardDecal.dx + cellSize * cell.x+inset.l, boardDecal.dy + cellSize * cell.y + inset.l), 
                    Length.of(cellSize-inset.l*2, cellSize-inset.l*2));
  }

  public void fillCellAt(Color color, Cell cell, Inset inset) {
    fillRect(color, color, 
        Pos.from(boardDecal.dx + cellSize * cell.x+inset.l, boardDecal.dy + cellSize * cell.y + inset.l), 
        Length.of(cellSize-inset.l*2, cellSize-inset.l*2));
  }

  public void fillCellAt(Color border, Color inside, Cell cell, Inset inset) {
    fillRect(border, inside, 
        Pos.from(boardDecal.dx + cellSize * cell.x+inset.l, boardDecal.dy + cellSize * cell.y + inset.l), 
        Length.of(cellSize-inset.l*2, cellSize-inset.l*2));
  }
  
  public void fillCellAt(Color border, Color inside, Cell cell, Decal decal, Length length) {
    fillRect(border, inside, 
        Pos.from(boardDecal.dx + cellSize * cell.x+decal.dx, boardDecal.dy + cellSize * cell.y + decal.dy), 
        length);
  }

  public void drawCellText(Color color, Cell cell, String text) {
    drawCellText(color, cell, Decal.NO, text);
  }

  /**
   * @deprecated use the one with Decal instead of Length
   */
  @Deprecated
  public void drawCellText(Color color, Cell cell, Length decal, String text) {
    super.drawText(color, Pos.from(boardDecal.dx + cellSize * cell.x + decal.dx, boardDecal.dy + cellSize * cell.y+decal.dy+16), text);
  }

  public void drawCellText(Color color, Cell cell, Decal decal, String text) {
    super.drawText(color, Pos.from(boardDecal.dx + cellSize * cell.x + decal.dx, boardDecal.dy + cellSize * cell.y+decal.dy+16), text);
  }

  public void drawCellCircle(Color color, Cell cell) {
    strokeCellCircle(color, cell, Inset.NO, cellSize / 2);
  }

  public void drawCellCircle(Color color, Cell cell, Inset inset) {
    strokeCellCircle(color, cell, inset, cellSize / 2);
  }

  private void strokeCellCircle(Color color, Cell cell, Inset inset, int radius) {
    drawCellCircle(color, null, cell, inset, radius);
  }

  public void drawCellCircle(Color color, Color fill, Cell cell, Inset inset, int radius) {
    drawCircle(color, fill, Pos.from(boardDecal.dx + cell.x * cellSize + cellSize / 2 + inset.l/4,
    									boardDecal.dy + cell.y * cellSize + cellSize / 2 + inset.l/4), 
                      Length.of(0, 0), radius - inset.l);
  }

  public void drawCellCircle(Color color, Color fill, Cell cell, Inset inset) {
    drawCellCircle(color,  fill,  cell, inset, cellSize / 2);
  }

  public void fillCellCircle(Color color, Cell cell, Inset inset) {
    drawCellCircle(color, color, cell, inset, cellSize/2);
  }

	public Pos getCellOrigin(Cell cell) {
		return Pos.from(boardDecal.dx + cell.x* cellSize, boardDecal.dy + cell.y * cellSize);
	}


}
