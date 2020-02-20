package cotc.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {
  @SafeVarargs
  public static final <T> String join(T... v) {
      return Stream.of(v).map(String::valueOf).collect(Collectors.joining(" "));
  }
  
  public static int clamp(int val, int min, int max) {
    return Math.max(min, Math.min(max, val)); 
  }

}
