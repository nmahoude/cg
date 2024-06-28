package view;

import pac.Player;
import pac.State;
import pac.agents.Pacman;
import pac.map.Pos;

public class PacView {

  
  public static void attachGame(State state) {
    
    CodingameView.game.setFunction(coords -> {
      if (Player.map.isWall(Pos.get(coords[0], coords[1]))) {
        return "#99999999";
      } else {
        return null;
      }
      
    });
    
    for (int i=0;i<5;i++) {
      final Pacman pacman = state.pacmen[i]; 
      if (pacman == null) continue;
      
      CodingameView.agents[i].setFunction(coords -> {
        if (pacman.pos.x == coords[0] && pacman.pos.y == coords[1]) {
          return "#FF0000";
        } else {
          return null;
        }
      });
    }
  }
  
}
