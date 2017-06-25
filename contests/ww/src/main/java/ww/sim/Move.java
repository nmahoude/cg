package ww.sim;

import ww.Dir;

public class Move {
  public int id;
  public Dir dir1;
  public Dir dir2;
  
  private boolean isDir1Valid;
  private boolean isDir2Valid;
  
  public boolean isPush;
  
  public int currentHeight;
  public int dir1X, dir1Y, dir1Height;
  public int dir2X, dir2Y, dir2Height;
  
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
}
