package csb.simulation;

import java.util.List;

import csb.Map;
import trigonometry.Vector;

public class Simulate {
  
  private Map map;

  public void play(Map map, List<Action[]> actions) {
    this.map = map;
    for (Action[] a : actions) {
      playOneTurn(a);
    }
    restoreState(map);
  }

  private void playOneTurn(Action[] actions) {
    applyMoves(actions);
    move();
    
  }

  private void move() {
    // TODO Handle collision !
    for (int i=0;i<4;i++) {
      map.pods[i].position = map.pods[i].position.add(map.pods[i].speed);
    }
  }

  private void applyMoves(Action[] actions) {
    for (int i=0;i<actions.length;i++) {
      double angle = actions[i].angle;
      Vector dir = new Vector(Math.cos(angle), Math.sin(angle));
      map.pods[i].apply(dir,actions[i].thrust);
    }
  }

  private void restoreState(Map map) {
    for (int i=0;i<4;i++) {
      map.pods[i].restore();
    }
  }
}
