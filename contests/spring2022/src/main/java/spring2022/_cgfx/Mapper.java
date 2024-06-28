package spring2022._cgfx;

import cgfx.CGFXColor;
import javafx.scene.paint.Color;
import spring2022.Pos;

public class Mapper {
  public static cgfx.Pos map(double centerx, double centery) {
    return cgfx.Pos.from(centerx, centery);
  }
  
  public static cgfx.Pos map(Pos pos) {
    return cgfx.Pos.from(pos.x, pos.y);
  }

  public static Color map(CGFXColor color) {
    switch(color) {
    case Black: return Color.BLACK;
    case Blue: return Color.BLUE;
    case Grey: return Color.GREY;
    case LightBlue: return Color.LIGHTBLUE;
    case LightRed: return Color.LIGHTCORAL;
    case Red: return Color.RED;
    default:
      System.out.println("Uknown color "+color);
      return Color.BLACK;
    }
  }
}
