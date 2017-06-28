package ww.sim;

import ww.Agent;
import ww.Dir;

public class Move {
  public Agent agent;
  public Dir dir1;
  public Dir dir2;
  
  private boolean isDir1Valid;
  private boolean isDir2Valid;
  
  public boolean isPush;
  
  public Move(Agent agent) {
    this.agent = agent;
  }

  public void copyTo(Move move) {
    move.agent = agent;
    move.dir1 = dir1;
    move.dir2 = dir2;
    
    move.isDir1Valid = isDir1Valid;
    move.isDir2Valid = isDir2Valid;
    
    move.isPush = isPush;
  }
  
  public boolean isDir1Valid() {
    return isDir1Valid;
  }
  public boolean isDir2Valid() {
    return isDir2Valid;
  }
  
  public boolean isValid() {
    return isDir1Valid && isDir2Valid;
  }
  
  public void dir1Invalid() {
    isDir1Valid = false;
    isDir2Valid = false;
  }
  
  public void dir2Invalid() {
    isDir1Valid = true;
    isDir2Valid = false;
  }
  
  public void allValid() {
    isDir1Valid = true;
    isDir2Valid = true;
  }
  public String toPlayerOutput() {
    if (isPush) {
      return  "PUSH&BUILD "+agent.id+" "+dir1.toString()+" "+dir2.toString();
    } else {
      return "MOVE&BUILD "+agent.id+" "+dir1.toString()+" "+dir2.toString();
    }
  }
  
  @Override
  public String toString() {
    return toPlayerOutput();
  }
}
