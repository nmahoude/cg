package spring2022.ai;

import cgfx.CGFXColor;
import spring2022.Pos;

public class NoDraw implements DrawDebugger {


  @Override
  public void drawLine(CGFXColor color, double drawSize, Pos start, Pos end) {
  }

  @Override
  public void drawLine(CGFXColor color, Pos start, Pos end) {
  }

  @Override
  public void drawCircle(CGFXColor color, Pos from, int radius) {
  }

  @Override
  public void drawText(CGFXColor black, Pos pos, String string) {
  }

}
