package cgfx;


import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Board {
	private int width;
	private int height;
	private Canvas canvas;
	private GraphicsContext gc;
	private int cellSize;
	
	public Board(Group parent, int cellSize, int width, int height) {
		this.cellSize = cellSize;
		this.width = width;
		this.height = height;
		this.canvas = new Canvas(cellSize * width, cellSize*height);
		this.gc = canvas.getGraphicsContext2D();
		parent.getChildren().add(canvas);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public GraphicsContext getGc() {
		return gc;
	}
	
	public void drawRect(Color color, int x, int y) {
		drawBoardRect(color, x, y, 0);
	}

	public void drawLine(Color color, int sx, int sy, int dx, int dy) {
		gc.setLineWidth(1.0);
		gc.setStroke(color);
		gc.moveTo(sx, sy);
		gc.lineTo(dx, dy);
		gc.stroke();
	}

	public void drawBoardRect(Color color, int x, int y, int inset) {
		gc.setFill(color);
		gc.fillRect(cellSize * x+inset, cellSize * y + inset, cellSize-inset*2, cellSize-inset*2);
	}

	public void drawRect(Color stroke, Color fill, int size, int centerx, int centery) {
		gc.setStroke(stroke);
		gc.setFill(fill);
		gc.strokeRect(centerx - size/2 , centery - size/2 , size, size);
	}
	public void drawRect(Color color, int x, int y, int size, int inset) {
		gc.setFill(color);
		gc.fillRect(x - size/2 +inset, y - size/2 + inset, size-inset*2, size-inset*2);
	}

	public void clear() {
		gc.clearRect(0, 0, cellSize*width, cellSize*height);
	}

	public void drawText(Color color, String text, int x, int y) {
		gc.setFill(color);
		gc.setFont(Font.font(16));
		gc.fillText(text, x, y);
	}

	public void drawBoardText(Color color, String text, int x, int y) {
		gc.setFill(color);
		gc.setFont(Font.font(16));
		gc.fillText(text, cellSize * x, cellSize * y+cellSize / 2);
	}

	
	public void drawCircle(Color color, int x, int y, int radius) {
		drawCircle(color, x, y, cellSize / 2 - radius, cellSize / 2 - radius, radius);
	}

	public void drawCircle(Color color, int x, int y, int decalx, int decaly, int radius) {
		gc.setFill(color);
		gc.fillOval(cellSize * x + decalx, cellSize * y  + decaly, 2*radius, 2*radius);
	}

	public void drawHexagon(int radius, int centerX, int centerY) {
		gc.setLineWidth(1.0);
		gc.setStroke(Color.BLACK);
		gc.moveTo(centerX, centerY-radius);
		gc.lineTo(centerX+radius, centerY-0.5*radius);
		gc.lineTo(centerX+radius, centerY+0.5*radius);
		gc.lineTo(centerX, centerY+1.0*radius);
		gc.lineTo(centerX-radius, centerY+0.5*radius);
		gc.lineTo(centerX-radius, centerY-0.5*radius);
		gc.lineTo(centerX, centerY-radius);
		gc.stroke();
		
	}
	
}
