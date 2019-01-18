package hypersonic.simulation;

import hypersonic.Board;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.entities.Bomberman;

public class MoveGeneratorWithCache {
  private static Move movesPerPosition[][] = new Move[Board.WIDTH * Board.HEIGHT][];
  private static Move movesPerPositionWithBombs[][] = new Move[Board.WIDTH * Board.HEIGHT][];
  
  static {
    // initialize Moves per positions
    for (int y=0;y<Board.HEIGHT;y++) {
      for (int x=0;x<Board.WIDTH;x++) {
        int size = 1; // stay
        if (x % 2 == 0) size+=2; // y
        if (y % 2 == 0) size+=2; // x
        if (y == 0 || y == Board.HEIGHT-1) size-=1; // border
        if (x == 0 || x == Board.WIDTH-1) size-=1; // border
        Move[] moves = new Move[size];
        int i = 0;
        moves[i++] = Move.STAY;
        if (y != 0 && x % 2 == 0) moves[i++] = Move.UP;
        if (y != Board.HEIGHT-1 && x % 2 == 0) moves[i++] = Move.DOWN;
        if (x != 0 && y % 2 == 0) moves[i++] = Move.LEFT;
        if (x != Board.WIDTH-1 && y %2 == 0) moves[i++] = Move.RIGHT;
        set(x, y, moves);
      }
    }
    
  }  

  private static void set(int x, int y, Move[] moves) {
    movesPerPosition[x + Board.WIDTH * y] = moves;
    
    Move[] movesWithBombs = new Move[moves.length * 2];
    int index = 0;
    for (Move move : moves) {
      if (move == Move.STAY) movesWithBombs[index++] = Move.STAY_BOMB;
      if (move == Move.UP) movesWithBombs[index++] = Move.UP_BOMB;
      if (move == Move.RIGHT) movesWithBombs[index++] = Move.RIGHT_BOMB;
      if (move == Move.DOWN) movesWithBombs[index++] = Move.DOWN_BOMB;
      if (move == Move.LEFT) movesWithBombs[index++] = Move.LEFT_BOMB;
      movesWithBombs[index++] = move;
    }
    movesPerPositionWithBombs[x + Board.WIDTH * y] = movesWithBombs;
  }

  
  private State board;

  public MoveGeneratorWithCache(State board) {
    this.board = board;
  }

  public int getPossibleMovesWithoutBombs(Move[] moves) {
    Bomberman me = board.players[Player.myId];
    int x = me.position.x;
    int y = me.position.y;

    int movesFE = 0;
    Move possibleMoves[] = movesPerPosition[x + Board.WIDTH*y];
    for (final Move move : possibleMoves) {
      if (isMovePossible(move)) {
        moves[movesFE++] = move;
      }
    }
    return movesFE;
  }
  
  public int getPossibleMoves(Move[] moves) {
    Bomberman me = board.players[Player.myId];
    int x = me.position.x;
    int y = me.position.y;

    int movesFE = 0;
    Move possibleMoves[] = movesPerPositionWithBombs[x + Board.WIDTH*y];
    for (final Move move : possibleMoves) {
      if (isMovePossible(move)) {
        moves[movesFE++] = move;
      }
    }
    return movesFE;
  }
  
  public final boolean isMovePossible(Move move) {
    Bomberman me = board.players[Player.myId];
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
