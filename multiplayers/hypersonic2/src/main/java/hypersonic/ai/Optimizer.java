package hypersonic.ai;

import hypersonic.Cache;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.ai.search.Search;
import hypersonic.entities.Bomberman;
import hypersonic.simulation.Simulation;
import hypersonic.utils.P;

public class Optimizer {
  private static Move allMoves[] = new Move[100];
  private static State state = new State();
  private static Simulation simulator = new Simulation(state);
  static double intermediateScores[] = new double[Search.DEPTH];

  
  public static double optimizeBombs(Move bestMoves[], double bestScore, int depth, State model, boolean dropEnnemyBombs) {
    System.err.println("Bomb optimizer - current bestScore is "+bestScore);
    if (bestMoves[0] == null || !bestMoves[0].dropBomb) {
      System.err.println("Not a bomb, early exit");
      return bestScore;
    }
    // do one last test with the same moves, but do not bomb the 1st move !
    switch (bestMoves[0]) {
    case STAY_BOMB:
      allMoves[0] = Move.STAY;
      break;
    case RIGHT_BOMB:
      allMoves[0] = Move.RIGHT;
      break;
    case LEFT_BOMB:
      allMoves[0] = Move.LEFT;
      break;
    case UP_BOMB:
      allMoves[0] = Move.UP;
      break;
    case DOWN_BOMB:
      allMoves[0] = Move.DOWN;
      break;
    }
    for (int i = 1; i < depth; i++) {
      allMoves[i] = bestMoves[i];
    }
    double score = 0;
    state.copyFrom(model);
    for (int t = 0; t < depth; t++) {
      state.players[0].points = 0;
      state.players[1].points = 0;
      state.players[2].points = 0;
      state.players[3].points = 0;
      dropEnnemiesBombIfNeeded(dropEnnemyBombs, t);
      simulator.simulate(allMoves[t]);
      double intermediateScore = Score.score(state, t, allMoves[t]);
      score += intermediateScore;
      intermediateScores[t] = intermediateScore;
      if (state.players[Player.myId].isDead) {
        break;
      }
    }
    System.err.println("IntScore : ");
    for (int i=0;i<Search.DEPTH;i++) {
      System.err.print(intermediateScores[i]+" , ");
    }

    if (score > bestScore) {
      System.err.println("Solutions without bomb at 0 is better ! " + score + " > " + bestScore);
      bestScore = score;
      bestMoves[0] = allMoves[0];
    } else {
      System.err.println("Solution without a bomb is worse " +score);
    }
    return bestScore;
  }

  public static double optimizeMoves(Move bestMoves[], double bestScore, int depth, State model, boolean dropEnnemyBombs) {
    System.err.println("Move optimizer - current bestScore is "+bestScore);
    if (bestMoves[0] == null && bestMoves[0] != Move.STAY && bestMoves[0] != Move.STAY_BOMB) {
      System.err.println("Not a STAY, early exit");
      return bestScore;
    }
    
    int newDepth = 0;
    boolean needBombNext = false;
    for (int i = 0; i < depth; i++) {
      if (bestMoves[i] == Move.STAY) continue;
      if (bestMoves[i] == Move.STAY_BOMB) {
        needBombNext = true;
        continue;
      }
      Move nextMove = bestMoves[i];
      if (needBombNext) {
        switch (nextMove) {
        case STAY:
          nextMove = Move.STAY_BOMB;
          break;
        case RIGHT:
          nextMove = Move.RIGHT_BOMB;
          break;
        case LEFT:
          nextMove = Move.LEFT_BOMB;
          break;
        case UP:
          nextMove = Move.UP_BOMB;
          break;
        case DOWN:
          nextMove = Move.DOWN_BOMB;
          break;
        }
      }
      
      allMoves[newDepth++] = nextMove;
      needBombNext = false;
    }
    
    depth = newDepth;
    
    double score = 0;
    state.copyFrom(model);
    for (int t = 0; t < depth; t++) {
      state.players[0].points = 0;
      state.players[1].points = 0;
      state.players[2].points = 0;
      state.players[3].points = 0;
      P newPos = P.get(state.players[Player.myId].position.x +  allMoves[t].dx, 
          state.players[Player.myId].position.y +  allMoves[t].dy);
      if (!state.canWalkOn(newPos)) {
        return bestScore;
      }
      
      dropEnnemiesBombIfNeeded(dropEnnemyBombs, t);
      simulator.simulate(allMoves[t]);
      double intermediateScore = Score.score(state, t, allMoves[t]);
      score += intermediateScore;
      intermediateScores[t] = intermediateScore;
      if (state.players[Player.myId].isDead) {
        score = bestScore;
        break;
      }
    }
    System.err.println("IntScore : ");
    for (int i=0;i<depth;i++) {
      System.err.print(intermediateScores[i]+" , ");
    }

    if (score > bestScore) {
      System.err.println("Solutions with move optimization is better ! " + score + " > " + bestScore);
      bestScore = score;
      bestMoves[0] = allMoves[0];
    } else {
      System.err.println("Solution cannot be optimize with moves " +score);
    }
    return bestScore;
  }
  
  static private void dropEnnemiesBombIfNeeded(boolean dropEnnemyBombs, int t) {
    if (dropEnnemyBombs && t == 0) {
      // for all players different than me and who can, drop a bomb at first one
      for (int i = 0; i < 4; i++) {
        if (i == Player.myId)
          continue;
        Bomberman b = state.players[i];
        if (b.isDead || b.bombsLeft == 0)
          continue;
        state.addBomb(Cache.popBomb(i, b.position, 8, b.currentRange));
      }
    }
  }
}
