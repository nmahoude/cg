package hypersonic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.utils.P;

public class Board {
  static public Deque<Board> availableBoards = new ArrayDeque<>();
  static {
    for (int i=0;i<10000;i++) {
      availableBoards.push(new Board());
    }
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

  List<P> boxes = new ArrayList<>();
  List<Bomb> bombs = new ArrayList<>();
  List<Item> items = new ArrayList<>();
  public List<Bomberman> players = new ArrayList<>();
  public Bomberman me;

  int destructedBox;

  public Board() {
    cells = new int[13*11];
  }
  private Board(Board board) {
  }

  private void clean() {
    boxes.clear();
    bombs.clear();
    items.clear();
    players.clear();
    me = null;
    destructedBox = 0;
    // cells will be copied later
  }

  public static void retrocede(Board board) {
    availableBoards.push(board);
  }
  
  public Board duplicate() {
    Board board;
    if (availableBoards.isEmpty()) {
      board = new Board();
    } else {
      board = availableBoards.pop();
    }
    board.clean();
    board.boxes.addAll(this.boxes);
    
    for (Bomb b : this.bombs) {
      board.bombs.add(b.duplicate(board));
    }
    for (Item i : this.items) {
      board.items.add(i.duplicate(board));
    }
    for (Bomberman b : this.players) {
      Bomberman copy = b.duplicate(board);
      if (b == this.me) {
        board.me = copy;
      }
      board.players.add(copy);
    }
    
    System.arraycopy(cells, 0, board.cells, 0, 13*11);
    return board;
  }
  
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
      cells[x+13*y] = value;
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
        b.explode();
        ite.remove();
      }
    }
  }

  public void addBomb(Bomb bomb) {
    bombs.add(bomb);
    cells[bomb.position.x+13*bomb.position.y] = BOMB;
  }

  public void explode(Bomb bomb) {
    List<Bomb> bombsToExplode = new ArrayList<>();
    bombsToExplode.add(bomb);
    while (!bombsToExplode.isEmpty()) {
      Bomb b = bombsToExplode.remove(0);
      Bomberman orginalBomberman = getBomberManWithId(b.owner);
      if (orginalBomberman != null) {
        orginalBomberman.bombsLeft+=1;
      }
      int range = b.range;
      P p = b.position;
      cells[p.x+13*p.y] = EMPTY;
      
      checkExplosion(bombsToExplode, orginalBomberman, p.x, p.y);
      for (int r = 0; r < 4; r++) {
        int dx = rot[r][0];
        int dy = rot[r][1];
        for (int d = 1; d < range; d++) {
          int x = p.x + d*dx;
          int y = p.y + d*dy;
          boolean shouldBreak = checkExplosion(bombsToExplode, orginalBomberman, x, y);
          if (shouldBreak) {
            break;
          }
        }
      }
    }
  }

  private boolean checkExplosion(List<Bomb> bombsToExplode, Bomberman orginalBomberman, int x, int y) {
    if (isOnBoard(x, y)) {
      int value = cells[x+13*y];
      if (value == WALL) {
        return true; // stop explosion
      }
      for (Bomberman bomberman : players) {
        if (bomberman.position.equals(new P(x,y))) {
          bomberman.isDead = true;
        }
      }
      if (value >= BOX && value <= BOX_2) {
        destructedBox++;
        if (orginalBomberman != null) {
          orginalBomberman.points++;
        }
        if (value == BOX) {
          cells[x+13*y] = EMPTY;
        } else if (value == BOX_1) {
          cells[x+13*y] = ITEM_1;
          items.add(new Item(this, 0, new P(x,y), 1, 0));
        } else {
          cells[x+13*y] = ITEM_2;
          items.add(new Item(this, 0, new P(x,y), 2, 0));
        }
        return true; // stop explosion
      } else if (value == ITEM_1 || value == ITEM_2) {
        cells[x+13*y] = EMPTY;
        return true; // stop explosion
      } else if (value == 'b') {
        bombsToExplode.add(getBombAt(x, y));
        cells[x+13*y] = EMPTY;
        return true; // stop explosion
      }
    } else {
      return true; // stop explosion
    }
    return false;
  }

  private Bomberman getBomberManWithId(int owner) {
    for (Bomberman b : players) {
      if (b.owner == owner) {
        return b;
      }
    }
    return null;
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
    if (item.type == 1) {
      cells[item.position.x+13*item.position.y] = ITEM_1;
    } else {
      cells[item.position.x+13*item.position.y] = ITEM_2;
    }
  }

  public boolean canMoveTo(int x, int y) {
    if (!isOnBoard(x, y)) {
      return false;
    }
    
    int value = cells[x+13*y];
    return value == EMPTY || value == ITEM_1 || value == ITEM_2;
  }

  public void walkOn(Bomberman player, P p) {
    int value = cells[p.x+13*p.y];
    if ( value == ITEM_1) {
      player.currentRange+=1;
    } else if (value == ITEM_2) {
      player.bombsLeft++;
    }
    player.position = p;
    cells[p.x+13*p.y] = EMPTY;
  }
  
  public boolean canWalkOn(P p) {
    int value = cells[p.x+13*p.y];
    return value != Board.WALL 
        && value != Board.BOX
        && value != Board.BOX_1
        && value != Board.BOX_2
        && value != Board.BOMB
        ;
  }
}
