package hypersonic.simulation;

import hypersonic.Board;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.entities.Bomberman;

public class MoveGenerator {

  public State state;

  public MoveGenerator(State board) {
    this.state = board;
  }

  public int getPossibleMovesWithoutBombs(Move[] moves) {
    return getPossibleMoves(moves, false);
  }
  
  // with bombs
  public int getPossibleMoves(Move[] moves) {
    boolean canBomb = state.players[Player.myId].bombsLeft > 0;
    return getPossibleMoves(moves, canBomb);
  }
  
  private int getPossibleMoves(Move[] moves, boolean wantToBomb) {
    Bomberman me = state.players[Player.myId];
    int x = me.position.x;
    int y = me.position.y;
    
    int movesFE = 0;
    moves[movesFE++] = Move.STAY;
    boolean canMoveUp = (y != 0) && (x & 0b1) == 0 && isMovePossible(Move.UP);
    boolean canMoveDown = (y != Board.HEIGHT-1) && (x & 0b1) == 0 && isMovePossible(Move.DOWN);
    boolean canMoveLeft = (x != 0) && (y & 0b1) == 0 && isMovePossible(Move.LEFT);
    boolean canMoveRight = (x != Board.WIDTH-1) && (y & 0b1) == 0 && isMovePossible(Move.RIGHT);

    
    if (canMoveUp) moves[movesFE++] = Move.UP;
    if (canMoveDown) moves[movesFE++] = Move.DOWN;
    if (canMoveLeft) moves[movesFE++] = Move.LEFT;
    if (canMoveRight) moves[movesFE++] = Move.RIGHT;
    
    if (wantToBomb && state.board.cells[me.position.offset] != Board.BOMB) {
      moves[movesFE++] = Move.STAY_BOMB;
      if (canMoveUp) moves[movesFE++] = Move.UP_BOMB;
      if (canMoveDown) moves[movesFE++] = Move.DOWN_BOMB;
      if (canMoveLeft) moves[movesFE++] = Move.LEFT_BOMB;
      if (canMoveRight) moves[movesFE++] = Move.RIGHT_BOMB;
    }
    
    return movesFE;
  }
  
  public final boolean isMovePossible(Move move) {
    if (move == Move.STAY) return true;
    Bomberman me = state.players[Player.myId];
    return state.board.canMoveTo(me.position.move(move));
  }

}
