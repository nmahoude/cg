package hypersonic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.utils.P;

public class Board {
  static public Deque<Board> cache = new ArrayDeque<>();
  static {
    for (int i=0;i<10000;i++) {
      cache.push(new Board());
    }
  }
  public static void retrocede(final Board board) {
    for (final Bomb b : board.bombs) {
      Bomb.cache.retrocede(b);
    }
    for (final Bomberman b : board.players) {
      Bomberman.cache.retrocede(b);
    }
    for (final Item i: board.items) {
      Item.cache.retrocede(i);
    }
    cache.push(board);
  }

  
  public static final char EMPTY = '.';
  public static final char WALL = 'X';
  
  public static final char BOX = '0';
  public static final char BOX_1 = '1';
  public static final char BOX_2 = '2';
  
  public static final char ITEM_1 = 'l';
  public static final char ITEM_2 = 'k';
  public static final char BOMB = 'b';

  int rot[][] = {
      { 1, 0 },
      { 0, 1 },
      { -1, 0 },
      { 0, -1 }
  };

  public int cells[];

  List<Bomb> bombs = new ArrayList<>();
  List<Item> items = new ArrayList<>();
  public List<Bomberman> players = new ArrayList<>();
  public Bomberman me;

  int destructedBox;
  int boxCount;

  public Board() {
    cells = new int[13*11];
  }
  private void clean() {
    bombs.clear();
    items.clear();
    players.clear();
    me = null;
    destructedBox = 0;
    boxCount = 0;
    // cells will be copied later
  }

  
  public Board duplicate() {
    Board board;
    if (cache.isEmpty()) {
      board = new Board();
    } else {
      board = cache.pop();
    }
    board.clean();
    
    board.boxCount = boxCount;

    for (final Bomb b : this.bombs) {
      board.bombs.add(b.duplicate(board));
    }
    for (final Item i : this.items) {
      board.items.add(i.duplicate(board));
    }
    for (final Bomberman b : this.players) {
      final Bomberman copy = b.duplicate(board);
      if (b == this.me) {
        board.me = copy;
      }
      board.players.add(copy);
    }
    
    System.arraycopy(cells, 0, board.cells, 0, 13*11);
    return board;
  }
  
  public void init() {
    bombs.clear();
    players.clear();
    items.clear();

    destructedBox = 0;
    boxCount = 0;
  }

  public void init(final int y, final String row) {
    for (int x = 0; x < 13; x++) {
      final char value = row.charAt(x);
      cells[x+13*y] = value;
      if (value == BOX || value == BOX_1 || value == BOX_2) {
        boxCount++;
      }
    }
  }

  public void simulate() {
    // simulate one turn
    final ListIterator<Bomb> ite = new ArrayList<>(bombs).listIterator();
    while (ite.hasNext()) {
      final Bomb b = ite.next();
      b.timer -= 1;
      if (b.timer == 0) {
        b.explode();
      }
    }
  }

  public void addBomb(final Bomb bomb) {
    bombs.add(bomb);
    cells[bomb.position.x+13*bomb.position.y] = BOMB;
  }
  
  public void explode(final Bomb bomb) {
    final List<Bomb> bombsToExplode = new ArrayList<>();
    final List<Bomb> explodedBombs = new ArrayList<>();
    
    bombsToExplode.add(bomb);
    while (!bombsToExplode.isEmpty()) {
      final Bomb b = bombsToExplode.remove(0);
      bombs.remove(b);
      explodedBombs.add(b);
      
      final Bomberman orginalBomberman = getBombermanWithId(b.owner);
      if (orginalBomberman != null) {
        orginalBomberman.bombsLeft+=1;
      }
      final int range = b.range;
      final P p = b.position;
      
      for (int r = 0; r < 4; r++) {
        final int dx = rot[r][0];
        final int dy = rot[r][1];
        for (int d = 1; d < range; d++) {
          final int x = p.x + d*dx;
          final int y = p.y + d*dy;
          final boolean shouldStopExplosion = checkExplosion(bombsToExplode, orginalBomberman, x, y);
          if (shouldStopExplosion) {
            break;
          }
        }
      }
    }

    for (final Bomb removeBombs : explodedBombs) {
      checkExplosion(bombsToExplode, null, removeBombs.position.x, removeBombs.position.y);
    }
  }

  private boolean checkExplosion(final List<Bomb> bombsToExplode, final Bomberman orginalBomberman, final int x, final int y) {
    if (isOnBoard(x, y)) {
      final int value = cells[x+13*y];
      if (value == WALL) {
        return true; // stop explosion
      }
      
      for (final Bomberman bomberman : players) {
        if (bomberman.position.equals(P.get(x,y))) {
          bomberman.isDead = true;
        }
      }

      switch (value) {
        case BOX:
          updatePoints(orginalBomberman, 1);
          cells[x+13*y] = EMPTY;
          return true;
        case BOX_1:
          updatePoints(orginalBomberman, 1.05);
          cells[x+13*y] = ITEM_1;
          items.add(Item.create(this, 0, P.get(x,y), 1, 0));
          return true;
        case BOX_2:
          updatePoints(orginalBomberman, 1.1);
          cells[x+13*y] = ITEM_2;
          items.add(Item.create(this, 0, P.get(x,y), 2, 0));
          return true;
        case ITEM_1:
        case ITEM_2:
          cells[x+13*y] = EMPTY;
          return true; // stop explosion
        case BOMB:
          final Bomb bombAt = getBombAt(x, y);
          if (bombAt != null) {
            bombsToExplode.add(bombAt);
          }
          return true; // stop explosion
      }
    } else {
      return true; // not on board
    }
    return false;
  }
  private void updatePoints(final Bomberman orginalBomberman, final double points) {
    destructedBox++;
    boxCount--;
    if (orginalBomberman != null) {
      orginalBomberman.points+=points;
    }
  }

  private Bomberman getBombermanWithId(final int owner) {
    for (final Bomberman b : players) {
      if (b.owner == owner) {
        return b;
      }
    }
    return null;
  }

  private Bomb getBombAt(final int x, final int y) {
    for (final Bomb b : bombs) {
      if (b.position.x == x && b.position.y == y) {
        return b;
      }
    }
    return null;
  }

  final boolean isOnBoard(final int x, final int y) {
    if (x < 0 || x > 12) {
      return false;
    }
    if (y < 0 || y > 10) {
      return false;
    }
    return true;
  }

  public void addPlayer(final Bomberman player) {
    players.add(player);
  }

  public void addItem(final Item item) {
    items.add(item);
    if (item.type == 1) {
      cells[item.position.x+13*item.position.y] = ITEM_1;
    } else {
      cells[item.position.x+13*item.position.y] = ITEM_2;
    }
  }

  public boolean canMoveTo(final int x, final int y) {
    if (!isOnBoard(x, y)) {
      return false;
    }
    
    final int value = cells[x+13*y];
    return value == EMPTY || value == ITEM_1 || value == ITEM_2;
  }

  public void walkOn(final Bomberman player, final P p) {
    final int value = cells[p.x+13*p.y];
    if ( value == ITEM_1) {
      player.currentRange+=1;
    } else if (value == ITEM_2) {
      player.bombsLeft+=1;
    }
    player.position = p;
    cells[p.x+13*p.y] = EMPTY;
  }
  
  public boolean canWalkOn(final P p) {
    final int value = cells[p.x+13*p.y];
    return value != Board.WALL 
        && value != Board.BOX
        && value != Board.BOX_1
        && value != Board.BOX_2
        && value != Board.BOMB
        ;
  }
}
