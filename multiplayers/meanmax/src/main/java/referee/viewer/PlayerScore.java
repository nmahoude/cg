package referee.viewer;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import meanmax.Player;

public class PlayerScore extends Group {
  
  private Text score;
  private Text rage;

  public PlayerScore() {
    score = new Text(0, 0, "");
    score.setFill(Color.BLUE);
    this.getChildren().add(score);  

    rage = new Text(0, 20, "");
    rage.setFill(Color.BLUE);
    this.getChildren().add(rage);  

  }
  
  public void update(Player player) {
    score.setText("Score: "+player.score);
    rage.setText("Rage: "+player.rage);
  }
}
