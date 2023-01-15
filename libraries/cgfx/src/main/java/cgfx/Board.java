package cgfx;


import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Board implements BoardDrawer {
	private static Font initFont = Font.font(16);

	final int width;
	final int height;
	final double scale;
	private Font currentFont = Font.font(16);
	
	Canvas canvas;
	GraphicsContext gc;

	public Board(Group parent, int width, int height) {
		this(parent, width+5, height+5, 1.0);
	}

	public Board(Group parent, int width, int height, double scale) {
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.canvas = new Canvas(width * scale, height * scale);
		this.gc = canvas.getGraphicsContext2D();

		parent.getChildren().add(canvas);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public GraphicsContext getGc() {
		return gc;
	}
	
	public void resetFont() {
		currentFont = initFont;
	}
	
	public void setFontSize(int size) {
		currentFont = Font.font(size);
	}

  public void drawLine(Color color, Pos start, Pos end) {
    drawLine(color, 1.0, start, end);
  }
  public void drawLine(Color color, double size, Pos start, Pos end) {
    gc.setLineWidth(size);
    gc.setStroke(color);
    gc.strokeLine(start.x * scale, start.y * scale, end.x * scale, end.y * scale);

  }

  public void strokeLine(Color color, double sx, double sy, double ex, double ey) {
    drawLine(color, Pos.from(sx,  sy), Pos.from(ex, ey));
  }
  public void strokeLine(Color color, double size, double sx, double sy, double ex, double ey) {
    drawLine(color, size, Pos.from(sx,  sy), Pos.from(ex, ey));
  }

  
	public void drawRect(Color stroke, Color fill, Pos center, Length length) {
	  drawRect(stroke, fill, center, length, Inset.NO);
	}
	
  public void drawRect(Color stroke, Pos center, Length length) {
    drawRect(stroke, stroke, center, length, Inset.NO);
  }

  public void drawRect(Color stroke, Color fill, Pos start, Length length, Inset inset) {
    gc.setStroke(stroke);
		gc.setFill(fill);
    gc.strokeRect((start.x + inset.l) * scale, 
                  (start.y + inset.l) * scale, 
                  (length.dx - 2*inset.l) * scale, 
                  (length.dy - 2 * inset.l) * scale);
	}

  public void fillRect(Color stroke, Color fill, Pos start, Length length) {
    fillRect(stroke, fill, start, length, Inset.NO);
  }
  
  public void fillRect(Color stroke, Color fill, Pos start, Length length, Inset inset) {
    gc.setStroke(stroke);
    gc.setFill(fill);
    gc.fillRect((start.x + inset.l) * scale, 
        (start.y + inset.l) * scale, 
        (length.dx - 2*inset.l) * scale, 
        (length.dy - 2 * inset.l) * scale);
  }

	public void clear() {
		gc.clearRect(0, 0, width * scale, height * scale);
	}

  public void drawText(Color color, Pos pos, String text) {
    gc.setFill(color);
    gc.setFont(currentFont);
    gc.fillText(text, pos.x * scale, pos.y * scale);
  }

	public void drawCircle(Color color, Pos center, int radius) {
		drawCircle(color, null, center, Length.of(0,0), radius);
	}

  public void strokeCircle(Color color, Pos center, Length decal, int radius) {
    drawCircle(color, null, center, decal, radius);
  }
  
  public void fillCircle(Color color, Pos center, int radius) {
    drawCircle(color, color, center, Length.NO, radius);
  }

  public void fillCircle(Color color, Pos center, Length decal, int radius) {
    drawCircle(color, color, center, decal, radius);
  }

  public void drawCircle(Color stroke, Color fill, Pos center, Length decal, int radius) {
		gc.setStroke(stroke);
		if (fill != null) {
      gc.setFill(fill);
  		gc.fillOval((center.x - radius + decal.dx) * scale, (center.y - radius + decal.dy) * scale, 2*radius* scale, 2*radius* scale);
		} else {
      gc.strokeOval((center.x - radius + decal.dx) * scale, (center.y - radius + decal.dy) * scale, 2*radius* scale, 2*radius* scale);
		}
	}

	public void drawHexagon(int radius, int centerX, int centerY) {
		gc.setLineWidth(1.0);
		gc.setStroke(Color.BLACK);
		gc.moveTo((centerX) * scale, (centerY-radius) * scale);
		gc.lineTo((centerX+radius) * scale, (centerY-0.5*radius) * scale);
		gc.lineTo((centerX+radius) * scale, (centerY+0.5*radius) * scale);
		gc.lineTo((centerX) * scale, (centerY+1.0*radius) * scale);
		gc.lineTo((centerX-radius) * scale, (centerY+0.5*radius) * scale);
		gc.lineTo((centerX-radius) * scale, (centerY-0.5*radius) * scale);
		gc.lineTo((centerX) * scale, (centerY-radius) * scale);
		gc.stroke();
		
	}


	
}
