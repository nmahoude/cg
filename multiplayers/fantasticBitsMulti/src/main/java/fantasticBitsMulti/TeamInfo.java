package fantasticBitsMulti;

import java.util.Scanner;

public class TeamInfo {
  public final int id;
  public int mana;
  public int score;
  
  private int _mana;
  private int _score;

  public TeamInfo(int id) {
    this.id = id;
  }

  public void backup() {
    _mana = mana;
    _score = score;
  }
  
  public void restore() {
    mana = _mana;
    score = _score;
  }

  public void read(Scanner in) {
    score = in.nextInt();
    mana = in.nextInt();
    TestOutputer.output(score, mana);
  }
  
  @Override
  public String toString() {
    return String.format("Team %d => score=%d, mana=%d", id, score, mana);
  }
}
