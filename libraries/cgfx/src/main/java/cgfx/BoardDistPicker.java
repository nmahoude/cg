package cgfx;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class BoardDistPicker {
	private final Board supportBoard;

	private Pos initialPos = Pos.from(-1, -1);
  private Pos finalPos = Pos.from(-1, -1);
  private Pos currentPosition = Pos.from(-1,  -1);
  private Label positionLabel;

	public BoardDistPicker(Board board, Runnable update) {
		this.supportBoard = board;
		
		positionLabel = new Label();
		board.attach(positionLabel);
		
		board.getCanvas().setOnMouseMoved(mv -> {
      currentPosition = fromMouseEvent(mv);
      updatePositionLabel();
    });
		
		board.getCanvas().setOnMouseClicked(mv -> {
      if (!mv.isControlDown()) {
        initialPos = fromMouseEvent(mv);
        finalPos = Pos.from(-1, -1);

        updatePositionLabel();
        update.run();
        drawDist();
      }
      if (mv.isControlDown()) {
        finalPos = fromMouseEvent(mv);
        
        updatePositionLabel();
        update.run();
        drawDist();
      }
    });
	}
	
	private void updatePositionLabel() {
    String distStr="";
    if (finalPos.x != -1 && initialPos.x != -1) {
      int dist = initialPos.dist(finalPos);
      distStr = " lastDist="+dist;
      distStr += " ("+initialPos+") ("+finalPos+") ["+(finalPos.x-initialPos.x)+","+(finalPos.y - initialPos.y)+"]";
    } else if (initialPos.x != -1) {
      int dist = initialPos.dist(currentPosition);
      distStr = " currentDist="+dist;
      distStr += " ("+initialPos+") ("+currentPosition+") ["+(currentPosition.x-initialPos.x)+","+(currentPosition.y - initialPos.y)+"]";

    }
    positionLabel.setText("("+currentPosition.x+","+currentPosition.y+")"+ distStr);
  }
	
	private Pos fromMouseEvent(MouseEvent mv) {
    return Pos.from((int)(mv.getX() / supportBoard.scale), (int)(mv.getY() / supportBoard.scale));
  }
	
  private void drawDist() {
    int crossSize = (int)(8 / supportBoard.scale);
    
    if (initialPos.x != -1) {
    	supportBoard.drawLine(Color.BLACK, initialPos.add(-crossSize, 0), initialPos.add(crossSize,  0));
    	supportBoard.drawLine(Color.BLACK, initialPos.add(0, -crossSize), initialPos.add(0,  crossSize));
    }
    if (finalPos.x != -1) {
    	supportBoard.drawLine(Color.BLACK, finalPos.add(-crossSize, 0), finalPos.add(crossSize,  0));
    	supportBoard.drawLine(Color.BLACK, finalPos.add(0, -crossSize), finalPos.add(0,  crossSize));
    }

    if (initialPos.x != -1 && finalPos.x != -1) {
    	supportBoard.drawLine(Color.BLUE, initialPos, finalPos);
    }
  }

}
