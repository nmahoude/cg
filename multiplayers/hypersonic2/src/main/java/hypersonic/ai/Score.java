package hypersonic.ai;

import hypersonic.Board;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.entities.Bomberman;
import hypersonic.simulation.Simulation;

public class Score {
  private static final double BOX_DESTROYED_BONUS = 10_000.0;
  public static final double DEAD_MALUS = -100_000_000;
  public static final double WALK_OVER_MALUS = -50_000;
  public static double patience[];
  static {
    patience = new double[100];
    for (int i=0;i<100;i++) {
      patience[i] =Math.pow(0.9, i);
    }
  }
  public static double score(State state, int depth, Move move) {
    double score = 0.0;
    Bomberman me = state.players[Player.myId];

    if (me.isDead) {
      return DEAD_MALUS;
    }

    if (depth == 0) {
      score += Player.KILLERBOMB_BONUS; // if we can reduce heavily the player movability (and maybe by kill him)
      
      // on first step, big malus for stepping over a opponent that has bomb (means we follow him)
      for (int i=0;i<4;i++) {
        if (i == Player.myId) continue;
        Bomberman p = state.players[i];
        if (p.isDead) continue;
        if (p.bombsLeft > 0 && p.position.x == me.position.x && p.position.y == me.position.y) {
          score += WALK_OVER_MALUS;
        }
      }
    }
    
    score += BOX_DESTROYED_BONUS * me.points;
    
    if (me.bombCount < 4) {
      score += 5000 * Simulation.deltaBomb;
    } else {
      // 0
      score += 1.1 * Simulation.deltaBomb;
    }
    if (me.currentRange < 8) {
      score += 500.0 * Simulation.deltaRange;
    } else if (me.currentRange < 5) {
      score += 0.1*Simulation.deltaRange;
    } else {
      // 0
    }

    if (!move.dropBomb) {
      score += 1000;
    }

    if (move == Move.STAY || move == Move.STAY_BOMB) {
      score -= 1.0;
    }
    
    score -= 10.0 * Math.abs(me.position.x - Board.WIDTH/2.);
    score -= 10.0 * Math.abs(me.position.y - Board.HEIGHT/2.);

//      score += 1.0 * HeatMap.score[me.position.x+Board.WIDTH*me.position.y];
    
    // TODO : if we are losing, better go next to our rivals and bomb them 
//    if (state.board.boxCount == 0) {
//      // go far from others
//      for (int i=0;i<4;i++) {
//        if (i == Player.myId) continue;
//        Bomberman b = state.players[i];
//        if (b.isDead) continue;
//        score += 1.0 * b.position.manhattanDistance(state.players[Player.myId].position);
//      }
//    }
    
    return patience[depth] * score;
  }
}
