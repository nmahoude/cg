package cgfx;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class CellBoard {

	private int width;
	private int height;
	private Canvas[][] canvases;
	
	public CellBoard(Group parent, int width, int height) {
		this.width = width;
		this.height = height;
		canvases = new Canvas[width][height];
		
		for (int y=0;y<height;y++) {
			for (int x=0;x<width;x++) {
				Canvas canvas = new Canvas(32, 32);
				canvases[x][y] = canvas;
				canvas.setTranslateX(32 * x);
				canvas.setTranslateY(32 * y);
				parent.getChildren().add(canvas);
			}
		}
	}
	
	public Canvas getCanvas(int x, int y) {
		return canvases[x][y];
	}
	
	public GraphicsContext getGc(int x, int y) {
		return getCanvas(x,y).getGraphicsContext2D();
	}
}
