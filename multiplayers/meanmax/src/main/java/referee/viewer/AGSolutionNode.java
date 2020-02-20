package referee.viewer;


import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import meanmax.Game;
import meanmax.ai.ag.AGSolution;

public class AGSolutionNode extends Group {
  Line reaperLines[] = new Line[3];
  Line destroyerLines[] = new Line[3];
  Line doofLines[] = new Line[3];
  
  public AGSolutionNode() {
    for (int i=0;i<3;i++) {
      Line line = new Line();
      line.setFill(Color.RED);
      line.setStroke(Color.RED);
      line.setStrokeWidth(10);
      reaperLines[i] = line;
      this.getChildren().add(reaperLines[i]);
      
      line = new Line();
      line.setFill(Color.GREEN);
      line.setStroke(Color.GREEN);
      line.setStrokeWidth(10);
      destroyerLines[i] = line;
      this.getChildren().add(destroyerLines[i]);
      
      line = new Line();
      line.setFill(Color.BLUE);
      line.setStroke(Color.BLUE);
      line.setStrokeWidth(10);
      doofLines[i] = line;
      this.getChildren().add(doofLines[i]);
    }
  }
  
  public void update(AGSolution solution) {
    reaperLines[0].setStartX(Game.players[0].reaper.position.x);
    reaperLines[0].setStartY(Game.players[0].reaper.position.y);
    destroyerLines[0].setStartX(Game.players[0].destroyer.position.x);
    destroyerLines[0].setStartY(Game.players[0].destroyer.position.y);
    doofLines[0].setStartX(Game.players[0].doof.position.x);
    doofLines[0].setStartY(Game.players[0].doof.position.y);
    
    for (int i=0;i<3;i++) {
      reaperLines[i].setEndX(solution.actions[i][0].target.x);
      reaperLines[i].setEndY(solution.actions[i][0].target.y);
      destroyerLines[i].setEndX(solution.actions[i][1].target.x);
      destroyerLines[i].setEndY(solution.actions[i][1].target.y);
      doofLines[i].setEndX(solution.actions[i][2].target.x);
      doofLines[i].setEndY(solution.actions[i][2].target.y);

      if (i<2) {
        reaperLines[i+1].setStartX(solution.actions[i][0].target.x);
        reaperLines[i+1].setStartY(solution.actions[i][0].target.y);
        destroyerLines[i+1].setStartX(solution.actions[i][1].target.x);
        destroyerLines[i+1].setStartY(solution.actions[i][1].target.y);
        doofLines[i+1].setStartX(solution.actions[i][2].target.x);
        doofLines[i+1].setStartY(solution.actions[i][2].target.y);
      }
    }
    
  }
}
