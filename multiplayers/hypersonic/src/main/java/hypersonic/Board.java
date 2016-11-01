package hypersonic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.utils.P;

public class Board {
  public static final char EMPTY = '.';
  public static final char WALL = 'X';

  int rot[][] = {
      { 1, 0 },
      { 0, 1 },
      { -1, 0 },
      { 0, -1 }
  };

  int cells[][] = new int[13][11];

  List<P> boxes = new ArrayList<>();
  List<Bomb> bombs = new ArrayList<>();
  List<Item> items = new ArrayList<>();
  List<Bomberman> players = new ArrayList<>();
  Bomberman me;

  int destructedBox;

  public void init() {
    boxes.clear();
    bombs.clear();
    players.clear();
    items.clear();

    destructedBox = 0;
  }

  public void init(int y, String row) {
    for (int x = 0; x < 13; x++) {
      char value = row.charAt(x);
      cells[x][y] = value;
      if (value >= '0' && value <= '3') {
        addBox(x, y);
      }
    }
  }

  public void addBox(int x, int y) {
    boxes.add(new P(x, y));
  }

  public P findClosestBox() {
    int bestDist = Integer.MAX_VALUE;
    P closest = null;
    for (P p : boxes) {
      int sqDist = p.squareDistance(me.position);
      if (sqDist < bestDist) {
        bestDist = sqDist;
        closest = p;
      }
    }
    return closest;
  }

  public void simulate() {
    // simulate one turn
    Iterator<Bomb> ite = bombs.iterator();
    for (;ite.hasNext();) {
      Bomb b = ite.next();
      b.timer -= 1;
      if (b.timer == 0) {
        b.explode(this);
        ite.remove();
      }
    }
  }

  public void addBomb(Bomb bomb) {
    bombs.add(bomb);
    cells[bomb.position.x][bomb.position.y] = 'b';
  }

  public void explode(Bomb bomb) {
    List<Bomb> bombsToExplode = new ArrayList<>();
    bombsToExplode.add(bomb);
    while (!bombsToExplode.isEmpty()) {
      Bomb b = bombsToExplode.remove(0);
      
      int range = b.range;
      P p = b.position;
      cells[p.x][p.y] = EMPTY;
      
      for (int r = 0; r < 4; r++) {
        int dx = rot[r][0];
        int dy = rot[r][1];

        for (int d = 0; d < range; d++) {
          int x = p.x + d*dx;
          int y = p.y + d*dy;
          if (isOnBoard(x, y)) {
            if (cells[x][y] == WALL) {
              break; // stop explosion
            }
            if (cells[x][y] >= '0' && cells[x][y] <= '3') {
              destructedBox++;
              if (cells[x][y] == '0') {
                cells[x][y] = EMPTY;
              } else if (cells[x][y] == '1') {
                cells[x][y] = EMPTY;
              } else {
                cells[x][y] = EMPTY;
              }
              break; // stop explosion in this direction
            } else if (cells[x][y] == 'i') {
              cells[x][y] = EMPTY;
            } else if (cells[x][y] == 'b') {
              bombsToExplode.add(getBombAt(x, y));
              cells[x][y] = EMPTY;
            }
          } else {
            break; // out of board
          }
        }
      }
    }
  }

  private Bomb getBombAt(int x, int y) {
    for (Bomb b : bombs) {
      if (b.position.x == x && b.position.y == y) {
        return b;
      }
    }
    return null;
  }

  final boolean isOnBoard(int x, int y) {
    if (x < 0 || x > 12) {
      return false;
    }
    if (y < 0 || y > 10) {
      return false;
    }
    return true;
  }

  public void addPlayer(Bomberman player) {
    players.add(player);
  }

  public void addItem(Item item) {
    items.add(item);
  }

  public void copyFrom(Board board) {
    me = new Bomberman(board.me.id, board.me.position, board.me.bombsLeft, board.me.currentRange);
    for (int y=0;y<11;y++) {
      for (int x=0;x<13;x++) {
        cells[x][y] = board.cells[x][y];
      }
    }
  }

  public boolean canMoveTo(int x, int y) {
    return isOnBoard(x, y) && cells[x][y] == EMPTY;
  }
}
