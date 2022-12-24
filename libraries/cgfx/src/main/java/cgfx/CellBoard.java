package cgfx;

import javafx.scene.Group;
import javafx.scene.paint.Color;

public class CellBoard extends Board {

  private int cellSize;
	private Length l;
	
	public CellBoard(Group parent, int cellSize, int width, int height) {
		this(parent, cellSize, width, height, Length.NO);
	}

	public CellBoard(Group parent, int cellSize, int width, int height, Length inset) {
		super(parent, cellSize * width + inset.dx, cellSize * height + inset.dy);
		this.l = inset;
		this.cellSize = cellSize;
	}
	
  public void drawCellAt(Color color, int x, int y, Inset inset) {
    drawCellAt(color, Cell.at(x, y), inset);
  }

  public void drawCellAt(Color color, Cell cell, Inset inset) {
    drawRect(color, Pos.from(l.dx + cellSize * cell.x+inset.l, l.dy + cellSize * cell.y + inset.l), 
                    Length.of(cellSize-inset.l*2, cellSize-inset.l*2));
  }

  public void fillCellAt(Color color, Cell cell, Inset inset) {
    fillRect(color, color, 
        Pos.from(l.dx + cellSize * cell.x+inset.l, l.dy + cellSize * cell.y + inset.l), 
        Length.of(cellSize-inset.l*2, cellSize-inset.l*2));
  }

  public void fillCellAt(Color border, Color inside, Cell cell, Inset inset) {
    fillRect(border, inside, 
        Pos.from(l.dx + cellSize * cell.x+inset.l, l.dy + cellSize * cell.y + inset.l), 
        Length.of(cellSize-inset.l*2, cellSize-inset.l*2));
  }
  
  public void drawCellText(Color color, Cell cell, String text) {
    drawCellText(color, cell, Length.NO, text);
  }

  public void drawCellText(Color color, Cell cell, Length decal, String text) {
    super.drawText(color, Pos.from(l.dx + cellSize * cell.x + decal.dx, l.dy + cellSize * cell.y+decal.dy+16), text);
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
    drawCircle(color, fill, Pos.from(l.dx + cell.x * cellSize + cellSize / 2 + inset.l/4,
    									l.dy + cell.y * cellSize + cellSize / 2 + inset.l/4), 
                      Length.of(0, 0), radius - inset.l);
  }

  public void drawCellCircle(Color color, Color fill, Cell cell, Inset inset) {
    drawCellCircle(color,  fill,  cell, inset, cellSize / 2);
  }

  public void fillCellCircle(Color color, Cell cell, Inset inset) {
    drawCellCircle(color, color, cell, inset, cellSize/2);
  }


}
