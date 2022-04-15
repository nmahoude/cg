package cgfx;


import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Board {
	int width;
	int height;
	Canvas canvas;
	GraphicsContext gc;
	
	public Board(Group parent, int width, int height) {
		this.width = width;
		this.height = height;
		this.canvas = new Canvas(width, height);
		this.gc = canvas.getGraphicsContext2D();
		parent.getChildren().add(canvas);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public GraphicsContext getGc() {
		return gc;
	}
	
	@Deprecated()
	/**
	 * {@link Deprecated} Use Pos for start & end
	 */
	public void drawLine(Color color, int sx, int sy, int dx, int dy) {
	  drawLine(color, Pos.from(sx, sy), Pos.from(dx, dy));
	}

  public void drawLine(Color color, Pos start, Pos end) {
    gc.setLineWidth(1.0);
    gc.setStroke(color);
    gc.moveTo(start.x, start.y);
    gc.lineTo(end.x, end.y);
    gc.stroke();
  }

	public void drawRect(Color stroke, Color fill, Pos center, Length length) {
	  drawRect(stroke, fill, center, length, Inset.NO);
	}
	
  public void drawRect(Color stroke, Pos center, Length length) {
    drawRect(stroke, stroke, center, length, Inset.NO);
  }

  public void drawRect(Color stroke, Color fill, Pos center, Length length, Inset inset) {
    gc.setStroke(stroke);
		gc.setFill(fill);
    gc.strokeRect(center.x - length.dx/2 +inset.l, center.y - length.dy/2 +inset.l, length.dx - 2*inset.l, length.dy - 2 * inset.l);
	}

  public void fillRect(Color stroke, Color fill, Pos center, Length length) {
    fillRect(stroke, fill, center, length, Inset.NO);
  }
  
  public void fillRect(Color stroke, Color fill, Pos center, Length length, Inset inset) {
    gc.setStroke(stroke);
    gc.setFill(fill);
    gc.fillRect(center.x - length.dx/2 +inset.l, center.y - length.dy/2 +inset.l, length.dx - 2*inset.l, length.dy - 2 * inset.l);
  }

	public void clear() {
		gc.clearRect(0, 0, width, height);
	}

  public void drawText(Color color, Pos pos, String text) {
    gc.setFill(color);
    gc.setFont(Font.font(16));
    gc.fillText(text, pos.x, pos.y);
  }

	public void drawCircle(Color color, Pos center, int radius) {
		drawCircle(color, color, center, Length.of(0,0), radius);
	}

  public void strokeCircle(Color color, Pos center, Length decal, int radius) {
    drawCircle(color, null, center, decal, radius);
  }
  
  public void fillCircle(Color color, Pos center, Length decal, int radius) {
    drawCircle(color, color, center, decal, radius);
  }

  public void drawCircle(Color stroke, Color fill, Pos center, Length decal, int radius) {
		gc.setStroke(stroke);
		if (fill != null) {
      gc.setFill(fill);
  		gc.fillOval(center.x + decal.dx, center.y  + decal.dy, 2*radius, 2*radius);
		} else {
      gc.strokeOval(center.x + decal.dx, center.y  + decal.dy, 2*radius, 2*radius);
		}
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
