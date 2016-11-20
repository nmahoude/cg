package hypersonic;

import java.util.ArrayList;
import java.util.List;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.utils.P;

public class Simulation {
  private static final int DEAD_MALUS = -999;
  private static final int BOX_BONUS = 8;
  public Board board;
  
  public final double getScoreHeuristic() {
    if (board.me.isDead) {
      return DEAD_MALUS;
    }
    if (board.boxCount > 0) {
      return 8*board.me.points+board.me.bombsLeft + Math.max(board.me.bombCount, 10)+Math.max(10, board.me.currentRange);
    } else {
      return 13-board.me.position.manhattanDistance(P.get(7, 5));
    }
  }
  public final boolean isFinished() {
    return false;
  }
  
  public List<Move> getPossibleMoves() {
    final List<Move> moves = new ArrayList<>();
    for (final Move move : Move.values()) {
      if (isMovePossible(move)) {
        moves.add(move);
      }
    }
    return moves;
  }
  
  private boolean horizontallyPossible(int x, int y) {
    return (!(y % 2 == 1 && x % 2 == 0));
  }
  
  private boolean verticallyPossible(int x, int y) {
    return (!(y % 2 == 0 && x % 2 == 1));
  }
  
  public final boolean isMovePossible(final Move move) {
    final Bomberman me = board.me;
    int x = me.position.x;
    int y = me.position.y;
    switch(move) {
      case DOWN:
        return verticallyPossible(x, y) && 
            board.canMoveTo(x, y+1);
      case DOWN_BOMB:
        return me.bombsLeft > 0 && 
            verticallyPossible(x, y) && 
            board.canMoveTo(x, y+1);
      case LEFT:
        return horizontallyPossible(x, y) && 
            board.canMoveTo(x-1, y);
      case LEFT_BOMB:
        return me.bombsLeft > 0 && 
            horizontallyPossible(x, y) &&
            board.canMoveTo(x-1, y);
      case RIGHT:
        return horizontallyPossible(x, y) && 
            board.canMoveTo(x+1, y);
      case RIGHT_BOMB:
        return me.bombsLeft > 0 &&
            horizontallyPossible(x, y) &&
            board.canMoveTo(x+1, y);
      case STAY:
        return true;
      case STAY_BOMB:
        return me.bombsLeft > 0 && true;
      case UP:
        return verticallyPossible(x, y) && 
            board.canMoveTo(x, y-1);
      case UP_BOMB:
        return me.bombsLeft > 0 && 
            verticallyPossible(x, y) && 
            board.canMoveTo(x, y-1);
      default:
        return false;
    }
  }
  public final void copyFrom(final Simulation simulation) {
    board = simulation.board.duplicate();
  }
  public final void simulate(final Move move) {
    board.destructedBox = 0;
    final List<Bomb> bombs = new ArrayList<>(board.bombs);
    for (final Bomb bomb : bombs) {
      bomb.update();
    }
    simulateMove(move);
  }
  private void simulateMove(final Move move) {
    int newX = board.me.position.x;
    int newY = board.me.position.y;
    boolean dropBomb = false;
    switch(move) {
      case DOWN_BOMB:
        dropBomb = true;
      case DOWN:
        newY+=1;
        break;
      case LEFT_BOMB:
        dropBomb = true;
      case LEFT:
        newX-=1;
        break;
      case RIGHT_BOMB:
        dropBomb = true;
      case RIGHT:
        newX+=1;
        break;
      case STAY_BOMB:
        dropBomb = true;
      case STAY:
        break;
      case UP_BOMB:
        dropBomb = true;
      case UP:
        newY-=1;
    }
    
    if (dropBomb) {
      board.addBomb(Bomb.create(board, board.me.owner, board.me.position, 8, board.me.currentRange));
      board.me.bombsLeft-=1;
    }
    if (newX != board.me.position.x || newY != board.me.position.y) {
      board.walkOn(board.me, P.get(newX, newY));
    }
  }
}
