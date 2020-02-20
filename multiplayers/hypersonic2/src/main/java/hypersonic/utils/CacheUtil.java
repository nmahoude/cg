package hypersonic.utils;

import java.util.ArrayDeque;
import java.util.Deque;

public class CacheUtil<T> {
  Deque<T> available = new ArrayDeque<>();
  
  public void retrocede(final T t) {
    available.push(t);
  }

  public void push(final T t) {
    available.push(t);
  }

  public final boolean isEmpty() {
    return available.isEmpty();
  }

  public T pop() {
    return available.pop();
  }

  public int size() {
    return available.size();
  }
}
