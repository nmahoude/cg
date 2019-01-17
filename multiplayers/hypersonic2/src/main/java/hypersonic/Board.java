package hypersonic;

import java.util.ArrayList;
import java.util.List;

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

  int rot[][] = {
      { 1, 0 },
      { 0, 1 },
      { -1, 0 },
      { 0, -1 }
  };

  public int cells[];
  public int turn;
  
  public Bomb bombs[] = new Bomb[MAX_BOMBS];
  public int bombsFE = 0;
  
  public Bomberman players[] = new Bomberman[4];
  public int playersFE = 0;
  public Bomberman me;

  int destructedBox;
  int boxCount;

  public Board() {
    cells = new int[WIDTH*HEIGHT];
  }
  private void clean() {
    bombsFE = 0;
    playersFE = 0;
    me = null;
    destructedBox = 0;
    boxCount = 0;
    // cells will be copied later
  }


  public void copyFrom(Board model) {
    this.clean();
    this.boxCount = model.boxCount;
    
    this.turn = model.turn;
    
    // copy des bombes non explosées (timer atteint ou chain-exploded ie ==null) 
    bombsFE = 0;
    for (int i=0;i<model.bombsFE;i++) {
      Bomb b = model.bombs[i];
      if (b != null && b.timer >= turn) {
        this.bombs[this.bombsFE++] = b;
      }
    }
    
    this.playersFE = 0;
    for (int p=0;p<model.playersFE;p++) {
      Bomberman b = model.players[p];
      final Bomberman copy = BombermanCache.pop();
      copy.copyFrom(b);
      if (b == model.me) {
        this.me = copy;
      }
      this.players[playersFE++] = copy;
    }
    
    System.arraycopy(model.cells, 0, this.cells, 0, WIDTH*HEIGHT);
  }
  
  public void init() {
    bombsFE = 0;
    playersFE = 0;

    destructedBox = 0;
    boxCount = 0;
  }

  public void init(final int y, final String row) {
    for (int x = 0; x < WIDTH; x++) {
      final char value = row.charAt(x);
      cells[x+WIDTH*y] = value;
      if (value == BOX || value == BOX_1 || value == BOX_2) {
        boxCount++;
      }
    }
  }

  public void updateBombs() {
    turn++;
    
    // simulate one turn
    for (int i=0;i<bombsFE;i++) {
      final Bomb b = bombs[i];
      if (b == null) continue;
      
      if (b.timer == turn) {
        bombs[i] = null; // not in this board anymore !
        explode(b);
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

  public void addBomb(final Bomb bomb) {
    bombs[bombsFE++] = bomb;
    cells[bomb.position.x+WIDTH*bomb.position.y] = BOMB;
  }
  
  final Bomb bombsToExplode[] = new Bomb[MAX_BOMBS];
  int bombsToExplodeFE = 0;
  void explode(final Bomb bomb) {
    bombsToExplodeFE = 0;
    
    bombsToExplode[bombsToExplodeFE++] = bomb;
    int currentBombToExplode = 0;
    while (currentBombToExplode != bombsToExplodeFE) {
      final Bomb b = bombsToExplode[currentBombToExplode++];
      if (b == null) continue; // already exploded
      
      final Bomberman orginalBomberman = getBombermanWithId(b.owner);
      if (orginalBomberman != null) {
        orginalBomberman.bombsLeft+=1;
      }
      final int range = b.range;
      final P p = b.position;
      
      checkExplosion(orginalBomberman, p.x, p.y); // at (0,0)
      for (int r = 0; r < 4; r++) {
        final int dx = rot[r][0];
        final int dy = rot[r][1];
        for (int d = 1; d < range; d++) {
          final int x = p.x + d*dx;
          final int y = p.y + d*dy;
          final boolean shouldStopExplosion = checkExplosion(orginalBomberman, x, y);
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

  private boolean checkExplosion(Bomberman orginalBomberman, int x, int y) {
    if (isOnBoard(x, y)) {
      final int value = cells[x+WIDTH*y];
      if (value == WALL) {
        return true; // stop explosion
      }
      
      for (int p=0;p<playersFE;p++) {
        Bomberman bomberman = players[p];
        if (bomberman.position == P.get(x,y)) {
          bomberman.isDead = true;
        }
      }

      switch (value) {
        case BOX:
          updatePoints(orginalBomberman);
          cells[x+WIDTH*y] = EMPTY;
          return true;
        case BOX_1:
          updatePoints(orginalBomberman);
          cells[x+WIDTH*y] = ITEM_1;
          return true;
        case BOX_2:
          updatePoints(orginalBomberman);
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
  
  private void updatePoints(Bomberman orginalBomberman) {
    destructedBox++;
    boxCount--;
    if (orginalBomberman != null) {
      orginalBomberman.points++;
    }
  }

  private Bomberman getBombermanWithId(final int owner) {
    for (int p=0;p<playersFE;p++) {
      Bomberman b = players[p];
      if (b.owner == owner) {
        return b;
      }
    }
    return null;
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

  final boolean isOnBoard(final int x, final int y) {
    if (x < 0 || x > WIDTH-1) {
      return false;
    }
    if (y < 0 || y > HEIGHT-1) {
      return false;
    }
    return true;
  }

  public void addPlayer(Bomberman player) {
    players[playersFE++] = player;
  }

  public void addItem(Item item) {
    if (item.type == 1) {
      cells[item.position.x+WIDTH*item.position.y] = ITEM_1;
    } else {
      cells[item.position.x+WIDTH*item.position.y] = ITEM_2;
    }
  }

  public boolean canMoveTo(final int x, final int y) {
    if (!isOnBoard(x, y)) {
      return false;
    }
    
    final int value = cells[x+WIDTH*y];
    return value == EMPTY || value == ITEM_1 || value == ITEM_2;
  }

  public void walkOn(final Bomberman player, final P p) {
    final int value = cells[p.x+WIDTH*p.y];
    if ( value == ITEM_1) {
      player.currentRange+=1;
    } else if (value == ITEM_2) {
      player.bombsLeft+=1;
      player.bombCount+=1;
    }
    player.position = p;
    cells[p.x+WIDTH*p.y] = EMPTY;
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
  
  public String getDebugString() {
    String output="";
    for (int y=0;y<HEIGHT;y++) {
      for (int x=0;x<WIDTH;x++) {
        output +=(char)(cells[y*WIDTH+x]);
      }
      output+="\n";
    }
    return output;
  }
  
}
