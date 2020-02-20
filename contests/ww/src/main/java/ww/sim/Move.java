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
  
  public void copyTo(Move move) {
    move.id = id;
    move.dir1 = dir1;
    move.dir2 = dir2;
    
    move.isDir1Valid = isDir1Valid;
    move.isDir2Valid = isDir2Valid;
    
    move.isPush = isPush;
    
    move.currentHeight = currentHeight;
    move.dir1X = dir1X; 
    move.dir1Y = dir1Y; 
    move.dir1Height = dir1Height;
    move.dir2X = dir2X;
    move.dir2Y = dir2Y;
    move.dir2Height = dir2Height;
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
      return  "PUSH&BUILD "+id+" "+dir1.toString()+" "+dir2.toString();
    } else {
      return "MOVE&BUILD "+id+" "+dir1.toString()+" "+dir2.toString();
    }
  }
}
