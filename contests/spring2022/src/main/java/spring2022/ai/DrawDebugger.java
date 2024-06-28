package spring2022.ai;

import cgfx.CGFXColor;
import spring2022.Pos;

public interface DrawDebugger {

  void drawLine(CGFXColor color, double drawSize, Pos start, Pos end);

  void drawLine(CGFXColor color, Pos start, Pos end);

  void drawCircle(CGFXColor color, Pos from, int radius);

  void drawText(CGFXColor black, Pos pos, String string);

}