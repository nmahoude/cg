package c4l.entities;

public class Translatable {
  String code;
  Object[] values;

  public Translatable(String code, Object... values) {
    this.code = code;
    this.values = values;
  }
}
