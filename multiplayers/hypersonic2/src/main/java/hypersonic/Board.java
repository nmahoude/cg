package hypersonic;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.utils.P;

public class Board {
  static final int HEIGHT = 11;
  static final int WIDTH = 13;
  private static final int MAX_BOMBS = 16; // NOTE : risk taken, only 16 bombs on the board max
  
  public static final int EMPTY = '.';
  public static final int WALL = 'X';
  
  public static final int BOX = '0';
  public static final int BOX_1 = '1';
  public static final int BOX_2 = '2';
  
  public static final int ITEM_1 = 'l';
  public static final int ITEM_2 = 'k';
  public static final int BOMB = 'b';
  static int rot[][] = {
      { 1, 0 },
      { 0, 1 },
      { -1, 0 },
      { 0, -1 }
  };

  int cells[];
  Bomb bombs[] = new Bomb[MAX_BOMBS];
  int bombsFE = 0;
  int boxCount;
  
  
  Board() {
    cells = new int[WIDTH*HEIGHT];
  }


  public void clean() {
    bombsFE = 0;
    boxCount = 0;
  }
  public void init() {
    bombsFE = 0;
    boxCount = 0;
  }
  public void init(int y, String row) {
    for (int x = 0; x < WIDTH; x++) {
      final char value = row.charAt(x);
      cells[x+WIDTH*y] = value;
      if (value == BOX || value == BOX_1 || value == BOX_2) {
        boxCount++;
      }
    }
  }

  public void copyFrom(Board model) {
    this.boxCount = model.boxCount;
    
    // copy des bombes non explosées (timer atteint ou chain-exploded ie ==null) 
    bombsFE = 0;
    for (int i=0;i<model.bombsFE;i++) {
      Bomb b = model.bombs[i];
      if (b != null) {
        this.bombs[this.bombsFE++] = b;
      }
    }
    System.arraycopy(model.cells, 0, this.cells, 0, WIDTH*HEIGHT);

  }


  public void updateBombs(State state) {
    // simulate one turn
    for (int i=0;i<bombsFE;i++) {
      final Bomb b = bombs[i];
      if (b == null) continue;
      
      if (b.timer == state.turn) {
        bombs[i] = null; // not in this board anymore !
        explode(state, b);
      }
    }
    
    // rearrange bombs
    int current = 0;
    for (int i=0;i<bombsFE;i++) {
      final Bomb b = bombs[i];
      if (b != null) bombs[current++] = b;
    }
    bombsFE = current;
  }

  final Bomb bombsToExplode[] = new Bomb[MAX_BOMBS];
  int bombsToExplodeFE = 0;
  void explode(State state, Bomb bomb) {
    bombsToExplodeFE = 0;
    
    bombsToExplode[bombsToExplodeFE++] = bomb;
    int currentBombToExplode = 0;
    while (currentBombToExplode != bombsToExplodeFE) {
      final Bomb b = bombsToExplode[currentBombToExplode++];
      if (b == null) continue; // already exploded
      
      final Bomberman orginalBomberman = state.getBombermanWithId(b.owner);
      if (orginalBomberman != null) {
        orginalBomberman.bombsLeft+=1;
      }
      final int range = b.range;
      final P p = b.position;
      
      checkExplosion(state, orginalBomberman, p.x, p.y); // at (0,0)
      for (int r = 0; r < 4; r++) {
        final int dx = rot[r][0];
        final int dy = rot[r][1];
        for (int d = 1; d < range; d++) {
          final int x = p.x + d*dx;
          final int y = p.y + d*dy;
          final boolean shouldStopExplosion = checkExplosion(state, orginalBomberman, x, y);
          if (shouldStopExplosion) {
            break;
          }
        }
      }
    }

    // clear bombs places
    for (int b=0;b<bombsToExplodeFE;b++) {
      Bomb removedBombs = bombsToExplode[b];
      int x = removedBombs.position.x;
      int y = removedBombs.position.y;
      cells[x+WIDTH*y] = EMPTY;
    }
  }

  private boolean checkExplosion(State state, Bomberman orginalBomberman, int x, int y) {
    if (isOnBoard(x, y)) {
      final int value = cells[x+WIDTH*y];
      if (value == WALL) {
        return true; // stop explosion
      }
      
      state.killPlayersAt(x,y);
      
      switch (value) {
        case BOX:
          state.updatePoints(orginalBomberman);
          boxCount--;

          cells[x+WIDTH*y] = EMPTY;
          return true;
        case BOX_1:
          state.updatePoints(orginalBomberman);
          boxCount--;

          cells[x+WIDTH*y] = ITEM_1;
          return true;
        case BOX_2:
          state.updatePoints(orginalBomberman);
          boxCount--;

          cells[x+WIDTH*y] = ITEM_2;
          return true;
        case ITEM_1:
        case ITEM_2:
          cells[x+WIDTH*y] = EMPTY;
          return true; // stop explosion
        case BOMB:
          int bombIndex = getBombIndexAt(x, y);
          if (bombIndex != -1) {
            Bomb newBombToExplode = bombs[bombIndex];
            // this bomb will explode and will not be here anymore (can't change state of bomb anymore
            bombs[bombIndex] = null; 
            bombsToExplode[bombsToExplodeFE++] = newBombToExplode;
          }
          return true; // stop explosion
      }
    } else {
      return true; // not on board
    }
    return false;
  }

  private int getBombIndexAt(final int x, final int y) {
    for (int i=0;i<bombsFE;i++) {
      Bomb b = bombs[i];
      
      if (b != null && b.position.x == x && b.position.y == y) {
        return i;
      }
    }
    return -1;
  }

  
  public void addBomb(Bomb bomb) {
    bombs[bombsFE++] = bomb;
    cells[bomb.position.x+WIDTH*bomb.position.y] = BOMB;
  }


  public boolean isOnBoard(int x, int y) {
    if (x < 0 || x > WIDTH-1) {
      return false;
    }
    if (y < 0 || y > HEIGHT-1) {
      return false;
    }
    return true;
  }


  public void addItem(Item item) {
    if (item.type == 1) {
      cells[item.position.x+WIDTH*item.position.y] = ITEM_1;
    } else {
      cells[item.position.x+WIDTH*item.position.y] = ITEM_2;
    }
  }

  public boolean canWalkOn(final P p) {
    final int value = cells[p.x+WIDTH*p.y];
    return value != Board.WALL 
        && value != Board.BOX
        && value != Board.BOX_1
        && value != Board.BOX_2
        && value != Board.BOMB
        ;
  }

  public boolean canMoveTo(int x, int y) {
    if (!isOnBoard(x, y)) {
      return false;
    }
    
    final int value = cells[x+WIDTH*y];
    return value == EMPTY || value == ITEM_1 || value == ITEM_2;
  }
  
}
