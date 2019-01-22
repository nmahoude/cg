package hypersonic.ai;

import static java.lang.Math.min;

import hypersonic.Board;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.entities.Bomberman;

public class Score {

  
  public static double new_score(State state) {
    double score = 0.0;

    Bomberman me = state.players[Player.myId];

    score += 10.0 * me.points;
    score += 0.8 * min(5, me.currentRange) + 0.4 * me.currentRange;
    score += 3.0 * min(2, me.bombCount)  + 1.1 * min(4, me.bombCount)  + 0.5 * me.bombCount;
    score -= 0.05 * Math.abs(me.position.x - Board.WIDTH/2.);
    score -= 0.05 * Math.abs(me.position.y - Board.HEIGHT/2.);
    return score;
  }

  
  public static double score(State state) {
    double score = 0.0;
    if (state.players[Player.myId].isDead) {
      return -1_000_000;
    }
    Bomberman me = state.players[Player.myId];

    score += 10_000.0 * me.points;
    if (me.bombCount < 5) {
      score += 10.0 * me.bombCount;
    } else {
      score += 1.1 * me.bombCount;
    }
    if (me.currentRange < 8) {
      score += 5.0 * me.currentRange;
    } else {
      score += me.currentRange;
    }
    score += 5.0 * me.bombsLeft;

    score -= 0.05 * Math.abs(me.position.x - Board.WIDTH/2.);
    score -= 0.05 * Math.abs(me.position.y - Board.HEIGHT/2.);

    // TODO : if we are losing, better go next to our rivals and bomb them 
    if (state.board.boxCount == 0) {
      // go far from others
      for (int i=0;i<4;i++) {
        if (i == Player.myId) continue;
        Bomberman b = state.players[i];
        if (b.isDead) continue;
        score += 1.0 * b.position.manhattanDistance(state.players[Player.myId].position);
      }
    }
    
    return score;
  }

}
