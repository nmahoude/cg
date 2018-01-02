package h;

import h.entities.Agent;
import h.entities.Bomb;

public class Cell {
  public static final int DIR_UP    = 0;
  public static final int DIR_RIGHT = 1;
  public static final int DIR_DOWN  = 2;
  public static final int DIR_LEFT  = 3;
  
  public static final Cell invalid = new Cell(null, -1, -1);
  static {
    for (int i=0;i<4;i++) {
      invalid.cells[i] = invalid;
    }
  }
  public Cell[] cells = new Cell[4];

  public final int x;
  public final int y;
  private GameState state;
  
  public Cell(GameState state, int x, int y) {
    this.state = state;
    this.x = x;
    this.y = y;
    this.flag = 0;
    for (int i=0;i<4;i++) {
      cells[i] = invalid;
    }
  }

  /**
   * bits : 
   * 0,7 -> bombs id (0->128)
   * 
   * 8 -> player 0
   * 9 -> player 1
   * 10 -> player 2
   * 11 -> player 3
   * 
   * 12 -> box
   * 13 -> item bomb+
   * 14 -> item range+
   * 
   */
  long flag;
  private long _flag;

  public static final long BOMBID_FLAG     = 0b0_00000011111111L;
  
  public static final long PLAYERS_MASK    = 0b0_00111100000000L;
  public static final long PLAYER0_MASK    = 0b0_00000100000000L;
  public static final long PLAYER1_MASK    = 0b0_00001000000000L;
  public static final long PLAYER2_MASK    = 0b0_00010000000000L;
  public static final long PLAYER3_MASK    = 0b0_00100000000000L;

  public static final long BOX_MASK        = 0b0_00100000000000L;
  public static final long ITEM_RANGE_FLAG = 0b0_01000000000000L;
  public static final long ITEM_BOMB_FLAG  = 0b0_10000000000000L;
  
  public void initValue(char value) {
    if (value == '.' || value=='X') {
      this.flag = 0;
    } else if (value == '0') {
      this.flag = BOX_MASK;
    } else if (value == '1') {
      this.flag = BOX_MASK | ITEM_RANGE_FLAG;
    } else if (value == '2') {
      this.flag = BOX_MASK | ITEM_BOMB_FLAG;
    }
  }

  public final void removeAgent(Agent agent) {
    this.flag &= ~(agent.mask);
  }

  public final void placeAgent(Agent agent) {
    this.flag |= agent.mask;
  }

  public void placeBomb(Bomb bomb) {
    this.flag |= bomb.id;
    bomb.cell = this;
  }

  public void flagExtraRangeItem() {
    this.flag |= ITEM_RANGE_FLAG;
  }

  public void flagExtraBombItem() {
    this.flag |= ITEM_BOMB_FLAG;
  }

  public void backup() {
    _flag = flag;
  }
  public void restore() {
    flag = _flag;
  }

  public final boolean explode() {
    boolean stopped = false;
    
    if ((flag & PLAYERS_MASK) != 0) {
      stopped = true;
      killPlayers();
    }
    if ((flag & BOX_MASK) != 0) {
      
    }
    
    return stopped;
  }

  private void killPlayers() {
    for (int p=0;p<state.agentsFE;p++) {
      Agent agent = state.agents[p];
      if (agent.dead) continue;
      agent.dead = true;
      removeAgent(agent);
    }
  }

}
