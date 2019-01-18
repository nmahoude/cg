package hypersonic.simulation;

import hypersonic.State;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.entities.Bomberman;

public class MoveGenerator {

  private State board;

  public MoveGenerator(State board) {
    this.board = board;
  }

  public int getPossibleMovesWithoutBombs(Move[] moves) {
    int movesFE = 0;
    moves[movesFE++] = Move.STAY;
    if (isMovePossible(Move.UP)) moves[movesFE++] = Move.UP;
    if (isMovePossible(Move.LEFT)) moves[movesFE++] = Move.LEFT;
    if (isMovePossible(Move.RIGHT)) moves[movesFE++] = Move.RIGHT;
    if (isMovePossible(Move.DOWN)) moves[movesFE++] = Move.DOWN;
    
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
  
  public final boolean isMovePossible(Move move) {
    if (move == Move.STAY) return true;
    Bomberman me = board.players[Player.myId];
    int x = me.position.x;
    int y = me.position.y;
    if (move.dropBomb && me.bombsLeft <=0) return false;
    return board.canMoveTo(x + move.dx , y + move.dy);
  }

}
