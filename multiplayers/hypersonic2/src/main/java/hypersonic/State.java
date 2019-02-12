package hypersonic;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.simulation.Simulation;
import hypersonic.utils.P;

public class State {
  public Board board = new Board();
  public long hash; // zobrist hash
  
  public int turn;
  public Bomberman players[] = new Bomberman[4];
  public int playersFE = 4;
  int destructedBox;

  public State() {
    for (int i=0;i<4;i++) {
      players[i] = new Bomberman(i, P.get(0, 0), 0, 0);
      players[i].isDead = true;
    }
    clean();
  }
  
  public void clean() {
    hash = 0L;
    board.clean();
    
    playersFE = 4;
    for (int i=0;i<4;i++) {
      players[i].isDead = true;
    }
    destructedBox = 0;
    // cells will be copied later
  }


  public void copyFrom(State model) {
    this.clean();

    this.hash = model.hash;
    this.turn = model.turn;
    this.board.copyFrom(model.board);
    
    this.playersFE = 4;
    for (int p=0;p<model.playersFE;p++) {
      Bomberman b = model.players[p];
      if (b.isDead) {
        players[p].isDead = true;
      } else {
        players[p].copyFrom(b);
      }
    }
  }
  
  public void init() {
    clean();
    board.init();
    playersFE = 4;

    destructedBox = 0;
  }

  public void init(final int y, final String row) {
    board.init(y, row);
  }

  public void updateBombs() {
    turn++;
    
    board.updateBombs(this);
  }

  public void addBomb(final Bomb bomb) {
    if (bomb.owner == Player.myId) {
      hash = hash ^ ZobristValues.fromBomb(bomb, turn);
    }
    board.addBomb(bomb);
  }
  
  
  void updatePoints(Bomberman orginalBomberman) {
    destructedBox++;
    if (orginalBomberman != null) {
      orginalBomberman.points++;
    }
  }

  Bomberman getBombermanWithId(int owner) {
    return players[owner];
  }

  public Bomberman getBomberman(int index) {
    return players[index];
  }

  public void addItem(Item item) {
    board.addItem(item);
  }

  public void walkOn(final Bomberman player, final P p) {
    final int value = board.cells[p.offset];
    if ( value == Board.ITEM_1) {
      hash = hash ^ ZobristValues.fromItem(p, turn);
      player.currentRange+=1;
      Simulation.deltaRange+=1;
      board.cells[p.offset] = Board.EMPTY;
    } else if (value == Board.ITEM_2) {
      hash = hash ^ ZobristValues.fromItem(p, turn);
      player.bombsLeft+=1;
      player.bombCount+=1;
      Simulation.deltaBomb+=1;
      board.cells[p.offset] = Board.EMPTY;
    }
    player.position = p;
  }

  public void killPlayersAt(P pos) {
    for (int p=0;p<Player.NUMBER_OF_PLAYER;p++) {
      Bomberman bomberman = players[p];
      if (bomberman.isDead) continue;
      
      if (bomberman.position.offset == pos.offset) {
        bomberman.isDead = true;
        if (p == Player.myId) {
          hash = -1; // zobrist du pauvre :)
        }
      }
    }
  }

  public boolean canWalkOn(P p) {
    return board.canWalkOn(p);
  }

  public void resetPlayerPoints() {
    players[0].points = 0;
    players[1].points = 0;
    players[2].points = 0;
    players[3].points = 0;
  }
}
