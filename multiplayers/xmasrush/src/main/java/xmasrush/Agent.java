package xmasrush;

import cgutils.test.TestOutputer;
import fast.read.FastReader;
import xmasrush.ai.push.Direction;
import xmasrush.ai.push.PushAction;

public class Agent {

  public final int id;
  public final State state;
  int numPlayerCards;
  public Pos pos = Pos.unknown;
  public Cell playerCell = new Cell(-1, -1);
  public int questItemsFE;
  public Pos[] questItems = new Pos[3];
  
  public Agent(int id, State state) {
    this.id = id;
    this.state = state;
  }
  
  public void read(FastReader in) {
    numPlayerCards = in.nextInt();
    int playerX = in.nextInt();
    int playerY = in.nextInt();
    pos = Pos.get(playerX, playerY);
    char[] playerTile = in.nextChars();
    playerCell.setDirections(playerTile);
    TestOutputer.output(numPlayerCards, playerX, playerY, playerTile);
  }

  @Override
  public String toString() {
    return String.format("%d @(%s)", id, pos);
  }
  
  public void addQuestItem(Pos pos) {
    questItems[questItemsFE++] = pos;
  }

  public void resetItems() {
    questItemsFE=0;
  }

  public void moveUp() {
    pos = Pos.getCircular(pos, Direction.UP);
  }

  public void moveDown() {
    pos = Pos.getCircular(pos, Direction.DOWN);
  }
  public void moveRight() {
    pos = Pos.getCircular(pos, Direction.RIGHT);
  }
  public void moveLeft() {
    pos = Pos.getCircular(pos, Direction.LEFT);
  }

  public void moveIfNecessary(PushAction action) {
    pos = pos.moveIfNecessary(action);
    for (int i=0;i<questItemsFE;i++) {
      questItems[i] = questItems[i].moveIfNecessary(action);
    }
  }
}
