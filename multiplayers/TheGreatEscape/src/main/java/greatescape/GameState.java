package greatescape;

import java.util.Scanner;

public class GameState {
  public Board board = new Board();
  public int playerCount;
  public int alivePlayer;
  
  public int w;
  public int h;
  public int myId;
  public int directOpponentId;
  
  public Agent[] agents = null;
  public Agent me;
  public int wallCount;

  public void readInitialInput(Scanner in) {
    w = in.nextInt();
    h = in.nextInt();
    playerCount = in.nextInt();
    myId = in.nextInt();

    agents = new Agent[playerCount];
    for (int i=0;i<playerCount;i++) {
      agents[i] = new Agent();
      agents[i].goal = getTargetFromId(i);
    }
    me = agents[myId];
  }
  
  static Target getTargetFromId(int id) {
    switch (id) {
      case 0:
        return Target.RIGHT;
      case 1:
        return Target.LEFT;
      case 2:
        return Target.DOWN;
      default:
        throw new RuntimeException("Unkown id "+id);
    }
  }
  
  public void readInput(Scanner in) {
    alivePlayer = 0;
    for (int i = 0; i < playerCount; i++) {
      int x = in.nextInt(); // x-coordinate of the player
      int y = in.nextInt(); // y-coordinate of the player
      int wl = in.nextInt(); // number of walls available for the // player
      alivePlayer += x != -1 ? 1 : 0;
      
      agents[i].position.x = x;
      agents[i].position.y = y;
      agents[i].wallsLeft = wl;
      System.err.println("Player"+i+" ("+x+","+y+","+wl+")");
    }
    Player.start = System.currentTimeMillis();
    
    wallCount = in.nextInt();
    System.err.println("Board board = new Board();");
    board.resetWalls();
    for (int i = 0; i < wallCount; i++) {
      int wallX = in.nextInt(); // x-coordinate of the wall
      int wallY = in.nextInt(); // y-coordinate of the wall
      String wallOrientation = in.next(); // wall orientation ('H' or 'V')
      board.addWall(i+1, wallX, wallY, WallOrientation.valueOf(wallOrientation));
      System.err.println("board.addWall("+(i+1)+","+wallX+", "+wallY+", WallOrientation."+wallOrientation+");");
    }
    
    updateNextOpponentId();
    backup();
  }

  private int updateNextOpponentId() {
    int nextOpponnentId = (myId + 1) % playerCount;
    while (agents[nextOpponnentId].isDead()) {
      nextOpponnentId = (nextOpponnentId + 1) % playerCount;
    }
    return nextOpponnentId;
  }
  
  private void backup() {
    board.backup();    
  }

  public void restore() {
    board.restore();
  }

  public Cell getMyCell() {
    return board.getCell(me.position);
  }
  
}
