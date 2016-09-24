import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Player {
  private static final int ENTITY_PLAYER = 0;
  private static final int ENTITY_BOMB = 1;
  private static final int ENTITY_ITEM = 2;
  
  static int[] rotx = { 1, 0,-1, 0};
  static int[] roty = { 0, 1, 0,-1};
  
  P pos;
  int id;

  enum EntityType {
    PLAYER,
    BOMB
  }
  static class P {
    final int x;
    final int y;
    public P(int x, int y) {
      super();
      this.x = x;
      this.y = y;
    }
    int distance(P p) {
      return (p.x-x)*(p.x-x)+(p.y-y)*(p.y-y);
    }
    @Override
    public String toString() {
      return "("+x+","+y+")";
    }
  }
  
  public interface BoardVisitor {
    static ResetVisitor reset = new ResetVisitor();
    static BoardVisitor putBombRadius = new PutBombRadiusVisitor();
    public void accept(Cell cell);
  }
  
  public static class ResetVisitor implements BoardVisitor {
    public void accept(Cell cell) {
      cell.bNeighbours = 0;
    }
  }
  public static class PutBombRadiusVisitor implements BoardVisitor {
    public void accept(Cell cell) {
      cell.bNeighbours++;
    }
  }
  public static class FindBestBombingZoneVisitor implements BoardVisitor {
    P p;
    int maxBombs = 0;
    int distance = Integer.MAX_VALUE;
    Cell bestCell = null;
    int range;
    
    public FindBestBombingZoneVisitor(P pos, int range) {
      this.p = pos;
      this.range = range;
    }    
    @Override
    public void accept(Cell cell) {
      if (bestCell == null) {
        bestCell = cell;
        return;
      } else {
        int cellDist = cell.pos.distance(p);
        if ( cell.bNeighbours > maxBombs) {
          bestCell = cell;
          maxBombs = cell.bNeighbours ;
        } else if ( cell.bNeighbours == maxBombs) {
          if (cellDist < distance) {
            bestCell = cell;
            maxBombs = cell.bNeighbours ;
            distance = cellDist; 
          }
        }
      }
    }
  }
  
  
  static class Bomb {
    public Bomb(int x, int  y) {
      pos = new P(x,y);
    }
    P pos;
  }
  
  static class Cell {
    public int bNeighbours;
    public Cell(int x, int y) {
      pos = new P(x,y);
    }
    static final int UP = 0;
    static final int DOWN = 1;
    static final int RIGHT = 2;
    static final int LEFT = 3;
    enum Type {
      BOMB, BOX, EMPTY, PLAYER, OPTION, BLOCK
    }
    
    Type type;
    Cell[] cells = new Cell[4];
    P pos; // pos on board
  }
  
  static class Game {
    static final int EMPTY = 0;
    static final int BOX = 9;
    static final int BLOCK = 20;
    private static final int NONE = -1;
    private static final Cell NO_CELL = new Cell(-1,-1);
    
    Map<Integer, Player> players = new HashMap<>();
    Player me = null;
    
    
    Cell[][] grid = new Cell[13][11];
    List<Bomb> bombs = new ArrayList<>();
    private int width;
    private int height;
    
    public Game(int width, int height) {
      this.width = width;
      this.height = height;
      initBoard();
    }
    
    private void initBoard() {
      for (int x=0;x<width;x++) {
        for (int y=0;y<height;y++) {
          Cell c = new Cell(x,y);
          grid[x][y] = c;
          if (x > 0) { c.cells[Cell.LEFT] = grid[x-1][y]; }
          if (x < width-1) { c.cells[Cell.RIGHT] = grid[x+1][y]; }
          if (y > 0) { c.cells[Cell.UP] = grid[x][y-1]; }
          if (y < height-1) { c.cells[Cell.DOWN] = grid[x][y+1]; }
        }
      }
    }

    public void addRow(int rowIndex, String row) {
      for (int i=0;i<row.length();i++) {
        char c = row.charAt(i);
        if(c == '.') {
          grid[i][rowIndex].type = Cell.Type.EMPTY;
        } else if (c=='0') {
          grid[i][rowIndex].type = Cell.Type.BOX;
          visitCross(i,rowIndex, 3, BoardVisitor.putBombRadius);
          bombs.add(new Bomb(i, rowIndex));
        }
      }
    }
    Bomb oldFindClosestBomb(P point) {
      Bomb nearest = null;
      double minDistance = 10000000;
      for (Bomb bomb : bombs) {
        double distance = bomb.pos.distance(point);
        if (distance < minDistance) {
          minDistance = distance;
          nearest = bomb;
        }
      }
      return nearest;
    }
    P findClosestBomb(P point) {
      int maxBombs = 0;
      P nearest = null;
      
      for (int x=-4;x<4;x++) {
        for (int y=-4;y<4;y++) {
          if (x == 0 && y==0) {
            P pos = new P(point.x+x, point.y+y);
            int bombs = countBombsReachableAt(pos);
            if( maxBombs < bombs) {
              maxBombs = bombs;
              nearest = pos;
            }
          }
        }
      }
      if (nearest == null) {
        return oldFindClosestBomb(point).pos;
      }
      return nearest;
    }
    
    int countBombsReachableAt(P pos) {
      int boxes = 0;
      for (int rot = 0;rot<4;rot++) {
        int dx = rotx[rot];
        int dy = roty[rot];
        
        for (int i=0;i<4;i++) {
          Cell cell = getCellAt(pos.x+dx, pos.y+dy);
          if ( cell.type == Cell.Type.BLOCK) {
            break;
          } else if ( cell.type == Cell.Type.BOX) {
            boxes++;
          }
        }
      }
      return boxes;
    }
    private Cell getCellAt(int x, int y) {
      if (x<0 || x>=13 || y<0 || y>=11) {
        return NO_CELL;
      }
      return grid[x][y];
    }
    public void removeBomb(Bomb nearest) {
      bombs.remove(nearest);
    }
    public void reset() {
      bombs.clear();
      visit(BoardVisitor.reset);
    }
    
    void visit(BoardVisitor visitor) {
      for (int x=0;x<width;x++) {
        for (int y=0;y<height;y++) {
          visitor.accept(grid[x][y]);
        }
      }
    }
    void visitCross(int x, int y, int range, BoardVisitor visitor) {
      int dx;
      int dy;
      Cell cell;
      for (int rot=0;rot<4;rot++) {
        for (int d=0;d<=range;d++) {
          dx = d*rotx[rot];
          dy = d*roty[rot];
          cell = getCellAt(x+dx, y+dy);
          if (cell.type == Cell.Type.BLOCK || cell.type == Cell.Type.BOX) {
            break;
          }
          if (cell != NO_CELL) {
            visitor.accept(cell);
          }
        }
      }
    }
  }

  int posx;
  int posy;
  
  public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        Game game = new Game(width, height);
        
        int me = in.nextInt();
        in.nextLine();

        
        Bomb lastBomb = null;
        // game loop
        while (true) {
          game.reset();
            for (int y = 0; y < height; y++) {
                String row = in.nextLine();
                game.addRow(y, row);
            }
            int entities = in.nextInt();
            for (int i = 0; i < entities; i++) {
                int entityType = in.nextInt();
                int owner = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();

                if (entityType == ENTITY_PLAYER) {
                  Player player = game.players.get(owner);
                  if (player == null) {
                    player = new Player();
                    game.players.put(owner, player);
                  }
                  player.pos = new P(x,y);
                  if (owner == me) {
                    game.me = player;
                  }
                }
                int param1 = in.nextInt();
                int param2 = in.nextInt();
            }
            in.nextLine();

            FindBestBombingZoneVisitor visitor = new FindBestBombingZoneVisitor(game.me.pos, 5);
            game.visitCross(game.me.pos.x, game.me.pos.y, 5, visitor);
            System.err.println("best bombing: "+visitor.bestCell.pos+" , dist:"+visitor.distance);
            if (visitor.bestCell != null) {
              Cell cell =visitor.bestCell;
              if (visitor.distance < 1.1) {
                System.out.println("BOMB "+cell.pos.x+" "+cell.pos.y);  
              } else {
                System.out.println("MOVE "+cell.pos.x+" "+cell.pos.y);  
              }
            } else {
              System.out.println("MOVE "+ (game.me.pos.x+5)+" "+(game.me.pos.y+5));  
            }
        }
    }
}