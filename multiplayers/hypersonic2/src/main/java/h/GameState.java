package h;

import java.util.Scanner;

import h.entities.Agent;
import h.entities.Bomb;

public class GameState {
  public Agent me;
  public Agent agents[] = new Agent[4];
  public int agentsFE = 0;
  public int base = 0;
  
  public final static int MAX_BOMBS_DEPTH = 8+20;
  Bomb bombsCalendar[][] = new Bomb[MAX_BOMBS_DEPTH][4];
  int bombsCalendarFE[] = new int[MAX_BOMBS_DEPTH];
  
  int _bombsCalendarFE[] = new int[MAX_BOMBS_DEPTH];
  
  Cell cells[][] = new Cell[13][11];
  
  public GameState() {
    for (int i=0;i<4;i++) {
      agents[i] = new Agent(i);
    }
    initBombs();
    
    initBoard();
  }

  private void initBombs() {
    int id = 0;
    for (int i=0;i<MAX_BOMBS_DEPTH;i++) {
      for (int b=0;b<4;b++) {
        bombsCalendar[i][b] = new Bomb(id++);
      }
    }
    initBombCalendar();
  }

  private void initBombCalendar() {
    for (int i=0;i<MAX_BOMBS_DEPTH;i++) {
      bombsCalendarFE[i] = 0;
    }    
  }

  private void initBoard() {
    for (int y=0;y<11;y++) {
      for (int x=0;x<13;x++) {
        if (y % 2 == 1 && x % 2 == 1) {
          cells[x][y] = Cell.invalid;
        } else {
          cells[x][y] = new Cell(this, x, y);
        }
      }
    }
    setNeighbors();
  }

  private void setNeighbors() {
    for (int y=0;y<11;y++) {
      for (int x=0;x<13;x++) {
        Cell current = cells[x][y];
        current.cells[Cell.DIR_UP] = y>0 ? cells[x][y-1] : Cell.invalid;
        current.cells[Cell.DIR_DOWN] = y<(11-1) ? cells[x][y+1] : Cell.invalid;
        current.cells[Cell.DIR_RIGHT] = x<(13-1) ? cells[x+1][y] : Cell.invalid;
        current.cells[Cell.DIR_LEFT] = x>0 ? cells[x-1][y] : Cell.invalid;
      }
    }
  }
  
  public void readBoard(Scanner in) {
    for (int y = 0; y < 11; y++) {
      String row = in.next();
      for (int x=0;x<13;x++) {
        char value = row.charAt(x);
        cells[x][y].initValue(value);
      }
    }
  }

  public void backup() {
    for (int i=0;i<agentsFE;i++) {
      agents[i].backup();
    }
    for (int y=0;y<11;y++) {
      for (int x=0;x<13;x++) {
        if (y % 2 != 1 || x % 2 != 1) {
          cells[x][y].backup();
        }
      }
    }
    for (int i=0;i<MAX_BOMBS_DEPTH;i++) {
      _bombsCalendarFE[i] = bombsCalendarFE[i];
      for (int b=0;b<4;b++) {
        bombsCalendar[i][b].backup();
      }
    }
  }

  public void restore() {
    base = 0;
    for (int i=0;i<agentsFE;i++) {
      agents[i].restore();
    }
    for (int y=0;y<11;y++) {
      for (int x=0;x<13;x++) {
        if (y % 2 != 1 || x % 2 != 1) {
          cells[x][y].restore();
        }
      }
    }
    for (int i=0;i<MAX_BOMBS_DEPTH;i++) {
      bombsCalendarFE[i] = _bombsCalendarFE[i];
      for (int b=0;b<4;b++) {
        bombsCalendar[i][b].restore();
      }
    }
  }

  public void addBomb(int x, int y, int owner, int turnsLeft, int range) {
    Bomb bomb = bombsCalendar[turnsLeft][bombsCalendarFE[base + turnsLeft]++];
    bomb.agent = agents[owner];
    bomb.turnsLeft =  turnsLeft;
    bomb.range = range;
    bomb.dead = false;
    bomb.backup();
    cells[x][y].placeBomb(bomb);
  }

  public void init() {
    base = 0;
    agentsFE = 0;
    me = null;
    initBombCalendar();
  }

  /**
   * explode undead bombs at base
   */
  public void explodeBombs() {
    int number = bombsCalendarFE[base];
    for (int i=0;i<number;i++) {
      Bomb bomb = bombsCalendar[base][i];
      if (bomb.dead) continue; // already exploded
      bomb.explode();
    }
  }
}
