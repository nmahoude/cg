package hypersonic.simulation;

import hypersonic.Board;
import hypersonic.Move;
import hypersonic.entities.Bomberman;

public class MoveGenerator {

  private Board board;

  public MoveGenerator(Board board) {
    this.board = board;
  }

  public int getPossibleMovesWithoutBombs(Move[] moves) {
    int movesFE = 0;
    if (isMovePossible(Move.UP)) moves[movesFE++] = Move.UP;
    if (isMovePossible(Move.LEFT)) moves[movesFE++] = Move.LEFT;
    if (isMovePossible(Move.RIGHT)) moves[movesFE++] = Move.RIGHT;
    if (isMovePossible(Move.DOWN)) moves[movesFE++] = Move.DOWN;
    if (isMovePossible(Move.STAY)) moves[movesFE++] = Move.STAY;
    return movesFE;
  }
  
  public int getPossibleMoves(Move[] moves) {
    int movesFE = 0;
    for (final Move move : Move.values()) {
      if (isMovePossible(move)) {
        moves[movesFE++] = move;
      }
    }
    return movesFE;
  }
  
  public final boolean isMovePossible(final Move move) {
    final Bomberman me = board.me;
    int x = me.position.x;
    int y = me.position.y;
    switch(move) {
      case DOWN:
        return board.canMoveTo(x, y+1);
      case DOWN_BOMB:
        return me.bombsLeft > 0 && 
            board.canMoveTo(x, y+1);
      case LEFT:
        return board.canMoveTo(x-1, y);
      case LEFT_BOMB:
        return me.bombsLeft > 0 &&
            board.canMoveTo(x-1, y);
      case RIGHT:
        return board.canMoveTo(x+1, y);
      case RIGHT_BOMB:
        return me.bombsLeft > 0 &&
            board.canMoveTo(x+1, y);
      case STAY:
        return true;
      case STAY_BOMB:
        return me.bombsLeft > 0 && true;
      case UP:
        return board.canMoveTo(x, y-1);
      case UP_BOMB:
        return me.bombsLeft > 0 && 
            board.canMoveTo(x, y-1);
      default:
        return false;
    }
  }

}
